package com.demandware.xlt.actions.storeLocator;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;

import com.demandware.xlt.actions.AbstractHtmlPageAction;
import com.demandware.xlt.util.FormUtils;
import com.demandware.xlt.util.Page;
import com.demandware.xlt.validators.Validator;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.xceptance.common.util.RegExUtils;
import com.xceptance.xlt.api.util.elementLookup.Results;

/**
 * Selects a region by country code from a drop-down list and executes a store search for that location.
 * 
 * @author Daniel Kirst (Xceptance Software Technologies GmbH)
 */
public class FindStoreByCountry extends AbstractHtmlPageAction
{
    /** Select the country selection drop-down */
    private static Results getCountryCodeSelect()
    {
        return Page.find().byId("dwfrm_storelocator_address_country");
    }

    /** Select the search submit button */
    private static Results getSubmitButton()
    {
        return Page.find().byId("dwfrm_storelocator_int").byCss("button[name=dwfrm_storelocator_findbycountry]");
    }

    /** Select the search result rows */
    private static Results getSearchResultRows()
    {
        return Page.find().byCss(".store-locator-no-results > p");
    }

    /** Select the result header */
    private static Results getResultHeader()
    {
        return Page.find().byCss("div.store-locator-header");
    }

    /** The country code to use for search */
    private final String countryCode;

    /**
     * Initializes store finder to lookup shops by a randomly chosen country code.
     */
    public FindStoreByCountry()
    {
        this.countryCode = null;
    }

    /**
     * Specify the location of the search.
     * 
     * @param countryCode
     *            the country code to use during search
     */
    public FindStoreByCountry(final String countryCode)
    {
        this.countryCode = countryCode;
    }

    /**
     * Search stores by selecting the country code from a drop-down list and submitting the search form.<br>
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() throws Exception
    {
        selectRegionByValue();
        submitForm();
    }

    /**
     * Selects the previously passed country code from the drop-down list. If the country code is blank a random value
     * is selected.
     */
    private void selectRegionByValue()
    {
        // If the countryCode is blank, select a random option ...
        if (StringUtils.isBlank(this.countryCode))
        {
            FormUtils.selectRandomly(getCountryCodeSelect(), true, false);
        }
        // ... otherwise select the given country
        else
        {
            FormUtils.select(getCountryCodeSelect(), this.countryCode);
        }
    }

    /**
     * Submit the search form
     * 
     * @throws Exception
     */
    private void submitForm() throws Exception
    {
        final HtmlButton button = getSubmitButton().asserted("Cannot find search button").single();
        this.loadPageByClick(button);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void postValidate() throws Exception
    {
        Validator.validateCommonPage();

        // After a search is performed (even if there are 0 results returned) a headline should be displayed.
        final String searchResultText = Page.getPrimaryContainerHeadline();

        // Parse the headline to find the number of results and the full name of the location.
        if (RegExUtils.isMatching(searchResultText, "Your search found(?s).*?\\d+ stores"))
        {
            // Check the country name.
            final String location = getResultHeader().asserted("Cannot find store locator header").single().getTextContent();
            Assert.assertNotNull("Location seems to be empty", location);
        }
        else
        {
            // No stores found -> check for functional error.
            getSearchResultRows().asserted("Store search does neither return results nor does it show the 'no-results'-element")
                                      .exists();
        }
    }
}
