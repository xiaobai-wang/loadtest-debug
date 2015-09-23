package com.demandware.xlt.tests;

import com.demandware.xlt.flows.FindStoreByCountryFlow;
import com.demandware.xlt.flows.FindStoreByZipCodeFlow;
import com.demandware.xlt.flows.VerifyStoreAddressFlow;
import com.demandware.xlt.flows.VisitFlow;

/**
 * Open the landing page and perform various store searches via the store locator. The first search looks for stores in
 * a random country - results are discarded. A second search performs a zipcode lookup. The last search looks for stores
 * at a known location and verifies that the address of store returned by the result page is equal to the address
 * provided by the detail-view-dialog.
 * 
 * @author Daniel Kirst (Xceptance Software Technologies GmbH)
 */

public class TStoreLocator extends AbstractTestCase
{
    /**
     * {@inheritDoc} 
     */
    @Override
    protected void test() throws Throwable
    {
        // Visit start homepage.
        new VisitFlow().run();

        // Search for international stores.
        new FindStoreByCountryFlow().run();

        // Search stores by entering zipcodes.
        new FindStoreByZipCodeFlow().run();

        // Search a store at a known location and verifies the correctness of its address.
        new VerifyStoreAddressFlow().run();
    }
}
