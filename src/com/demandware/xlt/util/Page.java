package com.demandware.xlt.util;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.xceptance.common.util.RegExUtils;
import com.xceptance.xlt.api.engine.Session;
import com.xceptance.xlt.api.util.XltLogger;
import com.xceptance.xlt.api.util.elementLookup.By;
import com.xceptance.xlt.api.util.elementLookup.HPU;
import com.xceptance.xlt.api.util.elementLookup.Results;

/**
 * Central class to access or retrieve information from the current page.
 * 
 * @author Bernd Weigel (Xceptance Software Technologies GmbH)
 */
public class Page
{
    // private String singleProductLocatorId = "product-content";
    /**
     * Find elements in current page.<br>
     * This is just a shortcut for:<br>
     * <code>HPU.find().in(currentPage)</code>
     */
    public static By find()
    {
        return HPU.find().in(Context.getPage());
    }

    /**
     * Find elements in current page.<br>
     * This is just a shortcut for:<br>
     * <code>HPU.findAsserted().in(currentPage)</code>
     */
    public static By findAsserted()
    {
        return findAsserted(null);
    }

    /**
     * Find elements in current page.<br>
     * This is just a shortcut for:<br>
     * <code>HPU.findAsserted(msg).in(currentPage)</code>
     */
    public static By findAsserted(final String assertionMessage)
    {
        return HPU.findAsserted(assertionMessage).in(Context.getPage());
    }

    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /*
     * Default pages
     */
    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /** The id of the main container. */
    public static final String MINI_CART_ID = "minicart_contents";

    /** Lookup the the mini cart container. */
    public static Results getMiniCartLocator()
    {
        return find().byId(MINI_CART_ID);
    }

    /** The id of the main container. */
    public static final String MAIN_CONTAINER_ID = "main";

    /** Lookup the main container. */
    public static Results getMainLocator()
    {
        return find().byId(MAIN_CONTAINER_ID);
    }

    /** The id of the search field. */
    public static final String SEARCHFIELD_ID = "tophat-search";

    /** Lookup the search field. */
    public static Results getSearchFieldLocator()
    {
        // return find().byId(SEARCHFIELD_ID);
        // return find().byId(SEARCHFIELD_ID).byXPath("./form/input[@type='search']");
        return find().byId(SEARCHFIELD_ID);
    }

    /** The id of the header container. */
    public static final String NAVIGATION_ID = "header";

    /** Lookup the header container. */
    public static Results getHeaderLocator()
    {
        return find().byId(NAVIGATION_ID);
    }

    /** Lookup the footer container. */
    public static Results getFooterLocator()
    {
        // return find().byId("wrapper").byXPath("./div/footer");
        // return find().byId("wrapper").byXPath("./div[@id='footer']");
        return find().byId("footer");
    }

    /** Lookup the footer container. */
    public static Results getTopCategoryFooterLocator()
    {
        return find().byId("footer");
        // return find().byId("wrapper").byXPath("id='footer'");
    }

    /**
     * Returns whether or not the mini cart is empty.
     * 
     * @return <code>true</code> if the mini cart is empty, <code>false</code> otherwise
     */
    public static boolean isMiniCartEmpty()
    {
        return getItemsInMiniCart() == 0;
    }

    /**
     * Returns whether or not the the header is available on the current page.
     * 
     * @return <code>true</code> if the header is available, <code>false</code> otherwise
     */
    public static boolean hasHeader()
    {
        return checkExistance(getHeaderLocator());
    }

    /**
     * Returns whether or not the the search field is available on the current page.
     * 
     * @return <code>true</code> if the search field is available, <code>false</code> otherwise
     */
    public static boolean hasSearchField()
    {
        return checkExistance(getSearchFieldLocator());
    }

    /**
     * Returns whether or not the main container is available on the current page.
     * 
     * @return <code>true</code> if the main container is available, <code>false</code> otherwise
     */
    public static boolean hasMainContainer()
    {
        return checkExistance(getMainLocator());
    }

    /**
     * Returns the main container from the current page. </br><b>An AssertionError is thrown if the container is not
     * available on the page.</b>
     * 
     * @return the main container
     * @throws AssertionError
     *             if the container is not available on the page
     */
    public static HtmlElement getMainContainer() throws AssertionError
    {
        return getMainLocator().asserted().single();
    }

