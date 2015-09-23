package com.demandware.xlt.util;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections.MapUtils;
import org.eclipse.jetty.util.ConcurrentHashSet;
import org.junit.Assert;

import com.demandware.xlt.actions.AbstractHtmlPageAction;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.xceptance.xlt.api.data.DataProvider;
import com.xceptance.xlt.api.engine.Session;
import com.xceptance.xlt.api.util.XltLogger;
import com.xceptance.xlt.api.util.elementLookup.Results;
import com.xceptance.xlt.common.XltConstants;

/**
 * Central component for configuration, current page, last action and all test related data.
 * 
 * @author Bernd Weigel (Xceptance Software Technologies GmbH)
 */
public class Context
{
    /**
     * Data provider for search results.
     */
    private static final Map<Site, DataProvider> HITS_PROVIDERS = initHitProviders();

    /**
     * Known test contexts.
     */
    private static final Map<ThreadGroup, Context> CONTEXTS = new ConcurrentHashMap<ThreadGroup, Context>();

    /**
     * The Configuration for the current thread, wrapped and buffered from the properties.
     */
    private final Configuration configuration;

    /**
     * The last executed action.
     */
    private com.xceptance.xlt.api.actions.AbstractHtmlPageAction previousAction;

    /**
     * The action which is currently executed.
     */
    private com.xceptance.xlt.api.actions.AbstractHtmlPageAction currentAction;

    /**
     * The account used in this TestCase.
     */
    private Account account;

    /** String that represents the JavaScript snippet that contains the app.URLs */
    private String appResources;

    /**
     * Hash code of page where the current {@link #appResources} were looked up from. This is used to determine if
     * {@link #appResources} needs to be updated.
     */
    private int appResourcesPageHashCode = 0;

    /** Only for debugging: Map to remember debug Messages. */
    private Map<String, Map<String, Integer>> debugMessagesMap;

    /** Only for debugging: Map to remember xpath which hadn't had a hit jet. */
    private Map<String, Integer> invalidXPathSubjectMonitor;

    /** Only for debugging: Set to remember all xpath which had at least one hit. */
    private Set<String> validXPathSet;

    /** String to remember all no-hit search params for test rerun. */
    private String noHitsSearchParams;

    /** Is test case expected to run with a customer that needs an existing account? */
    private boolean isRegisteredTestcase;

    /** PayPal account used for checkout. */
    private PaypalAccount paypalAccount;

    /** Test case site context. */
    private final Site site;

    /**
     * Constructor; Creates a new Context for a TestCase.
     * 
     * @param testName
     *            test name
     * @param site
     *            site context
     */
    private Context(final String testName, final Site site)
    {
        this.site = site;
        this.configuration = new Configuration(testName, site);
    }

    /**
     * Gets the current configuration for this TestCase.
     * 
     * @return the current configuration for this TestCase
     */
    public static Configuration getConfiguration()
    {
        return getCurrentContext().configuration;
    }

    /**
     * Retrieves the context instance for the current Thread.
     * 
     * @return the context instance for the current Thread
     */
    private static Context getCurrentContext()
    {
        Context context = CONTEXTS.get(Thread.currentThread().getThreadGroup());
        if (context == null)
        {
            XltLogger.runTimeLogger.error("No Context available in this Thread. Use this Method only in TestCase Instances..");
        }
        return context;
    }

    /**
     * Adds a new Context instance for the current Thread to the map. This Method is used by the AbstractTestCase and
     * therefore won't need to be called manually
     * 
     * @param testName
     *            the name of the test case for property identification
     * @param site
     *            site context
     */
    public static void addContext(final String testName, final Site site)
    {
        // NOTE: previous added Context instances for this Thread will be ignored
        CONTEXTS.put(Thread.currentThread().getThreadGroup(), new Context(testName, site));
    }

