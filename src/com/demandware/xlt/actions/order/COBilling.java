package com.demandware.xlt.actions.order;

import org.junit.Assert;

import com.demandware.xlt.actions.AbstractHtmlPageAction;
import com.demandware.xlt.util.Account;
import com.demandware.xlt.util.Context;
import com.demandware.xlt.util.CreditCard;
import com.demandware.xlt.util.FormUtils;
import com.demandware.xlt.util.Page;
import com.demandware.xlt.validators.Validator;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.xceptance.xlt.api.util.XltRandom;
import com.xceptance.xlt.api.util.elementLookup.Results;

/**
 * Fills in the billing address form.
 * 
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 * 
 */
public class COBilling extends AbstractHtmlPageAction
{
    /** Will PayPal be used for checkout? */
    private final boolean isPaypalCheckout;

    public COBilling()
    {
        isPaypalCheckout = XltRandom.nextBoolean(Context.getConfiguration().getPaypalProbability());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() throws Exception
    {
        // Get the billing form.
        final Results findInBillingForm = Page.find().byId("dwfrm_billing");

        // Determine the payment method.
        if (isPaypalCheckout)
        {
            // We have to clear the Accept-Encoding header so the PayPal Server handles the request right.
            getWebClient().addRequestHeader("Accept-Encoding", "");

            // Select method PayPal.
            FormUtils.checkRadioButton(findInBillingForm.byCss("input[value='PayPal']"));
        }
        else
        {
            // Select method Credit Card ...
            FormUtils.checkRadioButton(findInBillingForm.byCss("input[value='CREDIT_CARD']"));
            // ... and fill the credit card form.
            fillForm();
        }

        // Submit the billing form.
        final HtmlElement submitButton = Page.find().byXPath("id('dwfrm_billing')/div/button[@name='dwfrm_billing_save']")
                                             .asserted("No billing submit button found on page.").single();
        loadPageByClick(submitButton);
    }

    /**
     * Fills the billing form.
     * 
     * @param page
     *            the current page
     * @throws Exception
     */
    private void fillForm() throws Exception
    {
        // Get account and credit card data.
        final Account account = Context.getAccount();
        final CreditCard cc = Context.getCreditCard();

        // Enter email address.
        FormUtils.setInputValueByID("dwfrm_billing_billingAddress_email_emailAddress", account.getEmail());

        // Ensure that no newsletter is requested.
        FormUtils.checkCheckboxByID("dwfrm_billing_billingAddress_addToEmailList", false);

        // Fill the payment form.
        FormUtils.setInputValueByID("dwfrm_billing_paymentMethods_creditCard_owner", account.getFirstName() + " " + account.getLastName());
        FormUtils.setInputValueByID("dwfrm_billing_paymentMethods_creditCard_number", cc.getNumber());
        FormUtils.selectByID("dwfrm_billing_paymentMethods_creditCard_month", cc.getExpirationMonth());
        FormUtils.selectByID("dwfrm_billing_paymentMethods_creditCard_year", cc.getExpirationYear());
        FormUtils.setInputValueByID("dwfrm_billing_paymentMethods_creditCard_cvn", cc.getCVN());

        // Don't save credit card (for registered user only).
        final Results saveCreditCardCheckbox = Page.find().byId("dwfrm_billing_paymentMethods_creditCard_saveCard");
        if (saveCreditCardCheckbox.exists())
        {
            saveCreditCardCheckbox.<HtmlCheckBoxInput> single().setChecked(false);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void postValidate() throws Exception
    {
        if (isPaypalCheckout)
        {
            // Check we landet on a PayPal page.
            Assert.assertTrue("Expected to be on PayPal but URL is " + Context.getPage().getUrl().getHost(), Page.isPaypalPage());
        }
        else
        {
            // Check that the current page is the order review page.
            Validator.validateReviewOrderCheckoutStep();
        }
    }
}
