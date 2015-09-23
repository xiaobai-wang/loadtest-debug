package com.demandware.xlt.actions.storeLocator;

import com.demandware.xlt.actions.AbstractHtmlPageAction;
import com.demandware.xlt.util.Page;
import com.demandware.xlt.validators.Validator;
import com.gargoylesoftware.htmlunit.html.HtmlElement;

/**
 * Open the store locator page.
 * 
 * @author Daniel Kirst (Xceptance Software Technologies GmbH)
 */
public class GoToStoreLocator extends AbstractHtmlPageAction
{
    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() throws Exception
    {
        // Get the link which leads to the store locator page.
        final HtmlElement goToStore = Page.getHeaderLocator().byCss(".menu-utility-user a")
                                          .asserted("Store Locator link not found.").first();

        // Click it.
        loadPageByClick(goToStore);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void postValidate() throws Exception
    {
        // Basic page validation.
        Validator.validateCommonPage();

        // Check if the current page contains a form to perform a store search - if no form is available, an
        // AssertionError is thrown.
        Page.getZipCodeStoreLocatorForm();
    }
}