    /**
     * Returns whether or not the the mini cart is available on the current page.
     * 
     * @return <code>true</code> if the mini cart is available, <code>false</code> otherwise
     */
    public static boolean hasMiniCart()
    {
        return checkExistance(getMiniCartLocator());
    }

    /**
     * Get the appResource string.
     * 
     * @return the xml formated appResource string
     * @throws AssertionError
     *             if no appResource is found
     */
    public static String getAppResources() throws AssertionError
    {
        // Try exact lookup first.
        Results appResources = find().byXPath("(id('wrapper')|id('" + MAIN_CONTAINER_ID
                                                  + "'))/script[not(@src) and contains(. ,'appResources')]");
        if (!appResources.exists())
        {
            // If that failed, be a little more fuzzy but also CPU intensive.
            appResources = find().byXPath("/html/body//script[not(@src) and contains(. ,'appResources')]");
            Session.logEvent("app.urls at non expected position. Check for defective HTML.", Context.getPage().getUrl().toExternalForm());
        }

        return appResources.asserted("No AppResources found.").single().getTextContent();
    }

    /**
     * Checks if the current page is a landing page.
     * 
     * @return <code>true</code> if the current page is a landing page, <code>false</code> otherwise
     */
    public static boolean isLandingPage()
    {
        return checkExistance(getPrimaryContentContainerLocator().byCss(".category-main-banner"));
    }

    /**
     * Checks if the current page is a product gift certificate page.
     * 
     * @return <code>true</code> if the current page is a product gift certificate page, <code>false</code> otherwise
     */
    public static boolean isGiftCertificatePage()
    {
        return checkExistance(getPrimaryContentContainerLocator().byCss(".gift-certificate-purchase"));
    }

    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /*
     * Registration
     */
    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * get the create an account now button in the login/signup page (changed in 15.4, no longer register link along
     * header on home page)
     * 
     * @return
     */
    public static Results getRegisterLinkLocator()
    {
        return getHeaderLocator().byXPath("./ul/li/div/div[@class='user-links']/a[@title='Register']");
    }

    /**
     * Get the locator for the registration form.
     * 
     * @return locator for the registration form
     */
    public static Results getRegistrationFormLocator()
    {
        return find().byId("RegistrationForm");
    }

    /**
     * Get the locator for the login link.
     * 
     * @return locator for the login link
     */
    public static final Results getLoginLinkLocator()
    {
        return getHeaderLocator().byXPath("./ul/li[contains(@class, 'user-info')]/a[@class='user-account']");
    }

    /**
     * Get the locator for the account link.
     * 
     * @return locator for the account link.
     */
    public static final Results getAccountLinkLocator()
    {
        return getHeaderLocator().byXPath("./ul/li[contains(@class, 'user-info')]/a[@class='user-account']");
    }

    /**
     * Get the locator for the logout link, in 15.6 -- you will need to go to the Account page to find this.
     * 
     * @return locator for the logout link
     */
    public static final Results getLogoutLinkLocator()
    {
        return getPrimaryContentContainerLocator().byXPath("./h1/span[@class='account-logout']/a[@title='Logout']");
    }

    /**
     * Returns whether or not a customer is currently logged in. The state is determined by checking for the specific
     * login/logout links.
     * 
     * @param page
     *            HTML page to be used
     * @return <code>true</code> if a customer is currently logged in, <code>false</code> otherwise
     */
    public static boolean isCustomerLoggedIn()
    {
        // check if there title on the login link, should be firstname lastname
        String userName = getLoginLinkLocator().asserted("Login link not found").single().getAttribute("title").trim();
        
        
        // Check title for matches <firstname lastname> any case
        boolean isLoggedIn = RegExUtils.isMatching(userName, "([a-zA-Z])\\w+ ([a-zA-Z])\\w+");
        XltLogger.runTimeLogger.debug("# isLoggedIn = " + isLoggedIn);

        return isLoggedIn;
    }

    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /*
     * Product grid pages
     */
    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Checks if the current page is a grid page with no hits/search results page.
     * 
     * @return <code>true</code> if the current page is a no search results page, <code>false</code> otherwise
     */
    public static boolean isNoHitsPage()
    {
        // return
        // checkExistance(getPrimaryContentContainerLocator().byXPath("./div[@class='no-hits-banner' or @class='no-results']"));

        if (getSearchContentContainerLocator().byXPath("./div[@class='search-container']/div[@class='results-background']/div[@class='results products']/div[@class='no-results']") != null)
            return true;
        else
            return false;

    }

