package com.demandware.xlt.actions.storeLocator;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;

import com.demandware.xlt.actions.AbstractHtmlPageAction;
import com.demandware.xlt.util.Context;
import com.demandware.xlt.util.FormUtils;
import com.demandware.xlt.util.Page;
import com.demandware.xlt.validators.Validator;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.xceptance.xlt.api.util.elementLookup.Results;

/**
 * Enter a randomly chosen ZIP code from configuration and search within a provided radius for stores.
 * 
 * @author Daniel Kirst (Xceptance Software Technologies GmbH)
 */

public class FindStoreByZipCode extends AbstractHtmlPageAction
{
    /** Select the search form submit button */
    private static Results getSubmitButton()
    {
        return Page.getZipCodeStoreLocatorForm().byXPath(".//button[@name='dwfrm_storelocator_findbyzip']");
    }

    /** Select the search result rows */
    private static Results getSearchResultRows()
    {
        return Page.getStoreSearchResults();
    }

    /** The radius which is used to limit the search */
    private final String radius;

    /**
     * Requires the user to enter a radius to specify the range of the search.
     * 
     * @param radius
     *            search takes place within a distance of this value
     */
    public FindStoreByZipCode(final String radius)
    {
        this.radius = radius;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() throws Exception
    {
        // Get a ZIP code.
        final String zipcode = getRandomZipCode();

        // Search for stores around the ZIP code.
        searchStoresAround(zipcode);
    }

    /**
     * Performs a store search for the given ZIP code within the previously defined radius.
     * 
     * @param zipCode
     *            the ZIP code to search for
     * @throws Exception
     */
    private void searchStoresAround(final String zipCode) throws Exception
    {
        // Enter the ZIP code and select the radius.
        FormUtils.setInputValueByID("dwfrm_storelocator_postalCode", zipCode);
        FormUtils.selectByID("dwfrm_storelocator_maxdistance", radius);

        // Submit the search form.
        HtmlButton zipCodeSearchSubmitButton = getSubmitButton().asserted("Cannot find zip-code-search submit button.").single();
        loadPageByClick(zipCodeSearchSubmitButton);
    }

    /**
     * Returns a random ZIP code from properties.
     * 
     * @return a random ZIP code
     * @throws FileNotFoundException
     * @throws IOException
     */
    private String getRandomZipCode()
    {
        // Get a random ZIP code.
        final String zipCode = Context.getConfiguration().getRandomZipCode();
        Assert.assertFalse("No zipcode found", StringUtils.isBlank(zipCode));
        return zipCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void postValidate() throws Exception
    {
        Validator.validateCommonPage();

        // The result page should contain a table with 1 to n stores.
        getSearchResultRows().asserted("No store locations found").exists();
    }
}
