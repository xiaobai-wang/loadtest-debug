package com.demandware.xlt.actions.order.paypal.paypal1;

import com.demandware.xlt.actions.AbstractHtmlPageAction;
import com.demandware.xlt.util.FormUtils;
import com.demandware.xlt.util.Page;
import com.demandware.xlt.util.PaypalAccount;
import com.demandware.xlt.validators.Validator;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.xceptance.xlt.api.util.elementLookup.HPU;
import com.xceptance.xlt.api.util.elementLookup.Results;

/**
 * Login to PayPal (Variant 1)
 */
public class Paypal1Login extends AbstractHtmlPageAction
{
    /**
     * Select the login form.
     * 
     * @return login form locator
     */
    public static Results getLoginForm()
    {
        return Page.find().byId("parentForm");
    }

    /**
     * Is the expected login form contained?
     * 
     * @return <code>true</code> if the login form can be found, <code>false</code> otherwise.
     */
    public static boolean isPossible()
    {
        return getLoginForm().exists();
    }

    /** PayPal account */
    final PaypalAccount paypalAccount;

    /**
     * Create the PayPal login (Variant 1) action.
     * 
     * @param paypalAccount
     *            PayPal account
     */
    public Paypal1Login(final PaypalAccount paypalAccount)
    {
        super();
        this.paypalAccount = paypalAccount;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() throws Exception
    {
        // Clear the browser's Accept-Encoding header. This is necessary for PayPal to process the request correctly.
        getWebClient().addRequestHeader("Accept-Encoding", "");

        // Lookup the login form.
        final HtmlForm form = getLoginForm().single();

        // Login with email address and password.
        FormUtils.setInputValueByID("login_email", paypalAccount.getEmail());
        FormUtils.setInputValueByID("login_password", paypalAccount.getPassword());

        // Click login button.
        loadPageByClick(HPU.findAsserted().in(form).byId("submitLogin").single());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void postValidate() throws Exception
    {
        // If we are still on a PayPal page just validate the basics. Otherwise we should see the the order confirmation
        // page.
        if (Page.isPaypalPage())
        {
            Validator.validateBasics();
        }
        else
        {
            Validator.validateOrderConfirmationPage();
        }
    }
}
