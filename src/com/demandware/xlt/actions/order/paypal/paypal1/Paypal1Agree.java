package com.demandware.xlt.actions.order.paypal.paypal1;

import com.demandware.xlt.actions.AbstractHtmlPageAction;
import com.demandware.xlt.util.Page;
import com.demandware.xlt.validators.Validator;
import com.xceptance.xlt.api.util.elementLookup.Results;

/**
 * Agree to terms and conditions (Variant 1).
 */
public class Paypal1Agree extends AbstractHtmlPageAction
{
    /**
     * Select "Accept" button
     * 
     * @return "Accept" button locator
     */
    private static Results getAcceptButton()
    {
        return Page.find().byId("accept.x");
    }

    /**
     * Check if the page has an &quot;Accept&quot; for terms and conditions.
     * 
     * @return <code>true</code> if &quot;Accept&quot; button is found, <code>false</code> otherwise.
     */
    public static boolean isPossible()
    {
        return Page.checkExistance(getAcceptButton());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() throws Exception
    {
        // Clear the browser's Accept-Encoding header. This is necessary for PayPal to process the request correctly.
        getWebClient().addRequestHeader("Accept-Encoding", "");

        // Click "Accept" button.
        loadPageByClick(getAcceptButton().single());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void postValidate() throws Exception
    {
        Validator.validateBasics();
    }
}
