package com.demandware.xlt.flows;

import com.demandware.xlt.actions.order.ChangeLineItemQuantity;
import com.demandware.xlt.actions.order.RemoveUnvailableProduct;
import com.demandware.xlt.util.Context;
import com.demandware.xlt.util.FlowStoppedException;
import com.demandware.xlt.util.Page;
import com.demandware.xlt.util.SafetyBreak;

/**
 * Get a clean cart for checkout by removing as unavailable marked items as well as to stay outside any block cart
 * totals areas.
 * 
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 */
public class CartCleanUpFlow extends AbstractFlow
{
    /** Define how many attempts should be done at maximum to adjust the cart completely. */
    private final SafetyBreak validCartSafetyBreak = new SafetyBreak(4);

    /** Define how many attempts should be done at maximum to remove all unavailable items. */
    private final SafetyBreak outOfStockSafetyBreak = new SafetyBreak(20);

    /** Define how many attempts should be done at maximum to adjust the cart totals. */
    private final SafetyBreak priceLimitsSafetyBreak = new SafetyBreak(4);

    /**
     * Checks if there are items in the cart marked as out of stock.</br><b>NOTE: only works on the cart page</b>
     * <p>
     * <b>Page:</b> use this on the cart page.
     * </p>
     * 
     * @return whether there are items out of stock or not
     */
    private static boolean hasOutOfStockItemsInCart()
    {
        return Page.checkExistance(Page.getOutOfStockCartItems());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() throws Throwable
    {
        // Run until the cart is clean for checkout.
        while (isInvalidCart())
        {
            // Check overall attempts.
            validCartSafetyBreak.check("Cart could not be put into a valid state in a reasonable amount of tries.");

            // Remove unavailable products from cart.
            removeUnvailableProducts();

            // Update item quantity to stay within cart total limits (if configured).
            adjustCartTotals();
        }
    }

    /**
     * Remove items form cart that are not marked as 'In Stock'.
     * 
     * @throws Throwable
     */
    private void removeUnvailableProducts() throws Throwable
    {
        // Start with a clean safety break.
        outOfStockSafetyBreak.reset();
        // As long as we have unavailable items in cart, kick one.
        while (hasOutOfStockItemsInCart())
        {
            // Check if we are still allowed to proceed.
            outOfStockSafetyBreak.check("Unable to remove all unavailable products from cart.");

            // Remove one unavailable item.
            new RemoveUnvailableProduct().run();

            // Stop if we removed all items from cart.
            assertCartNotEmpty("Cart was empty after clean up.");
        }
    }

    /**
     * Adjust cart totals to be within configured limit.
     * 
     * @throws Throwable
     */
    private void adjustCartTotals() throws Throwable
    {
        // Start with a clean limiter.
        priceLimitsSafetyBreak.reset();

        // As long as we are within disallowed prce range ...
        while (ChangeLineItemQuantity.isInInvalidPriceRange())
        {
            // Check number of attempts.
            priceLimitsSafetyBreak.check("Unable to change quantity to leave payment limits.");

            // Adjust quantity of 1 item to modiy cart totals.
            new ChangeLineItemQuantity().run();

            // Stop if we removed all items from cart.
            assertCartNotEmpty("Cart was empty after item quantity update.");
        }
    }

    /**
     * Are unavailable items in the cart or are cart totals within disallowed price range?
     * 
     * @return <code>true</code> if there are unavailable items in the cart or cart totals are within disallowed price
     *         range.
     */
    private boolean isInvalidCart()
    {
        return hasOutOfStockItemsInCart() || ChangeLineItemQuantity.isInInvalidPriceRange();
    }

    /**
     * Asserts that cart is NOT empty.
     * 
     * @param failMsg
     *            optional exception message
     * @throws FlowStoppedException
     *             if cart is empty
     */
    private void assertCartNotEmpty(final String failMsg) throws FlowStoppedException
    {
        // nothing left in cart
        if (Page.isCartPageCartEmpty())
        {
            Context.logForDebug(failMsg);
            throw new FlowStoppedException(failMsg);
        }
    }
}
