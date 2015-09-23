package com.demandware.xlt.actions.catalog;

import org.apache.commons.lang.WordUtils;
import org.junit.Assert;

import com.demandware.xlt.actions.AbstractAjaxAction;
import com.demandware.xlt.util.AjaxUtils;
import com.demandware.xlt.util.Context;
import com.demandware.xlt.util.Page;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.xceptance.common.util.RegExUtils;
import com.xceptance.xlt.api.engine.Session;
import com.xceptance.xlt.api.util.elementLookup.Results;

/**
 * Performs a refinement.
 * 
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 */
public class Refine extends AbstractAjaxAction
{
    /**
     * XPath for an attribute refinement type that has at least one unselected refinement and is not a category
     * refinement.
     */
    private static final String REFINEMENT_TYPE_W_BRANDS_XPATH = "id('secondary')/div[contains(concat(' ',@class,' '),' refinement ') and not(contains(@class,'category-refinement')) and (./ul/li[not(contains(concat(' ',@class,' '),' unselectable ')) and not(contains(concat(' ',@class,' '),' selected '))]/a)]";

    /**
     * XPath for an attribute refinement type that has at least one unselected refinement and is neither a category nor
     * a brand refinement.
     */
    private static final String REFINEMENT_TYPE_NO_BRANDS_XPATH = "id('secondary')/div[contains(concat(' ',@class,' '),' refinement ') and not(contains(@class,'category-refinement')) and (./ul/li[not(contains(concat(' ',@class,' '),' unselectable ')) and not(contains(concat(' ',@class,' '),' selected '))]/a) and not (contains(@class,'Brand'))]";

    /**
     * Get the locator for the desired refinement types.
     * 
     * @return locator for the desired refinement types
     */
    private static Results getRefinementLocator()
    {
        final String xpath = Context.getConfiguration().isRefineByBrandEnabled() ? REFINEMENT_TYPE_W_BRANDS_XPATH
                                                                                : REFINEMENT_TYPE_NO_BRANDS_XPATH;
        return Page.find().byXPath(xpath);
    }

    /**
     * Checks if a refinement which does not change the category is possible. </br><b>NOTE: Only works on a product grid
     * page</b>
     * <p>
     * <b>Page:</b> use this on a product grid page.
     * </p>
     * 
     * @return <code>true</code> if refining is possible, <code>false</code> otherwise
     */
    public static boolean isPossible()
    {
        return Page.checkExistance(getRefinementLocator());
    }

    /**
     * The chosen refine link.
     */
    private HtmlElement refineLink;

    /**
     * Refinement type (such as Color or Size)
     */
    private final String refinementType;

    public Refine()
    {
        // Determine refinement type by random.
        refinementType = getRefinementLocator().asserted("No refinement option found in page.").random().getAttribute("class");

        // Set action name according to the chosen refinement type.
        this.handleTimerName(refinementType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() throws Exception
    {
        // Search and returns a random refinement link of the given type, which is no category refinement.
        refineLink = Page.getSecondaryContentContainerLocator()
                         .byXPath("./div[@class='"
                                      + refinementType
                                      + "']/ul/li[not(contains(concat(' ',@class,' '),' unselectable ')) and not(contains(concat(' ',@class,' '),' selected '))]/a")
                         .asserted("No refinement link for " + refinementType + " found in page.").random();

        // Perform the AJAX call and update the page.
        AjaxUtils.callAndUpdate(refineLink.getAttribute("href"));
    }

    /**
     * Changes the timer name of this Action, so you can differ the refinements by the refinement type in the report.
     * e.g. RefineByColor, RefineBySize etc.
     * 
     * @param refinementType
     */
    private void handleTimerName(final String refinementType)
    {
        // Initially set name to "RefineBy<Something>".
        String name = refinementType;

        // Then cut off the unwanted class information, transform to camel
        // case and remove spaces.
        final int firstWhitespaceIdx = refinementType.indexOf(" ");
        if (firstWhitespaceIdx >= 0)
        {
            name = refinementType.substring(firstWhitespaceIdx + 1);
        }

        setTimerName("RefineBy" + RegExUtils.replaceAll(WordUtils.capitalize(name), "\\s+", "") + Context.getSite().getSuffix());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void postValidate() throws Exception
    {
        // Determine the page type.
        // By refining we might get a set of products, a single product (details
        // page) as well as a 'no results' page if no product fits the chosen
        // refinement.
        if (Page.isProductGridPage())
        {
            // DISABLED FOR NOW, because html structure is not consistent and hence this check
            // can fail here and there
            // // If it is a results grid page check that the desired refinement is
            // // selected.
            // final HtmlElement refinementCategory = HtmlPageUtils.findHtmlElementsAndPickOne(page, xpath_refinement);
            // boolean found = false;
            //
            // List<HtmlElement> elements = null;
            // final String DETAIL_PATH = "./ul/li[contains(concat(' ',@class,' '),' selected ')]/a";
            // try
            // {
            // elements = HtmlPageUtils.findHtmlElements(refinementCategory, DETAIL_PATH);
            // }
            // catch (AssertionFailedError e)
            // {
            // Assert.fail("Nothing found for " + xpath_refinement + DETAIL_PATH + " and refinement text " +
            // refineText);
            // }
            //
            // for (final HtmlElement e : elements)
            // {
            // if (e.getTextContent().trim().equals(refineText))
            // {
            // found = true;
            // break;
            // }
            // }
            // Assert.assertTrue("Selected refinement not found: " + refineText, found);
        }
        else if (Page.isNoHitsPage())
        {
            // Having no results is not an error. Just log it.
            Session.logEvent("Refine ends up in 'no results' page.", refineLink.getAttribute("href"));
        }
        else if (Page.isSingleProductDetailPage() || Page.isProductSetPage())
        {
            // From a details page it is not possible to check if the current
            // refinement is the right one.
        }
        else
        {
            // All other types indicate an error.
            Assert.fail("Unexpected page type after refinement.");
        }
    }
}
