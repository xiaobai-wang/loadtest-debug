package com.demandware.xlt.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.Assert;

import com.xceptance.xlt.api.data.ExclusiveDataProvider;

/**
 * PayPal account manager. Use this class to get exclusive access to a PayPal account. Don't forget to put it back after
 * use.
 * 
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 */
public class PaypalAccountManager
{
    /**
     * Parser used for extracting PayPal accounts from CSV strings.
     */
    static class PaypalAccountParser extends ExclusiveDataProvider.Parser<PaypalAccount>
    {
        /**
         * Create a PayPal account from each passed string.
         */
        @Override
        public List<PaypalAccount> parse(final List<String> data)
        {
            // Initialize list that holds all parsed PayPal accounts.
            final List<PaypalAccount> accounts = new ArrayList<PaypalAccount>();

            // Parse all given strings.
            for (final String row : data)
            {
                // Convert CSV string to PayPal account.
                accounts.add(PaypalAccount.fromCsv(row));
            }

            return accounts;
        }
    }

    /** Instance of PayPal parser */
    static final PaypalAccountParser ppParser = new PaypalAccountParser();

    /**
     * Returns an account.
     * 
     * @return account an account or <code>null</code> if the accounts file is not available or cannot be parsed.
     */
    public PaypalAccount getAccount()
    {
        try
        {
            // Get a random account for exclusive use from the given account file parsed with the passed parser.
            return ExclusiveDataProvider.getInstance("paypal.csv", ppParser).getRandom();
        }
        catch (final IOException e)
        {
            // File does not exist, is not accessible, or cannot be parsed
            e.printStackTrace();
            Assert.fail("Getting PayPal account failed");
        }

        return null;
    }

    /**
     * Adds the given account.
     * 
     * @param account
     *            an account
     */
    public void addAccount(final PaypalAccount account)
    {
        if (account != null)
        {
            try
            {
                ExclusiveDataProvider.getInstance("paypal.csv", ppParser).add(account);
            }
            catch (final IOException e)
            {
                // Should not happen since we've read the file before. Now the data are in memory and there will be no
                // file access anymore.
                e.printStackTrace();
                Assert.fail("Releasing PayPal account failed");
            }
        }
    }

    /**
     * Returns the globally single account manager instance. Use {@link #getInstance(String)} instead to get a key
     * separated account manager if needed.
     * 
     * @return account manager instance
     */
    public static PaypalAccountManager getInstance()
    {
        return Singleton_Holder_Single.SINGLE;
    }

    /**
     * Returns the account manager mapped to the given key. Use {@link #getInstance()} instead if a single globally
     * shared account manager is all you need. This alternative is used implicitly if the given key is <code>null</code>
     * .
     * 
     * @param key
     *            The key the account manager is bound to. If the key is <code>null</code> the unmapped account manager
     *            is returned.
     * @return account manager instance
     */
    public static PaypalAccountManager getInstance(final String key)
    {
        if (key == null)
        {
            // Fall back to the singleton instance.
            return getInstance();
        }

        // Check if an account manager is still known for that key.
        PaypalAccountManager manager = Singleton_Holder_Multiple.MANAGERS.get(key);
        if (manager == null)
        {
            // If it's not map a new account manager.
            final PaypalAccountManager newManager = new PaypalAccountManager();
            final PaypalAccountManager existing = Singleton_Holder_Multiple.MANAGERS.putIfAbsent(key, newManager);
            manager = existing == null ? newManager : existing;
        }

        return manager;
    }

    /**
     * Helper class used for on-demand initialization of manager singleton.
     */
    private static class Singleton_Holder_Single
    {
        /**
         * Singleton instance of {@link PaypalAccountManager}.
         */
        private static final PaypalAccountManager SINGLE = new PaypalAccountManager();
    }

    /**
     * Helper class used for on-demand initialization of manager singleton.
     */
    private static class Singleton_Holder_Multiple
    {
        /**
         * Singleton instance of {@link PaypalAccountManager}.
         */
        private static final ConcurrentHashMap<String, PaypalAccountManager> MANAGERS = new ConcurrentHashMap<String, PaypalAccountManager>();
    }
}
