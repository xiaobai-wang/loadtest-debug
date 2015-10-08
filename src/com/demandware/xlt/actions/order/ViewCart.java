package com.demandware.xlt.actions.order;

import com.demandware.xlt.actions.AbstractHtmlPageAction;
import com.demandware.xlt.util.Page;
import com.demandware.xlt.validators.Validator;
import com.gargoylesoftware.htmlunit.html.HtmlElement;

/**
 * Access the cart page.
 * 
 * @author Xiaobai Wang
 */
public class ViewCart extends AbstractHtmlPageAction
{
    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() throws Exception
    {
        // Get mini cart link.
        final HtmlElement cartLink = Page.getMiniCartLocator().byCss("a.mini-cart-link").asserted("No link to view the cart found.")
                                         .single();
        // Click it.
        loadPageByClick(cartLink);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void postValidate() throws Exception
    {
        // Check it is the cart page.
        Validator.validateShoppingCart();
    }
}
