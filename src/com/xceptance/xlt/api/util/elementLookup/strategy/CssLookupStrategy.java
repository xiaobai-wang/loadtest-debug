package com.xceptance.xlt.api.util.elementLookup.strategy;

import java.util.List;

import com.gargoylesoftware.htmlunit.html.DomNode;
import com.xceptance.xlt.api.util.elementLookup.Strategy;

/**
 * CSS lookup strategy
 */
public class CssLookupStrategy extends AbstractLookupStrategy
{
    /**
     * CSS lookup strategy constructor.
     * 
     * @param parent
     *            lookup base
     * @param locator
     *            locator string
     */
    public CssLookupStrategy(final DomNode parent, String locator)
    {
        super(parent, locator);
    }

    /**
     * CSS lookup strategy constructor.
     * 
     * @param parent
     *            parent lookup strategy
     * @param locator
     *            locator string
     */
    public CssLookupStrategy(final Strategy parentStrategy, String locator)
    {
        super(parentStrategy, locator);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<?> lookup(final DomNode parent)
    {
        return parent.querySelectorAll(getLocator());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getStrategyName()
    {
        return "CSS";
    }
}
