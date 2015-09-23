package com.xceptance.xlt.api.util.elementLookup;

import com.gargoylesoftware.htmlunit.html.DomNode;
import com.xceptance.xlt.api.util.elementLookup.strategy.CssLookupStrategy;
import com.xceptance.xlt.api.util.elementLookup.strategy.IdLookupStrategy;
import com.xceptance.xlt.api.util.elementLookup.strategy.XPathLookupStrategy;


/**
 * Lookup strategy
 */
public class By
{
    /**
     * Lookup base.
     */
    private final DomNode parent;
    
    /**
     * The strategy the current strategy is based on. 
     */
    private final Strategy parentStrategy;

    /**
     * Constructor. Initializes object with lookup base.
     * 
     * @param parent
     *            lookup base
     */
    public By(final DomNode parent)
    {
        this.parent = parent;
        this.parentStrategy = null;
    }

    /**
     * Constructor. Initializes object with a parent strategy. The current lookup is based on the results of the given parent strategy.
     * 
     * @param parentStrategy
     *            parent strategy base
     */
    public By(final Strategy parentStrategy)
    {
        this.parent = null;
        this.parentStrategy = parentStrategy;
    }

    /**
     * Set XPath lookup strategy and locator.
     * 
     * @param locator
     *            XPath locator
     * @return {@link Results} object to query the results from
     * @throws IllegalArgumentException
     *             if given locator is <code>null</code> or empty
     */
    public Results byXPath(final String locator) throws IllegalArgumentException
    {
        final Strategy strategy = parent != null ? new XPathLookupStrategy(parent, locator) : new XPathLookupStrategy(parentStrategy, locator);
        return by(strategy);
    }

    /**
     * Set CSS lookup strategy and locator.
     * 
     * @param locator
     *            CSS locator
     * @return {@link Results} object to query the results from
     * @throws IllegalArgumentException
     *             if given locator is <code>null</code> or empty
     */
    public Results byCss(final String locator) throws IllegalArgumentException
    {
        final Strategy strategy = parent != null ? new CssLookupStrategy(parent, locator) : new CssLookupStrategy(parentStrategy, locator);
        return by(strategy);
    }

    /**
     * Set ID lookup strategy and ID. The ID lookup strategy finds the first element with the given ID.
     * 
     * @param id
     *            the element's ID
     * @return {@link Results} object to query the result from
     * @throws IllegalArgumentException
     *             if given ID is <code>null</code> or empty
     */
    public Results byId(final String id) throws IllegalArgumentException
    {
        return by(new IdLookupStrategy(parent, id));
    }

    /**
     * Get new result object based on given lookup strategy
     * 
     * @param strategy
     *            lookup strategy
     * @return result
     */
    protected Results by(final Strategy strategy)
    {
        return new Results(strategy);
    }
}
