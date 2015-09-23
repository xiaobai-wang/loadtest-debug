package com.demandware.xlt.actions.catalog;

import org.junit.Assert;

import com.demandware.xlt.actions.AbstractAjaxAction;
import com.demandware.xlt.util.AjaxUtils;
import com.demandware.xlt.util.Context;
import com.demandware.xlt.util.Page;
import com.demandware.xlt.validators.Validator;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.xceptance.common.util.RegExUtils;
import com.xceptance.xlt.api.engine.Session;
import com.xceptance.xlt.api.util.elementLookup.Results;

/**
 * Page the results of a grid page.
 * 
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 */
public class Paging extends AbstractAjaxAction
{
    /**
     * Locate the paging links.
     * 
     * @return locator for the paging links.
     */
    private static Results locatePagingLinks()
    {
        return Page.getPrimaryContentContainerLocator().byCss(".search-result-options .pagination > ul > li > a");
    }

    /**
     * Checks if paging is possible. </br><b>NOTE: Only works on a product grid page</b>
     * <p>
     * <b>Page:</b> use this on a product grid page.
     * </p>
     * 
     * @return whether paging is possible or not. <tt>false</tt>, if not on a product grid page
     */
    public static boolean isPossible()
    {
        return Page.checkExistance(locatePagingLinks());
    }

    /**
     * Page number we're going to click.
     */
    private String targetPageNo;

    /**
     * Page number we start from.
     */
    private String basePageNo;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() throws Exception
    {
        // Choose a random and not yet selected page link and remember the link text.
        final HtmlElement pagingLink = locatePagingLinks().asserted("No paging link found!").random();

        // Remember the number of the target page.
        targetPageNo = pagingLink.getTextContent().trim();

        // Furthermore remember the number of the current page.
        basePageNo = getCurrentPageNumber();

        // Perform the AJAX call and update the page.
        AjaxUtils.callAndUpdate(pagingLink.getAttribute("href"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void postValidate() throws Exception
    {
        // Check that it is still a product grid page since paging doesn't
        // change the page type.
        Validator.validateCommonPage();
        Assert.assertTrue("Paging doesn't result in product grid page.", Page.isProductGridPage());

        // Get the current page element.
        final String currentPageNo = getCurrentPageNumber();

        // If the target link contained a number (such as 2, 3, or 4 check that this number matches the current page
        // number.
        if (RegExUtils.isMatching(targetPageNo, "\\d+"))
        {
            if (!targetPageNo.equals(currentPageNo))
            {
                // Target and current page number do not match.
                Session.logEvent("Unexpected page number.",
                                 "expected:" + targetPageNo + ", current:" + currentPageNo + " -> " + Context.getPage().getUrl());
            }
        }
        else
        {
            // The target link contained not a number but something like '>' or '>>'. In this case just check that at
            // least the page number has changed.
            Assert.assertFalse("Current page did not change after paging from \"" + basePageNo + "\" by \"" + targetPageNo + "\"",
                               basePageNo.equals(currentPageNo));
        }
    }

    /**
     * Get the current product grid page number.
     * <p>
     * <b>Page:</b> use this on a product grid page.
     * </p>
     * 
     * @return the current page number
     * @throws AssertionError
     *             if there is no current page number available on the page
     */
    public static String getCurrentPageNumber() throws AssertionError
    {
        return Page.getPrimaryContentContainerLocator()
                   .byXPath("./div[@class='search-result-options']/div[@class='pagination']/ul/li[@class='current-page']")
                   .asserted("No actual page number in product grid found!").first()
                   .getTextContent().trim();
    }
}
