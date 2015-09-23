package com.demandware.xlt.actions.account;

import org.junit.Assert;

import com.demandware.xlt.actions.AbstractHtmlPageAction;
import com.demandware.xlt.util.Account;
import com.demandware.xlt.util.Context;
import com.demandware.xlt.util.FormUtils;
import com.demandware.xlt.util.Page;
import com.demandware.xlt.validators.Validator;
import com.gargoylesoftware.htmlunit.html.HtmlElement;

/**
 * Fill and submit the registration form.
 * 
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 */
public class Register extends AbstractHtmlPageAction
{
    /**
     * Gets the button to finish the registration.</br><b>An AssertionError is thrown if no button is available.</b>
     * <p>
     * <b>Page:</b> use this on the registration page.
     * </p>
     * 
     * @return the button to finish the registration
     * @throws AssertionError
     *             if there is no button available on the page
     */
    private static HtmlElement getRegistrationButton() throws AssertionError
    {
        return Page.getRegistrationFormLocator().byXPath("./fieldset/div/button[@name='dwfrm_profile_confirm']")
                   .asserted("No registration button found.").single();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() throws Exception
    {
        // Get the user account.
        final Account account = Context.getAccount();

        // Fill the form.
        FormUtils.setInputValueByID("dwfrm_profile_customer_firstname", account.getFirstName());
        FormUtils.setInputValueByID("dwfrm_profile_customer_lastname", account.getLastName());
        FormUtils.setInputValueByID("dwfrm_profile_customer_email", account.getEmail());
        FormUtils.setInputValueByID("dwfrm_profile_customer_emailconfirm", account.getEmail());
        FormUtils.setInputValueByID("dwfrm_profile_login_password", account.getPassword());
        FormUtils.setInputValueByID("dwfrm_profile_login_passwordconfirm", account.getPassword());

        // Ensure to not request the newsletter.
        FormUtils.checkCheckboxByID("dwfrm_profile_customer_addtoemaillist", false);

        // Submit the registration form.
        loadPageByClick(getRegistrationButton());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void postValidate() throws Exception
    {
        // Check the basics.
        Validator.validateCommonPage();

        // XltLogger.runTimeLogger.debug("# asXml() -- debug -- Body BEGIN");
        // XltLogger.runTimeLogger.debug(Context.getPage().asXml());
        // XltLogger.runTimeLogger.debug("# asXml() -- debug -- Body END");

        // Check the created user is logged in.
        Assert.assertTrue("User is not logged in.", Page.isCustomerLoggedIn());

        // Check the current page is the 'My Account' page
        Assert.assertTrue("Could not identify 'My Account' page.",
                          Page.checkExistance(Page.getPrimaryContentContainerLocator().byCss("div > ul.account-options")));

        // Now it's safe to mark the account as 'registered'.
        Context.getAccount().setRegistered();
    }
}
