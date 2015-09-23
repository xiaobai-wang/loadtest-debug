package com.demandware.xlt.util;

/**
 * Session Dropper used for crawler.
 * 
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 */
public class DropSession
{
    /** Indicator to drop a session never */
    private static final int DROP_NEVER = -1;

    /** Indicator to drop a session never */
    private static final int DROP_ALWAYS = 0;

    /** Drop value unit */
    enum DropUnit
    {
        TIME,
        PAGES
    }
    
    /** Underlying crawler configuration */
    private final CrawlerConfig crawlerConfig;

    /** When to drop the session */
    private int threshold;

    /** Threshold unit */
    private DropUnit unit;
    
    /** Number of pages since last session drop. */
    private int pageCount;

    /** Timestamp of last session drop. */
    private long lastDropTime;

    /**
     * Create new session dropper
     * 
     * @param crawlerConfig
     *            underlying crawler configuration
     */
    DropSession(final CrawlerConfig crawlerConfig)
    {
        this.crawlerConfig = crawlerConfig;
        reset();
    }

    /**
     * Define session drop threshold value.
     * 
     * @param threshold
     * @return threshold unit query
     */
    public DropSessionUnit every(final int threshold)
    {
        // Check for valid value.
        if (threshold <= 0)
        {
            throw new IllegalArgumentException("Threshold must be greater than zero");
        }

        // Set threshold value and query unit.
        this.threshold = threshold;
        return new DropSessionUnit();
    }

    /**
     * Drop session always.
     * 
     * @return resulting crawler configuration
     */
    public CrawlerConfig always()
    {
        this.threshold = DROP_ALWAYS;
        return publishDropSession();
    }

    /**
     * Drop session never.
     * 
     * @return resulting crawler configuration
     */
    public CrawlerConfig never()
    {
        this.threshold = DROP_NEVER;
        return publishDropSession();
    }

    /**
     * Update session dropper. that might trigger a session drop if configured threshold is reached.
     */
    public void update()
    {
        switch (threshold)
        {
            case DROP_ALWAYS:
                dropSession();
                break;

            case DROP_NEVER:
                // Do nothing.
                break;

            default:
                // Increase page counter.
                pageCount++;

                // Decide if threshold unit is page or time
                switch (unit)
                {
                    case PAGES:
                        // Drop session if current page count has reached threshold page count.
                        if (pageCount >= threshold)
                        {
                            dropSession();
                        }
                        break;
                    case TIME:
                        // Drop session if time since last session drop has reached or exceeded threshold time.
                        if (now() - lastDropTime >= threshold)
                        {
                            dropSession();
                        }
                        break;
                    default:
                        throw new IllegalStateException("No such drop unit type : " + unit);
                }
        }
    }

    /**
     * Drop the browser session.
     */
    private void dropSession()
    {
        dropCookies();
        reset();
    }

    /**
     * Set page counter to zero and last drop time to current time.
     */
    private void reset()
    {
        lastDropTime = now();
        pageCount = 0;
    }

    /**
     * Drop all cookies.
     */
    private void dropCookies()
    {
        Context.getPage().getWebClient().getCookieManager().clearCookies();
    }

    /**
     * Publish this session dropper for given crawler configuration.
     * 
     * @return resulting crawler configuration
     */
    private CrawlerConfig publishDropSession()
    {
        return crawlerConfig.setDropSession(this);
    }

    /**
     * Provide an interface for defining threshold unit.
     * 
     * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
     */
    public class DropSessionUnit
    {
        /**
         * Given threshold unit is 'seconds'
         * 
         * @return resulting crawler configuration
         */
        public CrawlerConfig seconds()
        {
            threshold *= 1000;
            unit = DropUnit.TIME;
            return publishDropSession();
        }

        /**
         * Given threshold unit is 'minutes'
         * 
         * @return resulting crawler configuration
         */
        public CrawlerConfig minutes()
        {
            threshold *= 60000;
            unit = DropUnit.TIME;
            return publishDropSession();
        }

        /**
         * Given threshold unit is 'hours'
         * 
         * @return resulting crawler configuration
         */
        public CrawlerConfig hours()
        {
            threshold *= 3600000;
            unit = DropUnit.TIME;
            return publishDropSession();
        }

        /**
         * Given threshold unit is 'pages'
         * 
         * @return resulting crawler configuration
         */
        public CrawlerConfig pages()
        {
            unit = DropUnit.PAGES;
            return publishDropSession();
        }
    }

    /**
     * Current timestamp.
     * 
     * @return current timestamp
     */
    private long now()
    {
        return System.currentTimeMillis();
    }
}