    /**
     * Checks if the current page is a product grid page with visible products.
     * 
     * @return <code>true</code> if the current page is a grid page with visible products, <code>false</code> otherwise
     */
    public static boolean isProductGridPage()
    {
        boolean isProdGrid = false;

        String myURL = Context.getPage().getUrl().toString();
        Results productSearchContainer;

        // XltLogger.runTimeLogger.debug("#  -- debug -- Body BEGIN");
        // XltLogger.runTimeLogger.debug(Context.getPage().asXml());
        // XltLogger.runTimeLogger.debug("#  -- debug -- Body END");


        // /div[@class = 'camera-twoX-wrapper')]/div/div[@class = 'twoXCameras')]
        if (myURL.equals("http://shop.gopro.com/cameras"))
        {
            // HtmlElement test1 = find().byId("main").asserted("not found").first();
            // HtmlElement test2 = find().byId("main").byXPath("./div").asserted("not found").first();
            // HtmlElement test3 =
            // find().byId("main").byXPath("./div[@class='camera-landing']").asserted("not found").first();

            productSearchContainer = find().byId("main")
                                           .byXPath("./div[@class='camera-landing']/div[@class='camera-twoX-wrapper']/div/div[@class='twoXCameras']");
            isProdGrid = productSearchContainer.exists();

            XltLogger.runTimeLogger.debug("# isProdGrid = " + isProdGrid);

            return isProdGrid;
        }
        else
        {
            isProdGrid = checkIdExistanceInPage("search-result-items");
            XltLogger.runTimeLogger.debug("# isProdGrid = " + isProdGrid);

            return isProdGrid;
        }
    }

    /** The ID of the primary content container on the product grid page. */
    public static final String PRIMARY_CONTENT_CONTAINER_ID = "primary";

    /**
     * Get the locator for the primary content container on the product grid page.
     *
     * @return locator for the primary content container on the product grid page.
     */
    public static Results getPrimaryContentContainerLocator()
    {
        return find().byId(PRIMARY_CONTENT_CONTAINER_ID);
    }

    /** The ID of the secondary content container on the product grid page. */
    public static final String SECONDARY_CONTENT_CONTAINER_ID = "secondary";

    /**
     * Get the locator for the secondary content container on the product grid page.
     * 
     * @return locator for the secondary content container on the product grid page
     */
    public static Results getSecondaryContentContainerLocator()
    {
        return find().byId(SECONDARY_CONTENT_CONTAINER_ID);
    }

    /**
     * Checks if the secondary content container is available.
     * 
     * @return <code>true</code> if the secondary content is available, <code>false</code> if not
     */
    public static boolean hasSecondaryContentContainer()
    {
        return checkExistance(getSecondaryContentContainerLocator());
    }

    /**
     * Gets the secondary content container.
     * 
     * @return the secondary content container
     * @throws AssertionError
     *             if there is no such container available
     */
    public static HtmlElement getSecondaryContentContainer() throws AssertionError
    {
        return getSecondaryContentContainerLocator().asserted("No secondary content container found").first();
    }

    /**
     * Checks if the primary content container is available.
     * 
     * @return <code>true</code> if the primary content container is available, <code>false</code> if not
     */
    public static boolean hasPrimaryContentContainer()
    {
        return checkExistance(getPrimaryContentContainerLocator());
    }

    /** The ID of the Search content container on the product grid page. */
    public static final String SEARCH_CONTENT_CONTAINER_ID = "site-search-container";

    /**
     * Get the locator for the search content container on the product grid page.
     *
     * @return locator for the search content container on the product grid page.
     */
    public static Results getSearchContentContainerLocator()
    {
        return find().byId(SEARCH_CONTENT_CONTAINER_ID);
    }

