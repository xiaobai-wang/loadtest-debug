package com.demandware.xlt.actions.storeLocator;

import org.junit.Assert;

import com.demandware.xlt.actions.AbstractHtmlPageAction;
import com.demandware.xlt.util.Context;
import com.demandware.xlt.util.Page;
import com.demandware.xlt.util.XHR;
import com.demandware.xlt.validators.Validator;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import com.xceptance.common.util.RegExUtils;
import com.xceptance.xlt.api.util.elementLookup.HPU;

/**
 * Compares two addresses of one store. Therefore it is required to perform a store search (like FindStoreBy*** ) first.
 * The first store address is extracted from the table of the result-page, the second address is retrieved by following
 * a link to the detail-view-dialog.
 * 
 * @author Daniel Kirst (Xceptance Software Technologies GmbH)
 */
public class VerifyStoreAddress extends AbstractHtmlPageAction
{
    /** The address from the result table. */
    private String resultsStoreAddress;

    /**
     * {@inheritDoc}
     * 
     * @throws Exception
     */
    @Override
    protected void doExecute() throws Exception
    {
        // Get the search result rows.
        final HtmlTableRow storeRow = Page.getStoreSearchResults().random();
        
        // Extract the initial store address
        resultsStoreAddress = HPU.find().in(storeRow).byCss("td.store-address").asserted("Store address not found.").single()
                               .getTextContent();

        // Then get the link to the corresponding details view.
        final String tableDetailLink = HPU.find().in(storeRow).byCss("td.store-information div.store-name a[href]")
                                          .asserted("Could not find link to shop details.").single().getAttribute("href");

        // Open the details view. Request the information by XHR and append the returned content to the end of the body.
        new XHR().url(tableDetailLink).param("format", "ajax").appendTo(Context.getPage().getBody()).fire();
    }

    @Override
    protected void postValidate() throws Exception
    {
        Validator.validateCommonPage();

        // Get the second address from the detail view.
        final String detailViewStoreAddress = Page.find().byCss(".store-locator-details > p > strong")
                                             .asserted("Could not find shop-address on details page.").single().getTextContent();

        // Remove any whitespaces.
        final String addressOne = RegExUtils.removeAll(resultsStoreAddress, "\\s");
        final String addressTwo = RegExUtils.removeAll(detailViewStoreAddress, "\\s");

        // Compare the two addresses (ignore the case).
        Assert.assertTrue("Stores appear to have different addressess.", addressOne.equalsIgnoreCase(addressTwo));
    }
}
