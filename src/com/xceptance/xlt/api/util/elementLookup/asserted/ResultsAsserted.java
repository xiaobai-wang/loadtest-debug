package com.xceptance.xlt.api.util.elementLookup.asserted;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.xceptance.xlt.api.util.elementLookup.Results;
import com.xceptance.xlt.api.util.elementLookup.Strategy;

/**
 * Asserted result set.
 */
public class ResultsAsserted extends Results
{
    /**
     * Custom assertion message.
     */
    private final String customAssertionMessage;

    /**
     * Initialize result set with lookup strategy to use and custom assertion-failed message.
     * 
     * @param strategy
     *            lookup strategy
     * @param customAssertionMessage
     *            custom assertion-failed message
     */
    public ResultsAsserted(Strategy strategy, String customAssertionMessage)
    {
        super(strategy);
        this.customAssertionMessage = customAssertionMessage;
    }

    /**
     * Assertion runner for checking a single criteria.
     */
    protected abstract class Asserter implements Runnable
    {
        /**
         * Execute the assertion.
         * 
         * @throws AssertionError
         *             if the criteria is not fulfilled
         */
        @Override
        public void run() throws AssertionError
        {
            try
            {
                assertRequired();
            }
            catch (final AssertionError e)
            {
                // if assertion fails and there's a custom message wrap the original exception to not loose the
                // element's lookup path
                if (StringUtils.isNotBlank(customAssertionMessage))
                {
                    throw new AssertionError(customAssertionMessage, e);
                }

                throw e;
            }
        }

        /**
         * Assert a single required criteria (i.e. minimum or maximum amount of result elements)
         * 
         * @throws AssertionError
         *             if the criteria is not fulfilled
         */
        public abstract void assertRequired() throws AssertionError;
    }

    /**
     * {@inheritDoc}
     * 
     * @return the unique result element
     * @throws AssertionError
     *             if no or more than <code>1</code> result element is found
     */
    @Override
    public <T extends HtmlElement> T single() throws AssertionError
    {
        final T result = super.single();

        // assert uniqueness
        if (result == null)
        {
            new Asserter()
            {
                @Override
                public void assertRequired()
                {
                    Assert.assertEquals("Too many results for: " + getStrategy().getLocatorDescription(), 1, count());
                }
            }.run();
        }

        return result;
    }

    /**
     * {@inheritDoc}
     * 
     * @return the result element with given index from result list
     * @throws AssertionError
     *             if no result element is found or the given result index does not exist
     */
    @Override
    public <T extends HtmlElement> T index(final int index) throws AssertionError, IllegalArgumentException
    {
        final T result = super.index(index);

        // result index must exist
        if (result == null)
        {
            new Asserter()
            {
                @Override
                public void assertRequired()
                {
                    Assert.fail("Given index is higher than maximum results index (" + (count() - 1) + ") for: "
                                + getStrategy().getLocatorDescription());
                }
            }.run();
        }

        return result;
    }

    /**
     * {@inheritDoc}
     * 
     * @return <code>true</code> if the results count is equal to the expected count
     * @throws AssertionError
     *             if no result element is found or amount of result elements does not fit the expected count
     */
    @Override
    public boolean isCount(final int expectedCount) throws AssertionError, IllegalArgumentException
    {
        final boolean isCount = super.isCount(expectedCount);

        // result index must exist
        if (!isCount)
        {
            new Asserter()
            {
                @Override
                public void assertRequired()
                {
                    Assert.assertEquals("Number of current results is not equal to number of expected results for: "
                                        + getStrategy().getLocatorDescription(), expectedCount, count());
                }
            }.run();
        }

        return isCount;
    }

    /**
     * {@inheritDoc}
     * 
     * @return <code>true</code> if results count is within given range
     * @throws AssertionError
     *             if no result element is found or the amount of result elements is not within the given range
     *             (min/max)
     */
    @Override
    public boolean isCount(final int min, final int max) throws AssertionError, IllegalArgumentException
    {
        final boolean isMinMax = super.isCount(min, max);

        // result index must exist
        if (!isMinMax)
        {
            new Asserter()
            {
                @Override
                public void assertRequired()
                {
                    Assert.fail("Number of current results (" + count() + ") is out of given boundaries for: "
                                + getStrategy().getLocatorDescription());
                }
            }.run();
        }

        return isMinMax;
    }

    /**
     * {@inheritDoc}
     * 
     * @throws AssertionError
     *             if no result element is found
     */
    @Override
    public List<?> raw() throws AssertionError
    {
        final List<?> results = super.raw();

        // assert minimum results if desired
        if (results.isEmpty())
        {
            new Asserter()
            {
                @Override
                public void assertRequired()
                {
                    Assert.fail("No element found for: " + getStrategy().getLocatorDescription());
                }
            }.run();
        }

        return results;
    }

    /**
     * Does the described element exist?
     * 
     * @return <code>true</code> if there's at least 1 result element
     * @throws AssertionError
     *             if no result element is found
     */
    @Override
    public boolean exists() throws AssertionError
    {
        return super.exists();
    }

    /**
     * {@inheritDoc}
     * 
     * @return the first result element
     * @throws AssertionError
     *             if no result element is found
     */
    @Override
    public <T extends HtmlElement> T first() throws AssertionError
    {
        return super.first();
    }

    /**
     * {@inheritDoc}
     * 
     * @return the last result element
     * @throws AssertionError
     *             if no result element is found
     */
    @Override
    public <T extends HtmlElement> T last() throws AssertionError
    {
        return super.last();
    }

    /**
     * {@inheritDoc}
     * 
     * @return a random result element
     * @throws AssertionError
     *             if no result element is found
     */
    @Override
    public <T extends HtmlElement> T random() throws AssertionError
    {
        return super.random();
    }

    /**
     * {@inheritDoc}
     * 
     * @return all result elements
     * @throws AssertionError
     *             if no result element is found
     */
    @Override
    public <T extends HtmlElement> List<T> all() throws AssertionError
    {
        return super.all();
    }

    /**
     * {@inheritDoc}
     * 
     * @throws AssertionError
     *             if no result element is found
     */
    @Override
    public int count() throws AssertionError
    {
        return super.count();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResultsAsserted byXPath(final String locator) throws IllegalArgumentException
    {
        return new ByAsserted(getStrategy()).byXPath(locator);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResultsAsserted byCss(final String locator) throws IllegalArgumentException
    {
        return new ByAsserted(getStrategy()).byCss(locator);
    }
}
