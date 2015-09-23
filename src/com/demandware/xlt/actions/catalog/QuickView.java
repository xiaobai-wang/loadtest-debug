package com.demandware.xlt.actions.catalog;

import org.junit.Assert;

import com.demandware.xlt.actions.AbstractAjaxAction;
import com.demandware.xlt.util.Context;
import com.demandware.xlt.util.Page;
import com.demandware.xlt.util.XHR;
import com.demandware.xlt.validators.Validator;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.xceptance.xlt.api.util.HtmlPageUtils;
import com.xceptance.xlt.api.util.elementLookup.Results;

/**
 * Open the quick view page for a randomly chosen product.
 * 
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 */
public class QuickView extends AbstractAjaxAction
{
    /** The quick view container's ID. */
    public static final String QUICKVIEW_CONTAINER_ID = "QuickViewDialog";

    private static final Results getQuickViewLocator()
    {
        return Page.find().byId(QUICKVIEW_CONTAINER_ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() throws Exception
    {
        // Get a random products link URL.
        final HtmlElement randomProduct = Page.getRandomProduct();

        // Ensure that a quick view section is present.
        final HtmlElement quickviewContainer;
        if (!Page.checkExistance(getQuickViewLocator()))
        {
            // Create quick view container if necessary.
            quickviewContainer = HtmlPageUtils.createHtmlElement("div", Context.getPage().getBody());
            quickviewContainer.setAttribute("id", QUICKVIEW_CONTAINER_ID);
        }
        else
        {
            // Get the existing quick view container.
            quickviewContainer = getQuickViewLocator().first();
        }

        // Quick view URLs have attached certain parameters. Prepare the URL and request the quick view data to update
        // the page.
        new XHR().url(randomProduct.getAttribute("href").trim())
                 .param("source", "quickview")
                 .param("format", "ajax")
                 .replaceContentOf(quickviewContainer)
                 .fire();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void postValidate() throws Exception
    {
        // Check a product is shown in quick view mode.
        Validator.validateCommonPage();

        Assert.assertTrue("Page is no QuickView page",
                          Page.isProductGridPage()
                              && (Page.isSingleProductDetailPage() || Page.isProductSetPage() || Page.isProductBundlePage()));

        Assert.assertTrue("This not a quick view page since the underlaying product grid view is missing.", Page.isProductGridPage());
    }
}
