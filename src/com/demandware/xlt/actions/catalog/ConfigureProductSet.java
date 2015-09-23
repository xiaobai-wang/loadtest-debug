package com.demandware.xlt.actions.catalog;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;

import com.demandware.xlt.actions.AbstractAjaxAction;
import com.demandware.xlt.util.AjaxUtils;
import com.demandware.xlt.util.FlowStoppedException;
import com.demandware.xlt.util.Page;
import com.demandware.xlt.util.XHR;
import com.demandware.xlt.validators.Validator;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.xceptance.xlt.api.util.elementLookup.HPU;
import com.xceptance.xlt.api.util.elementLookup.Results;

/**
 * Chooses a random product configuration for each product of the set.
 * 
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 */
public class ConfigureProductSet extends AbstractAjaxAction
{
    /** While processing the variation attributes we need an upper limit of configuration attempts to not try endless. */
    private static final int SAFETY_BREAK = 15;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() throws Exception
    {
        // Gets a list of all products from a product set page.
        final List<HtmlElement> products = getProductSetItemsLocator().asserted("No product list found on product set page.").all();

        // configure each single product
        for (final HtmlElement product : products)
        {
            processAttributesWithLink(product);
            // NOTE: check for variation selection boxes. In standard SiteGenesis not available, but possible. For an
            // example
            // see ConfigureProduct for single product
            processProductOptions(product);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void postValidate() throws Exception
    {
        Validator.validateCommonPage();
        Assert.assertTrue("Page is no valid product set page.", Page.isProductSetPage());
    }

    private void processProductOptions(HtmlElement product) throws AssertionError
    {
        // we might have some product options, we have to set, things like warranties
        final Results productOptions = Page.getProductOptionsLocator(product);
        if (productOptions.exists())
        {
            final List<HtmlSelect> selects = productOptions.all();
            for (int i = 0; i < selects.size(); i++)
            {
                final HtmlSelect select = selects.get(i);

                // get any option, if a selected one and simple set it selected, full randomness
                final HtmlOption option = HPU.findAsserted().in(select).byXPath("option").random();
                option.setSelected(true);
            }
        }
    }

    private void processAttributesWithLink(HtmlElement product) throws Exception
    {
        int attributesCounter = 0;
        HtmlElement attribute;
        final Set<String> processedAttributes = new HashSet<String>();

        while ((attribute = getUnprocessedAttributeWithLink(processedAttributes, product)) != null)
        {
            // Check if the scripts is within the defined limits.
            if (attributesCounter++ >= SAFETY_BREAK)
            {
                throw new FlowStoppedException("Product attribute configuration seems to be out of control.");
            }

            // Should contain something like 'swatches size'. So we know 'size'
            // is processed already.
            processedAttributes.add(attribute.getAttribute("class"));

            // Get a random unselected attribute variation.
            final Results attributeVariations = Page.getVariationAttributeWithLinkLocator(attribute);
            if (attributeVariations.exists())
            {
                final HtmlAnchor variation = attributeVariations.random();

                // Request the details via XHR.
                final String url = AjaxUtils.getAppResourceValue("getSetItem") + "?"
                                   + StringUtils.substringAfter(variation.getAttribute("href"), "?");
                new XHR().url(url.trim())
                         .param("Quantity", "1")
                         .param("format", "ajax")
                         .replaceContentOf(product)
                         .fire();
            }
        }
    }

    /**
     * Get an unselected attribute element except already processed ones.
     * 
     * @param product
     * @return an unselected attribute element except already processed ones
     */
    private HtmlElement getUnprocessedAttributeWithLink(Set<String> processedAttributes, HtmlElement product)
    {
        List<HtmlElement> attributes = getUnselectedAttributesWithLinkFromProductSetPage(product);

        for (HtmlElement attribute : attributes)
        {
            if (!processedAttributes.contains(attribute.getAttribute("class")))
            {
                return attribute;
            }
        }
        return null;
    }

    /**
     * Gets a list of all unselected product variations for a product from a set page.</br><b>An AssertionError is
     * thrown if no product variation is available for this set product.</b>
     * <p>
     * <b>Page:</b> use this on a product set page.
     * </p>
     * 
     * @return all product variations for this product
     * @throws AssertionError
     *             if there is no product variation available for the product on this product set page
     */
    private List<HtmlElement> getUnselectedAttributesWithLinkFromProductSetPage(HtmlElement product)
    {
        return HPU.find().in(product).byCss(".product-set-details .product-variations > ul > li.attribute > div > ul").all();
    }

    /**
     * Get the locator for the single items in the product set.
     */
    private Results getProductSetItemsLocator()
    {
        return Page.getProductSetContainerLocator().byCss(".product-set-item");
    }
}
