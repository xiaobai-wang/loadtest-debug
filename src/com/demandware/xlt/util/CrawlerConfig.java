package com.demandware.xlt.util;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.xceptance.common.util.ParseUtils;
import com.xceptance.common.util.RegExUtils;
import com.xceptance.xlt.api.util.XltProperties;

/**
 * Base class for storing crawler flow configuration.
 * <ul>
 * <li>set patterns to include and/or exclude URLs</li>
 * <li>process page links in dependency of page text</li>
 * <li>limit crawler by depth, number of pages, or runtime</li>
 * <li>configure session drop (always, never, or when certain limit of pages or runtime is reached)</li>
 * </ul>
 * Example:
 *
 * <pre>
 * final CrawlerConfig crawlerConfig.loadDefaults() //
 *                                  .includeUrlPattern("whitelist_substring_pattern").includeUrlPatterns(collection) //
 *                                  .excludeUrlPattern("blacklist_substring_pattern").excludeUrlPatterns(collection) //
 *                                  .requireText("good").requireTexts(collection) //
 *                                  .disallowText("bad").disallowTexts(collection) //
 *                                  .depthMax(3) //
 *                                  .numberOfPages(100) //
 *                                  .runMax(10).minutes() //
 *                                  .dropSession().always() //
 *                                  .dropSession().every(30).minutes() //
 *                                  .dropSession().every(10).pages() //
 *                                  .dropSession().never() //
 *                                  .noCache(true)
 *                                  ;
 * </pre>
 */
public class CrawlerConfig
{
    /** Default exclude patterns */
    private static final Collection<String> EXCLUDE_DEFAULTS = new HashSet<String>(7);
    static
    {
        EXCLUDE_DEFAULTS.add("^mailto:");
        EXCLUDE_DEFAULTS.add("^#");
        EXCLUDE_DEFAULTS.add("\\.jpg$");
        EXCLUDE_DEFAULTS.add("\\.png$");
        EXCLUDE_DEFAULTS.add("\\.pdf$");
        EXCLUDE_DEFAULTS.add("\\.txt$");
        EXCLUDE_DEFAULTS.add("\\.xml$");
    }

    /** Default maximum crawling depth */
    private static final int DEFAULT_MAX_DEPTH = 2;

    /** The total number of pages to crawl */
    private int totalNumberOfPages = -1;

    /** The depth of recursion */
    private int depthOfRecursion = 1;

    /** Exclude patterns for URLs */
    private final Collection<String> excludePatterns = new HashSet<String>();

    /** Include patterns for URLs */
    private final Collection<String> includePatterns = new HashSet<String>();

    /** Maximum runtime of crawler in seconds */
    private long runtime = -1;

    /** Text patterns which are required on each page */
    private final Set<String> requiredTexts = new HashSet<String>();

    /** Text patterns which are disallowed on each page */
    private final Set<String> disallowedTexts = new HashSet<String>();

    /** Session drop configuration */
    private DropSession dropSession;

    /** NoCache - add some fake parameter to URL to bypass server side caching */
    private boolean noCache = false;

    private CrawlerConfig()
    {
        // Create via fabric method only.
    }

    /**
     * Create new crawler configuration object.
     * 
     * @return crawler configuration
     * @throws ParseException
     *             if loading the properties fails
     */
    public static CrawlerConfig create() throws ParseException
    {
        CrawlerConfig config = new CrawlerConfig();
        config.loadProperties();
        return config;
    }

