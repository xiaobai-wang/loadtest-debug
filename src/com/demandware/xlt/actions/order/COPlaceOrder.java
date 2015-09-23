package com.demandware.xlt.actions.order;

import com.demandware.xlt.actions.AbstractHtmlPageAction;
import com.demandware.xlt.util.Page;
import com.demandware.xlt.validators.Validator;
import com.gargoylesoftware.htmlunit.html.HtmlElement;

/**
 * Places the order finally.
 * 
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 * 
 */
public class COPlaceOrder extends AbstractHtmlPageAction
{
    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() throws Exception
    {
        // Click the Submit button to place the order.
        final HtmlElement submitButton = Page.getPrimaryContentContainerLocator().byCss(".order-summary-footer form button[name='submit']")
                                             .asserted("Submit button to place the order not found on page.").single();
        loadPageByClick(submitButton);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void postValidate() throws Exception
    {
        // Check it is the order confirmation page.
        Validator.validateOrderConfirmationPage();
    }
}
