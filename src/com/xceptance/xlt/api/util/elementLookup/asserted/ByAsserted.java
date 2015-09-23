package com.xceptance.xlt.api.util.elementLookup.asserted;

import com.gargoylesoftware.htmlunit.html.DomNode;
import com.xceptance.xlt.api.util.elementLookup.By;
import com.xceptance.xlt.api.util.elementLookup.Strategy;


/**
 * Lookup strategy setter. This object contains an implicit results assertion request.
 */
public class ByAsserted extends By
{
    /**
     * Custom assertion-failed message.
     */
    private final String customAssertionMesage;

    /**
     * Constructor. Initializes object with lookup base.
     * 
     * @param parent
     *            lookup base
     */
    public ByAsserted(final DomNode parent)
    {
        this(parent, null);
    }

    /**
     * Constructor. Initializes object with lookup base and custom assertion-failed message.
     * 
     * @param parent
     *            lookup base
     * @param customAssertionMesage
     *            custom assertion-failed message
     */
    public ByAsserted(final DomNode parent, final String customAssertionMesage)
    {
        super(parent);
        this.customAssertionMesage = customAssertionMesage;
    }

    /**
     * Constructor. Initializes object with lookup base.
     * 
     * @param parentStrategy
     *            lookup base
     */
    public ByAsserted(final Strategy parentStrategy)
    {
        this(parentStrategy, null);
    }

    /**
     * Constructor. Initializes object with lookup base and custom assertion-failed message.
     * 
     * @param parentStrategy
     *            lookup base
     * @param customAssertionMesage
     *            custom assertion-failed message
     */
    public ByAsserted(final Strategy parentStrategy, final String customAssertionMesage)
    {
        super(parentStrategy);
        this.customAssertionMesage = customAssertionMesage;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ResultsAsserted by(final Strategy strategy)
    {
        return new ResultsAsserted(strategy, customAssertionMesage);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResultsAsserted byXPath(String locator) throws IllegalArgumentException
    {
        return (ResultsAsserted) super.byXPath(locator);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResultsAsserted byCss(String locator) throws IllegalArgumentException
    {
        return (ResultsAsserted) super.byCss(locator);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResultsAsserted byId(String id) throws IllegalArgumentException
    {
        return (ResultsAsserted) super.byId(id);
    }
}