    /**
     * Load crawler configuration properties.
     * 
     * @throws ParseException
     *             if loading the properties fails
     */
    protected void loadProperties() throws ParseException
    {
        // Initialize exclude URL patterns
        {
            final String prop = getProperty("excludeURLs");
            if (StringUtils.isNotBlank(prop))
            {
                // Patterns are separated by whitespace
                final String[] patterns = StringUtils.split(prop);
                for (final String pattern : patterns)
                {
                    excludeUrlPattern(pattern);
                }
            }
        }

        // Initialize include URL patterns
        {
            final String prop = getProperty("includeURLs");
            if (StringUtils.isNotBlank(prop))
            {
                // Patterns are separated by whitespace
                final String[] patterns = StringUtils.split(prop);
                for (final String pattern : patterns)
                {
                    includeUrlPattern(pattern);
                }
            }
        }

        // Initialize max depth of recursion
        depthMax(getProperty("maxDepth", depthOfRecursion));

        // Initialize max number of pages
        numberOfPages(getProperty("maxPages", totalNumberOfPages));

        // Initialize maximum crawler runtime
        {
            final String prop = getProperty("maxRuntime");
            if (StringUtils.isNotBlank(prop))
            {
                // Time is expected to be in seconds
                final int seconds = ParseUtils.parseTimePeriod(prop);
                runMax(seconds).seconds();
            }
        }

        // Initialize session dropping
        {
            // Get property value and check for keywords 'always', 'never', and 'every'
            final String prop = getProperty("dropSession");
            if (StringUtils.isNotBlank(prop))
            {
                if (prop.equals("always"))
                {
                    dropSession().always();
                }
                else if (prop.equals("never"))
                {
                    dropSession().never();
                }
                else if (prop.startsWith("every"))
                {
                    // Extract sub value and convert to time (or pages respectively)
                    final String interval = StringUtils.substringAfter(prop, " ").trim();
                    try
                    {
                        // Unit is SECONDS
                        final int seconds = ParseUtils.parseTimePeriod(interval);
                        dropSession().every(seconds).seconds();
                    }
                    catch (final Exception e)
                    {
                        // Unit is PAGES
                        final String pagesRAW = RegExUtils.getFirstMatch(prop, "(\\d+)p$", 1);
                        if (pagesRAW != null)
                        {
                            final int pages = Integer.valueOf(pagesRAW);
                            dropSession().every(pages).pages();
                        }
                        else
                        {
                            // Unknown time format
                            throw new ParseException(String.format("Unknown format of drop session period '%s'.", prop), 0);
                        }
                    }
                }
            }
        }

        // Initialize cache bypassing
        {
            final String prop = getProperty("noCache");
            if (StringUtils.isNotBlank(prop))
            {
                noCache(Boolean.valueOf(prop));
            }
        }

        // Initialize text requirements
        {
            final Collection<String> texts = getProperties("requireText");
            requireTexts(texts);
        }

        // Initialize text bans
        {
            final Collection<String> texts = getProperties("disallowText");
            disallowTexts(texts);
        }
    }

    /**
     * Read property value for given crawler key. Property is of format <code>crawler.&lt;key&gt;</code>
     * 
     * @param crawlerKey
     *            crawler property key
     * @return property value
     */
    private String getProperty(final String crawlerKey)
    {
        return Context.getConfiguration().getProperty("crawler." + crawlerKey);
    }

    /**
     * Read property value for given crawler key. Property is of format <code>crawler.&lt;key&gt;</code>
     * 
     * @param crawlerKey
     *            crawler property key
     * @param defaultKey
     *            default property value
     * @return property value or given default value if no property found for given crawler key
     */
    private int getProperty(final String crawlerKey, final int defaultKey)
    {
        return Context.getConfiguration().getProperty("crawler." + crawlerKey, defaultKey);
    }

    /**
     * Returns all property values for the domain key <code>crawler.&lt;key&gt;</code>.<br>
     * Attention: Properties without a domain (e.g. foobar=test) or domain only properties will be ignored. A property
     * has to have at least this form: <code>crawler.key=value</code>
     * 
     * @param crawlerKey
     *            crawler property key
     * @return all property values of domain <code>crawler.&lt;key&gt;</code>
     */
    private Collection<String> getProperties(final String crawlerKey)
    {
        return Context.getConfiguration().getPropertiesForKey("crawler." + crawlerKey).values();
    }

