package com.demandware.xlt.actions;

import java.net.URL;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import com.demandware.xlt.util.Context;
import com.demandware.xlt.validators.Validator;

/**
 * Opens the start page.
 * 
 * @author Xiaobai Wang
 */
public class Homepage extends AbstractHtmlPageAction
{
    /**
     * Start page URL string.
     */
    private final String urlString;

    /**
     * Constructor.
     * 
     * @param urlString
     *            start page URL as string
     */
    public Homepage(final String urlString)
    {
        this.urlString = urlString;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() throws Exception
    {
        final URL url = new URL(urlString);

        // Set the Basic Authentication header if necessary.
        setBasicAuthenticationHeader();

        // Open the start page.
        loadPage(url);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void postValidate() throws Exception
    {
        // Check some basics.
        Validator.validateCommonPage();
    }

    /**
     * Set the Basic Authentication header if credentials are configured.
     */
    private void setBasicAuthenticationHeader()
    {
        // Is a user name for Basic Authentication configured?
        final String username = Context.getConfiguration().getProperty("com.xceptance.xlt.auth.userName");
        if (StringUtils.isNotBlank(username))
        {
            // It is. So let's get the password.
            final String password = Context.getConfiguration().getProperty("com.xceptance.xlt.auth.password", "");

            // Set the request header.
            final String userPass = username + ":" + password;
            final String userPassBase64 = Base64.encodeBase64String(userPass.getBytes());
            getWebClient().addRequestHeader("Authorization", "Basic " + userPassBase64);
        }
    }
}
