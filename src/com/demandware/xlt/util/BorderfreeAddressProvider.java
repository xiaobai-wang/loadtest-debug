package com.demandware.xlt.util;

import java.util.ArrayList;
import java.util.List;

import com.xceptance.xlt.api.data.DataProvider;
import com.xceptance.xlt.api.engine.Session;
import com.xceptance.xlt.api.util.XltRandom;

/**
 * Account manager for Borderfree addresses.
 * 
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 */
public class BorderfreeAddressProvider
{
    /**
     * Available addresses.
     */
    private final List<BorderfreeAddress> addresses = new ArrayList<BorderfreeAddress>();

    /**
     * Create a {@link BorderfreeAddressProvider} instance
     */
    private BorderfreeAddressProvider()
    {
        try
        {
            // Read file into memory.
            final DataProvider d = new DataProvider("borderfree.csv");
            for (final String row : d.getAllRows())
            {
                addresses.add(BorderfreeAddress.fromCsv(row));
            }
        }
        catch (Exception e)
        {
            Session.logEvent("Could not read BorderFree addresses", e.getMessage());
        }
    }

    /**
     * Returns an random address from pool.
     * 
     * @return address or <code>null</code> if no address is available.
     */
    public BorderfreeAddress getAddress()
    {
        return addresses.get(XltRandom.nextInt(addresses.size()));
    }

    /**
     * Returns the global single address manager instance.
     * 
     * @return address manager instance
     */
    public static BorderfreeAddressProvider getInstance()
    {
        return Singleton_Holder_Single.SINGLE;
    }

    /**
     * Helper class used for on-demand initialization of manager singleton.
     */
    private static class Singleton_Holder_Single
    {
        /**
         * Singleton instance of {@link BorderfreeAddressProvider}.
         */
        private static final BorderfreeAddressProvider SINGLE = new BorderfreeAddressProvider();
    }
}
