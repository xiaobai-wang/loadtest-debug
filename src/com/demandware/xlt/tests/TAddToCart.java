package com.demandware.xlt.tests;

import com.demandware.xlt.actions.order.ViewCart;
import com.demandware.xlt.flows.AddToCartFlow;
import com.demandware.xlt.flows.VisitFlow;

/**
 * Open the landing page and browse the catalog to a random product. Configure this product and add it to the cart.
 * 
 * @author Xiaobai Wang
 */
public class TAddToCart extends AbstractTestCase
{
    /**
     * {@inheritDoc}
     */
    @Override
    public void test() throws Throwable
    {
        // Start at the landing page.
        new VisitFlow().run();//

        // Fill and view the cart.
        new AddToCartFlow().run();//

        // View the cart.
        new ViewCart().run();
    }
}
