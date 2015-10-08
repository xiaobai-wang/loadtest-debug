package com.demandware.xlt.validators;

import org.junit.Assert;

import com.demandware.xlt.actions.catalog.SelectCategory;
import com.demandware.xlt.util.Context;
import com.demandware.xlt.util.Page;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.xceptance.common.util.RegExUtils;
import com.xceptance.xlt.api.engine.Session;
import com.xceptance.xlt.api.util.XltLogger;
import com.xceptance.xlt.api.util.elementLookup.Results;
import com.xceptance.xlt.api.validators.StandardValidator;

public class Validator
{
    /**
     * Validates a page during the checkout. Checks for standard components and performs a validateBasics() check.
     * 
     * @throws Exception
     */
    public static void validateCheckOutPage() throws Exception
    {
        validateBasics();

        // Tests under development and real load test will behave slightly
        // different.
        if (Context.isLoadTest())
        {
            // check if every Element is present
            Assert.assertTrue("Page without footer found", Page.getFooterLocator().exists());
            Assert.assertTrue("Page without header found", Page.hasHeader());
            Assert.assertTrue("Page without main container found", Page.hasMainContainer());
            Assert.assertTrue("Page without primary content container  found", Page.hasPrimaryContentContainer());
        }
        else
        {
            // check if every Element is present and unique
            Page.getFooterLocator().asserted().single();
            Page.find().byXPath("//*[@id='" + Page.NAVIGATION_ID + "']").single();
            Page.find().byXPath("//*[@id='" + Page.MAIN_CONTAINER_ID + "']").asserted().single();
            Page.find().byXPath("//*[@id='" + Page.PRIMARY_CONTENT_CONTAINER_ID + "']").asserted().single();
        }
    }

    /**
     * Validates a common page. Checks for standard components and performs a validateBasics() check.
     * 
     * @throws Exception
     */
    public static void validateCommonPage() throws Exception
    {
        validateBasics();

        // Tests under development and real load test will behave slightly
        // different.
        if (Context.isLoadTest())
        {
            // check if every Element is present
            Assert.assertTrue("Page without footer found", Page.getFooterLocator().exists());
            Assert.assertTrue("Page without header found", Page.hasHeader());
            Assert.assertTrue("Page without main container found", Page.hasMainContainer());
            Assert.assertTrue("Page without mini-cart found", Page.hasMiniCart());
            Assert.assertTrue("Page without search field found", Page.hasSearchField());
        }
        else
        {
            // check if every Element is present and unique
            Page.getFooterLocator().asserted().single();
            Page.find().byXPath("//*[@id='" + Page.NAVIGATION_ID + "']").asserted().single();
            Page.find().byXPath("//*[@id='" + Page.MAIN_CONTAINER_ID + "']").asserted().single();
            Page.find().byXPath("//*[@id='" + Page.MINI_CART_ID + "']").asserted().single();
            Page.find().byXPath("//*[@id='" + Page.SEARCHFIELD_ID + "']").asserted().single();
        }
    }

    /**
     * Validates the basics of the current page.
     * <ul>
     * <li>state 200</li>
     * <li>enclosing html</li>
     * <li>valid xhtml</li>
     * </ul>
     * 
     * @throws Exception
     */
    public static void validateBasics() throws Exception
    {
        // Combined validation.
        // Is the response code 200?
        // Does the length match the header value?
        // Has the page a closing 'html' tag?
        // Use XHTML validator for a global conformity check.
        StandardValidator.getInstance().validate(Context.getPage());
    }

    /**
     * Validates the shopping cart page. Checks for standard components and performs a validateBasics() check.
     * 
     * @throws Exception
     */
    public static void validateShoppingCart() throws Exception
    {
        validateCommonPage();

        // Check the headline.
        // The text replacement for cart.headline can be found in the project.properties file

        Assert.assertTrue("Unexpected shopping cart headline.", Page.isCartPage());
        Boolean isMiniCartEmpty = Page.isMiniCartEmpty();

        if (!isMiniCartEmpty)
        {
            // Check the presence of the cart table that hold the cart items.
            Assert.assertTrue("Cart table not found", Page.checkExistance(Page.getCartTableLocator()));

            Results cartTotals = Page.find().byId("main")
                                     .byXPath("./div[contains(@class, 'cart-order-totals')]/table")
                                     .byXPath("./tbody/tr[@class='order-total']/td[@class='notranslate']");

            Boolean cartTotalsExists = Page.checkExistance(cartTotals);

            // Check the presence of the cart totals.
            Assert.assertTrue("Cart totals not found.", cartTotalsExists);
        }
    }

