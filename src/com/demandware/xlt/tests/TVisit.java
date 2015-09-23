package com.demandware.xlt.tests;

import com.demandware.xlt.flows.VisitFlow;

/**
 * Single click visitor. The visitor opens the landing page and will not do any interaction.
 * 
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 * 
 */
public class TVisit extends AbstractTestCase
{
    /**
     * {@inheritDoc}
     */
    @Override
    public void test() throws Throwable
    {
        // Start at the landing page and leave immediately.
        new VisitFlow().run();
    }
}
