package com.demandware.xlt.actions.order;

import java.net.URL;
import java.util.List;
import java.util.Map;

import org.junit.Assert;

import com.demandware.xlt.actions.AbstractAjaxAction;
import com.demandware.xlt.util.AjaxUtils;
import com.demandware.xlt.util.Context;
import com.demandware.xlt.util.Page;
import com.demandware.xlt.util.XHR;
import com.demandware.xlt.validators.Validator;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.xceptance.xlt.api.util.XltLogger;

/**
 * Adds the currently shown product or more specifically one of its variations to the cart.
 * 
 * @author Xiaobai Wang
 */
public class AddToCart extends AbstractAjaxAction
{
    /**
     * Number of initial cart items count. The cart may contain the same item with quantity > 1
     */
    private int total_items_count_in_cart;

    /**
     * Search and return a list of all add to cart forms of a product page. In case of a single product page this is
     * just 1 form. Product set pages usually have several.</br>
     * <p>
     * <b>Page:</b> use this on a product detail, product set or quick view page.
     * </p>
     * 
     * @return a list of add to cart forms
     * @throws AssertionError
     *             if there is no add to cart form available on the page
     */
    public static List<HtmlForm> getAddToCartForms() throws AssertionError
    {
        // Single Product Page
        // Get the form of a single product page. It is the most likely product page type so it's queried first.
        List<HtmlForm> forms = Page.getSingleProductContainerLocator().byCss(".product-add-to-cart > form[id]").all();
        if (forms.isEmpty())
        {
            // Product Set Page
            // If we did not find an add-to-cart form let's try to assume a product-set page and query its add-to-cart
            // forms.
            forms = Page.getProductSetContainerLocator()
                        .byCss("div[id].product-set-item > .block-add-to-cart  > form[id]")
                        .all();
            if (forms.isEmpty())
            {
                // Product Bundle Page
                // We have found an add-to-cart form neither on a single product nor on a product set page. So it might
                // be a product bundle page.
                forms = Page.find().byId("pdpMain").byCss(".product-col-2 > form.bundle").all();
            }
        }

        Assert.assertFalse("No add-to-cart form found in page.", forms.isEmpty());

        return forms;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() throws Exception
    {
        // Remember the number of cart items.
        this.total_items_count_in_cart = Page.getNumItemsInMiniCartByAjax();

        // Get the add-to-cart form(s).
        final List<HtmlForm> addToCartForms = getAddToCartForms();
        for (final HtmlForm form : addToCartForms)
        {
            // Collect the form's parameters.
            final Map<String, String> params = AjaxUtils.serializeForm(form);

            // Get the Add-To-Cart URL.
            final String url = AjaxUtils.getAppResourceValue("addProduct");

            // Update the minicart.
            final HtmlElement minicart = Page.getMiniCartLocator().asserted("No mini cart found on page.").single();

            // Send request by GET on product set pages and POST on single product pages.
            final XHR xhr = new XHR().url(url).param("format", "ajax");
            if (Page.isSingleProductDetailPage())
            {
                xhr.POST().postParams(params);
            }
            else
            {
                xhr.GET().params(params);
            }
            xhr.replaceContentOf(minicart).fire();

        } // for

    } // doExecute

    /**
     * {@inheritDoc}
     */
    @Override
    protected void postValidate() throws Exception
    {
        // Check if add-to-cart was successful
        // This can be achieved by comparing the number of cart items, the cart totals that have to be higher, or any
        // other characteristic. In this case we use the cart item count.
        URL itemURL = Context.getPage().getUrl();
        int CurrentNumItemsInCart = Page.getNumItemsInMiniCartByAjax();
        XltLogger.runTimeLogger.debug("CurrentNumItemsInCart = " + CurrentNumItemsInCart);
        XltLogger.runTimeLogger.debug("total_items_count_in_cart = " + total_items_count_in_cart);

        Assert.assertTrue("Item: " + itemURL + " was not added to cart",
                          CurrentNumItemsInCart > total_items_count_in_cart);

        // Check if we are still on a Product Page
        Validator.validateCommonPage();
        Boolean singleProdDetailPage = Page.isSingleProductDetailPage();
        Boolean productSetPage = Page.isProductSetPage();

        Assert.assertTrue("Page is no valid product page.", singleProdDetailPage || productSetPage);
    }
}