    /**
     * Get a random product link from the product grid page.
     * 
     * @return a random product link from the grid
     * @throws AssertionError
     *             if there is no such link available on the page
     */
    public static HtmlElement getRandomProduct() throws AssertionError
    {

        String myURL = Context.getPage().getUrl().toString();
        Results productSearchContainer;

        By myFound = find();

        if (myURL.equals("http://shop.gopro.com/cameras"))
        {
            productSearchContainer = find().byId("main")
                                           .byXPath("./div[@class='camera-landing']/div[@class='camera-twoX-wrapper']/div/div[@class='twoXCameras']")
                                           .byXPath(".//a[contains(@href, 'http')]")
                                           .asserted("No product found.");
        }
        else
        {
            productSearchContainer = myFound.byId("search-result-items")
                                            .byXPath(".//a[contains(@href, 'http')]")
                                            .asserted("No product found");
        }
        /*
         * HtmlElement test1 = siteSearchContainer.asserted("not found").first(); HtmlElement test2 =
         * siteSearchContainer.byXPath("./div[@class='search-container']").asserted("not found").first(); HtmlElement
         * test4 = siteSearchContainer.byXPath("./div[@class='search-container']/div/div[contains(@class,'products')]")
         * .asserted("not found").first(); HtmlElement test5 = siteSearchContainer.byXPath(
         * "./div[@class='search-container']/div/div[contains(@class,'products')]/div[contains(@class,'product-box')]")
         * .asserted("not found").first();
         */

        return productSearchContainer.random();

    }

    /**
     * Get the first product link from the product grid page.
     * 
     * @return the first product link from the grid
     * @throws AssertionError
     *             if there is no such link available on the page
     */
    public static HtmlElement getFirstProduct() throws AssertionError
    {

        String myURL = Context.getPage().getUrl().toString();
        Results productSearchContainer;

        By myFound = find();

        if (myURL.equals("http://shop.gopro.com/cameras"))
        {
            productSearchContainer = find().byId("main")
                                           .byXPath("./div[@class='camera-landing']/div[@class='camera-twoX-wrapper']/div/div[@class='twoXCameras']")
                                           .byXPath(".//a[contains(@href, 'http')]")
                                           .asserted("No product found.");
        }
        else
        {
            productSearchContainer = myFound.byId("search-result-items")
                                            .byXPath(".//a[contains(@href, 'http')]")
                                            .asserted("No product found");
        }
        /*
         * HtmlElement test1 = siteSearchContainer.asserted("not found").first(); HtmlElement test2 =
         * siteSearchContainer.byXPath("./div[@class='search-container']").asserted("not found").first(); HtmlElement
         * test4 = siteSearchContainer.byXPath("./div[@class='search-container']/div/div[contains(@class,'products')]")
         * .asserted("not found").first(); HtmlElement test5 = siteSearchContainer.byXPath(
         * "./div[@class='search-container']/div/div[contains(@class,'products')]/div[contains(@class,'product-box')]")
         * .asserted("not found").first();
         */

        return productSearchContainer.first();

    }

    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /*
     * Product detail pages
     */
    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Get the locator for a single product container.
     * 
     * @return locator for a single product container
     */
    public static Results getSingleProductContainerLocator()
    {
        return find().byId("product-content");
    }

    /**
     * Get the locator for a single product container.
     * 
     * @return locator for a single product container
     */
    public static Results getCameraSingleProductContainerLocator()
    {
        return find().byId("product-content");
    }

    /**
     * Checks if the current page is a single product detail page.
     * 
     * @return <code>true</code> if the current page is a single product detail page, <code>false</code> otherwise
     */
    public static boolean isSingleProductDetailPage()
    {
        return checkExistance(getSingleProductContainerLocator());
    }

    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /*
     * Product set pages
     */
    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Get the locator for a product set container.
     * 
     * @return locator for a product set container
     */
    public static Results getProductSetContainerLocator()
    {
        return find().byId("product-set-list");
    }

    /**
     * Checks if the current page is a product set page.
     * 
     * @return <code>true</code> if the current page is a product set page, <code>false</code> otherwise
     */
    public static boolean isProductSetPage()
    {
        return checkExistance(getProductSetContainerLocator());
    }

    /**
     * Checks if the current page is a product bundle page. A Bundle page is a subset of product set pages without
     * prices or add to cart buttons for every product
     * 
     * @return <code>true</code> if the current page is a product bundle page, <code>false</code> otherwise
     */
    public static boolean isProductBundlePage()
    {
        return checkXPathExistanceInPage("id('pdpMain')/div[contains(@class,'product-col-2')]/form[contains(@class,'bundle')]");
    }

    /**
     * Checks if the current page is a single product detail page, a product set page, a quickview page, or a product
     * grid page.
     * 
     * @return <code>true</code> if the current page is a product or grid page, <code>false</code> otherwise
     */
    public static boolean isGridOrProductPage()
    {
        return isSingleProductDetailPage() || isProductGridPage() || isProductSetPage() || isProductBundlePage();
    }

