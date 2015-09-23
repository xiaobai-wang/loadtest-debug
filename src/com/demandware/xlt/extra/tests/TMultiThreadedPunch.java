package com.demandware.xlt.extra.tests;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadFactory;

import org.junit.Test;

import com.demandware.xlt.extra.actions.SimpleURL;
import com.demandware.xlt.tests.AbstractTestCase;
import com.gargoylesoftware.htmlunit.util.Cookie;
import com.xceptance.common.util.Getter;
import com.xceptance.common.util.concurrent.DaemonThreadFactory;
import com.xceptance.xlt.api.actions.AbstractHtmlPageAction;
import com.xceptance.xlt.api.engine.Session;

/**
 * Runs a test with concurrency for follow up requests to create a blockage of some kind.
 * 
 * @author Rene Schwietzke
 * @version 1.0
 */
public class TMultiThreadedPunch extends AbstractTestCase
{
    @Override
    @Test
    public void test() throws Throwable
    {
        final String url = getProperty("url");
        final String xpath = getProperty("xpath");
        final String text = getProperty("text");
        final int iterations = getProperty("iterations", 1) - 1;
        final int threadCount = getProperty("thread.count", 1);

        // our list of parallel browsers
        final List<AbstractHtmlPageAction> browsers = new ArrayList<AbstractHtmlPageAction>(threadCount);

        // get all browsers initialized
        for (int i = 0; i < threadCount; i++)
        {
            final AbstractHtmlPageAction action = new SimpleURL(url, xpath, text);
            action.run();

            browsers.add(action);
        }

        // ok, make them all user the same session hence get them all on the same cookie base
        // the first one is our source
        final Set<Cookie> cookies = browsers.get(0).getWebClient().getCookieManager().getCookies();
        for (int i = 1; i < threadCount; i++)
        {
            browsers.get(i).getWebClient().getCookieManager().clearCookies();
            for (final Cookie cookie : cookies)
            {
                browsers.get(i).getWebClient().getCookieManager().addCookie(cookie);
            }
        }

        // ok, the semaphore for the organization of the punch
        final Semaphore startLock = new Semaphore(0);

        // our worker threads
        final ThreadFactory threadFactory = new DaemonThreadFactory(new Getter<String>()
        {
            @Override
            public String get()
            {
                return Session.getCurrent().getUserID() + "-pool-";
            }
        });

        ExecutorService executorService = null;
        try
        {
            executorService = Executors.newFixedThreadPool(threadCount, threadFactory);

            // ok, the part with the punch, repeat that as often as desired
            for (int i = 0; i < iterations; i++)
            {
                final List<Future<Integer>> futures = new ArrayList<Future<Integer>>(threadCount);

                // ok, we create a number of threads within the current thread group
                for (int t = 0; t < threadCount; t++)
                {
                    final Worker worker = new Worker(String.valueOf(t), startLock, browsers.get(t), url, xpath, text);
                    futures.add(executorService.submit(worker));
                }

                // start them all
                startLock.release(threadCount);

                // read all results and wait therefore until all are finished
                for (final Future<Integer> future : futures)
                {
                    future.get();
                }
            }
        }
        finally
        {
            if (executorService != null)
            {
                executorService.shutdownNow();
            }
        }
    }

    class Worker implements Callable<Integer>
    {
        private final Semaphore start;

        private AbstractHtmlPageAction lastAction;

        private final String url;

        private final String xpath;

        private final String text;

        private final String name;

        public Worker(final String name, final Semaphore start, final AbstractHtmlPageAction lastAction, final String url,
            final String xpath, final String text)
        {
            super();

            this.start = start;
            this.lastAction = lastAction;

            this.name = name;

            this.url = url;
            this.xpath = xpath;
            this.text = text;
        }

        @Override
        public Integer call()
        {
            try
            {
                start.acquire();
            }
            catch (final InterruptedException e)
            {
                e.printStackTrace();
            }

            lastAction = new SimpleURL(lastAction, url, xpath, text);
            lastAction.setTimerName(lastAction.getTimerName() + "-" + name);
            try
            {
                lastAction.run();
            }
            catch (final Throwable e1)
            {
                e1.printStackTrace();
            }

            return 0;
        }

    }
}