    /**
     * Load predefined common configuration parameters.
     * <table border=1>
     * <tr>
     * <td>Recursion Depth</td>
     * <td>2</td>
     * </tr>
     * <tr>
     * <td>Max crawled pages</td>
     * <td>1000</td>
     * </tr>
     * <tr>
     * <td>Max runtime</td>
     * <td>10 minutes</td>
     * </tr>
     * <tr>
     * <td>Drop Session</td>
     * <td>every 4 minutes</td>
     * </tr>
     * <tr>
     * <td>Exclude URL patterns</td>
     * <td>^mailto:<br>
     * ^#<br>
     * \\.jpg$<br>
     * \\.png$<br>
     * \\.pdf$<br>
     * \\.txt$<br>
     * \\.xml$<br>
     * &lt;in properties files configured include/exclude patterns&gt;</td>
     * </tr>
     * </table>
     * 
     * @return resulting crawler configuration
     */
    public CrawlerConfig loadDefaults()
    {
        final String seaparatorChars = ",; ";

        this.depthMax(DEFAULT_MAX_DEPTH)
            .numberOfPages(1000)
            .runMax(10).minutes()
            .dropSession().every(4).minutes()
            .includeUrlPatterns(Arrays.asList(StringUtils.split(XltProperties.getInstance()
                                                                             .getProperty("com.xceptance.xlt.http.filter.include"),
                                                                seaparatorChars)))
            .excludeUrlPatterns(Arrays.asList(StringUtils.split(XltProperties.getInstance()
                                                                             .getProperty("com.xceptance.xlt.http.filter.exclude"),
                                                                seaparatorChars)))
            .excludeUrlPatterns(EXCLUDE_DEFAULTS);

        return this;
    }

    /**
     * Set the total number of pages to crawl.
     * 
     * @param totalNumberOfPages
     *            the total number of pages to crawl
     * @return resulting crawler configuration
     */
    public CrawlerConfig numberOfPages(final int totalNumberOfPages)
    {
        this.totalNumberOfPages = totalNumberOfPages;
        return this;
    }

    /**
     * Get the total number of pages to crawl.
     * 
     * @return the numberOfPages
     */
    public int getMaxNumberOfPages()
    {
        return totalNumberOfPages;
    }

    /**
     * @param depthOfRecursion
     *            the depthOfRecursion to set
     * @return resulting crawler configuration
     */
    public CrawlerConfig depthMax(final int depthOfRecursion)
    {
        this.depthOfRecursion = depthOfRecursion;
        return this;
    }

    /**
     * @return the depthOfRecursion
     */
    public int getMaxDepthOfRecursion()
    {
        return depthOfRecursion;
    }

    /**
     * @param excludePatterns
     *            the excludePatterns to set
     * @return resulting crawler configuration
     */
    public CrawlerConfig excludeUrlPattern(final String excludePattern)
    {
        nullOrEmptyCheck(excludePattern, "Exclude pattern");

        this.excludePatterns.add(excludePattern);
        return this;
    }

    /**
     * @param excludePatterns
     *            the excludePatterns to set
     * @return resulting crawler configuration
     */
    public CrawlerConfig excludeUrlPatterns(final Collection<String> excludePatterns)
    {
        nullCheck(excludePatterns, "Exclude URL pattern collection");

        for (final String pattern : excludePatterns)
        {
            excludeUrlPattern(pattern);
        }

        return this;
    }

    /**
     * @return the excludePatterns
     */
    public Collection<String> getExcludePatterns()
    {
        return excludePatterns;
    }

    /**
     * @param includePatterns
     *            the includePatterns to set
     * @return resulting crawler configuration
     */
    public CrawlerConfig includeUrlPattern(final String includePattern)
    {
        nullOrEmptyCheck(includePattern, "Include pattern");

        this.includePatterns.add(includePattern);
        return this;
    }

    /**
     * @param includePatterns
     *            the includePatterns to set
     * @return resulting crawler configuration
     */
    public CrawlerConfig includeUrlPatterns(final Collection<String> includePatterns)
    {
        nullCheck(includePatterns, "Include URL pattern collection");

        for (final String pattern : includePatterns)
        {
            includeUrlPattern(pattern);
        }

        return this;
    }

    /**
     * @return the includePatterns
     */
    public Collection<String> getIncludePatterns()
    {
        return includePatterns;
    }

    /**
     * @param runtime
     *            the runtime to set. Set to 0 or below if no limit is desired.
     */
    public TimeUnit runMax(final int runtime)
    {
        this.runtime = runtime;
        return new TimeUnit(this);
    }

    /**
     * @return the runtime
     */
    public long getMaxRuntime()
    {
        return runtime;
    }

    /**
     * @param requiredText
     *            the requiredText to set
     * @return resulting crawler configuration
     */
    public CrawlerConfig requireTexts(final Collection<String> requiredTexts)
    {
        nullCheck(requiredTexts, "Required text collection");

        for (final String text : requiredTexts)
        {
            requireText(text);
        }

        return this;
    }

