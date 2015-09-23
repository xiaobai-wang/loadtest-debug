package com.demandware.xlt.tests;

import com.demandware.xlt.actions.order.COBilling;
import com.demandware.xlt.actions.order.COPlaceOrder;
import com.demandware.xlt.actions.order.COShipping;
import com.demandware.xlt.actions.order.Checkout;
import com.demandware.xlt.actions.order.GuestCheckout;
import com.demandware.xlt.actions.order.ViewCart;
import com.demandware.xlt.flows.AddToCartFlow;
import com.demandware.xlt.flows.CartCleanUpFlow;
import com.demandware.xlt.flows.PaypalFlow;
import com.demandware.xlt.flows.VisitFlow;
import com.demandware.xlt.util.Page;

/**
 * Open the landing page and browse the catalog to a random product. Configure this product and add it to the cart.
 * Finally process the checkout including the final order placement step.
 * 
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 */
public class TGuestOrder extends AbstractTestCase
{
    /**
     * {@inheritDoc}
     */
    @Override
    public void test() throws Throwable
    {
        // Start at the landing page.
        new VisitFlow().run();

        // Fill and view the cart.
        new AddToCartFlow().run();

        // View the cart.
        new ViewCart().run();

        // remove unavailable products from cart
        new CartCleanUpFlow().run();

        // Press the checkout button.
        new Checkout().run();

        // Press the Guest Checkout button.
        new GuestCheckout().run();

        // Fill and submit the shipping form.
        new COShipping().run();

        // Fill and submit the billing form.
        new COBilling().run();

        // Checkout with PayPal if necessary.
        if (Page.isPaypalPage())
        {
            new PaypalFlow().run();
        }

        // Place the order
        new COPlaceOrder().run();
    }
}
