package com.demandware.xlt.actions.catalog;

import java.net.URL;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;

import com.demandware.xlt.actions.AbstractHtmlPageAction;
import com.demandware.xlt.util.AjaxUtils;
import com.demandware.xlt.util.Context;
import com.demandware.xlt.util.Page;
import com.demandware.xlt.util.XHR;
import com.demandware.xlt.validators.Validator;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.xceptance.xlt.api.util.XltLogger;
import com.xceptance.xlt.api.util.elementLookup.Results;

/**
 * Open the product detail page for a randomly chosen product.
 * 
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 */
public class ProductDetailView extends AbstractHtmlPageAction
{
    /**
     * On product pages there might be a navigation such as 'Next Product' or 'Previous Product'. This method returns
     * the locator for the navigation section.
     * 
     * @return the product navigation locator
     */
    private static Results getProductNavigationLocator()
    {
        return Page.find().byId("product-nav-container");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() throws Exception
    {
        // XltLogger.runTimeLogger.debug("# -- debug -- Body BEGIN");
        // XltLogger.runTimeLogger.debug(Context.getPage().asXml());
        // XltLogger.runTimeLogger.debug("# -- debug -- Body END");

        // Remember a random product's link URL.
        // final HtmlElement randomProduct = Page.getRandomProduct();
        final HtmlElement firstProduct = Page.getFirstProduct();

        // The 'real' product link is a combination of the current URL
        // parameters (eventually modified by refinement actions) and product
        // specific parts. So the product URL must get updated first.
        final URL currentUrl = Context.getPage().getUrl();
        XltLogger.runTimeLogger.debug("# Current URL: " + currentUrl.toString());
        // final String hash = AjaxUtils.extend(currentUrl.getRef(), currentUrl.getQuery());

        // Call the product URL.
        // randomProduct.setAttribute("href", randomProduct.getAttribute("href") + hash);
        // loadPageByClick(randomProduct);
        loadPageByClick(firstProduct);

        // Load the product navigation.
        if (getProductNavigationLocator().exists())
        {
            productNav();
        }
    }

    /**
     * Perform product navigation AJAX calls.
     * 
     * @param page
     *            the current page
     * @param prodNavUrl
     *            the product navigation call URL
     * @throws Exception
     */
    public static void productNav() throws Exception
    {
        final HtmlPage page = Context.getPage();

        // remember the URL hash part
        final String hash = page.getUrl().getRef();

        final Results pidElement = Page.find().byId("pid");

        // Element that will receive the product navigation update.
        final Results navContainerResults = getProductNavigationLocator();

        // Skip if no hash, no PID exists, or no navigation container exists.
        if (StringUtils.isNotEmpty(hash) && pidElement.exists() && navContainerResults.exists())
        {
            // Look up PID.
            final String pid = pidElement.single().getAttribute("value").trim();
            Assert.assertFalse(StringUtils.isBlank(pid));

            // Build the navigation URL.
            final String prodNavUrl = AjaxUtils.getAppResourceValue("productNav");
            final String url = new StringBuilder().append(prodNavUrl)
                                                  .append("?format=ajax&pid=").append(pid)
                                                  .append("&").append(hash).toString();

            // Perform the XHR call and update the page.
            new XHR().url(url)
                     .replaceContentOf(navContainerResults.first())
                     .fireFrom(page);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void postValidate() throws Exception
    {
        // Check it is a product detail page.
        Validator.validateCommonPage();
        Assert.assertTrue("Page is no valid product page.",
                          Page.isSingleProductDetailPage() || Page.isProductSetPage());
    }
}
