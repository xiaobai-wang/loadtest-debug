package com.demandware.xlt.actions.catalog;

import java.util.Random;

import com.demandware.xlt.actions.AbstractHtmlPageAction;
import com.demandware.xlt.util.Page;
import com.demandware.xlt.validators.Validator;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.xceptance.xlt.api.engine.Session;
import com.xceptance.xlt.api.util.elementLookup.Results;

/**
 * Selects a top category from the top navigation menu.
 * 
 * @author Xiaobai Wang
 */
public class SelectTopCategory extends AbstractHtmlPageAction
{

    public static final String baseUrl = "http://shop.gopro.com";

    /**
     * Locate top category links.
     * 
     * @return Locator for top category links
     */
    public static Results getTopCatLocator()
    {
        return Page.find().byId("tophat-products-subnav").byXPath("./div/div/a");
    }

    /**
     * Search and returns a random top category link. </br><b>An AssertionError is thrown if no category link is
     * available on the page.</b>
     * 
     * @return the html element of the top category link
     * @throws AssertionError
     *             if there is no category available on the page
     */
    private static HtmlElement getRandomTopCategoryLink() throws AssertionError
    {
        return getTopCatLocator().asserted("No top category link found!").random();
    }

    /**
     * Chosen top-category.
     */
    // private HtmlElement topCategory;

    private String topCategoryURL;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() throws Exception
    {
        // Get a link from the Page

        // topCategory = getRandomTopCategoryLink();
        // topCategoryURL = "http://shop.gopro.com";

        String topCategoryArray[] =
        {
            "cameras", "mounts", "accessories", "shopbyactivity"
        };

        int idx = new Random().nextInt(topCategoryArray.length);
        String topCategory = topCategoryArray[idx];

        // String topCategory = "mounts"; // Select a specific category for debugging
        // topCategory = RegExUtils.getFirstMatch(getRandomTopCategoryLink().getAttribute("href"), "\\/.+\\/");
        System.out.println("topCategory: " + topCategory);

        topCategoryURL = buildTopCategoryUrl(topCategory);
        // topCategoryURL = topCategoryURL.concat(topCategory);

        // Click the chosen top category.
        // loadPageByClick(topCategory);
        loadPage(topCategoryURL);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void postValidate() throws Exception
    {
        // Basic check - validate a common shop page.
        Validator.validateCommonPage();

        /*
         * Check the result page type. Since we need an expected page type without error message we check the page is
         * not 'unknown' and it's not a 'no results' page.
         * 
         * So allowed are a result grid page, a category landing page, the gift certificates page or even a product
         * details page.
         */
        if (Page.isProductGridPage() || Page.isLandingPage() || Page.isSingleProductDetailPage() || Page.isProductSetPage()
            || Page.isGiftCertificatePage())
        {
            // everything is fine. no further checks are necessary
        }
        else if (Page.isNoHitsPage())
        {
            Session.logEvent(SelectTopCategory.class.getSimpleName() + " - No results page", topCategoryURL);
        }
        else
        {
            Session.logEvent(SelectTopCategory.class.getSimpleName() + " - Unexpected page", topCategoryURL);
        }
    }

    protected String buildTopCategoryUrl(String uri)
    {
        return baseUrl + "/on/demandware.store/Sites-GoPro-Site/default/Search-Show?cgid=" + uri;
    }
}
