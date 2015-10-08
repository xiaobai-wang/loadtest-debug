package com.demandware.xlt.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Assert;

import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.xceptance.common.util.RegExUtils;
import com.xceptance.xlt.api.actions.AbstractHtmlPageAction;
import com.xceptance.xlt.api.util.XltRandom;
import com.xceptance.xlt.api.util.elementLookup.By;
import com.xceptance.xlt.api.util.elementLookup.HPU;

/**
 * Ajax utility class.
 * 
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 */
public final class AjaxUtils
{
    // Some generic parameters used for the analytics call.
    private static final Map<String, String> analyticParamsCommon = new HashMap<String, String>();
    static
    {
        analyticParamsCommon.put("res", "1600x1200");
        analyticParamsCommon.put("cookie", "1");
        analyticParamsCommon.put("cmpn", "");
        analyticParamsCommon.put("java", "0");
        analyticParamsCommon.put("gears", "0");
        analyticParamsCommon.put("fla", "0");
        analyticParamsCommon.put("ag", "0");
        analyticParamsCommon.put("dir", "0");
        analyticParamsCommon.put("pct", "0");
        analyticParamsCommon.put("pdf", "0");
        analyticParamsCommon.put("qt", "0");
        analyticParamsCommon.put("realp", "0");
        analyticParamsCommon.put("tz", "US/Eastern");
        analyticParamsCommon.put("wma", "1");
        analyticParamsCommon.put("dwac", "0.7869769714444649");
        analyticParamsCommon.put("pcat", "new-arrivals");
    }

    /**
     * Default constructor. Declared private to prevent external instantiation.
     */
    private AjaxUtils()
    {
        // Nothing to do
    }

    /**
     * Transform the given parameter list to an URL conform parameter string.
     * 
     * @param parameters
     *            parameters to transform
     * @return an URL parameter string
     */
    static String paramsToQueryString(final Map<String, String> parameters) throws Exception
    {
        final ArrayList<org.apache.http.NameValuePair> arr = new ArrayList<org.apache.http.NameValuePair>();
        for (final Map.Entry<String, String> param : parameters.entrySet())
        {
            arr.add(new BasicNameValuePair(param.getKey(), param.getValue()));
        }

        return URLEncodedUtils.format(arr, "UTF-8");
    }

    /**
     * Get the form's fields as list of name value pairs.
     * 
     * @param form
     *            the form to parse
     * @return the form's fields as list of name value pairs.
     */
    public static Map<String, String> serializeForm(final HtmlForm form)
    {
        return serializeForm(form, true);
    }

    /**
     * Get the form's fields as list of name value pairs.
     * 
     * @param form
     *            the form to parse
     * @param includeLostChildren
     *            whether or not to include the form's lost children
     * @return the form's fields as list of name value pairs.
     */
    public static Map<String, String> serializeForm(final HtmlForm form, final boolean includeLostChildren)
    {
        final Map<String, String> children = new HashMap<String, String>();

        for (final Object o : form.getByXPath(".//select|.//input"))
        {
            handleElement((HtmlElement) o, children);
        }

        if (includeLostChildren)
        {
            for (final HtmlElement e : form.getLostChildren())
            {
                handleElement(e, children);
            }
        }

        return children;
    }

    /**
     * Get the form elements wrapped by the given element as list of name-value pairs.
     * 
     * @param element
     *            the element that holds the form elements
     * @return list of name-value pairs
     */
    public static Map<String, String> serialize(final HtmlElement element)
    {
        final Map<String, String> children = new HashMap<String, String>();

        for (final Object o : element.getByXPath(".//select|.//input"))
        {
            handleElement((HtmlElement) o, children);
        }

        return children;
    }

