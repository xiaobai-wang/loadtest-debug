package com.demandware.xlt.flows;

import org.junit.Assert;

import com.demandware.xlt.actions.catalog.ChangeItemsPerPage;
import com.demandware.xlt.actions.catalog.InfiniteScroll;
import com.demandware.xlt.actions.catalog.Paging;
import com.demandware.xlt.actions.catalog.Refine;
import com.demandware.xlt.actions.catalog.SortBy;
import com.demandware.xlt.util.Context;
import com.demandware.xlt.util.Page;
import com.xceptance.xlt.api.util.XltRandom;

/**
 * Page, sort, change items per page and refine results on the product grid page.
 * 
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 */
public class RefineSortingAndPagingFlow extends AbstractFlow
{
    /**
     * {@inheritDoc}
     */
    @Override
    public void run() throws Throwable
    {
        // This flow have to start on a product grid page, so check if we are there.
        Assert.assertTrue("RefineSortingAndPagingFlow did not start on product grid page.", Page.isProductGridPage());

        performPaging();

        performSort();

        performChangeItemsPerPage();

        performRefinement();
    }

    /**
     * According to the configured probability perform the paging or not.
     */
    private void performPaging() throws Throwable
    {
        if (XltRandom.nextBoolean(Context.getConfiguration().getPagingProbability()))
        {
            // Get current number of paging rounds determined from the configured
            // min and max value for paging.
            final int nbPagings = Context.getConfiguration().getRandomPagingCount();
            for (int i = 0; i < nbPagings; i++)
            {
                if (Context.getConfiguration().useInfiniteScroll() && InfiniteScroll.isPossible())
                {
                    new InfiniteScroll().run();
                }
                else if (Context.getConfiguration().usePaging() && Paging.isPossible())
                {
                    new Paging().run();
                }
                else
                {
                    // If there's no paging available, write out an debug log. If there are to much logging message, the
                    // XPath or the catalog may have issues
                    Context.logForDebug("No paging/inifinte scroll available.");

                    // and skip this step.
                    break;
                }
            }
        }
    }

    /**
     * Processes sorting with the configured probability.
     */
    private void performSort() throws Throwable
    {
        if (XltRandom.nextBoolean(Context.getConfiguration().getSortingProbability()))
        {
            if (SortBy.isPossible())
            {
                new SortBy().run();
            }
            else
            {
                // If there's no sorting option available, write out an debug log. If there are to much logging message,
                // the XPath or the catalog may have issues
                Context.logForDebug("No sorting available.");
            }
        }
    }

    /**
     * Changing the items per page is processed with the configured probability.
     */
    private void performChangeItemsPerPage() throws Throwable
    {
        if (XltRandom.nextBoolean(Context.getConfiguration().getItemsPerPageProbability()))
        {
            if (ChangeItemsPerPage.isPossible())
            {
                new ChangeItemsPerPage().run();
            }
            else
            {
                // If there's no possibility to change the number of items per page, write out an debug log. If there
                // are to much logging message, the XPath or the catalog may have issues
                Context.logForDebug("Cannot change items per Page.");
            }
        }
    }

    /**
     * The configured probability will decide whether to run the refining action or not.
     */
    private void performRefinement() throws Throwable
    {
        if (XltRandom.nextBoolean(Context.getConfiguration().getRefineProbability()))
        {
            // Get the number of refining rounds according to the configured min and
            // max values.
            final int nbRefines = Context.getConfiguration().getRandomRefinementCount();
            for (int i = 0; i < nbRefines; i++)
            {
                if (Refine.isPossible())
                {
                    new Refine().run();
                }
                else
                {
                    // If there's no possibility to refine, write out an debug log. If there are to much logging
                    // message, the XPath or the catalog may have issues
                    Context.logForDebug("No Refine available.");
                    // and skip this step.
                    break;
                }
            }
        }
    }
}
