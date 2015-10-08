package com.demandware.xlt.flows;

import com.demandware.xlt.actions.Search;
import com.demandware.xlt.util.Context;
import com.demandware.xlt.util.SearchOption;
import com.xceptance.xlt.api.util.XltRandom;

/**
 * Search for a phrase, browse the results and view a result's details.
 * 
 * @author Xiaobai Wang
 */
public class SearchFlow extends AbstractFlow
{
    /**
     * {@inheritDoc}
     */
    @Override
    public void run() throws Throwable
    {
        // Get the number of searches determined from the configured min and max
        // products.
        final int searches = Context.getConfiguration().getRandomNumberOfSearchProducts();
        for (int i = 0; i < searches; i++)
        {
            // The search option is the indicator whether to search for one of
            // the search phrases from the 'HITS_PROVIDER' that results in a hit
            // or a generated phrase that results in a 'no results' page.
            final SearchOption option = getSearchOption(Context.getConfiguration().getSearchNoHitsProbability());

            // Run the search with an appropriate search phrase according to the
            // search option.
            new Search(Context.getSearchPhrase(option), option).run();
            // new Search("black", SearchOption.HITS).run();

            // If possible process the results (sort, paging, change items per page, refine).
            // if (Page.isProductGridPage())
            // {
            // new RefineSortingAndPagingFlow().run();
            // }

            // Check if we are still on a product grid page and view a random product either by QuickView or Product
            // Details Page.
            /*
             * if (Page.isProductGridPage()) //{ if
             * (XltRandom.nextBoolean(Context.getConfiguration().getQuickViewProbability())) { new QuickView().run(); }
             * else { new ProductDetailView().run(); } }
             */

            // Configure current product(s).
            /*
             * if (Page.isSingleProductDetailPage()) { new ConfigureProductFlow().run(); } else if
             * (Page.isProductSetPage()) { new ConfigureProductSetFlow().run(); } else if (Page.isNoHitsPage()) { //
             * There's no product hit. We tolerate this so skip silently. } else { // We already logged an event in the
             * Refine.java if we refined to a page without any hits.
             * Assert.fail("Browsing flow ended on unknown page."); }
             */
        }
    }

    /**
     * Returns a search option using the given probability.
     * 
     * @param searchNoHitsProbability
     *            probability to grab the {@link SearchOption#NO_HITS} search option
     * @return search option
     */
    private SearchOption getSearchOption(final int searchNoHitsProbability)
    {
        if (XltRandom.nextBoolean(searchNoHitsProbability))
        {
            return SearchOption.NO_HITS;
        }
        else
        {
            return SearchOption.HITS;
        }
    }
}
