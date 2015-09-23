package com.demandware.xlt.actions;

import org.apache.commons.lang.RandomStringUtils;
import org.json.JSONObject;
import org.junit.Assert;

import com.demandware.xlt.util.Page;
import com.demandware.xlt.util.SearchOption;
import com.demandware.xlt.util.XHR;
import com.demandware.xlt.validators.Validator;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.xceptance.xlt.api.util.XltRandom;

/**
 * Enter a given search phrase in the site's search bar and submit.
 * 
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 */
public class Search extends AbstractHtmlPageAction
{
    /** Search phrase. */
    private final String phrase;

    /** Search form. */
    private HtmlForm searchForm;

    /** Search option ({@link SearchOption#HITS} or {@link SearchOption#NO_HITS} ). */
    private final SearchOption searchOption;

    /**
     * Constructor.
     * 
     * @param phrase
     *            search phrase
     * @param searchOption
     *            search option specifies if search results are expected or not
     */
    public Search(final String phrase, final SearchOption searchOption)
    {
        this.searchOption = searchOption;
        this.phrase = phrase;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() throws Exception
    {
        // Fill the search form with the given phrase and add a hidden field
        // containing an always changing value to bypass the query cache.
        /*
         * Results searchFormLocator =
         * Page.getSearchFieldLocator().byXPath("./..").asserted("No search form found on page."); searchForm =
         * searchFormLocator.single(); FormUtils.setInputValue(searchFormLocator, phrase);
         * HtmlPageUtils.createInput(searchForm, "hidden", "fake", getFake());
         */

        // Submit the search.
        // loadPageByFormSubmit(searchForm);

        String searchUrl = "http://gopro.com/site-search.json"; // need to create a function to find searchUrl in
                                                           // header.configure json string
        // searchUrl = searchUrl + "?q=" + phrase; // temp for debugging

        // AJAX Call for the search. This is problematic because the response is a json blob
        /*
         * new XHR().url(searchUrl) .param("lang", "en_US") .param("q", phrase) .replaceContentOf(unknown_htmlElement)
         * //ajax call to search returns json blob, not sure how to parse that into the page. .fire();
         */

        WebResponse r = new XHR().url(searchUrl)
                 .param("lang", "en_US")
                 .param("q", phrase)
                 .fire();

        // String ajaxResponse = Context.getPage().asXml();
        // JSONObject json = new JSONObject(ajaxResponse);

        // XltLogger.runTimeLogger.debug("Search Ajax -- debug -- Body BEGIN");
        // XltLogger.runTimeLogger.debug(r.getContentAsString());
        // XltLogger.runTimeLogger.debug("Search Ajax -- debug -- Body END");

        JSONObject json = new JSONObject(r.getContentAsString());

        // int num_items = json.length();
        // System.out.println("num_items = " + num_items);
        // JSONArray elementsArray = json.names();

        JSONObject prod = json.getJSONObject("products");
        int prod_count = prod.getInt("count");
        System.out.println("prod_count = " + prod_count);

        if (prod_count != 0)
            System.out.println("Found");
        else
            System.out.println("Not Found");

        // XltLogger.runTimeLogger.debug("# -- debug -- Body BEGIN");
        // XltLogger.runTimeLogger.debug(ajaxResponse);
        // XltLogger.runTimeLogger.debug("# -- debug -- Body END");

        // loadPage(searchUrl);
        // XltLogger.runTimeLogger.debug("# postValidate() -- debug -- Body BEGIN");
        // XltLogger.runTimeLogger.debug(Context.getPage().getWebResponse().getContentAsString());
        // XltLogger.runTimeLogger.debug("# postValidate() -- debug -- Body END");
    }

    /**
     * Get a random search phrase that does not result in a hit.
     * 
     * @return random search phrase that does not result in a hit
     */
    private static String getFake()
    {
        return RandomStringUtils.randomAlphanumeric(XltRandom.nextInt(5, 10));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void postValidate() throws Exception
    {
        // Check the basics.
        Validator.validateCommonPage();

        // Check that the desired option result was achieved.
        switch (searchOption)
        {
            case HITS:
                Assert.assertTrue("Expected at least one hit for '" + phrase + "'", Page.isGridOrProductPage());
                break;

            case NO_HITS:
                Assert.assertTrue("Search phrase '" + phrase + "' should result in no hits", Page.isNoHitsPage());
                break;

            default:
                Assert.fail("Unknown search option");
                break;
        }
    }
}
