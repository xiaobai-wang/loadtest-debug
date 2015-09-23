package com.demandware.xlt.extra.actions;

import java.util.List;

import org.junit.Assert;

import com.demandware.xlt.actions.AbstractHtmlPageAction;
import com.demandware.xlt.util.Page;
import com.demandware.xlt.validators.Validator;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.xceptance.common.util.RegExUtils;

/**
 * This is a simple test class for pulling urls.
 * 
 * @author Rene Schwietzke
 * @version
 */
public class SimpleURL extends AbstractHtmlPageAction
{
    private final String url;

    private final String xpath;

    private final String text;

    private boolean isValid;

    /**
     * Creates an SimpleUrlObject.
     * 
     * @param url
     *            the URL which is beeing loaded
     */
    public SimpleURL(final String url)
    {
        this(url, "", "");
    }

    /**
     * Creates an SimpleUrlObject.
     * 
     * @param url
     *            the URL which is beeing loaded
     * @param xpath
     *            an xpath wich will be evaluated after the page was loaded
     * @param text
     *            the expected text content of the element represented by xpath
     */
    public SimpleURL(final String url, final String xpath, final String text)
    {
        this.url = url;
        this.xpath = xpath;
        this.text = text;
    }

    /**
     * Creates an SimpleUrlObject.
     * 
     * @param action
     *            the previous action
     * @param url
     *            the URL which is beeing loaded
     * @param xpath
     *            an xpath wich will be evaluated after the page was loaded
     * @param text
     *            the expected text content of the element represented by xpath
     */
    public SimpleURL(final com.xceptance.xlt.api.actions.AbstractHtmlPageAction action, final String url, final String xpath,
        final String text)
    {
        super(action);
        this.setTimerName("SimpleURL.Followup");
        this.url = url;
        this.xpath = xpath;
        this.text = text;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.xceptance.xlt.api.actions.AbstractAction#postValidate()
     */
    @Override
    protected void postValidate() throws Exception
    {
        // validate response code
        Validator.validateBasics();
        isValid = true;

        // check the special path
        if (xpath == null || text == null)
        {
            return;
        }
        if (xpath.length() == 0)
        {
            return;
        }

        // ok, do it
        final List<HtmlElement> elements = Page.find().byXPath(xpath).all();
        Assert.assertFalse("xpath not found '" + xpath + "'", elements.isEmpty());

        Assert.assertTrue("Text does not match", RegExUtils.isMatching(elements.get(0).asText().trim(), text.trim()));
    }

    @Override
    protected void doExecute() throws Exception
    {
        loadPage(url);
    }

    public boolean isValidBasicPage()
    {
        return isValid;
    }
}