    /**
     * @param element
     * @param children
     */
    private static void handleElement(final HtmlElement element, final Map<String, String> children)
    {
        if (element instanceof HtmlInput)
        {
            final HtmlInput input = (HtmlInput) element;

            final String nameAtt = input.getNameAttribute();
            if (StringUtils.isEmpty(nameAtt))
            {
                return;
            }
            final String valueAtt = input.getValueAttribute();

            String typeAtt = input.getTypeAttribute().toLowerCase();
            // fall-back to HTML4 input type
            if (!RegExUtils.isMatching(typeAtt, "^text|password|checkbox|radio|submit|reset|file|hidden|image|button$"))
            {
                typeAtt = "text";
            }

            boolean add = false;
            if (("radio".equals(typeAtt) || "checkbox".equals(typeAtt)) && input.hasAttribute("checked"))
            {
                add = input.hasAttribute("checked");
            }
            else
            {
                add = "text".equals(typeAtt) || "hidden".equals(typeAtt) || "password".equals(typeAtt);
            }

            if (add)
            {
                children.put(nameAtt, valueAtt);
            }
        }
        else if (element instanceof HtmlSelect)
        {
            final HtmlSelect select = (HtmlSelect) element;
            final String nameAtt = select.getNameAttribute();
            if (StringUtils.isEmpty(nameAtt))
            {
                return;
            }

            for (final HtmlOption option : select.getSelectedOptions())
            {
                children.put(nameAtt, option.getValueAttribute());
            }
        }
    }

    /**
     * Update shipping methods on given page (GetApplicableShippingMethods call).
     * 
     * @param page
     *            the current page
     * @throws Exception
     */
    public static void getApplicableShippingMethods(final HtmlPage page) throws Exception
    {
        // Extract the URL.
        final String url = getAppResourceValue("shippingMethodsJSON");

        // Collect the parameters.
        final Map<String, String> parameters = getShippingAddressParams(page);

        // Perform the call.
        new XHR().url(url)
                 .params(parameters)
                 .expectJsonArray()
                 .fireFrom(page);
    }

    /**
     * Update shipping methods on current page (GetApplicableShippingMethods call).
     * 
     * @throws Exception
     */
    public static void getApplicableShippingMethods() throws Exception
    {
        getApplicableShippingMethods(Context.getPage());
    }

    /**
     * Update the order summary section on given page (UpdateShippingMethodsList call).
     * 
     * @param page
     *            the current page
     * @throws Exception
     */
    public static void updateSummary(final HtmlPage page) throws Exception
    {
        // Extract the URL.
        final String url = getAppResourceValue("summaryRefreshURL");

        // Perform the call and update the page
        new XHR().url(url)
                 .replaceContentOf(Page.getSecondaryContentContainer())
                 .fireFrom(page);
    }

    /**
     * Update the order summary section on current page (UpdateShippingMethodsList call).
     * 
     * @throws Exception
     */
    public static void updateSummary() throws Exception
    {
        updateSummary(Context.getPage());
    }

    /**
     * Update shipping methods on given page (UpdateShippingMethodsList call).
     * 
     * @param page
     *            the current page
     * @throws Exception
     */
    public static void updateShippingMethodsList(final HtmlPage page) throws Exception
    {
        // Get the url.
        final String url = getAppResourceValue("shippingMethodsList");

        // Collect the parametetrs.
        final Map<String, String> parameters = getShippingAddressParams(page);

        // Update the page.
        final HtmlElement shippingMethodForm = Page.find().byId("shipping-method-list")
                                                   .asserted("No list of shipping methods found on page.").first();

        // Perform the call and update the page.
        new XHR().url(url)
                 .params(parameters)
                 .replaceContentOf(shippingMethodForm)
                 .fireFrom(page);
    }

    /**
     * Update shipping methods on current page (UpdateShippingMethodsList call).
     * 
     * @throws Exception
     */
    public static void updateShippingMethodsList() throws Exception
    {
        updateShippingMethodsList(Context.getPage());
    }

