package com.demandware.xlt.extra.tests;

import java.util.Set;

import org.junit.Assert;

import com.demandware.xlt.actions.Homepage;
import com.demandware.xlt.actions.order.ViewCart;
import com.demandware.xlt.flows.AddToCartFlow;
import com.demandware.xlt.flows.VisitFlow;
import com.demandware.xlt.tests.AbstractTestCase;
import com.demandware.xlt.util.Context;
import com.demandware.xlt.util.Page;
import com.gargoylesoftware.htmlunit.util.Cookie;

/**
 * Runs an add to cart scenarios but leaves and returns later on and of course expects a restored cart.
 * 
 * @author Rene Schwietzke (Xceptance Software Technologies GmbH)
 * 
 */
public class TReturningAddToCart extends AbstractTestCase
{
    @Override
    public void test() throws Throwable
    {
        new VisitFlow().run();
        // fill the cart in session A
        new AddToCartFlow().run();

        new ViewCart().run();

        // preserve item count and totals

        final int itemCount = Page.getCartItemCount();
        final String totals = Page.getMiniCartTotal();

        // remember last session to make sure it changes
        final String oldSessionID = Context.getPreviousAction().getWebClient().getCookieManager().getCookie("dwsid").getValue();

        // rescue the cookie around the cart
        Set<Cookie> cookies = Context.getPreviousAction().getWebClient().getCookieManager().getCookies();

        Cookie anonymousCookie = null;
        for (Cookie cookie : cookies)
        {
            if (cookie.getName().startsWith("dwanonymous_"))
            {
                anonymousCookie = cookie;
                break;
            }
        }
        Assert.assertNotNull(anonymousCookie);

        // finish session
        Context.getPreviousAction().closeWebClient();

        // restart the visit
        Homepage homepage = new Homepage(Context.getConfiguration().getStartURL());

        // put the cookie back
        homepage.getWebClient().getCookieManager().addCookie(anonymousCookie);

        // run it
        homepage.run();

        // make sure we are new
        final String newSessionID = Context.getPreviousAction().getWebClient().getCookieManager().getCookie("dwsid").getValue();
        Assert.assertFalse("SessionIDs match, but should not.", oldSessionID.equals(newSessionID));

        // cart should be back!

        // get item count and totals
        final int newSessionItemCount = Page.getCartItemCount();
        final String newSessionTotals = Page.getMiniCartTotal();

        Assert.assertEquals("Item count not the same", itemCount, newSessionItemCount);
        Assert.assertEquals("Totals not the same", totals, newSessionTotals);

        // fill the cart in session B
        new AddToCartFlow().run();
    }
}
