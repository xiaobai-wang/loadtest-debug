package com.demandware.xlt.actions.account;

import org.junit.Assert;

import com.demandware.xlt.actions.AbstractHtmlPageAction;
import com.demandware.xlt.util.Page;
import com.demandware.xlt.validators.Validator;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.xceptance.xlt.api.util.XltLogger;

/**
 * Open the registration page.
 * 
 * @author Damian Goshey (Demandware, Inc.)
 */
public class GoToLoginSignUp extends AbstractHtmlPageAction
{
    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() throws Exception
    {
        final HtmlElement loginButton = Page.getLoginLinkLocator()
                                            .asserted("Login link not found.")
                                            .single();
        XltLogger.runTimeLogger.debug("# loginButton = " + loginButton.getAttribute("href").trim());

        // go to where sign in button will be available
        loadPageByClick(loginButton);

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
        Assert.assertTrue("Request results in wrong page.", Page.checkExistance(Page.getLoginFormLocator()));

        Assert.assertTrue("Request results in wrong page.", Page.checkExistance(Page.getCreateAccountFormLocator()));
    }
}
