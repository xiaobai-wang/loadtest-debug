package com.demandware.xlt.flows;

import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.demandware.xlt.extra.actions.SimpleURL;
import com.demandware.xlt.util.Context;
import com.demandware.xlt.util.CrawlerConfig;
import com.demandware.xlt.util.DropSession;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.xceptance.common.util.RegExUtils;
import com.xceptance.xlt.api.engine.Session;

public class CrawlerFlow
{
    /** crawler configuration */
    private final CrawlerConfig crawlerConfig;

    /** Collected URLs with corresponding crawl depth */
    private final List<KeyValue> urlsTovisit = new LinkedList<KeyValue>();

    /** Visited URLs */
    private final Collection<String> visitedUrls = new HashSet<String>();

    /** Crawler start time */
    private final long startTime;

    /**
     * create new crawler flow
     * 
     * @param crawlerConfig
     *            crawler configuration
     */
    public CrawlerFlow(final CrawlerConfig crawlerConfig)
    {
        this.crawlerConfig = crawlerConfig;
        startTime = System.currentTimeMillis();
    }

    /**
     * {@inheritDoc}
     */
    public void run() throws Throwable
    {
        collectPageLinks(1);
        processPage();
    }

    /**
     * Grab the links on page.
     * 
     * @param depth
     *            link depth of collected links
     * @throws Throwable
     */
    private void collectPageLinks(final int depth) throws Throwable
    {
        final Collection<HtmlAnchor> anchors = getPageAnchors();
        for (final HtmlAnchor anchor : anchors)
        {
            final String href = anchor.getHrefAttribute();
            // check URL includes and excludes
            if (isIncluded(href) && !isExcluded(href))
            {
                final String fullyQualified = Context.getPage().getFullyQualifiedUrl(href).toExternalForm();
                urlsTovisit.add(new KeyValue(fullyQualified, depth));
            }
        }
    }

    /**
     * process the page
     * 
     * @throws Throwable
     */
    private void processPage() throws Throwable
    {
        while (!urlsTovisit.isEmpty() && !isTimedOut() && !isMaxNumberOfPagesReached())
        {
            final KeyValue link = urlsTovisit.remove(0);

            // not visited
            if (!visitedUrls.contains(link.getHref()))
            {
                final boolean pageOpenedSuccessfully = follow(link.getHref());
                if (pageOpenedSuccessfully)
                {
                    final int nextDepth = link.getDepth();

                    // process page if text assertions match only
                    // check if we have already reached maximum depth
                    if (isPageTextOk() && isDepthAllowed(nextDepth) && !isTimedOut())
                    {
                        collectPageLinks(nextDepth);
                    }
                }
            }
        }
    }

    /**
     * Check page text for required or disallowed text
     * 
     * @return <code>true</code> if page text contains required text and does not contain disallowed text
     */
    private boolean isPageTextOk()
    {
        // check that page contains required text(s) AND
        // doesn't contain disallowed text(s)
        final String pageText = getPageText();
        return isRequiredText(pageText) && !isDisallowedText(pageText);
    }

    /**
     * Get page text
     * 
     * @return page text
     */
    private String getPageText()
    {
        return Context.getPage().getTextContent();
    }

    /**
     * Are we allowed to proceed to next depth
     * 
     * @return
     */
    private boolean isDepthAllowed(final int depth)
    {
        if (crawlerConfig.getMaxDepthOfRecursion() <= 0)
        {
            return true;
        }

        return depth < crawlerConfig.getMaxDepthOfRecursion();
    }

    /**
     * Get page anchors shuffled around to get some randomness.
     * 
     * @return shuffled page anchors
     */
    private List<HtmlAnchor> getPageAnchors()
    {
        // anchors found on page
        final List<HtmlAnchor> anchors = Context.getPage().getAnchors();

        // bring some random into game
        Collections.shuffle(anchors);

        return anchors;
    }

    /**
     * open page referenced by given anchor and crawl the resulting page
     * 
     * @param anchor
     * @throws Throwable
     */
    private boolean follow(final String href) throws Throwable
    {
        // update session
        crawlerSessionUpdate();

        // update history
        visitedUrls.add(href);

        // open page
        return openPage(href);
    }

    /**
     * Update the crawler session
     */
    private void crawlerSessionUpdate()
    {
        final DropSession dropSession = crawlerConfig.getDropSession();
        if (dropSession != null)
        {
            dropSession.update();
        }
    }

    /**
     * Open page referenced by link
     * 
     * @param href
     *            the link
     * @throws Throwable
     */
    private boolean openPage(String href) throws Throwable
    {
        // add some parameter to bypass server side caching
        if (crawlerConfig.isNoCache())
        {
            href = appendNoCacheParameter(href);
        }

        try
        {
            final SimpleURL simpleClick = new SimpleURL(href);
            simpleClick.run();
            return simpleClick.isValidBasicPage();
        }
        catch (AssertionError e)
        {
            Session.logEvent(e.getMessage(), href);
        }

        return false;
    }

    public static String appendNoCacheParameter(final String url)
    {
        return url + (url.contains("?") ? "&" : "?") + System.currentTimeMillis();
    }

