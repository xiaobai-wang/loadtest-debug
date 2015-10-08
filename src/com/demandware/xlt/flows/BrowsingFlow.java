package com.demandware.xlt.flows;

import org.junit.Assert;

import com.demandware.xlt.actions.catalog.ProductDetailView;
import com.demandware.xlt.actions.catalog.QuickView;
//import com.demandware.xlt.actions.catalog.RefineByCategory;
import com.demandware.xlt.actions.catalog.SelectCategory;
import com.demandware.xlt.actions.catalog.SelectTopCategory;
import com.demandware.xlt.util.Context;
import com.demandware.xlt.util.FlowStoppedException;
import com.demandware.xlt.util.Page;
import com.demandware.xlt.util.SafetyBreak;
import com.xceptance.xlt.api.engine.Session;
import com.xceptance.xlt.api.util.XltRandom;

/**
 * Browse the catalog and view product details.
 * 
 * @author Xiaobai Wang
 */
public class BrowsingFlow extends AbstractFlow
{
    private final SafetyBreak browsingBreak = new SafetyBreak(10);

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() throws Throwable
    {
        try
        {
            do
            {
                // Break if we tend to browse endless.
                browsingBreak.check("Did not reach product or grid page with browsing.");

                // Perform a browsing round.
                browse();
            }
            // Check that the catalog browsing ends in a single or multiple results.
            while (!Page.isGridOrProductPage());

            // Process the results (refine, sort, page, change page size).
            /*
             * if (Page.isProductGridPage()) { new RefineSortingAndPagingFlow().run(); }
             */

            // View the product's details if this option is available.
            if (Page.isProductGridPage())
            {
                // Decide to open details as quick view or separate details page.
                if (XltRandom.nextBoolean(Context.getConfiguration().getQuickViewProbability()))
                {
                    // Show details as quick view.
                    new QuickView().run();
                }
                else
                {
                    // Show details on separate page.
                    new ProductDetailView().run();
                }
            }

            // Check if the product(s) available
            if (Page.isSingleProductDetailPage())
            {
                Page.isProductOnSinglePageAvailable();
            }
            else if (Page.isProductSetPage())
            {
                Page.isProductAvailable();
            }
            else if (Page.isNoHitsPage())
            {
                // We got no product hits and did not open the product details. So there's nothing to do.
            }
            else
            {
                // We already logged an event if we've refined to a page without any hits.
                Assert.fail("Browsing flow ended on unknown page.");
            }
        }
        catch (FlowStoppedException e)
        {
            // Log the failed attempt of the browsing round.
            Session.logEvent("BrowsingFlowStopped - " + Session.getCurrent().getUserName(), e.getMessage());
        }
    }

    /**
     * Browse the top and sub categories. Refine by further category if necessary.
     * 
     * @throws Throwable
     */
    private void browse() throws Throwable
    {
        // Click top or sub category from the top menu.
        if (XltRandom.nextBoolean(Context.getConfiguration().getTopCategoryBrowsingProbability()))
        {
            new SelectTopCategory().run();
        }
        else
        {
            new SelectCategory().run();
        }

        // While the resulting page is not a product grid or a detail page refine to get there. Even if the the current
        // page is a product grid page the category might be refined to a certain configured probability.
        // boolean refinementDesired =
        // XltRandom.nextBoolean(Context.getConfiguration().getCategoryRefinementProbability());
        // Define a safety break to not browse endlessly.
        // final SafetyBreak categoryRefinementBreak = new SafetyBreak(4);
        // while (refinementDesired && Page.isGridOrProductPage())
        // {
            // Check the limit of attempts.
        // if (categoryRefinementBreak.reached())
        // {
        // break;
        // }

            // If a grid or product page is reached, don't request further refining.
        // refinementDesired = false;

            // Refine by category.
        // if (RefineByCategory.isPossible())
        // {
        // new RefineByCategory().run();
        // }
        // else
        // {
                // Log for development and debugging. If there are to much logging messages, the XPath or the catalog
                // may have issues.
        // Context.logForDebug("No category refinement possible.");
                // Cancel if there's no category to refine by.
        // break;
        // }
        // }
    } // browse()
}
