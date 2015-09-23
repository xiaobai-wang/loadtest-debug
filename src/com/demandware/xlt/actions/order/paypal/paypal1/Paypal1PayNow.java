package com.demandware.xlt.actions.order.paypal.paypal1;

import com.demandware.xlt.actions.AbstractHtmlPageAction;
import com.demandware.xlt.util.Context;
import com.demandware.xlt.util.Page;
import com.demandware.xlt.validators.Validator;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.xceptance.xlt.api.util.elementLookup.HPU;

/**
 * Press "Pay Now" button (Variant 1).
 */
public class Paypal1PayNow extends AbstractHtmlPageAction
{
    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() throws Exception
    {
        // Get the confirmation form.
        final HtmlForm form = HPU.findAsserted("Paypal pay button not found").in(Context.getPage())
                                 .byId("parentForm").single();

        // Remove field that don't need to be send with the request.
        form.getInputByName("miniPager").remove();

        // Click the submit button.
        final HtmlElement button = Page.findAsserted().byId("continue_abovefold").first();
        loadPageByClick(button);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void postValidate() throws Exception
    {
        // check we are on the order review page.
        Validator.validateReviewOrderCheckoutStep();
    }
}