    /**
     * Has the crawler reached its EOL?
     * 
     * @return
     */
    private boolean isTimedOut()
    {
        if (crawlerConfig.getMaxRuntime() <= 0)
        {
            return false;
        }

        return now() - startTime >= crawlerConfig.getMaxRuntime();
    }

    /**
     * Current time
     * 
     * @return
     */
    private long now()
    {
        return System.currentTimeMillis();
    }

    /**
     * Has the crawler parsed the maximum number of pages?
     * 
     * @return
     */
    private boolean isMaxNumberOfPagesReached()
    {
        if (crawlerConfig.getMaxNumberOfPages() <= 0)
        {
            return false;
        }

        return visitedUrls.size() >= crawlerConfig.getMaxNumberOfPages();
    }

    /**
     * Anchor URL is included if no include filter is given or it matches at least one include filter
     * 
     * @param anchor
     * @return
     * @throws Exception
     */
    private boolean isIncluded(final String href) throws Exception
    {
        boolean isIncluded = true;

        if (!StringUtils.isBlank(href))
        {
            if (!crawlerConfig.getIncludePatterns().isEmpty())
            {
                isIncluded = false;
                final String urlString = getFullyQualifiedUrlString(href);
                if (!StringUtils.isBlank(urlString))
                {
                    for (final String includePattern : crawlerConfig.getIncludePatterns())
                    {
                        if (RegExUtils.isMatching(urlString, includePattern))
                        {
                            isIncluded = true;
                            break;
                        }
                    }

                    if (!isIncluded)
                    {
                        Session.logEvent("Skipped loading (not included)", urlString);
                    }
                }
                else
                {
                    Session.logEvent("Skipped loading (malformed URL)", href);
                }
            }
        }
        else
        {
            isIncluded = false;
            Session.logEvent("Skipped loading (blank)", "(blank)");
        }

        return isIncluded;
    }

    /**
     * Anchor URL is excluded if it matches at least one exclude filter
     * 
     * @param anchor
     * @return
     * @throws Exception
     */
    private boolean isExcluded(final String href) throws Exception
    {
        boolean isExcluded = false;

        if (!StringUtils.isBlank(href))
        {
            final String urlString = getFullyQualifiedUrlString(href);
            if (!StringUtils.isBlank(urlString))
            {
                for (final String excludePattern : crawlerConfig.getExcludePatterns())
                {
                    if (RegExUtils.isMatching(urlString, excludePattern))
                    {
                        isExcluded = true;
                        break;
                    }
                }

                if (isExcluded)
                {
                    Session.logEvent("Skipped loading (excluded)", urlString);
                }
            }
            else
            {
                Session.logEvent("Skipped loading (malformed URL)", href);
            }
        }
        else
        {
            isExcluded = true;
            Session.logEvent("Skipped loading (blank)", "(blank)");
        }

        return isExcluded;
    }

    private String getFullyQualifiedUrlString(final String urlString)
    {
        try
        {
            return Context.getPage().getFullyQualifiedUrl(urlString).toExternalForm();
        }
        catch (MalformedURLException e)
        {
            Session.logEvent("Skipped (malformed URL)", urlString);
        }

        return null;
    }

    private boolean isRequiredText(final String pageText)
    {
        boolean isRequiredText = true;

        // no restriction -> required text condition is fulfilled
        // otherwise check page
        if (!crawlerConfig.getRequiredTexts().isEmpty())
        {
            isRequiredText = false;
            if (!StringUtils.isEmpty(pageText))
            {
                isRequiredText = true;
                // check if at least one of the required texts is contained
                // req texts condition: ALL or ONE
                for (final String text : crawlerConfig.getRequiredTexts())
                {
                    if (!RegExUtils.isMatching(pageText, text))
                    {
                        isRequiredText = false;
                        break;
                    }
                }
            }

            // log event if no required text was found on page
            if (!isRequiredText)
            {
                Session.logEvent("Skipped processing (required text not found)", getCurrentUrlString());
            }
        }

        return isRequiredText;
    }

    private boolean isDisallowedText(final String pageText)
    {
        boolean isDisallowedText = false;

        // no restriction -> disallowed text condition cannot be fulfilled
        // otherwise check page
        if (!crawlerConfig.getDisallowedTexts().isEmpty())
        {
            if (!StringUtils.isEmpty(pageText))
            {
                // check if at least one of the required texts is contained
                // req texts condition: ALL or ONE
                for (final String text : crawlerConfig.getDisallowedTexts())
                {
                    if (RegExUtils.isMatching(pageText, text))
                    {
                        isDisallowedText = true;
                        break;
                    }
                }
            }

            // log event if disallowed text was found on page
            if (isDisallowedText)
            {
                Session.logEvent("Skipped processing (disallowed text found)", getCurrentUrlString());
            }
        }

        return isDisallowedText;
    }

    private String getCurrentUrlString()
    {
        return Context.getPage().getUrl().toExternalForm();
    }

    private static class KeyValue
    {
        private final String href;

        private final int depth;

        public KeyValue(final String href, final int depth)
        {
            this.href = href;
            this.depth = depth;
        }

        public String getHref()
        {
            return href;
        }

        public int getDepth()
        {
            return depth;
        }
    }
}
