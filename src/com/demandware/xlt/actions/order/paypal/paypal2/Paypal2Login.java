package com.demandware.xlt.actions.order.paypal.paypal2;

import java.util.Map;

import org.json.JSONObject;
import org.junit.Assert;

import com.demandware.xlt.actions.AbstractAjaxAction;
import com.demandware.xlt.util.AjaxUtils;
import com.demandware.xlt.util.Context;
import com.demandware.xlt.util.FormUtils;
import com.demandware.xlt.util.Page;
import com.demandware.xlt.util.PaypalAccount;
import com.demandware.xlt.util.XHR;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.xceptance.xlt.api.util.HtmlPageUtils;
import com.xceptance.xlt.api.util.elementLookup.Results;

/**
 * Login to PayPal (Variant 2)
 */
public class Paypal2Login extends AbstractAjaxAction
{
    /**
     * Select the login form.
     * 
     * @return login form locator
     */
    public static Results getPaypalLoginFormLocator()
    {
        return Page.find().byId("loginForm");
    }

    /**
     * Is the expected login form contained?
     * 
     * @return <code>true</code> if the login form can be found, <code>false</code> otherwise.
     */
    public static boolean isPossible()
    {
        return getPaypalLoginFormLocator().exists();
    }

    /** JSON returned on login. */
    private JSONObject loginJson;

    /** PayPal account. */
    private final PaypalAccount paypalAccount;

    /**
     * Create the PayPal login action.
     * 
     * @param paypalAccount
     *            PayPal account
     */
    public Paypal2Login(final PaypalAccount paypalAccount)
    {
        this.paypalAccount = paypalAccount;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() throws Exception
    {
        // Get the login form.
        final HtmlForm loginForm = getPaypalLoginFormLocator().single();

        // Fill login credentials (email address and password).
        FormUtils.setInputValueByID("email", paypalAccount.getEmail());
        FormUtils.setInputValueByID("password", paypalAccount.getPassword());

        // Collect all form fields and add other required information.
        final Map<String, String> params = AjaxUtils.serializeForm(loginForm);
        params.put("bp_ks1",
                   "v=1;l=10;Di0:7195Ui0:64Di1:144Ui1:72Di2:441Ui2:71Di3:439Ui3:89Di4:330Ui4:62Di5:96Ui5:87Di6:96Di7:3Ui6:38Ui7:1Dk8:639Dk8:499Dk8:33Dk8:34Dk8:35Dk8:32Dk8:32Dk8:34Dk8:33Dk8:31Dk8:33Dk8:33Dk8:33Uk8:4Di8:485Ui8:64Di9:128Ui9:73Di10:183Ui10:88Di11:112Ui11:103Di12:287Ui12:34Di13:107Ui13:85Di14:88Ui14:40Di15:314Ui15:63Di16:121Ui16:64Di17:114Ui17:272");
        params.put("bp_ks2", "");
        params.put("bp_ks3", "");
        params.put("bp_mid", "v=1;a1=na~a2=na~a3=na~a4=Mozilla~a5=Netscape~a6=5.0 (Windows)~a7=20100101~a8=na~a9=true~a10=Windows NT 6.1; WOW64~a11=true~a12=Win32~a13=na~a14=Mozilla/5.0 (Windows NT 6.1; WOW64; rv:31.0) Gecko/20100101 Firefox/31.0~a15=false~a16=en-US~a17=na~a18=www.sandbox.paypal.com~a19=na~a20=na~a21=na~a22=na~a23=1680~a24=1050~a25=24~a26=1010~a27=na~a28=Fri Sep 05 2014 18:45:37 GMT+0200~a29=2~a30=na~a31=yes~a32=na~a33=yes~a34=no~a35=no~a36=yes~a37=no~a38=online~a39=no~a40=Windows NT 6.1; WOW64~a41=no~a42=no~");
        params.put("flow_name", "login");
        params.put("fso_enabled", "14");

        // TODO check if adding the header before removing them for the XHR is necessary

        // Prepare browser's request headers.
        getWebClient().addRequestHeader("Accept", "application/json, text/javascript, */*; q=0.01");
        getWebClient().addRequestHeader("X-CSRF-Token", Context.getPage().getBody().getAttribute("data-token"));

        // Send the XHR.
        final WebResponse r = new XHR().url(loginForm.getActionAttribute())
                                       .POST()
                                       .postParams(params)
                                       .removeHeader("X-CSRF-Token")
                                       .removeHeader("Accept")
                                       .fire();

        // Remember the response.
        loginJson = new JSONObject(r.getContentAsString());

        // TODO check if resetting the email field is necessary
        HtmlPageUtils.setInputValue(loginForm, "email", "");

        Page.findAsserted().byXPath("//input[@name='incontext']").single().remove();
        Page.findAsserted().byXPath("//input[@name='password']").single().remove();
    }

    public JSONObject getLoginJson()
    {
        return loginJson;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void postValidate() throws Exception
    {
        Assert.assertNotNull("No login JSON response available.", loginJson);
    }
}
