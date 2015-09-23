package com.demandware.xlt.actions.catalog;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;

import com.demandware.xlt.actions.AbstractAjaxAction;
import com.demandware.xlt.util.FlowStoppedException;
import com.demandware.xlt.util.FormUtils;
import com.demandware.xlt.util.Page;
import com.demandware.xlt.util.XHR;
import com.demandware.xlt.validators.Validator;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.xceptance.xlt.api.util.XltRandom;
import com.xceptance.xlt.api.util.elementLookup.Results;

/**
 * Chooses a random product configuration.
 * 
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 */
public class ConfigureProduct extends AbstractAjaxAction
{
    /** While processing the variation attributes we need an upper limit of configuration attempts to not try endless. */
    private static final int SAFETY_BREAK = 15;

    /** Locate attributes that can be chosen by a link. */
    private static Results locateVariationAttributesWithLink()
    {
        return Page.getSingleProductContainerLocator().byCss(".product-variations .attribute")
                   .byXPath("./div/ul[./li[contains(@class,'available') and not(contains(@class,'selected'))]/a]");
    }

    /** Locate attributes that can be chosen by a selection input. */
    private static Results locateVariationAttributeWithSelect()
    {
        return Page.getSingleProductContainerLocator().byCss(".product-variations .attribute.variant-dropdown select.variation-select");
    }

    /** Locate product options. */
    private static Results locateOptions()
    {
        return Page.getSingleProductContainerLocator().byCss(".product-add-to-cart .product-options ul > li .value")
                   .byXPath("./select[option[not(@selected)]]");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() throws Exception
    {
        processAttributesWithLink();

        processAttributesWithSelect();

        // Product Options may be e.g. additional guarantees
        processProductOptions();
    }

    /**
     * Process all variation attribute that can be configured by clicking a link.
     * 
     * @throws Exception
     */
    private void processAttributesWithLink() throws Exception
    {
        // Safety counter. How many attributes do we have parsed?
        int attributesCounter = 0;

        // Remember all processed attributes to not configure them twice.
        final Set<String> processedAttributesWithLink = new HashSet<String>();

        // As long as we have unprocessed attributes ...
        HtmlElement attribute;
        while ((attribute = getUnprocessedAttributeWithLink(processedAttributesWithLink)) != null)
        {
            // Check if we have exceeded the parsing limit.
            if (attributesCounter++ >= SAFETY_BREAK)
            {
                throw new FlowStoppedException("Product attribute configuration seems to be out of control.");
            }

            // Get a random unselected attribute variation.
            final Results attributeVariations = Page.getVariationAttributeWithLinkLocator(attribute);
            if (attributeVariations.exists())
            {
                final HtmlAnchor variation = attributeVariations.random();

                // Request the details via XHR and update the page.
                new XHR().url(variation.getAttribute("href").trim())
                         .param("Quantity", "1")
                         .param("format", "ajax")
                         .replaceContentOf(getProductContentContainer())
                         .fire();
            }
        }
    }

    /**
     * Get an unselected 'Link' attribute (excludes already processed ones).
     * 
     * @return an unselected attribute element
     */
    private HtmlElement getUnprocessedAttributeWithLink(Set<String> processedAttributes)
    {
        // Locate all the 'Link' attributes.
        final List<HtmlElement> attributes = locateVariationAttributesWithLink().all();

        for (final HtmlElement attribute : attributes)
        {
            // Focus on unprocessed attributes only.
            if (processedAttributes.add(attribute.getAttribute("class")))
            {
                return attribute;
            }
        }

        // No such attribute found.
        return null;
    }

    /**
     * Process all variation attribute that can be configured by selection an option of an HTML select box.
     * 
     * @throws Exception
     */
    private void processAttributesWithSelect() throws Exception
    {
        // Safety counter. How many attributes do we have parsed?
        int attributesCounter = 0;

        // Remember all processed attributes to not configure them twice.
        final Set<String> processedSelects = new HashSet<String>();

        // As long as we have unprocessed attribute selects ...
        HtmlSelect select;
        while ((select = getUnprocessedAttributeWithSelect(processedSelects)) != null)
        {
            // Check if we have exceeded the parsing limit.
            if (attributesCounter++ >= SAFETY_BREAK)
            {
                throw new FlowStoppedException("Product attribute configuration seems to be out of control.");
            }

            final List<HtmlOption> options = select.getOptions();

            // If there's nothing to select, skip silently
            if (options.size() <= 1)
            {
                continue;
            }

            // Get the update URL from a random option.
            final HtmlOption option = options.get(XltRandom.nextInt(1, options.size() - 1));
            final String url = option.getValueAttribute();

            // Fire the XHR to get the updated product configuration and update the page.
            new XHR().url(url)
                     .param("Quantity", "1")
                     .param("format", "ajax")
                     .replaceContentOf(getProductContentContainer())
                     .fire();
        }
    }

    /**
     * Get an unselected 'Select' attribute (excludes already processed ones).
     * 
     * @return an unselected attribute element
     */
    private HtmlSelect getUnprocessedAttributeWithSelect(final Set<String> processedSelects)
    {
        // Locate all the 'Link' attributes.
        final List<HtmlSelect> selects = locateVariationAttributeWithSelect().all();

        for (final HtmlSelect select : selects)
        {
            // Focus on unprocessed attributes only.
            final String ID = select.getId();
            if (!processedSelects.contains(ID))
            {
                // save ID that we done that
                processedSelects.add(ID);
                return select;
            }
        }

        return null;
    }

    /**
     * Process all product options.
     * 
     * @throws AssertionError
     */
    private void processProductOptions() throws AssertionError
    {
        // There might be some product options that have to be set (such as warranty).
        final List<HtmlSelect> selects = locateOptions().all();

        for (final HtmlSelect select : selects)
        {
            // get any option, if a selected one and simple set it selected, full randomness
            FormUtils.selectRandomly(select, false, false);
        }
    }

    /**
     * Get the container which contains the product variation attributes from the page.
     * 
     * @return
     */
    private static HtmlElement getProductContentContainer()
    {
        return Page.getSingleProductContainerLocator().asserted("Product content container not found.").first();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void postValidate() throws Exception
    {
        // Check that the result still displays product details.
        Validator.validateCommonPage();
        Assert.assertTrue("Page does not display a single product's details.", Page.isSingleProductDetailPage());
    }
}
