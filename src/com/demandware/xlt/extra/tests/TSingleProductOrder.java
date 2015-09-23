package com.demandware.xlt.extra.tests;

import com.demandware.xlt.extra.actions.DirectProductDetailView;
import com.demandware.xlt.extra.flows.SingleProductOrderFlow;
import com.demandware.xlt.tests.AbstractTestCase;
import com.demandware.xlt.util.Context;

/**
 * Open the landing page, register account if necessary and browse the catalog to a random product. Configure this
 * product and add it to the cart. Finally process the checkout including the final order placement step.
 * 
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 * 
 */
public class TSingleProductOrder extends AbstractTestCase
{
    @Override
    public void test() throws Throwable
    {
        new DirectProductDetailView(Context.getConfiguration().getProperty("singleOrder.products.directUrl")).run();

        new SingleProductOrderFlow().run();
    }
}
