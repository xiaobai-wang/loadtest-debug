package com.demandware.xlt.flows;

import org.junit.Assert;

import com.demandware.xlt.actions.catalog.ConfigureProductSet;
import com.demandware.xlt.util.Page;
import com.demandware.xlt.util.SafetyBreak;

/**
 * Selects a random available product variation.
 * 
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 */
public class ConfigureProductSetFlow extends AbstractFlow
{
    /**
     * Maximum number of attempts to find a available variation.
     */
    private final SafetyBreak safetyBreak = new SafetyBreak(5);

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() throws Throwable
    {
        // This flow have to start on a product set page, so check if we are there.
        Assert.assertTrue("ConfigureProductSetFlow did not start on product set page.", Page.isProductSetPage());

        do
        {
            safetyBreak.check();

            // Select a random variation
            new ConfigureProductSet().run();

            // Check if the variation is in stock.
        }
        while (!Page.isProductAvailable());
    }
}
