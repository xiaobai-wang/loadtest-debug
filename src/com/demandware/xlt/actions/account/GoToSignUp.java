package com.demandware.xlt.actions.account;

import org.junit.Assert;

import com.demandware.xlt.actions.AbstractHtmlPageAction;
import com.demandware.xlt.util.Page;
import com.demandware.xlt.validators.Validator;
import com.gargoylesoftware.htmlunit.html.HtmlElement;

/**
 * Open the registration page.
 * 
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 */
public class GoToSignUp extends AbstractHtmlPageAction
{
    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() throws Exception
    {
        // Lookup the Register button.
        final HtmlElement signUpButton = Page.getRegisterLinkLocator()
                                             .asserted("No sign up button found.")
                                             .single();

        // Click it.
        loadPageByClick(signUpButton);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void postValidate() throws Exception
    {
        // Check the basics.
        Validator.validateCommonPage();

        // Check it is the account creation form page.
        Assert.assertTrue("Request results in wrong page.", Page.checkExistance(Page.getRegistrationFormLocator()));
    }
}