    /**
     * Collect the parameters for the shipping page's XHR requests.
     * 
     * @param page
     *            the current page
     * @return the collected parameters
     */
    private static Map<String, String> getShippingAddressParams(final HtmlPage page)
    {

        String myURL = Context.getPage().getUrl().toString();
        // XltLogger.runTimeLogger.debug("# getShippingAddressParams -- debug -- Body BEGIN");
        // XltLogger.runTimeLogger.debug("HtmlPage = " + page.toString());
        // XltLogger.runTimeLogger.debug("myURL = " + myURL);
        // XltLogger.runTimeLogger.debug(Context.getPage().asXml());
        // XltLogger.runTimeLogger.debug("# getShippingAddressParams -- debug -- Body END");

        final By findInPage = HPU.findAsserted().in(page);

        // // Look up address1.
        final String address1 = findInPage.byId("dwfrm_singleshipping_shippingAddress_addressFields_address1")
                                          .<HtmlInput> first().getValueAttribute();

        // Look up country code.
        final String countryCode = findInPage.byId("dwfrm_singleshipping_shippingAddress_addressFields_country")
                                             .<HtmlSelect> first().getSelectedOptions().get(0)
                                             .getValueAttribute();

        // Look up state code.
        final String stateCode = findInPage.byId("dwfrm_singleshipping_shippingAddress_addressFields_states_state")
                                           .<HtmlSelect> first().getSelectedOptions().get(0)
                                           .getValueAttribute();

        // Look up city.
        final String city = findInPage.byId("dwfrm_singleshipping_shippingAddress_addressFields_city")
                                      .<HtmlInput> first().getValueAttribute();

        // Look up ZIP.
        final String postalCode = findInPage.byId("dwfrm_singleshipping_shippingAddress_addressFields_zip")
                                            .<HtmlInput> first().getValueAttribute();

        // Summarize parameters.
        final Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("address1", address1);
        parameters.put("countryCode", countryCode);
        parameters.put("stateCode", stateCode);
        parameters.put("postalCode", postalCode);
        parameters.put("city", city);

        return parameters;
    }

    /**
     * Request search suggestions for the given phrase via XHR call.
     * 
     * @param page
     *            the current page
     * @param phrase
     *            the phrase to get the suggestions for
     * @throws Exception
     *             is something bad happens
     */
    public static void searchSuggest(final HtmlPage page, final String phrase)
        throws Exception
    {
        // Lookup the search suggestion URL. It is located in an embedded script
        // that contains the 'app resources'. The desired URL is extracted by a
        // precompiled regular expression pattern.
        final String url = getAppResourceValue("searchsuggest");

        // Since a human mostly enters a search phrase slow enough to present
        // some search suggestion while still typing, we simulate this behavior
        // for the test case. So we split the search phrase and request the
        // suggestions for the partial search phrase.
        final int phraseLength = phrase.length();
        if (phraseLength > 3)
        {
            int snippetLength = 0;
            do
            {
                // Calculate new length.
                snippetLength += XltRandom.nextInt(3, 6);
                // Adjust length if necessary.
                snippetLength = snippetLength > phraseLength ? phraseLength : snippetLength;
                // Get the snippet.
                final String snippet = phrase.substring(0, snippetLength);
                // Get the suggestion(s). Add a 'fake' parameter with altering
                // value for each call to bypass caching.
                new XHR().url(url)
                         .param("q", snippet)
                         .param("fake", RandomStringUtils.randomAlphanumeric(XltRandom.nextInt(5, 10)))
                         .fireFrom(page);
            }
            while (snippetLength < phraseLength);
        }
    }

    /**
     * Request search suggestions for the given phrase via XHR call. This requests will be based on the current page.
     * 
     * @param phrase
     *            the phrase to get the suggestions for
     * @throws Exception
     *             is something bad happens
     */
    public static void searchSuggest(final String phrase)
        throws Exception
    {
        searchSuggest(Context.getPage(), phrase);
    }

    /**
     * Get the value for the given key from the app-resources data. Key value is expected to be of format
     * 
     * <pre>
     * "key":"value"
     * </pre>
     * 
     * @param key
     *            the key to look up
     * @return the value for the given key from the app-resources data
     * @throws AssertionError
     *             if no value was found for the given key
     */
    public static String getAppResourceValue(final String key) throws AssertionError
    {
        final String value = RegExUtils.getFirstMatch(Context.getAppResources(), "\"" + key + "\":\"([^\"]+?)\"", 1);
        Assert.assertNotNull("Failed to get app resources value for key: " + key, value);
        return value;
    }

