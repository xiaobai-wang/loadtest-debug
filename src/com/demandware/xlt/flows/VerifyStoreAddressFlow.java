package com.demandware.xlt.flows;

import java.util.List;

import com.demandware.xlt.actions.storeLocator.FindStoreByCountry;
import com.demandware.xlt.actions.storeLocator.GoToStoreLocator;
import com.demandware.xlt.actions.storeLocator.VerifyStoreAddress;
import com.demandware.xlt.util.Context;
import com.demandware.xlt.util.FlowStoppedException;
import com.xceptance.xlt.api.util.XltRandom;

/**
 * Get a list of stores for a known location. One address is picked randomly and compared to the address provided by the
 * detail-view-dialog (which should be the same).
 * 
 * @author Daniel Kirst (Xceptance Software Technologies GmbH)
 */
public class VerifyStoreAddressFlow extends AbstractFlow
{
    /**
     * {@inheritDoc}
     */
    @Override
    public void run() throws Throwable
    {
        // Go to store locator.
        new GoToStoreLocator().run();

        // Get a list of country codes which will yield a positive search result.
        final List<String> countryCodeList = Context.getConfiguration().getAvailableCountryCodesForStores();

        if (countryCodeList.isEmpty())
        {
            // No country code is provided, stop here.
            throw new FlowStoppedException("No country code list provided");
        }

        // Pick up a random country code and perform the search.
        new FindStoreByCountry(countryCodeList.get(XltRandom.nextInt(countryCodeList.size()))).run();

        // Compare the addresses.
        new VerifyStoreAddress().run();
    }
}
