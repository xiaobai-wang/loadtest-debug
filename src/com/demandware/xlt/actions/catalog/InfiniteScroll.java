package com.demandware.xlt.actions.catalog;

import org.junit.Assert;

import com.demandware.xlt.actions.AbstractAjaxAction;
import com.demandware.xlt.util.Page;
import com.demandware.xlt.util.XHR;
import com.demandware.xlt.validators.Validator;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.xceptance.xlt.api.util.elementLookup.Results;

/**
 * Trigger infinite scroll feature.
 * 
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 */
public class InfiniteScroll extends AbstractAjaxAction
{
    /**
     * Checks if infinite scroll is possible. </br><b>NOTE: Only works on a product grid page</b>
     * <p>
     * <b>Page:</b> use this on a product grid page.
     * </p>
     * 
     * @return <code>true</code> if paging is possible, <code>false</code> otherwise.
     */
    public static boolean isPossible()
    {
        return Page.checkExistance(getScollUrlLocator());
    }

    /**
     * Get the infinite-scroll placeholder that contains the URL to the next bunch of products.
     * 
     * @return the infinite-scroll placeholder that contains the URL to the next bunch of products.
     */
    private static Results getScollUrlLocator()
    {
        // Infinite scroll placeholder that is located after the last UL tag.
        // Last infinite scroll placeholder only will not work here.
        return getProductGridContainer().byXPath("./ul[last()]/following-sibling::div[contains(@class,'infinite-scroll-placeholder')]");
    }

    /**
     * Get the locator to select the search results.
     * 
     * @return the locator to select the search results
     */
    private static Results getProductGridContainer()
    {
        return Page.getPrimaryContentContainerLocator().byCss(".search-result-content");
    }

    /**
     * How many items does the current page contains?
     * 
     * @return number of items on the page
     */
    private static int getNrOfGridItems()
    {
        return getProductGridContainer().byCss(".product-tile").count();
    }

    private int nrOfInitialItems = 0;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() throws Exception
    {
        // remember the number of items before we scroll
        nrOfInitialItems = getNrOfGridItems();

        // load further items and parse the response into the page
        final String url = getScollUrlLocator().asserted().single().getAttribute("data-grid-url");
        final HtmlElement productGridContainer = getProductGridContainer().asserted("Product grid container not found.").single();

        new XHR().url(url)
                 .appendTo(productGridContainer)
                 .fire();
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

        // number of products should have been increased
        Assert.assertTrue("No further items loaded", nrOfInitialItems < getNrOfGridItems());
    }
}
