package com.demandware.xlt.flows;

import java.util.List;

import com.demandware.xlt.actions.storeLocator.FindStoreByZipCode;
import com.demandware.xlt.actions.storeLocator.GoToStoreLocator;
import com.demandware.xlt.util.Context;
import com.demandware.xlt.util.FlowStoppedException;
import com.xceptance.xlt.api.util.XltRandom;

/**
 * Search stores near a randomly chosen zipcode.
 * 
 * @author Daniel Kirst (Xceptance Software Technologies GmbH)
 */
public class FindStoreByZipCodeFlow extends AbstractFlow
{
    /**
     * {@inheritDoc}
     */
    @Override
    public void run() throws Throwable
    {
        // The ZIP code search is limited to a certain radius. All radii need to be defined in the property file.
        final List<String> radii = Context.getConfiguration().getStoreSearchRadii();

        // Check if there's any radius available.
        if (radii.isEmpty())
        {
            throw new FlowStoppedException("Radius is missing");
        }

        // Do the desired number of searches (configured as min/max).
        final int nbSearches = Context.getConfiguration().getRandomNumberOfStoreSearches();
        for (int i = 0; i < nbSearches; i++)
        {
            // Open the store locator page.
            new GoToStoreLocator().run();

            // Choose a random radius from the list for the current search.
            final String randomRadius = radii.get(XltRandom.nextInt(radii.size()));

            // Search stores based on a random ZIP code within the given radius.
            new FindStoreByZipCode(randomRadius).run();
        }
    }
}
