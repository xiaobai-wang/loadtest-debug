package com.demandware.xlt.tests;

import com.demandware.xlt.actions.account.GoToAccount;
import com.demandware.xlt.actions.account.Logout;
import com.demandware.xlt.actions.order.COBilling;
import com.demandware.xlt.actions.order.COLogin;
import com.demandware.xlt.actions.order.COShipping;
import com.demandware.xlt.actions.order.Checkout;
import com.demandware.xlt.actions.order.ViewCart;
import com.demandware.xlt.flows.AddToCartFlow;
import com.demandware.xlt.flows.CartCleanUpFlow;
import com.demandware.xlt.flows.RegisterFlow;
import com.demandware.xlt.flows.VisitFlow;
import com.demandware.xlt.util.Context;

/**
 * Open the landing page, register account if necessary and browse the catalog to a random product. Configure this
 * product, add it to the cart and process the checkout steps but do not place the order finally.
 * 
 * @author Xiaobai Wang
 */
public class TCheckout extends AbstractTestCase
{
    /**
     * {@inheritDoc}
     */
    @Override
    public void test() throws Throwable
    {
        // Mark test case to be used by a registered customer.
        Context.initRegisteredTestcase();

        // Start at the landing page.
        new VisitFlow().run();

        if (!Context.getAccount().isRegistered())
        {
            // Register user
            new RegisterFlow().run();
        }

        // Fill and view the cart.
        new AddToCartFlow().run();

        // View the cart.
        new ViewCart().run();

        // remove unavailable products from cart
        new CartCleanUpFlow().run();

        // Press the checkout button.
        new Checkout().run();

        // Checkout as registered user.
        new COLogin().run();

        // Fill and submit the shipping form.
        new COShipping().run();

        // Fill and submit the billing form.
        new COBilling().run();

        // Go To Account Page
        new GoToAccount().run();

        // Logout
        new Logout().run();
    }
}