    /**
     * Checks price, availability and the add-to-cart button of all available products on the product page.</br><b>NOTE:
     * this will work on a product page only. On other pages this method will return <code>false</code>.</b>
     * <p>
     * <b>Page:</b> use this on a product detail, product set or quickview page.
     * </p>
     * 
     * @return <code>true</code> if all products are available, <code>false</code> otherwise
     */
    public static boolean isProductAvailable()
    {
        Boolean isProductOnSinglePageAvailable = isProductOnSinglePageAvailable();
        Boolean isProductOnBundlePageAvailable = isProductOnBundlePageAvailable();
        Boolean isProductOnSetPageAvailable = isProductOnSetPageAvailable();

        return isProductOnSinglePageAvailable || isProductOnBundlePageAvailable || isProductOnSetPageAvailable;
    }

    /**
     * Checks price, availability and the button of all available products on a bundle page product page.</br><b>NOTE:
     * this will work only on a product bundle page. Other pages will return false.</b>
     * <p>
     * <b>Page:</b> use this on a product bundle page.
     * </p>
     * 
     * @return <code>true</code> if all products are available, <code>false</code> otherwise
     */
    private static boolean isProductOnBundlePageAvailable()
    {
        if (!isProductBundlePage())
        {
            // Not a bundle page? Then a bundle cannot be available.
            return false;
        }

        // Bundle pages have no price or add-to-cart button for every product. So the availability message of each item
        // has to be checked as only property.
        final List<HtmlElement> products = getProductSetContainerLocator().asserted("No products found on Page.")
                                                                          .byCss(".product-set-item").all();
        for (final HtmlElement product : products)
        {
            // Get the set item's availability message.
            final HtmlElement availability = HPU.find().in(product)
                                                .byCss("div.product-set-details > .availability > .value > .availability-msg").asserted()
                                                .single();

            // Check item is marked as 'In Stock'
            if (!Context.getLanguageSpecificText("product.availabilityMsg").equals(availability.getTextContent().trim()))
            {
                return false;
            }
        }

        // Check that the Add(-All)-To-Cart button is not disabled.
        return checkXPathExistanceInPage("(id('add-to-cart')|id('add-all-to-cart'))[not(@disabled)]");
    }

    /**
     * Checks price, availability and the add-to-cart button of all available products on a product set page.
     * 
     * @return <code>true</code> if all products are available, <code>false</code> otherwise
     */
    private static boolean isProductOnSetPageAvailable()
    {
        if (!isProductSetPage())
        {
            // No a product set page? Then product set cannot be available.
            return false;
        }

        // Get all product set items.
        final List<HtmlElement> products = getProductSetContainerLocator().asserted("No products found on Page.")
                                                                          .byCss(".product-set-item").all();

        // For each item check price, availability message, and add-to-cart button.
        for (final HtmlElement product : products)
        {
            // Let the current item be the base of all element selections.
            final By findInProduct = HPU.find().in(product);

            // Get the set item's price and check that the price is valid (e.g. not N/A)
            final HtmlElement priceElement = findInProduct.byCss("div.add-sub-product > form > .product-price > .price-sales").single();
            String priceElementTextContent = priceElement.getTextContent().trim();
            if (priceElement == null || !RegExUtils.isMatching(priceElementTextContent, PRICE_REGEXP))
            {
                Session.logEvent("Product has no or invalid price", Context.getPage().getUrl().toExternalForm());
                return false;
            }

            // Check set item's add-to-cart button is not disabled.
            final HtmlElement addToCartButton = findInProduct.byCss("div.add-sub-product > form > .add-to-cart").asserted().single();
            if (addToCartButton.hasAttribute("disabled"))
            {
                return false;
            }

            // Check item is marked as 'In Stock'.
            // final HtmlElement availability =
            // findInProduct.byCss("div.product-set-details > .availability > .value > .availability-msg")
            // .asserted().single();
            HtmlElement availability = null;
            try
            {
                availability = findInProduct.byCss("div.add-sub-product")
                                            .byXPath("./form/input[@name='availability' and @value='IN_STOCK']")
                                            .asserted()
                                            .single();
            }
            catch (Throwable e)
            { // Log the failed attempt of the browsing round.
                XltLogger.runTimeLogger.debug("# Exception Msg: " + e.getMessage());
            }
            // if
            // (!Context.getLanguageSpecificText("product.availabilityMsg").equals(availability.getTextContent().trim()))
            if (availability == null)
            {
                return false;
            }
        }

        // Check that the Add-(All-)To-Cart button is not disabled.
        // if (!checkXPathExistanceInPage("id('add-all-to-cart')[not(@disabled)]"))
        if (!checkXPathExistanceInPage("//button[contains(@class, 'add-all-to-cart') and not(@disabled)]"))
        {
            return false;
        }

        return true;
    }

