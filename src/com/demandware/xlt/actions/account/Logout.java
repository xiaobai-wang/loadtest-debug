package com.demandware.xlt.actions.account;

import org.junit.Assert;

import com.demandware.xlt.actions.AbstractHtmlPageAction;
import com.demandware.xlt.util.Page;
import com.demandware.xlt.validators.Validator;
import com.gargoylesoftware.htmlunit.html.HtmlElement;

/**
 * Logout currently logged in user.
 * 
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 */
public class Logout extends AbstractHtmlPageAction
{
    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() throws Exception
    {
        HtmlElement logoutLink = Page.getLogoutLinkLocator().asserted("No logout link found.").single();

        // Click the logout link.
        loadPageByClick(logoutLink);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void postValidate() throws Exception
    {
        // Check the basics.
        Validator.validateCommonPage();

        // Assert the user is not logged in anymore.
        Assert.assertFalse("User is not logged out.", Page.isCustomerLoggedIn());

        // Mini cart must be empty in any case after logout.
        Assert.assertTrue("Cart is not empty after logout.", Page.isMiniCartEmpty());
    }
}
