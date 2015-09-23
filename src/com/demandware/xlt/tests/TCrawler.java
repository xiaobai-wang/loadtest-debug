package com.demandware.xlt.tests;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;

import com.demandware.xlt.actions.Homepage;
import com.demandware.xlt.flows.CrawlerFlow;
import com.demandware.xlt.util.Context;
import com.demandware.xlt.util.CrawlerConfig;

/**
 * A crawler exloring the site as configured by property file or API while API rules over property.
 * 
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 */
public class TCrawler extends AbstractTestCase
{
    /**
     * {@inheritDoc}
     */
    @Override
    public void test() throws Throwable
    {
        // Configure crawler
        /**
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
        final CrawlerConfig crawlerConfig = CrawlerConfig.create();

        // Start at the landing page.
        // Get the configured start URL (see project.properties) and open the landing page.
        String startUrlString = Context.getConfiguration().getStartURL();
        Assert.assertFalse("Start URL is empty", StringUtils.isBlank(startUrlString));
        if (crawlerConfig.isNoCache())
        {
            startUrlString = CrawlerFlow.appendNoCacheParameter(startUrlString);
        }
        new Homepage(startUrlString).run();

        // Start to crawl.
        new CrawlerFlow(crawlerConfig).run();
    }
}
