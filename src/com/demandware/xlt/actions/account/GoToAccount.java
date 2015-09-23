package com.demandware.xlt.actions.account;

import org.junit.Assert;

import com.demandware.xlt.actions.AbstractHtmlPageAction;
import com.demandware.xlt.util.Page;
import com.demandware.xlt.validators.Validator;
import com.gargoylesoftware.htmlunit.html.HtmlElement;

/**
 * Open the My Account page
 * 
 * @author Damian Goshey (Demandware, Inc)
 */
public class GoToAccount extends AbstractHtmlPageAction
{
    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() throws Exception
    {
        // Lookup the Register button.
        final HtmlElement accountLink = Page.getAccountLinkLocator().asserted("No Account link found.").single();

        // Click it.
        loadPageByClick(accountLink);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void postValidate() throws Exception
    {
        // Check the basics.
        Validator.validateCommonPage();

        // Check the current page is the 'My Account' page
        Assert.assertTrue("Could not identify 'My Account' page.",
                          Page.checkExistance(Page.getPrimaryContentContainerLocator().byCss("div > ul.account-options")));
    }
}
