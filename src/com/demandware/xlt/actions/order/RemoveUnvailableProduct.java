package com.demandware.xlt.actions.order;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;

import com.demandware.xlt.actions.AbstractHtmlPageAction;
import com.demandware.xlt.util.Context;
import com.demandware.xlt.util.Page;
import com.demandware.xlt.validators.Validator;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.xceptance.xlt.api.util.elementLookup.HPU;

/**
 * Removes an unavailable product from the shopping cart. Unavailable products might have a price 'N/A' or are marked as
 * 'Out of Stock'.
 * 
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 */
public class RemoveUnvailableProduct extends AbstractHtmlPageAction
{
    /**
     * How many items of the given SKU are in the current cart?
     * 
     * @param sku
     *            SKU to look up
     * @return number of items with given SKU
     */
    private static int getSkuCount(final String sku)
    {
        return Page.getCartTableLocator().byCss(".cart-row > .item-details > .product-list-item > .sku")
                   .byXPath("./span[@class='value' and normalize-space(.)='" + sku + "']").count();
    }

    /** The remove link/button. */
    private HtmlElement removeButton;

    /** The product's item number. */
    private String sku;

    /** Number of cart items at the beginning of the action. */
    private int initialCartItemCount;

    /** Number of cart items with the chosen SKU number. There might be several variations of the same product in cart. */
    private int initialSkuCount;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() throws Exception
    {
        // Remember the number of initial cart items.
        initialCartItemCount = Page.getCartItemCount();
        Assert.assertFalse("Nothing to remove from empty cart.", initialCartItemCount <= 0);

        // Get the first product that is not marked available.
        final HtmlElement productRow = Page.getOutOfStockCartItems().asserted("No product row on cart page found.").random();

        // Look up the remove button.
        final String removeText = Context.getConfiguration().getLanguageProperty("cart.remove");
        removeButton = HPU.findAsserted("No remove button in product row found.")
                          .in(productRow)
                          .byCss(".item-quantity-details > div > button[type='submit'][value='" + removeText + "']")
                          .single();

        // Remember the SKU.
        sku = HPU.findAsserted("SKU information in product row not found.").in(productRow)
                 .byCss(".item-details > .product-list-item > .sku > .value").single().getTextContent().trim();

        Assert.assertFalse("SKU not found.", StringUtils.isBlank(sku));

        // Remember SKU count.
        initialSkuCount = getSkuCount(sku);

        // Remove the item.
        loadPageByClick(removeButton);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void postValidate() throws Exception
    {
        // Validate the cart basically.
        Validator.validateShoppingCart();

        // An empty cart is allowed (and expected) if the last item was removed only.
        if (initialCartItemCount == 1)
        {
            Assert.assertTrue("Cart was expected to be empty.", Page.isMiniCartEmpty());
        }
        else
        {
            // The initial cart item count was higher than 1. We removed 1 item so there must be at least 1 item left.
            Assert.assertTrue("Cart is empty unexpectedly.", !Page.isMiniCartEmpty());

            // Furthermore we should have 1 item less than before ...
            Assert.assertEquals("Number of cart items has not been decreased.", initialCartItemCount - 1, Page.getCartItemCount());

            // ... and the number of cart rows with the removed item's SKU should have been also decreased by 1.
            Assert.assertEquals("Number of SKU items has not been decreased for SKU " + sku + ".", initialSkuCount - 1, getSkuCount(sku));
        }
    }
}
