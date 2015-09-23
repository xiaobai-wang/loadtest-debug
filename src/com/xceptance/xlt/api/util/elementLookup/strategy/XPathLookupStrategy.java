package com.xceptance.xlt.api.util.elementLookup.strategy;

import java.util.List;

import com.gargoylesoftware.htmlunit.html.DomNode;
import com.xceptance.xlt.api.util.elementLookup.Strategy;

/**
 * XPath lookup strategy
 */
public class XPathLookupStrategy extends AbstractLookupStrategy
{
    /**
     * XPath lookup strategy constructor.
     * 
     * @param parent
     *            lookup base
     * @param locator
     *            locator string
     */
    public XPathLookupStrategy(final DomNode parent, String locator)
    {
        super(parent, locator);
    }

    /**
     * XPath lookup strategy constructor.
     * 
     * @param parent
     *            parent lookup strategy
     * @param locator
     *            locator string
     */
    public XPathLookupStrategy(final Strategy parentStrategy, String locator)
    {
        super(parentStrategy, locator);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<?> lookup(final DomNode parent)
    {
        return parent.getByXPath(getLocator());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getStrategyName()
    {
        return "XPath";
    }
}
