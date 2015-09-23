package com.demandware.xlt.actions.order;

import com.demandware.xlt.actions.AbstractHtmlPageAction;
import com.demandware.xlt.util.AjaxUtils;
import com.demandware.xlt.util.Page;
import com.demandware.xlt.validators.Validator;
import com.gargoylesoftware.htmlunit.html.HtmlElement;

/**
 * Checkout as a guest.
 * 
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 * 
 */
public class GuestCheckout extends AbstractHtmlPageAction
{
    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() throws Exception
    {
        // Click the guest checkout button.
        final HtmlElement guestCheckOutButton = Page.getPrimaryContentContainerLocator()
                                                    .byCss(".login-box form button[name='dwfrm_login_unregistered']")
                                                                                   .asserted("No guest checkout button found on page.")
                                                                                   .single();
        loadPageByClick(guestCheckOutButton);

        // Perform the pending AJAX calls for updating shipping method and order summary.
        AjaxUtils.getApplicableShippingMethods();
        AjaxUtils.updateShippingMethodsList();
        AjaxUtils.updateSummary();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void postValidate() throws Exception
    {
        // Check it is the shipping page.
        Validator.validateShippingCheckoutStep();
    }
}
