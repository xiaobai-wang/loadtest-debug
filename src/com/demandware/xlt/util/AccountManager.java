package com.demandware.xlt.util;

import java.util.concurrent.ConcurrentHashMap;

import com.xceptance.xlt.api.data.DataPool;
import com.xceptance.xlt.api.util.XltProperties;

/**
 * Account manager that provides exclusive accounts.
 * 
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 */
public class AccountManager
{
    /**
     * Available accounts pool.
     */
    private final DataPool<Account> accounts;

    /**
     * The account pool is initialized with a configured size and expiration rate. In this case the account reusage
     * probability is the base to determine the expiration rate. If not set, all accounts will expire.
     */
    private AccountManager()
    {
        // Get the properties
        final XltProperties props = XltProperties.getInstance();

        // Initial data pool with configured pool size and expiration rate to control percentage of reusable accounts.
        int size = props.getProperty("account.pool.size", 200);
        int reusageProbability = XltProperties.getInstance().getProperty("account.reusageProbability", 0);
        accounts = new DataPool<Account>(size, 100 - reusageProbability);
    }

    /**
     * Get an account.
     * 
     * @return an account or <code>null</code> if no account is available.
     */
    public Account getAccount()
    {
        return accounts.getDataElement();
    }

    /**
     * Adds the given account to the pool.
     * 
     * @param account
     *            an account
     */
    public void addAccount(final Account account)
    {
        if (account != null)
        {
            accounts.add(account);
        }
    }

    /**
     * Returns the global single account manager instance. Use {@link #getInstance(String)} instead to get a
     * key-separated account manager if needed.<br>
     * This methods behaves the same as {@link #getInstance(String)} is called with argument <code>null</code>. .
     * 
     * @return global account manager instance
     */
    public static AccountManager getInstance()
    {
        return Singleton_Holder_Single.SINGLE;
    }

    /**
     * Returns the account manager mapped to the given key. Use {@link #getInstance()} instead if a single globally
     * shared account manager is all you need.
     * 
     * @param key
     *            The key the account manager is bound to. If the key is <code>null</code> the unmapped global account
     *            manager is returned.
     * @return account manager instance
     */
    public static AccountManager getInstance(final String key)
    {
        // Fall back to global account manager.
        if (key == null)
        {
            return getInstance();
        }

        // Lookup existing manager for given key or create new one if necessary.
        AccountManager manager = Singleton_Holder_Multiple.MANAGERS.get(key);
        if (manager == null)
        {
            // Map new account manager if necessary.
            final AccountManager newManager = new AccountManager();
            final AccountManager existing = Singleton_Holder_Multiple.MANAGERS.putIfAbsent(key, newManager);
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
         * Singleton instance of {@link AccountManager}.
         */
        private static final AccountManager SINGLE = new AccountManager();
    }

    /**
     * Helper class used for on-demand initialization of manager singleton.
     */
    private static class Singleton_Holder_Multiple
    {
        /**
         * Singleton instance of {@link AccountManager}.
         */
        private static final ConcurrentHashMap<String, AccountManager> MANAGERS = new ConcurrentHashMap<String, AccountManager>();
    }
}