    /**
     * Gets the action which is currently executed.
     * 
     * @return the current action
     */
    public static com.xceptance.xlt.api.actions.AbstractHtmlPageAction getCurrentAction()
    {
        return getCurrentContext().currentAction;
    }

    /**
     * Sets the action which is currently executed. Just a wrapper method for the static call.
     * 
     * @param action
     *            the current action
     */
    private void setCurrentActionInternal(final AbstractHtmlPageAction action)
    {
        this.previousAction = this.currentAction;
        this.currentAction = action;

        if (this.currentAction.getHtmlPage() == null && this.previousAction != null)
        {
            this.currentAction.setHtmlPage(this.previousAction.getHtmlPage());
        }
    }

    /**
     * Gets the action which was executed before the current one.
     * 
     * @return the previous action if any or <code>null</code>
     */
    public static com.xceptance.xlt.api.actions.AbstractHtmlPageAction getPreviousAction()
    {
        return getCurrentContext().previousAction;
    }

    /**
     * Sets the action which was executed last.
     * 
     * @param action
     *            the current action
     */
    public static void setCurrentAction(final AbstractHtmlPageAction action)
    {
        getCurrentContext().setCurrentActionInternal(action);
    }

    /**
     * Gets the current page.
     * 
     * @return the current page
     */
    public static HtmlPage getPage()
    {
        return getCurrentAction().getHtmlPage();
    }

    /**
     * Overrides the current page if it was loaded or changed externally.
     * 
     * @param page
     *            the current page object
     */
    public static void setCurrentPage(final HtmlPage page)
    {
        getCurrentAction().setHtmlPage(page);
    }

    /**
     * Get the customer account.
     * 
     * @return customer account
     */
    public static Account getAccount()
    {
        // Use the current account if existing.
        Account account = getCurrentContext().account;
        if (account == null)
        {
            // No account was assigned to that test case.
            if (isRegisteredTestcase())
            {
                // For registered customer scenarios try to reuse an existing previously registered account.
                account = AccountManager.getInstance(getAccountPoolKey()).getAccount();
            }

            // Either it's a guest account or no pool account was available. In any case we have to generate a new
            // unregistered account.
            if (account == null)
            {
                account = new Account();
            }

            // Publish the new account.
            getCurrentContext().account = account;
        }

        return account;
    }

    /**
     * Gets an account pool key based on start URL. This way it's possible to test across multiple sites that do not
     * share a common account pool. Use this key to query an account from the pool.
     * 
     * @return account pool key
     */
    private static String getAccountPoolKey()
    {
        // By default there's no key so we could stay with NULL.
        String accountPoolKey = null;

        // If separated account pools should be used extract the key from start URL.
        if (getConfiguration().isAccountPoolSiteSeparated())
        {
            try
            {
                final URL url = new URL(getConfiguration().getStartURL());
                accountPoolKey = url.getHost() + url.getPath();
            }
            catch (final Exception e)
            {
                throw new RuntimeException("Unable to extract account pool key from URL: " + getConfiguration().getStartURL(), e);
            }
        }

        return accountPoolKey;
    }

    /**
     * Releases the used account and put it back to the pool for reusage if possible.
     */
    public static void releaseAccount()
    {
        // Get the current account.
        final Account account = getCurrentContext().account;

        // Release the user's exclusively used account if it is a registered account. Guest user account will be
        // dropped.
        if (account != null && account.isRegistered())
        {
            AccountManager.getInstance(getAccountPoolKey()).addAccount(account);
        }
    }

    /**
     * Release currently assigned PayPal account.
     */
    public static void releasePaypalAccount()
    {
        final PaypalAccount ppAccount = getCurrentContext().paypalAccount;
        if (ppAccount != null)
        {
            // Release the user's exclusively used PayPal account.
            PaypalAccountManager.getInstance().addAccount(ppAccount);
            getCurrentContext().paypalAccount = null;
        }
    }

