package com.demandware.xlt.actions.order;

import com.demandware.xlt.actions.AbstractHtmlPageAction;
import com.demandware.xlt.util.Account;
import com.demandware.xlt.util.Context;
import com.demandware.xlt.util.FormUtils;
import com.demandware.xlt.util.Page;
import com.demandware.xlt.validators.Validator;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.xceptance.xlt.api.util.elementLookup.Results;

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

        // String myURL = Context.getPage().getUrl().toString();
        // XltLogger.runTimeLogger.debug("# GuestCheckout -- debug -- Body BEGIN");
        // XltLogger.runTimeLogger.debug("myURL = " + myURL);
        // XltLogger.runTimeLogger.debug(Context.getPage().asXml());
        // XltLogger.runTimeLogger.debug("# GuestCheckout -- debug -- Body END");

        // Find the Email Input field.
        final Results guestEmailInput = Page.find().byId("dwfrm_login_unregistered")
                                            .byXPath("./fieldset/div[contains(@class, 'required')]/input");

        final HtmlElement guestEmailInputElem = guestEmailInput.asserted("No guest email input field on page.").single();
        final Account account = Context.getAccount();
        final String email = account.getEmail();
        FormUtils.setInputValue(guestEmailInput, email);

        // Click the guest checkout button.
        final HtmlElement guestCheckOutButton = Page.find().byId("dwfrm_login_unregistered")
                                                    .byXPath("./fieldset/div[contains(@class, 'form-row-button')]/button")
                                                    .asserted("No guest checkout button found on page.")
                                                    .single();

        loadPageByClick(guestCheckOutButton);

        // Perform the pending AJAX calls for updating shipping method and order summary.
        // AjaxUtils.getApplicableShippingMethods();
        // AjaxUtils.updateShippingMethodsList();
        // AjaxUtils.updateSummary();
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
