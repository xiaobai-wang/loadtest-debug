package com.xceptance.xlt.api.util.elementLookup;

import java.util.List;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.xceptance.common.util.ParameterCheckUtils;
import com.xceptance.xlt.api.util.XltRandom;
import com.xceptance.xlt.api.util.elementLookup.asserted.ResultsAsserted;

/**
 * Result set.
 */
public class Results
{
    /**
     * Lookup strategy.
     */
    private final Strategy strategy;

    /**
     * The raw results.
     */
    private List<?> results;

    /**
     * Constructor
     * 
     * @param strategy
     *            lookup strategy
     */
    public Results(final Strategy strategy)
    {
        this.strategy = strategy;
    }

    /**
     * Get the lookup strategy.
     * 
     * @return the lookup strategy
     */
    protected Strategy getStrategy()
    {
        return strategy;
    }

    /**
     * Get the locator description.
     * 
     * @return
     */
    public String getLocatorDescription()
    {
        return getStrategy().getLocatorDescription();
    }

    /**
     * Does the described element exist?
     * 
     * @return <code>true</code> if there's at least 1 result element or <code>false</code> otherwise.
     */
    public boolean exists()
    {
        return !raw().isEmpty();
    }

    /**
     * Get the unique element.
     * 
     * @return the unique result element or <code>null</code> if no such unique element is present
     */
    public <T extends HtmlElement> T single()
    {
        // return unique result element or null
        if (raw().size() == 1)
        {
            @SuppressWarnings("unchecked")
            final T element = (T) raw().get(0);
            return element;
        }

        return null;
    }

    /**
     * Get the first result element.
     * 
     * @return the first result element or <code>null</code> if no such element is present
     */
    public <T extends HtmlElement> T first()
    {
        if (!raw().isEmpty())
        {
            @SuppressWarnings("unchecked")
            final T element = (T) raw().get(0);
            return element;
        }

        return null;
    }

    /**
     * Get the last result element.
     * 
     * @return the last result element or <code>null</code> if no such element is present
     */
    public <T extends HtmlElement> T last()
    {
        if (!raw().isEmpty())
        {
            @SuppressWarnings("unchecked")
            final T element = (T) raw().get(raw().size() - 1);
            return element;
        }

        return null;
    }

    /**
     * Get a random result element.
     * 
     * @return a random result element or <code>null</code> if no such element is present
     */
    public <T extends HtmlElement> T random()
    {
        if (!raw().isEmpty())
        {
            @SuppressWarnings("unchecked")
            final T element = (T) raw().get(XltRandom.nextInt(raw().size()));
            return element;
        }

        return null;
    }

    /**
     * Get all result elements.
     * 
     * @return all result elements (never <code>null</code>)
     */
    public <T extends HtmlElement> List<T> all()
    {
        @SuppressWarnings("unchecked")
        final List<T> elements = (List<T>) raw();
        return elements;
    }

    /**
     * Get the result element with given index.
     * 
     * @param index
     *            result list index
     * @return the result element with given index from result list or <code>null</code> if no such element is present
     * @throws IllegalArgumentException
     *             if the index value is lower than <code>0</code>
     */
    public <T extends HtmlElement> T index(final int index)
        throws IllegalArgumentException
    {
        // index must be 0 at least
        ParameterCheckUtils.isGreaterThan(index, -1, "index");

        if (count() > index)
        {
            @SuppressWarnings("unchecked")
            final T element = (T) raw().get(index);
            return element;
        }

        return null;
    }

    /**
     * Get the number of result elements.
     * 
     * @return number of result elements
     */
    public int count()
    {
        return raw().size();
    }

    /**
     * Check if result elements count is as expected.
     * 
     * @param expectedCount
     *            the expected results count
     * @return <code>true</code> if the results count is equal to the expected count, <code>false</code> otherwise
     * @throws IllegalArgumentException
     *             if expected count is lower than <code>0</code>
     */
    public boolean isCount(final int expectedCount) throws IllegalArgumentException
    {
        // expectedCount must be 0 at least
        ParameterCheckUtils.isGreaterThan(expectedCount, -1, "expectedCount");

        return count() == expectedCount;
    }

    /**
     * Check if result elements count is within the given range.
     * 
     * @param min
     *            minimum asserted count
     * @param max
     *            maximum asserted count
     * @return <code>true</code> if results count is within given range, <code>false</code> otherwise
     * @throws IllegalArgumentException
     *             if <code>min</code> is lower than <code>0</code> or <code>max</code> is lower than <code>min</code>
     */
    public boolean isCount(final int min, final int max) throws IllegalArgumentException
    {
        // min must be at least 0
        ParameterCheckUtils.isGreaterThan(min, -1, "min");
        // max must not be lower than min
        ParameterCheckUtils.isGreaterThan(max, min - 1, "max");

        final int count = count();
        return min <= count && count <= max;
    }

    /**
     * Get the unprocessed results.
     * 
     * @return unprocessed results (never <code>null</code>)
     */
    public List<?> raw()
    {
        // lookup results if necessary
        if (results == null)
        {
            results = getStrategy().lookup();
        }

        return results;
    }

    /**
     * Enable result assertions(for example expected minimum/maximum amount of result elements).
     * 
     * @return {@link ResultsAsserted} object to query the results from
     */
    public ResultsAsserted asserted()
    {
        return asserted(null);
    }

    /**
     * Enable result assertions(for example expected minimum/maximum amount of result elements). Fail with given custom
     * message if necessary.
     * 
     * @param message
     *            assertion-failed message
     * @return {@link ResultsAsserted} object to query the results from
     */
    public ResultsAsserted asserted(final String assertionMessage)
    {
        return new ResultsAsserted(getStrategy(), assertionMessage);
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
    public Results byXPath(String locator) throws IllegalArgumentException
    {
        return new By(strategy).byXPath(locator);
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
    public Results byCss(String locator) throws IllegalArgumentException
    {
        return new By(strategy).byCss(locator);
    }
}
