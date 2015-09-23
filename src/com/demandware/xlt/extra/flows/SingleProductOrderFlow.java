package com.demandware.xlt.extra.flows;

import org.junit.Assert;

import com.demandware.xlt.actions.order.AddToCart;
import com.demandware.xlt.actions.order.COBilling;
import com.demandware.xlt.actions.order.COLogin;
import com.demandware.xlt.actions.order.COPlaceOrder;
import com.demandware.xlt.actions.order.COShipping;
import com.demandware.xlt.actions.order.Checkout;
import com.demandware.xlt.actions.order.GuestCheckout;
import com.demandware.xlt.actions.order.ViewCart;
import com.demandware.xlt.flows.AbstractFlow;
import com.demandware.xlt.flows.CartCleanUpFlow;
import com.demandware.xlt.flows.ConfigureProductFlow;
import com.demandware.xlt.flows.ConfigureProductSetFlow;
import com.demandware.xlt.util.Context;
import com.demandware.xlt.util.Page;
import com.xceptance.xlt.api.util.XltProperties;

/**
 * Order a single configured product.
 * 
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 */
public class SingleProductOrderFlow extends AbstractFlow
{
    /**
     * {@inheritDoc}
     */
    @Override
    public void run() throws Throwable
    {
        // Configure product.
        if (Page.isSingleProductDetailPage())
        {
            new ConfigureProductFlow().run();
        }
        else if (Page.isProductSetPage())
        {
            new ConfigureProductSetFlow().run();
        }

        Assert.assertTrue("Product is not available", Page.isProductAvailable());

        // how often do we want to do an add 2 cart?
        final int add2Cart = Math.max(1, XltProperties.getInstance().getProperty("singleOrder.products.addToCartNumber", 1));

        for (int i = 0; i < add2Cart; i++)
        {
            // Add product variation to cart.
            new AddToCart().run();
        }

        // View the cart finally.
        new ViewCart().run();

        // If the product is not available: remove it. We'll run into an empty cart in this case.
        new CartCleanUpFlow().run();

        // Click at 'Checkout'.
        new Checkout().run();

        // Checkout as guest or registered user (login).
        if (Context.getAccount().isRegistered())
        {
            new COLogin().run();
        }
        else
        {
            new GuestCheckout().run();
        }

        // Fill and submit the shipping form.
        new COShipping().run();

        // Fill and submit the billing form.
        new COBilling().run();

        // Place the order.
        new COPlaceOrder().run();
    }
}
