package com.demandware.xlt.extra.tests;

import org.junit.Test;

import com.demandware.xlt.extra.actions.SimpleURL;
import com.demandware.xlt.tests.AbstractTestCase;
import com.xceptance.xlt.api.actions.AbstractHtmlPageAction;

/**
 * Just runs a single url test. Configurable
 * 
 * @author Rene Schwietzke
 * @version 1.0
 */
public class TSimpleURL extends AbstractTestCase
{
    @Override
    @Test
    public void test() throws Throwable
    {
        final String url = getProperty("url");
        final String xpath = getProperty("xpath");
        final String text = getProperty("text");
        final int iterations = getProperty("iterations", 1) - 1;

        AbstractHtmlPageAction lastAction = new SimpleURL(url, xpath, text);
        lastAction.getWebClient().getOptions().setJavaScriptEnabled(false);
        lastAction.run();

        for (int i = 0; i < iterations; i++)
        {
            lastAction = new SimpleURL(lastAction, url, xpath, text);
            lastAction.run();
        }
    }
}
