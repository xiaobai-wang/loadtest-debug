package com.xceptance.xlt.api.util.elementLookup.asserted;

import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.xceptance.common.util.ParameterCheckUtils;
import com.xceptance.xlt.api.util.elementLookup.In;

/**
 * Base element. This object contains an implicit results assertion request.
 */
public class InAsserted extends In
{
    /**
     * Custom assertion-failed message.
     */
    private final String customAssertionMessage;

    /**
     * Create new base element holder with implicit result assertion request.
     */
    public InAsserted()
    {
        this(null);
    }

    /**
     * Create new base element holder with implicit result assertion request and custom assertion-failed message.
     * 
     * @param customAssertionMessage
     *            custom assertion-failed message
     */
    public InAsserted(String customAssertionMessage)
    {
        this.customAssertionMessage = customAssertionMessage;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ByAsserted in(final DomNode parent, final String parameterName) throws IllegalArgumentException
    {
        ParameterCheckUtils.isNotNull(parent, parameterName);
        return new ByAsserted(parent, customAssertionMessage);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByAsserted in(HtmlElement element) throws IllegalArgumentException
    {
        return (ByAsserted) super.in(element);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByAsserted in(HtmlPage page) throws IllegalArgumentException
    {
        return (ByAsserted) super.in(page);
    }
}
