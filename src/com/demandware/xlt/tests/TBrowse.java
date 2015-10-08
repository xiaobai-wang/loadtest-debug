package com.demandware.xlt.tests;

import com.demandware.xlt.flows.BrowsingFlow;
import com.demandware.xlt.flows.VisitFlow;
import com.demandware.xlt.util.Context;

/**
 * Open the landing page, browse the catalog. If there's a result grid open a random product's quick or detail view.
 * 
 * @author Xiaobai Wang
 */
public class TBrowse extends AbstractTestCase
{
    /**
     * {@inheritDoc}
     */

    @Override
    public void test() throws Throwable
    {
        // Start at the landing page.
        new VisitFlow().run();

        // Determine a random number of rounds between the configured
        // min and max values for browsing.
        final int rounds = Context.getConfiguration().getRandomNumberOfBrowsingProducts();

        // Browse.
        for (int i = 0; i < rounds; i++)
        {
            new BrowsingFlow().run();
        }
    }
}