    /**
     * Checks price, availability and the add-to-cart button of a single product on a product details page or quickview
     * page.
     * 
     * @return <code>true</code> if the product is available, <code>false</code> otherwise
     */
    public static boolean isProductOnSinglePageAvailable()
    {
        if (!isSingleProductDetailPage())
        {
            // Not a single product page? Then it cannot be available.
            return false;
        }

        // Get product add-to-cart area and lookup elements just inside it.
        String myURL = Context.getPage().getUrl().toString();
        Results productSearchContainer;

        if (myURL.contains("http://shop.gopro.com/hero4"))
        {
            productSearchContainer = find().byId("pdpMain").asserted("No product found.");

            XltLogger.runTimeLogger.debug("# -- debug -- Body BEGIN");
            XltLogger.runTimeLogger.debug(Context.getPage().asXml());
            XltLogger.runTimeLogger.debug("# -- debug -- Body END");

            XltLogger.runTimeLogger.debug("# Page output from url: " + myURL);
            HtmlElement t4 = null;

            try
            {
                HtmlElement t1 = find().byId("pdpMain").asserted("No product found.").single();
                HtmlElement t2 = find().byId("pdpMain")
                                       .byXPath("./div[@class='pdpTop']/div[@class='fixed']")
                                       .asserted("No product found.")
                                       .single();

                HtmlElement t3 = find().byId("pdpMain")
                                       .byXPath("./div[@class='pdpTop']/div[@class='fixed']/div[contains(@class, 'product-col-2')]")
                                       .asserted("No product found.")
                                       .single();

                t4 = find().byId("pdpMain")
                           .byXPath("./div[@class='pdpTop']/div[@class='fixed']/div[contains(@class, 'product-col-2')]")
                           .byXPath("./div[@class='product-add-to-cart'])]")
                                       .asserted("No product found.")
                                       .single();

            }
            catch (Throwable e)
            {

                // Log the failed attempt of the browsing round.
                XltLogger.runTimeLogger.debug("# Exception Msg: " + e.getMessage());
            }

            final By findInProduct = HPU.find().in(t4);
            final Results priceElement = findInProduct.byCss("form>fieldset>.product-price>.price-sales>.price-sales");

            if (!priceElement.exists())
            {
                Session.logEvent("Product has no or invalid price", Context.getPage().getUrl().toExternalForm());
                return false;
            }

        } // else // {

        // productSearchContainer = find().byId("product-content")
        // .byXPath(".//a[contains(@href, 'http')]")
        // .asserted("No product found");
        // }

        /*
         * final HtmlElement product = getSingleProductContainerLocator().asserted("No product found on Page")
         * .byCss(".product-add-to-cart") .single(); final By findInProduct = HPU.find().in(product);
         * 
         * // Get the product's price and check if it's of a valid format. final Results priceElement =
         * findInProduct.byCss("form > fieldset > .product-price > .price-sales"); if (!priceElement.exists() ||
         * !RegExUtils.isMatching(priceElement.single().getTextContent().trim(), PRICE_REGEXP)) {
         * Session.logEvent("Product has no or invalid price", Context.getPage().getUrl().toExternalForm()); return
         * false; }
         * 
         * // Get the product's availability message and check if it is marked as 'In Stock'. No availability message is
         * // also valid. final Results currentAvailabilityMsg =
         * findInProduct.byCss("form > fieldset .availability > .value > .availability-msg"); final HtmlElement
         * currentAvailabilityMsgEle = currentAvailabilityMsg.first(); final String currentAvailabilityMsgText =
         * currentAvailabilityMsgEle.getTextContent().trim(); // final String expectedAvailabilityMessage =
         * Context.getLanguageSpecificText("product.availabilityMsg"); // final String expectedAvailabilityMessage =
         * "Availability: In Stock";
         * 
         * if (currentAvailabilityMsg.exists() &&
         * !currentAvailabilityMsgText.equals("Availability: In Stock\nUsually ships within 24 hrs")) {
         * Session.logEvent("Availability message does not match: " + currentAvailabilityMsgText,
         * Context.getPage().getUrl().toExternalForm()); return false; }
         * 
         * // Check that the Add(-All)-To-Cart button is not disabled. if
         * (!checkXPathExistanceInPage("id('add-to-cart')[not(@disabled)]")) {
         * Session.logEvent("Add-to-cart button is disabled.", Context.getPage().getUrl().toExternalForm()); return
         * false; }
         */
        return true;
    }

