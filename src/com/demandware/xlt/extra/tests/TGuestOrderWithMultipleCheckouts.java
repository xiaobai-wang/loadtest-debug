package com.demandware.xlt.extra.tests;

import com.demandware.xlt.actions.order.COBilling;
import com.demandware.xlt.actions.order.COPlaceOrder;
import com.demandware.xlt.actions.order.COShipping;
import com.demandware.xlt.actions.order.Checkout;
import com.demandware.xlt.actions.order.GuestCheckout;
import com.demandware.xlt.actions.order.ViewCart;
import com.demandware.xlt.flows.AddToCartFlow;
import com.demandware.xlt.flows.VisitFlow;
import com.demandware.xlt.tests.AbstractTestCase;

/**
 * Open the landing page and browse the catalog to a random product. Configure this product and add it to the cart.
 * Finally process the checkout including the final order placement step. All as Guest user. The checkout will be
 * restarted twice to emulate a partially started checkout.
 * 
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 * 
 */
public class TGuestOrderWithMultipleCheckouts extends AbstractTestCase
{
    @Override
    public void test() throws Throwable
    {
        new VisitFlow().run();

        // Fill and view the cart.
        new AddToCartFlow().run();

        new ViewCart().run();

        // Press the checkout button.
        new Checkout().run();

        // Checkout as guest or registered user.
        new GuestCheckout();

        // *************** Restart checkout
        // nah, we check the cart again first
        // View the cart finally.
        new ViewCart().run();

        // Press the checkout button.
        new Checkout().run();

        // Checkout as guest or registered user.
        new GuestCheckout().run();

        // Fill and submit the shipping form.
        new COShipping().run();

        // Fill and submit the billing form.
        new COBilling().run();

        // ************* Restart checkout
        // nah, we check the cart again first
        // View the cart finally.
        new ViewCart().run();

        // Press the checkout button.
        new Checkout().run();

        // Checkout as guest or registered user.
        new GuestCheckout().run();

        // Fill and submit the shipping form.
        new COShipping().run();

        // Fill and submit the billing form.
        new COBilling().run();

        // Place the order finally
        new COPlaceOrder().run();
    }
}