    /**
     * Releases the context for the current thread.
     */
    public static void releaseContext()
    {
        try
        {
            getCurrentContext().handlePreRelease();
        }
        finally
        {
            CONTEXTS.remove(Thread.currentThread().getThreadGroup());
        }
    }

    /**
     * Initializes a new {@link Account} object for this test case.
     * 
     * @see #setAccount(Account)
     */
    public static void initNewAccount()
    {
        setAccount(null);
    }

    /**
     * Sets the account to use for this test.
     * 
     * @param account
     *            the account to use. If set to <code>null</code> a newly generated account will assigned.
     */
    public static void setAccount(Account account)
    {
        // Create a new account if necessary.
        if (account == null)
        {
            account = new Account();
        }

        // Assign the account.
        getCurrentContext().account = account;
    }

    /**
     * Get a CreditCard object. Credit cards are usually configured via properties file.
     * 
     * @return a credit card object
     */
    public static CreditCard getCreditCard()
    {
        return CreditCardProvider.getInstance().getCreditCard();
    }

    /**
     * Indicates if the current test runs in load test mode or is just a development/debug run.
     * 
     * @return <code>true</code> if the test is run in load test mode, <code>false</code> otherwise
     */
    public static boolean isLoadTest()
    {
        return Session.getCurrent().isLoadTest();
    }

    /**
     * Adds a log message, which will be accounted for the current page and displayed at the end of the test case.
     * </br><b>Note: This is only usable in non-load-test mode.</b>
     * 
     * @param message
     */
    public static void logForDebug(final String message)
    {
        getCurrentContext().addDebugLog(message);
    }

    /**
     * Adds an XPath execution to the internal XPath monitor, which will display an XPath which never had a hit at the
     * end of the test on console.</br><b>Note: This is only usable in non-load-test mode.</b>
     * 
     * @param xPath
     *            the XPath to monitor
     * @param hit
     *            indicates if the XPath had at least one hit on the page
     */
    static void addXPathToNoHitCheck(String xPath, final boolean hit)
    {
        // Do only in load test mode.
        if (!isLoadTest())
        {
            String additionalMessage = null;

            // Try to get the name of the method to insert an additional message
            final StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
            for (int i = 0; i < stackTraceElements.length; i++)
            {
                if (stackTraceElements[i].getClassName().equals(Page.class.getName()))
                {
                    // This will filter out the Page.checkExistance*() methods and will give us the real method
                    // which calls it.
                    final String className = stackTraceElements[i + 1].getClassName();
                    additionalMessage = className.substring(className.lastIndexOf('.') + 1) + "."
                                        + stackTraceElements[i + 1].getMethodName();

                    // We have found what we were looking for so no further parsing is necessary.
                    break;
                }
            }

            // Quote XPath and add with possible message to monitor.
            xPath = "\"" + xPath + "\"";
            getCurrentContext().monitorXPath(additionalMessage == null ? xPath : (additionalMessage + ": " + xPath), hit);
        }
    }

    /**
     * Adds an XPath execution to the internal XPath monitor, which will display an XPath which never had a
     * hit.</br><b>Note: this is only usable in non load test runs.</b>
     * 
     * @param xPath
     *            the XPath to monitor
     * @param hit
     *            indicates if the XPath had a hit on the page
     */
    private void monitorXPath(final String xPath, final boolean hit)
    {
        // Initialize XPath monitoring maps.
        initXPathMonitoring();

        // If the XPath had (ever) a hit mark it as 'valid' otherwise mark it as 'invalid' and increase the
        // invalid-counter.
        if (hit)
        {
            validXPathSet.add(xPath);
        }
        else if (!validXPathSet.contains(xPath))
        {
            Integer value = invalidXPathSubjectMonitor.get(xPath);
            if (value == null)
            {
                value = 0;
            }
            invalidXPathSubjectMonitor.put(xPath, value + 1);
        }
    }

