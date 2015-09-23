package com.demandware.xlt.flows;

import com.demandware.xlt.actions.storeLocator.FindStoreByCountry;
import com.demandware.xlt.actions.storeLocator.GoToStoreLocator;
import com.demandware.xlt.util.Context;

/**
 * Search stores by country code.
 * 
 * @author Daniel Kirst (Xceptance Software Technologies GmbH)
 */
public class FindStoreByCountryFlow extends AbstractFlow
{
    /**
     * {@inheritDoc}
     */
    @Override
    public void run() throws Throwable
    {
        // Do the desired number of searches (configured as min/max).
        final int nbSearches = Context.getConfiguration().getRandomNumberOfStoreSearches();
        for (int i = 0; i < nbSearches; i++)
        {
            // Go to the store locator page.
            new GoToStoreLocator().run();

            // Select a region by country code and search stores near this location.
            new FindStoreByCountry().run();
        }
    }
}