    /**
     * Get locator of product variation attribute links.
     * 
     * @param attribute
     *            variation attribute (e.g. for color or size) to get the links for
     * @return variation attribute links
     */
    public static Results getVariationAttributeWithLinkLocator(final HtmlElement attribute)
    {
        return HPU.find().in(attribute).byXPath("./li[contains(@class,'available') and not(contains(@class,'selected'))]/a");
    }

    /**
     * Get the locator that targets at the product's options (such as warranty).
     * 
     * @param product
     *            product details container
     * @return locator that targets at the product's options
     */
    public static Results getProductOptionsLocator(final HtmlElement product)
    {
        return HPU.find().in(product).byCss(".product-set-details > .product-options .value").byXPath("./select[option[not(@selected)]]");
    }

    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /*
     * Mini cart
     */
    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /** Regular expression to match a price such as "Cart (1) Item, Total: $899.99" or "899,99€" */
    public static final String PRICE_REGEXP = "\\d{1,3}([,.]\\d{3})*[,.]\\d{2}";

    /**
     * Get the totals from the mini cart.
     * 
     * @return the mini cart totals
     * @throws AssertionError
     *             if no mini cart was found
     */
    public static String getMiniCartTotal() throws AssertionError
    {
        // Get the mini cart link.
        final HtmlElement minicartLink = getMiniCartLocator().byCss(".mini-cart-total > a.mini-cart-link")
                                                             .asserted("No mini cart totals found on the page.").single();

        // Extract the text.
        final String wcItemsText = minicartLink.getTextContent().trim();

        // Return the text snippet that matches the price pattern.
        return RegExUtils.getFirstMatch(wcItemsText, PRICE_REGEXP);
    }

    /**
     * Search and returns the number of items in the mini cart. </br><b>An AssertionError is thrown if the cart is not
     * available on the page.</b>
     * 
     * @return the number of items in the mini cart
     * @throws AssertionError
     *             if there is no mini cart available on the page
     */
    public static int getItemsInMiniCart() throws AssertionError
    {
        // Get text content of cart total summary.
        final String cartContent = getMiniCartLocator().byCss("div.mini-cart-total a.mini-cart-link span.minicart-quantity")
                                                       .asserted("No valid mini-cart value found.").single().getTextContent().trim();

        // There should be a number that represents the amount of cart items such as "Cart (3)"
        final String countRaw = RegExUtils.getFirstMatch(cartContent, "\\d+");
        if (StringUtils.isNotBlank(countRaw))
        {
            try
            {
                return Integer.valueOf(countRaw);
            }
            catch (final Exception e)
            {
                XltLogger.runTimeLogger.error("Unable to parse Integer from: " + cartContent);
            }
        }

        return 0;
    }

    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /*
     * Cart Page
     */
    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Get the headline of the primary container from the current page.
     * 
     * @return the headline for the current primary container
     * @throws AssertionError
     *             if no headline is available
     */
    public static String getPrimaryContainerHeadline() throws AssertionError
    {
        return getPrimaryContentContainerLocator().byXPath("./h1").asserted("No headline or primary container found on the page.").single()
                                                  .getTextContent().trim();
    }

    /**
     * Get the locator for the cart table.
     * 
     * @return locator for the cart table
     */
    public static Results getCartTableLocator()
    {
        return find().byId("cart-table");
    }

    /** Cart item row inside cart table. */
    private static final String CART_ROW_CSS = "tbody > tr.cart-row";

    /**
     * Get the number of cart items from the cart page.
     * 
     * @return number of cart items
     * @throws AssertionError
     *             if the current page is not the cart page or the page does not contain the cart table
     */
    public static int getCartItemCount() throws AssertionError
    {
        final HtmlElement cart = getCartTableLocator().asserted("No cart table found.").single();
        return HPU.find().in(cart).byCss(CART_ROW_CSS).count();
    }

    /**
     * Is the cart empty or are there any items listed. This works on a cart page only.
     * 
     * @return <code>true</code> if cart is empty, <code>false</code> otherwise
     */
    public static boolean isCartPageCartEmpty()
    {
        return getCartTableLocator().byCss(CART_ROW_CSS).count() == 0;
    }