    /**
     * Initializes the HashMaps which check if there are XPaths which never hit.
     */
    private synchronized void initXPathMonitoring()
    {
        if (validXPathSet == null)
        {
            validXPathSet = new ConcurrentHashSet<String>();
        }

        if (invalidXPathSubjectMonitor == null)
        {
            invalidXPathSubjectMonitor = new ConcurrentHashMap<String, Integer>();
        }
    }

    /**
     * Adds a log message which will be accounted for the current page and displayed at the end of the test case. This
     * method is wrapped by the static method {@link #logForDebug}. </br><b>Note: This is only usable in non-load-test
     * mode.</b>
     * 
     * @param message
     *            log message (must not be <code>null</code>)
     */
    private void addDebugLog(final String message)
    {
        // Do for non-load-test mode only.
        if (!isLoadTest())
        {
            // Create messages map if necessary.
            if (this.debugMessagesMap == null)
            {
                synchronized (this)
                {
                    if (this.debugMessagesMap == null)
                    {
                        this.debugMessagesMap = new ConcurrentHashMap<String, Map<String, Integer>>();
                    }
                }
            }

            // Get URL string.
            final String urlKey = this.currentAction.getHtmlPage().getUrl().toString();

            // Get URL-counter map for current message. Create new map if necessary.
            Map<String, Integer> set = this.debugMessagesMap.get(message);
            if (set == null)
            {
                set = new ConcurrentHashMap<String, Integer>();
                set.put(this.currentAction.getHtmlPage().getUrl().toString(), 0);
            }

            // Increase counter.
            final Integer count = set.get(urlKey);
            set.put(urlKey, count == null ? 1 : count + 1);

            // Publish URL-counter.
            this.debugMessagesMap.put(message, set);
        }
    }

    /**
     * Executed before the release of the context. Includes handling for additional debug logging.
     */
    private void handlePreRelease()
    {
        // Do for non-load-test mode only.
        if (!isLoadTest())
        {
            // Introduce debug summary.
            final StringBuilder out = new StringBuilder("\n\n");
            out.append("###############\n");
            out.append("#Debug summary#\n");
            out.append("###############\n");

            // Debug messages.
            if (MapUtils.isNotEmpty(debugMessagesMap))
            {
                // Introduction.
                out.append("There were debug messages. These are no errors, but could could give a hint on catalog and/or XPath issues. Please review them.\nBeware that Ajax calls, like quickview are not displayed in the URLs.\n");

                // Log debug messages and corresponding pages.
                for (final String message : this.debugMessagesMap.keySet())
                {
                    // Print message.
                    out.append("\t - ").append(message).append(" URLs:\n");

                    // Print corresponding pages.
                    final Map<String, Integer> messageMap = this.debugMessagesMap.get(message);
                    for (final String url : messageMap.keySet())
                    {
                        out.append("\t\t- ").append(url).append(" (message was catched ").append(messageMap.get(url))
                           .append(" times)\n");
                    }
                }
            }

            // XPath(s) that did not have a hit at all.
            if (invalidXPathSubjectsAvailable())
            {
                // Introduction
                out.append("Some XPaths never had a hit in this test. These are no errors, but could give an hint on XPath issues, please review them.\n");

                // Print XPaths without hit.
                for (final String xPath : this.invalidXPathSubjectMonitor.keySet())
                {
                    if (!validXPathSet.contains(xPath))
                    {
                        out.append("\t - ").append(xPath).append(" had ").append(this.invalidXPathSubjectMonitor.get(xPath))
                           .append(" tries, but never had a hit.\n");
                    }
                }
            }

            // Test rerun information.
            out.append("\n\nIf you want to rerun this testcase, insert the following lines into your config/dev.properties file: \n\n");
            // Account information
            if (account != null)
            {
                out.append("account.email").append(" = ").append(account.getEmail()).append("\n");
                out.append("account.password").append(" = ").append(account.getPassword()).append("\n");
                out.append("account.isRegistered").append(" = ").append(account.isRegistered()).append("\n");
            }

            // No-Hits search phrases.
            if (this.noHitsSearchParams != null)
            {
                out.append("search.noHitsValues").append(" = ").append(this.noHitsSearchParams).append("\n");
            }

            // Randomizer initialization value.
            out.append(XltConstants.RANDOM_INIT_VALUE_PROPERTY).append(" = ")
               .append(getConfiguration().getProperty(XltConstants.RANDOM_INIT_VALUE_PROPERTY)).append("\n\n");

            // Print the collected information.
            XltLogger.runTimeLogger.debug(out.toString());
        }
    }

