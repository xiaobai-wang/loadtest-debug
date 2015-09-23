package com.demandware.xlt.actions.order;

import org.junit.Assert;

import com.demandware.xlt.actions.AbstractHtmlPageAction;
import com.demandware.xlt.util.Page;
import com.demandware.xlt.validators.Validator;
import com.gargoylesoftware.htmlunit.html.HtmlElement;

/**
 * Goto Checkout page.
 * 
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 */
public class Checkout extends AbstractHtmlPageAction
{
    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() throws Exception
    {
        // Checkout makes sense only if at least 1 item is in the cart.
        Assert.assertFalse("Try to checkout an empty cart.", Page.isMiniCartEmpty());

        // Click the 'Checkout' button.
        final HtmlElement checkoutButton = Page.find().byXPath("id('checkout-form')/fieldset/button[@name='dwfrm_cart_checkoutCart']")
                                               .asserted("No checkout button found on page.").random();
        loadPageByClick(checkoutButton);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void postValidate() throws Exception
    {
        Validator.validateBasics();
        // We should see now either the login page or the shipping page (depending on the user's login state before)
        if (Page.getLoginFormLocator().exists())
        {
            // be happy
        }
        else
        {
            Validator.validateShippingCheckoutStep();
        }
    }
}
