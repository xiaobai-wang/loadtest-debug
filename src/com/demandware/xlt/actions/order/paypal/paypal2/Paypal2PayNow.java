package com.demandware.xlt.actions.order.paypal.paypal2;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.junit.Assert;

import com.demandware.xlt.actions.AbstractHtmlPageAction;
import com.demandware.xlt.util.AjaxUtils;
import com.demandware.xlt.util.Context;
import com.demandware.xlt.util.XHR;
import com.demandware.xlt.validators.Validator;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.xceptance.xlt.api.util.HtmlPageUtils;

/**
 * Press "Pay Now" button.
 */
public class Paypal2PayNow extends AbstractHtmlPageAction
{
    /** Login response from previous action. */
    private final JSONObject loginJson;

    /**
     * Create "Pay Now" action.
     * 
     * @param loginJson
     *            Login response from previous action
     */
    public Paypal2PayNow(final JSONObject loginJson)
    {
        super();
        this.loginJson = loginJson;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() throws Exception
    {
        // Collect parameters.
        final Map<String, String> params = new HashMap<String, String>();
        params.put("_csrf", loginJson.getJSONObject("data").getString("_csrf"));
        params.put("_eventId_submit", "");
        params.put("bp_ks1", "");
        params.put("bp_ks2", "");
        params.put("bp_ks3", "");
        params.put("bp_mid", "");
        params.put("execution", loginJson.getJSONObject("data").getString("flowExecutionKey"));
        params.put("token", getPaypalToken());

        // Send the XHR.
        final WebResponse r = new XHR().url(loginJson.getJSONObject("data").getString("flowExecutionUrl"))
                                       .POST()
                                       .postParams(params)
                                       .fire();

        // Create flow control link that points back to the DW system.
        final JSONObject json2 = new JSONObject(r.getContentAsString());
        final HtmlElement a = HtmlPageUtils.createHtmlElement("a", Context.getPage().getBody());
        a.setAttribute("href", json2.getJSONObject("data").getJSONObject("flowControl").getString("location"));

        // Click that link.
        loadPageByClick(a);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void postValidate() throws Exception
    {
        // Check we are on the order review page.
        Validator.validateReviewOrderCheckoutStep();
    }

    /**
     * Parse PayPal token from URL.
     * 
     * @return PayPal token
     */
    private static String getPaypalToken()
    {
        final String token = AjaxUtils.parseUrlParams(Context.getPage().getUrl().getQuery()).get("token");
        Assert.assertNotNull("Could not find PayPal token", token);
        return token;
    }
}