    /**
     * Checks if there were XPaths which did never had a hit.
     * 
     * @return <code>true</code> if at least one monitored XPath had no hit at all, <code>false</code> otherwise
     */
    private boolean invalidXPathSubjectsAvailable()
    {
        return MapUtils.isNotEmpty(invalidXPathSubjectMonitor) && !validXPathSet.containsAll(invalidXPathSubjectMonitor.keySet());
    }

    /**
     * Get the text representation for the given key from the properties file in the configured language.
     * 
     * @param propertyKey
     *            plain property key
     * @return the text representation for the given key in the configured language
     */
    public static String getLanguageSpecificText(final String propertyKey)
    {
        return getConfiguration().getLanguageProperty(propertyKey);
    }

    /**
     * Returns a search phrase corresponding to the given option.
     * 
     * @return search phrase or <code>null</code> if search option is unknown
     */
    public static String getSearchPhrase(final SearchOption option)
    {
        switch (option)
        {
            case HITS:
                // Return one of the predefined search phrases.
                return HITS_PROVIDERS.get(getSite()).getRandomRow(false);

            case NO_HITS:
                // Return a random alphanumeric string, make it random and long enough.
                String noHitSearchParam = getConfiguration().getNoHitSearchParam();
                getCurrentContext().saveNoHitsSearchPhrase(noHitSearchParam);
                return noHitSearchParam;

            default:
                return null;
        }
    }

    /**
     * Remembers all randomly generated no-hit search phrases for the debug log at the end of the test case
     * 
     * @param noHitSearchParam
     *            search parameters that do not have hits
     */
    private void saveNoHitsSearchPhrase(final String noHitSearchParam)
    {
        // Do for non-load-test mode only.
        if (!isLoadTest())
        {
            if (this.noHitsSearchParams == null)
            {
                // Initial search phrase
                this.noHitsSearchParams = noHitSearchParam;
            }
            else
            {
                // Append search phrase to existing one(s)
                this.noHitsSearchParams += ";" + noHitSearchParam;
            }
        }
    }

    /**
     * Add an ID that was used for element lookup but did not had a hit.
     * 
     * @param id
     *            element ID
     * @param hit
     *            did element lookup with given ID hat a hit?
     */
    static void addIdToNoHitCheck(final String id, final boolean hit)
    {
        // Do for non-load-test mode only.
        if (!isLoadTest())
        {
            // Convert to XPath id-funktion before logging it.
            addXPathToNoHitCheck("id('" + id + "')", hit);
        }
    }

    /**
     * Add an element selector that was used for element lookup but did not had a hit.
     * 
     * @param target
     *            element selector
     * @param hit
     *            did element lookup with given selector hat a hit?
     */
    static void addNoHitCheck(final Results target, final boolean hit)
    {
        // Do for non-load-test mode only.
        if (!isLoadTest())
        {
            addXPathToNoHitCheck(target.getLocatorDescription(), hit);
        }
    }

    /**
     * Initialize a registered customer test case.
     */
    public static void initRegisteredTestcase()
    {
        Context.getCurrentContext().isRegisteredTestcase = true;
    }

    /**
     * Runs the current test case a registered customer scenario?
     * 
     * @return <code>true</code> if the current test case a registered customer scenario, <code>false</code> otherwise
     */
    private static boolean isRegisteredTestcase()
    {
        return Context.getCurrentContext().isRegisteredTestcase;
    }

