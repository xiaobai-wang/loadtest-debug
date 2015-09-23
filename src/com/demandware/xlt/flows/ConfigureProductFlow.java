package com.demandware.xlt.flows;

import org.junit.Assert;

import com.demandware.xlt.actions.catalog.ConfigureProduct;
import com.demandware.xlt.util.Page;
import com.demandware.xlt.util.SafetyBreak;

/**
 * Selects a random available product variation.
 * 
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 */
public class ConfigureProductFlow extends AbstractFlow
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
        // This flow have to start on a single product page, so check if we are there.
        Assert.assertTrue("ConfigureProductFlow did not start on product page.", Page.isSingleProductDetailPage());

        do
        {
            // Check if the max number of attempts is not yet reached and if configuration is possible at all
            safetyBreak.check();

            // Select a random variation
            new ConfigureProduct().run();
        }
        // Check if the variation is in stock.
        while (!Page.isProductAvailable());
    }
}
