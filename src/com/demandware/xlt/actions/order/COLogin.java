package com.demandware.xlt.actions.order;

import com.demandware.xlt.actions.AbstractHtmlPageAction;
import com.demandware.xlt.util.AjaxUtils;
import com.demandware.xlt.util.Context;
import com.demandware.xlt.util.FormUtils;
import com.demandware.xlt.util.Page;
import com.demandware.xlt.validators.Validator;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.xceptance.xlt.api.util.elementLookup.HPU;

/**
 * Login during checkout.
 * 
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 */
public class COLogin extends AbstractHtmlPageAction
{
    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() throws Exception
    {
        // Get the login form.
        final HtmlForm signInForm = Page.getLoginFormLocator().asserted("No sign up form found!").first();

        // Enter email address.
        HPU.find().in(signInForm).byCss(".email-input").asserted("No email field in sign up form.").single()
           .setAttribute("value", Context.getAccount().getEmail());

        // Enter password.
        FormUtils.setInputValueByID("dwfrm_login_password", Context.getAccount().getPassword());

        // Submit the form.
        loadPageByClick(signInForm.getButtonByName("dwfrm_login_login"));

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