    /**
     * Get assigned PayPal account or get one from pool if no PayPal account is assigned yet.
     * 
     * @return PayPal account or <code>null</code> if no PayPal account is available
     */
    public static PaypalAccount getPaypalAccount()
    {
        final Context c = Context.getCurrentContext();
        if (c.paypalAccount == null)
        {
            c.paypalAccount = PaypalAccountManager.getInstance().getAccount();
        }
        return c.paypalAccount;
    }

    /**
     * Get the site context.
     * 
     * @return the site context
     */
    public static Site getSite()
    {
        return Context.getCurrentContext().site;
    }

    /**
     * Get app resources script content of current page.
     * 
     * @return app resources script content
     */
    public static String getAppResources()
    {
        final Context cc = getCurrentContext();

        // Check if lookup from current page is desired.
        if (!Context.getConfiguration().loadAppResources())
        {
            // Check that we have a page to lookup from.
            final HtmlPage currentPage = cc.currentAction.getHtmlPage();
            if (currentPage != null)
            {
                // If we do not have any appResources yet it's worth to update.
                // We also do an update if the page itself has changed.
                final int currentPageHash = currentPage.hashCode();
                if (cc.appResources == null || currentPageHash != cc.appResourcesPageHashCode)
                {
                    // Lookup appResources from current page and remember the page's hash value.
                    cc.appResources = Page.getAppResources();
                    cc.appResourcesPageHashCode = currentPageHash;
                }
            }
            else
            {
                Assert.fail("No page to lookup AppResources from.");
            }
        }

        return cc.appResources;
    }

    /**
     * Initialize search phrases providers.
     * 
     * @return map containing the site and corresponding search phrase provider
     */
    private static Map<Site, DataProvider> initHitProviders()
    {
        final Map<Site, DataProvider> hitsProviders = new HashMap<Site, DataProvider>();

        // Initialize default first to have a fallback provider.
        hitsProviders.put(Site.DEFAULT, initDataProvider(Site.DEFAULT));

        // Initialize all the other providers.
        for (final Site site : Site.values())
        {
            // Of course skip 'default' because it's already processed.
            if (!Site.DEFAULT.equals(site))
            {
                // Check if a search phrases file is available for current site
                DataProvider hitsProvider = initDataProvider(site);
                if (hitsProvider == null)
                {
                    // If not, fallback to default search file.
                    hitsProvider = hitsProviders.get(Site.DEFAULT);
                }
                // Assign provider with site.
                hitsProviders.put(site, hitsProvider);
            }
        }

        return hitsProviders;
    }

    /**
     * Initializes the DataProvier for search terms, depending on the current site context.
     * 
     * @param site
     *            site context
     * @return search term provider for given site context or <code>null</code> if expected search phrases file was not
     *         found.
     */
    private static DataProvider initDataProvider(final Site site)
    {
        DataProvider hitsProvider = null;

        // Build file name of expected site specific search phrases file.
        final String searchPhraseFileName = "search-phrases" + site.getSuffix() + ".txt";

        try
        {
            // Initialize the search provider with the site specific search phrases file.
            hitsProvider = DataProvider.getInstance(searchPhraseFileName);
        }
        catch (IOException ioe)
        {
            Session.logEvent("Search phrases file", "missing: " + searchPhraseFileName);
        }

        return hitsProvider;
    }

    /**
     * Load the app resources if necessary.</br> NOTE: That might be necessary for any SG 14.x and lower but should be
     * avoided for SG 15.x and higher. However we keep that possibility for compatibility reasons.
     * 
     * @throws Exception
     */
    public static void loadAppResources() throws Exception
    {
        Context currentContext = getCurrentContext();
        if (currentContext.configuration.loadAppResources())
        {
            currentContext.appResources = AjaxUtils.loadAppResources();
        }
    }
}