    /**
     * Get the locator for cart rows containing an item that is not in stock.
     * 
     * @return locator for cart rows containing an item that is not in stock
     */
    public static Results getOutOfStockCartItems()
    {
        return Page.getCartTableLocator()
                   .byXPath("./tbody/tr[@class='cart-row' and ./td[@class='item-quantity-details' and not(./ul[@class='product-availability-list']/li[@class='is-in-stock' and .='"
                                + Context.getLanguageSpecificText("product.availabilityMsg") + "'])]]");
    }

    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /*
     * General methods
     */
    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Checks if an element with the given XPath exists on the current page. Remembers the result for later statistics
     * output.
     * 
     * @param xPath
     *            XPath to check
     * @return <code>true</code> if an element with the given XPath exists on the current page, <code>false</code>
     *         otherwise
     */
    public static boolean checkXPathExistanceInPage(final String xPath)
    {
        // Lookup element
        final boolean exists = find().byXPath(xPath).exists();

        // Remember result
        Context.addXPathToNoHitCheck(xPath, exists);

        return exists;
    }

    /**
     * Checks if an element with the given ID exists on the current page. Remembers the result for later statistics
     * output.
     * 
     * @param id
     *            ID to check
     * @return <code>true</code> if an element with the given ID exists on the current page, <code>false</code>
     *         otherwise
     */
    public static boolean checkIdExistanceInPage(final String id)
    {
        // Lookup element
        boolean exists = find().byId(id).exists();

        // Remember result
        Context.addIdToNoHitCheck(id, exists);

        return exists;
    }

    /**
     * Checks if an element with the given CSS selector exists on the current page. Remembers the result for later
     * statistics output.
     * 
     * @param css
     *            CSS selector to check
     * @return <code>true</code> if an element with the given CSS selector exists on the current page,
     *         <code>false</code> otherwise
     */
    public static boolean checkCssExistanceInPage(final String css)
    {
        // Lookup element
        boolean exists = find().byCss(css).exists();

        // Remember result
        Context.addXPathToNoHitCheck(css, exists);

        return exists;
    }

    /**
     * Checks if an element with the given locator exists on the current page. Remembers the result for later statistics
     * output.
     * 
     * @param locator
     *            locator to check
     * @return
     */
    public static boolean checkExistance(final Results locator)
    {
        // Lookup element
        boolean exists = locator.exists();

        // Remember result
        Context.addNoHitCheck(locator, exists);

        return exists;
    }

    /**
     * Checks if the current page is a PayPal page. This is done by checking the page URL.
     * 
     * @return <code>true</code> if the current page is detected as PayPal page.
     */
    public static boolean isPaypalPage()
    {
        return Context.getPage().getUrl().getHost().contains("paypal");
    }

    /**
     * Get the login form locator.
     * 
     * @return login form locator
     */
    public static Results getLoginFormLocator()
    {
        return Page.find().byId("dwfrm_login");
    }

    /**
     * Get the locator for the account creation form.
     * 
     * @return locator for the account creation form
     */
    public static Results getCreateAccountFormLocator()
    {
        return Page.find().byId("dwfrm_login_register");
    }

    /**
     * Check if the current page is the cart page
     * 
     * @return <code>true</code> if the current page is the cart page, <code>false</code> otherwise
     */
    public static boolean isCartPage()
    {
        return checkExistance(Page.find().byId("cart-items-form"))
               || checkExistance(getPrimaryContentContainerLocator().byCss(".cart-empty"));
    }

    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /*
     * Store locator pages
     */
    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    // TODO: Find a proper name
    /**
     * Get the store locator form to run a zipcode search.</br><b>An AssertionError is thrown if no form available.</b>
     * 
     * @return the store locator form as <code>HtmlElement</code>
     * @throws AssertionError
     *             if the current page has no store locator zipcode search form
     */
    public static Results getZipCodeStoreLocatorForm() throws AssertionError
    {
        return Page.find().byId("dwfrm_storelocator").asserted("No form for zipcode search found");
    }

    /**
     * Get all rows of the table which contains the results after a store search was performed. If no table is available
     * an AssertionError is thrown.
     * 
     * @return all rows from the result table
     * @throws AssertionError
     */
    public static Results getStoreSearchResults() throws AssertionError
    {
        return Page.find().byId("store-location-results").byXPath("./tbody/tr").asserted("No result table entries found");
    }
    
}