    /**
     * Since paging, sorting, refining, and changing the page size behave in the way that they just call a given URL
     * with attached 'format=ajax' parameter this method will attach this parameter, perform the call, and update the
     * page.
     * 
     * @param page
     *            the current page
     * @param ajaxBaseUrl
     *            the URL to call as it is referenced in the element to click.
     * @throws Exception
     *             if anything goes wrong.
     */
    public static void callAndUpdate(final HtmlPage page, final String ajaxBaseUrl) throws Exception
    {
        // Call the AJAX URL.
        final WebResponse response = new XHR().url(ajaxBaseUrl)
                                              .param("format", "ajax")
                                              .replaceContentOf(Page.getMainContainer())
                                              .fireFrom(page);

        // Update the URL hash.
        AjaxUtils.updateUrlHash(page, response);

        // remember the current Page in the context
        Context.setCurrentPage(page);
    }

    /**
     * Since paging, sorting, refining, and changing the page size behave in the way that they just call a given URL
     * with attached 'format=ajax' parameter this method will attach this parameter, perform the call, and update the
     * current page.
     * 
     * @param ajaxBaseUrl
     *            the URL to call as it is referenced in the element to click.
     * @throws Exception
     *             if anything goes wrong.
     */
    public static void callAndUpdate(final String ajaxBaseUrl) throws Exception
    {
        callAndUpdate(Context.getPage(), ajaxBaseUrl);
    }

    /**
     * Updates the URL hash according to <code>app.js</code>.<br>
     * Usually called from AJAX actions like sort/itemsPerPage/refine/..
     * 
     * @param page
     * @param response
     *            last web response (if any; might be emty or <code>null</code> ).
     * @throws MalformedURLException
     */
    public static void updateUrlHash(final HtmlPage page, final WebResponse response) throws MalformedURLException
    {
        // get current URL
        final URL currentUrl = page.getUrl();
        final String hash = AjaxUtils.extend(currentUrl.getRef(), parseUrlParams(response.getWebRequest().getUrl()
                                                                                         .getQuery()));

        // update hash and build updated URL
        String urlString = currentUrl.toString();
        int hashIndex = urlString.lastIndexOf("#");
        if (hashIndex > 0)
        {
            urlString = urlString.substring(0, hashIndex);
        }
        final URL updatedURL = new URL(new StringBuilder().append(urlString).append(hash).toString());

        // set updated URL
        page.getWebResponse().getWebRequest().setUrl(updatedURL);
    }

    /**
     * Updates the URL hash on current page according to <code>app.js</code>.<br>
     * Usually called from AJAX actions like sort/itemsPerPage/refine/..
     * 
     * @param response
     *            last web response (if any; might be emty or <code>null</code> ).
     * @throws MalformedURLException
     */
    public static void updateUrlHash(final WebResponse response) throws MalformedURLException
    {
        updateUrlHash(Context.getPage(), response);
    }

    /**
     * Returns an updated URL hash built from given hash parameters and query string parameters.
     * 
     * @param hashParams
     *            hash parameters
     * @param qsParams
     *            query string parameters
     * @return updated URL hash built from given hash parameters and query string parameters
     */
    public static String extend(final String hashParams, final String qsParams)
    {
        return extend(hashParams, parseUrlParams(qsParams));
    }

    /**
     * Returns an updated URL hash built from given URL hash parameters and query string parameters.
     * 
     * @param hashParams
     *            URL hash parameters
     * @param qsParams
     *            query string parameters
     * @return updated URL hash built from given URL hash parameters and query string parameters
     */
    public static String extend(final String hashParams, final Map<String, String> qsParams)
    {
        final Map<String, String> params = new HashMap<String, String>();
        // remove format parameter if present
        qsParams.remove("format");

        // Parse hash.
        params.putAll(parseUrlParams(hashParams));
        // Parse query string.
        params.putAll(qsParams);
        // Set start parameter.
        if (!params.containsKey("start"))
        {
            params.put("start", "1");
        }

        // Summarize parameters and transform them to a hash string.
        final StringBuilder sb = new StringBuilder();
        sb.append("#");
        for (final Map.Entry<String, String> pair : params.entrySet())
        {
            sb.append(pair.getKey()).append("=").append(pair.getValue()).append("&");
        }

        String result = sb.toString();

        // remove trailing ampersand.
        if (result.endsWith("&"))
        {
            result = result.substring(0, result.length() - 1);
        }

        return result;
    }

