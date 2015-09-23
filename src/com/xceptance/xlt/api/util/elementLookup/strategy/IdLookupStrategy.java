package com.xceptance.xlt.api.util.elementLookup.strategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * ID lookup strategy
 */
public class IdLookupStrategy extends AbstractLookupStrategy
{
    /**
     * ID lookup strategy constructor.
     * 
     * @param parent
     *            lookup base
     * @param locator
     *            locator string
     */
    public IdLookupStrategy(final DomNode parent, String locator)
    {
        super(parent, locator);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<?> lookup(DomNode parent)
    {
        final DomNode result = ((HtmlPage) parent.getPage()).getElementById(getLocator());
        if (result != null)
        {
            final List<DomNode> results = new ArrayList<DomNode>(1);
            results.add(result);
            return results;
        }
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getStrategyName()
    {
        return "ID";
    }
}
