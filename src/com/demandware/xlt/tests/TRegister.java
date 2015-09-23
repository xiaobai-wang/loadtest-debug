package com.demandware.xlt.tests;

import com.demandware.xlt.flows.RegisterFlow;
import com.demandware.xlt.flows.VisitFlow;

/**
 * Open landing page and navigate to the registration form. Register a new customer and log out afterwards.
 * 
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 */
public class TRegister extends AbstractTestCase
{
    /**
     * {@inheritDoc}
     */
    @Override
    protected void test() throws Throwable
    {
        // Start at the landing page.
        new VisitFlow().run();

        // Register user
        new RegisterFlow().run();
    }
}
