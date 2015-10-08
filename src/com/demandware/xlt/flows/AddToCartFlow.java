package com.demandware.xlt.flows;

import com.demandware.xlt.actions.order.AddToCart;
import com.demandware.xlt.util.Context;
import com.demandware.xlt.util.Page;
import com.demandware.xlt.util.SafetyBreak;

/**
 * Browses the catalog, adds a number of products (respectively variations of products) to the cart and opens the cart
 * page finally.
 * 
 * @author Xiaobai Wang
 */
public class AddToCartFlow extends AbstractFlow
{
    /** Maximum number of attempts to add the desired number of products to the cart. */
    private final SafetyBreak safetyBreak = new SafetyBreak(5);

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() throws Throwable
    {
        // Get the number of desired cart items determined from the configured values of min and max products.
        final int nrOfProducts = Context.getConfiguration().getNumberOfProductsForAddToCart();

        // Current number of cart items.
        // int itemsInMiniCart = Page.getItemsInMiniCart();
        int itemsInMiniCart = Page.getNumItemsInMiniCartByAjax();

        // Add another item to cart.
        while (itemsInMiniCart < nrOfProducts)
        {
            // Check if the maximum number of attempts is reached.
            safetyBreak.check("Unable to add the desired number of products to the cart.");

            // Browse the catalog, navigate to a product, and configure it if possible.
            new BrowsingFlow().run();

            // Add the current product or more specifically one of its variations to the cart.
            if (Page.isProductAvailable())
            {
                new AddToCart().run();

                // Check if adding the product was successful.
                int newItemsInMiniCart = Page.getNumItemsInMiniCartByAjax();
                if (itemsInMiniCart < newItemsInMiniCart)
                {
                    // Reset the attempts-checker.
                    safetyBreak.reset();
                }

                // Update the number of cart items.
                itemsInMiniCart = newItemsInMiniCart;
            }
            else
            {
                if (!Page.isNoHitsPage())
                {
                    // Log if no products are available. If there are to much logging message, the XPath or the catalog
                    // may have issues.
                    Context.logForDebug("The product(s) on this detail/quickview/set page could not be added to the cart.");
                }
            } // else
        } // while
    } // run
}
