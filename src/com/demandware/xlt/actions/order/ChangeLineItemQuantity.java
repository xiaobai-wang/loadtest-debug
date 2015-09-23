package com.demandware.xlt.actions.order;

import java.util.List;

import org.junit.Assert;

import com.demandware.xlt.actions.AbstractAjaxAction;
import com.demandware.xlt.util.Context;
import com.demandware.xlt.util.Page;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.xceptance.common.util.RegExUtils;
import com.xceptance.xlt.api.util.elementLookup.HPU;

/**
 * Adjust the item quantity on a cart page to move the cart totals into a potentially configured range.
 * 
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 */
public class ChangeLineItemQuantity extends AbstractAjaxAction
{
    /**
     * The quantity which is set to reach the payment limit.
     */
    private int newQuantity;

    /**
     * The totals of the cart before the line items get updated.
     */
    private double cartTotals;

    /**
     * Check if the totals are within the blocked price range
     * 
     * @return <code>true</code> if the cart totals are within the configured blocked price range, <code>false</code>
     *         otherwise
     */
    public static boolean isInInvalidPriceRange()
    {
        // Get cart totals string and convert it to a number.
        final double cartTotals = priceStringToNumber(getCartTotals());

        // Get the configured limits.
        final int lowerLimit = Context.getConfiguration().getPaymentsBlockedAbove();
        final int upperLimit = Context.getConfiguration().getPaymentsBlockedBelow();

        // Check if the cart totals are within the configured blocked range
        return lowerLimit <= cartTotals && cartTotals <= upperLimit;
    }

    /**
     * Take the most expensive line item and set its quantity up, so the upper payment limit will be reached, or removes
     * it if no upper limit is set to get beyond the lower limit.
     */
    @Override
    protected void doExecute() throws Exception
    {
        // Get the cart totals.
        cartTotals = priceStringToNumber(getCartTotals());

        // Get most expensive item.
        final HtmlElement mostExpensiveItem = getMostExpensiveFrom(getItemRows());

        // Determine the upper cart totals limit.
        final int upperLimit = Context.getConfiguration().getPaymentsBlockedBelow();

        // If we have to stay below the lower limit (upper limit is not specified or set to Int.MAX) remove the most
        // expensive item by setting its quantity to zero.
        if (upperLimit >= Integer.MAX_VALUE)
        {
            newQuantity = 0;
        }
        else
        {
            // If we have a blocked price range we can raise the quantity of the most expensive element to get the cart
            // totals over this upper limit.
            final double itemPrice = getItemPrice(mostExpensiveItem);
            newQuantity = getQuantityForUpperPaymentLimitBound(itemPrice, cartTotals, upperLimit);
        }

        // Set item quantity.
        final HtmlInput quantityField = getQuantityField(mostExpensiveItem);
        quantityField.setValueAttribute(Integer.toString(newQuantity));

        // Lookup the 'Update' button and click it.
        final HtmlElement button = Page.findAsserted("Wrong number update quantity buttons.")
                                       .byId("update-cart")
                                      .single();
        loadPageByClick(button);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void postValidate() throws Exception
    {
        // Validate if line was updated.
        if (newQuantity > 0)
        {
            Assert.assertFalse("Cart totals not fixed", isInInvalidPriceRange());
        }
        else
        {
            // When we remove items there is a risk that we've cleared the cart. So validate that any item is left.
            if (!Page.isCartPageCartEmpty())
            {
                // Validate that cart totals are lower than before.
                Assert.assertFalse("Cart totals don't have decreased.", cartTotals < priceStringToNumber(getCartTotals()));
            }
        }
    }

    /**
     * Get the cart total string from the page.
     * 
     * @return the cart total string
     */
    private static String getCartTotals()
    {
        return Page.getPrimaryContentContainerLocator().asserted("Cart totals not found")
                   .byCss(".order-totals-table .order-total>td:last-child")
                   .last().getTextContent();
    }

    /**
     * Convert a string containing a price into a double number.<br>
     * Input might be:
     * <ul>
     * <li>$1,000.00</li>
     * <li>1.000,00€</li>
     * <li>1000</li>
     * <li>...</li>
     * </ul>
     * 
     * @param priceString
     * @return converted price
     */
    private static double priceStringToNumber(final String priceString)
    {
        // Strip currency.
        String cleanPrice = RegExUtils.getFirstMatch(priceString.trim(), Page.PRICE_REGEXP);

        // Remove any whitespaces.
        cleanPrice = RegExUtils.removeAll(cleanPrice, "\\s");

        final double result;

        if (RegExUtils.isMatching(cleanPrice, "^\\d$"))
        {
            // It's plain number (1000)
            result = Double.parseDouble(cleanPrice);
        }
        else
        {
            // Remember if price has decimal part.
            final boolean hasDecimalSeparator = RegExUtils.isMatching(cleanPrice, "[.,]\\d{2}$");

            // Keep the digits only, remove all separators.
            cleanPrice = RegExUtils.removeAll(cleanPrice, ",");
            cleanPrice = RegExUtils.removeAll(cleanPrice, "\\.");

            // Parse to double.
            final double d = Double.parseDouble(cleanPrice);

            // Fix decimal if necessary.
            result = hasDecimalSeparator ? (d / 100) : d;
        }

        return result;
    }

    /**
     * Returns all product rows from the cart page.
     * 
     * @return all product rows
     */
    private static List<HtmlElement> getItemRows()
    {
        return Page.find().byId("cart-table").byCss(".cart-row").all();
    }

    /**
     * Iterates over the list of line items and returns the row index of the most expensive item.
     * 
     * @param list
     *            all product rows
     * @return row index of most expensive item
     */
    private static HtmlElement getMostExpensiveFrom(List<HtmlElement> list)
    {
        double maxPrice = Double.MIN_VALUE;
        HtmlElement maxPriceRow = null;

        for (final HtmlElement row : list)
        {
            final double itemPrice = getItemPrice(row);

            if (maxPrice < itemPrice)
            {
                maxPrice = itemPrice;
                maxPriceRow = row;
            }
        }

        return maxPriceRow;
    }

    /**
     * Get the single item price of the given row
     * 
     * @param row
     * @return item price
     */
    private static double getItemPrice(final HtmlElement row)
    {
        return priceStringToNumber(HPU.find().in(row).byCss(".price-sales").single().getTextContent());
    }

    /**
     * Calculate which quantity is needed to put the cart into a valid price range.
     * 
     * @param itemPrice
     *            item price
     * @return quantity
     */
    private static int getQuantityForUpperPaymentLimitBound(final double itemPrice, final double lastCartTotals, final int upperLimit)
    {
        return (int) (Math.floor((upperLimit - lastCartTotals) / itemPrice) + 2);
    }

    /**
     * Get the quantity field from the given row.
     * 
     * @param row
     *            cart item row
     * @return quantity field
     * @throws AssertionError
     */
    private static HtmlInput getQuantityField(final HtmlElement row) throws AssertionError
    {
        return HPU.findAsserted("Wrong number of quantity input field in cartRow").in(row)
                  .byCss(".item-quantity > .input-text")
                  .single();
    }
}