    /**
     * Transform parameter string to key value map.
     * 
     * @param params
     *            hash/query parameters
     * @return parameter map
     */
    public static Map<String, String> parseUrlParams(final String params)
    {
        // Initialize
        final Map<String, String> parsedParams = new HashMap<String, String>();
        // IS there something to do?
        if (params != null)
        {
            // Split at ampersand an iterate over the resulting parts. Split
            // each part at the equal sign what gives the key value pair for a
            // map entry.
            final String[] pairs = params.split("&");
            for (int i = 0; i < pairs.length; i++)
            {
                final String[] pair = pairs[i].split("=");
                if (pair.length == 2)
                {
                    parsedParams.put(pair[0], pair[1]);
                }
            }
        }

        return parsedParams;
    }

    /**
     * Perform an analytics call with static parameters.
     * 
     * @param page
     *            the current page
     * @param previousAction
     *            previous action
     * @throws Exception
     */
    public static void loadAnalytics(final HtmlPage page, final AbstractHtmlPageAction previousAction) throws Exception
    {
        // Check if an analytics script is present.
        final List<?> analyticsScripts = page.getByXPath("(/html/body/script)[last() and contains(.,'__Analytics-Tracking')]");
        for (final Object o : analyticsScripts)
        {
            final HtmlElement analyticsScript = (HtmlElement) o;

            // Get Analytics base URL.
            final String analyticsUrl = RegExUtils.getFirstMatch(analyticsScript.getTextContent(),
                                                                 "var\\s+trackingUrl\\s*=\\s*\"([^\"]+?)\"", 1);
            Assert.assertTrue("Failed to get app resource value.", StringUtils.isNotBlank(analyticsUrl));

            final URL pageURL = page.getUrl();
            final Map<String, String> params = new HashMap<String, String>();

            params.put("url", pageURL.toExternalForm());
            params.putAll(analyticParamsCommon);

            params.put("title", page.getTitleText());
            params.put("fake", Long.toString(System.nanoTime()));

            if (previousAction != null)
            {
                params.put("ref", previousAction.getHtmlPage().getUrl().toExternalForm());
            }
            else
            {
                params.put("ref", "");
            }

            final WebRequest request = new WebRequest(
                                                      page.getFullyQualifiedUrl(new StringBuilder(analyticsUrl).append("?")
                                                                                                               .append(paramsToQueryString(params))
                                                                                                               .toString()));
            request.setAdditionalHeader("Accept", "image/png,image/*;q=0.8,*/*;q=0.5");
            request.setAdditionalHeader("Accept-Encoding", "gzip, deflate");
            request.setAdditionalHeader("Referer", pageURL.toExternalForm());

            // Perform the call.
            final WebResponse r = page.getWebClient().loadWebResponse(request);
            Assert.assertEquals("Analytics not load", 200, r.getStatusCode());
        }
    }

    /**
     * Perform an analytics call with static parameters on the current page.
     * 
     * @throws Exception
     */
    public static void loadAnalytics() throws Exception
    {
        loadAnalytics(Context.getPage(), Context.getPreviousAction());
    }

    /**
     * Perform an Resources-Load call. The call has static parameters.
     * 
     * @param page
     *            the current page
     * @throws Exception
     * @return content of Resources-Load call (app.resources)
     */
    public static String loadAppResources(final HtmlPage page) throws Exception
    {
        final List<?> resourcesScripts = page.getByXPath("/html/body/script[@src and contains(@src,'/Resources-Load')]");
        Assert.assertFalse("No App.Resources script found.", resourcesScripts.isEmpty());

        String appResources = null;

        for (final Object o : resourcesScripts)
        {
            final WebResponse r = new XHR().url(((HtmlElement) o).getAttribute("src"))
                                           .param("t", "appresources")
                                           .expectStatusCode(200, "App ressources not loaded.")
                                           .expectNotBlank("App resources script doesn't have any content.")
                                           .fireFrom(page);
            appResources = r.getContentAsString().trim();
        }

        return appResources;
    }

    /**
     * Perform an Resources-Load call for the current page. The call has static parameters.
     * 
     * @throws Exception
     * @return content of Resources-Load call (app.resources)
     */
    public static String loadAppResources() throws Exception
    {
        return loadAppResources(Context.getPage());
    }
}