    /**
     * Require given text on page to process it.
     * 
     * @param requiredText
     *            required text
     * @return resulting crawler configuration
     */
    public CrawlerConfig requireText(final String requiredText)
    {
        nullOrEmptyCheck(requiredText, "Required text");

        this.requiredTexts.add(requiredText);
        return this;
    }

    /**
     * @return the requiredText
     */
    public Collection<String> getRequiredTexts()
    {
        return requiredTexts;
    }

    /**
     * @param disallowedText
     *            the disallowedText to set
     * @return resulting crawler configuration
     */
    public CrawlerConfig disallowTexts(final Collection<String> disallowedTexts)
    {
        nullCheck(requiredTexts, "Disallowed text collection");

        for (final String text : disallowedTexts)
        {
            disallowText(text);
        }

        return this;
    }

    /**
     * Add a text to the collection of disallowed texts. The crawler will not process a page that contains this text.
     * 
     * @param disallowedText
     *            disallowed text
     * @return resulting crawler configuration
     */
    public CrawlerConfig disallowText(final String disallowedText)
    {
        nullOrEmptyCheck(disallowedText, "Disallowed text");

        this.disallowedTexts.add(disallowedText);
        return this;
    }

    /**
     * Get the tests that are not allowed. The crawler will not process a page that contains one of these texts.
     * 
     * @return the disallowedText disallowed texts.
     */
    public Collection<String> getDisallowedTexts()
    {
        return disallowedTexts;
    }

    /**
     * Set if cache bypassing is desired.
     * 
     * @param noCache
     *            <code>true</code> if cache bypassing is desired, <code>false</code> if not.
     */
    public void noCache(final boolean noCache)
    {
        this.noCache = noCache;
    }

    /**
     * Is cache bypassing desired?
     * 
     * @return <code>true</code> if cache bypassing is desired, <code>false</code> otherwise
     */
    public boolean isNoCache()
    {
        return noCache;
    }

    /**
     * Initialize session drop configuration.
     * 
     * @return initialized session dropper
     */
    public DropSession dropSession()
    {
        return new DropSession(this);
    }

    /**
     * Set session drop configuration.
     * 
     * @param dropSession
     *            session drop configuration
     * @return resulting crawler configuration
     */
    CrawlerConfig setDropSession(final DropSession dropSession)
    {
        this.dropSession = dropSession;
        return this;
    }

    /**
     * Get the session drop configuration.
     * 
     * @return session drop configuration
     */
    public DropSession getDropSession()
    {
        return dropSession;
    }

    /**
     * Throw exception with custom message if passed string is <code>null</code> or empty.
     * 
     * @param s
     *            string to check for <code>null</code> or empty
     * @param type
     *            custom message hint. Message text will be: <br>
     *            <code>&lt;type&gt; must not be null or empty.</code>
     */
    private void nullOrEmptyCheck(final String s, final String type)
    {
        if (StringUtils.isEmpty(s))
        {
            throw new IllegalArgumentException(type + " must not be null or empty.");
        }
    }

    /**
     * Throw exception with custom message if passed object is <code>null</code>.
     * 
     * @param o
     *            object to check for <code>null</code>
     * @param type
     *            custom message hint. Message text will be: <br>
     *            <code>&lt;type&gt; must not be null.</code>
     */
    private void nullCheck(final Object o, final String type)
    {
        if (o == null)
        {
            throw new IllegalArgumentException(type + " must not be null.");
        }
    }

    /**
     * Convert passed value according to time unit to a common milliseconds base.
     */
    public class TimeUnit
    {
        /** Crawler configuration to set up. */
        private final CrawlerConfig crawlerConfig;

        /**
         * @param crawlerConfig
         *            crawler configuration to set up.
         */
        public TimeUnit(final CrawlerConfig crawlerConfig)
        {
            this.crawlerConfig = crawlerConfig;
        }

        /**
         * Set time unit 'seconds'.
         * 
         * @return resulting crawler configuration
         */
        public CrawlerConfig seconds()
        {
            runtime *= 1000;
            return crawlerConfig;
        }

        /**
         * Set time unit 'minutes'.
         * 
         * @return resulting crawler configuration
         */
        public CrawlerConfig minutes()
        {
            runtime *= 60000;
            return crawlerConfig;
        }

        /**
         * Set time unit 'hours'.
         * 
         * @return resulting crawler configuration
         */
        public CrawlerConfig hours()
        {
            runtime *= 3600000;
            return crawlerConfig;
        }
    }
}