    /**
     * Validates the shipping page. Checks for standard components, the headline and performs a validateBasics() check.
     * 
     * @throws Exception
     */
    public static void validateShippingCheckoutStep() throws Exception
    {
        validateCheckOutPage();
        validateActiveCheckoutStep("shipping");
    }

    /**
     * Validates the billing page. Checks for standard components, the headline and performs a validateBasics() check.
     * 
     * @throws Exception
     */
    public static void validateBillingCheckoutStep() throws Exception
    {
        validateCheckOutPage();
        validateActiveCheckoutStep("billing");
    }

    /**
     * Validates the review order page. Checks for standard components, the headline, order totals and performs a
     * validateBasics() check.
     * 
     * @throws Exception
     */
    public static void validateReviewOrderCheckoutStep() throws Exception
    {
        validateCheckOutPage();
        validateActiveCheckoutStep("summary");

        // Check order totals
        final String orderTotal = Page.find().byId("summary")
                                      .byXPath("./div[contains(@class, 'currentStep')]")
                                      .byXPath("./div[@class='review-content']/div[@class='order-totals']")
                                      .byXPath("./table/tbody/tr[@class='order-total']")
                                      .byXPath("./td[2]")
                                      .asserted("No order totals found.")
                                      .first().getTextContent().trim();

        Assert.assertTrue("Order totals are not formated correctly: " + orderTotal,
                          RegExUtils.isMatching(orderTotal, Page.PRICE_REGEXP));
    }

    /**
     * Validates the order confirmation page. Checks for standard components, the order number and performs a
     * validateBasics() check.
     * 
     * @throws Exception
     */
    public static void validateOrderConfirmationPage() throws Exception
    {
        validateCheckOutPage();
        final String orderNumber = Page.getPrimaryContentContainerLocator()
                                       .byCss(".order-confirmation-details > .item-list .order-information > .order-number > .value")
                                       .asserted("No order number found on page.").single().getTextContent().trim();
        XltLogger.runTimeLogger.debug("# Order Number: " + orderNumber);
        Assert.assertTrue("Order number not found", RegExUtils.isMatching(orderNumber, "\\d+"));
    }

    /**
     * Validate the specific checkout step text.
     * 
     * @param page
     *            the current page
     * @param stepText
     *            the expected text for the active step.
     */
    private static void validateActiveCheckoutStep(String activeStep)
    {
        // Get the text of the active step.
        Page.find().byId("checkout-steps")
            .byXPath("./div/div[@id='" + activeStep + "' and contains(@class, 'active')]")
            .asserted("Expected active Checkout step '" + activeStep + "' not found.")
            .first();

    }

    /**
     * A category page is an umbrella term here. The validator will accept the page if it is
     * <ul>
     * <li>a product page</li>
     * <li>a no-hits page</li>
     * <li>a landing page</li>
     * <li>a gift certificate page</li>
     * </ul>
     * since clicking a (sub) category link might end in one of these page types.
     * 
     * @param categoryLink
     *            the link which was clicked to reach this page
     * @throws Exception
     */
    public static void validateCategoryPage(final DomElement categoryLink) throws Exception
    {
        // Check it is a common shop page.
        Validator.validateCommonPage();

        // As long as it is not an unknown page the action is fine.
        boolean gridOrProductPage = Page.isGridOrProductPage();
        Assert.assertTrue("Opening category ended in unexpected page.",
                          gridOrProductPage || Page.isNoHitsPage() || Page.isLandingPage() || Page.isGiftCertificatePage());

        // A sub menu link may point to a product results page, a single
        // product, a landing page or the gift certificates page. So just check
        // for unknown page types and 'no results' pages.
        if (gridOrProductPage)
        {
            // we reached a product grid, perfect. no further checks are necessary
        }
        else if (Page.isNoHitsPage())
        {
            // Nevertheless log 'no results' pages.
            Session.logEvent(SelectCategory.class.getSimpleName() + " - No results page", categoryLink.getAttribute("href"));
        }
    }
}
