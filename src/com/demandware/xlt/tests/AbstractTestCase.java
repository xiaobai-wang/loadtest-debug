package com.demandware.xlt.tests;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.demandware.xlt.util.Context;
import com.demandware.xlt.util.FlowStoppedException;
import com.demandware.xlt.util.Site;
import com.xceptance.xlt.api.engine.Session;
import com.xceptance.xlt.api.util.XltProperties;
import com.xceptance.xlt.common.XltConstants;

/**
 * Base class of all tests.
 * 
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 */
public abstract class AbstractTestCase extends com.xceptance.xlt.api.tests.AbstractTestCase
{
    /**
     * Initialize the randomizer.
     */
    @BeforeClass
    public static void preInitialize()
    {
        // If configured use a pre-defined value for initialization so the test run is repeatable.
        String value = XltProperties.getInstance().getProperty(XltConstants.RANDOM_INIT_VALUE_PROPERTY);
        if (value == null)
        {
            // Nothing configured
            value = String.valueOf(System.currentTimeMillis());
        }

        XltProperties.getInstance().setProperty(XltConstants.RANDOM_INIT_VALUE_PROPERTY, value);
    }

    /**
     * Creates a new Context for the current Thread.
     */
    public AbstractTestCase()
    {
        // Set test name.
        setTestName(getTestName() + getSite().getSuffix());
        // Publish test.
        Context.addContext(getClass().getName(), getSite());
    }

    /**
     * Test preparation. Nothing to do here by default. Feel free to override.
     * 
     * @throws Throwable
     *             thrown on error
     */
    @Before
    public void init() throws Throwable
    {
        // Nothing to do by default. Feel free to override.
    }

    /**
     * Run the test scenario.
     * 
     * @throws Throwable
     */
    @Test
    public void run() throws Throwable
    {
        try
        {
            // Execute the main test method.
            test();
        }
        catch (final FlowStoppedException e)
        {
            // If there's a flow stopper, log it. You'll find these entries in the report's 'Events' section.
            Session.logEvent("FlowStopped - " + Session.getCurrent().getUserName(), e.getMessage());

            // Break in development mode to notice problems but continue in load test mode to not break the complete
            // test.
            if (!Context.isLoadTest())
            {
                throw new FlowStoppedException(e.getMessage());
            }
        }
    }

    /**
     * Main test method.
     * 
     * @throws Throwable
     */
    protected abstract void test() throws Throwable;

    /**
     * {@inheritDoc}
     */
    @Override
    public void tearDown()
    {
        super.tearDown();
        Context.releaseAccount();
        Context.releaseContext();
    }

    /**
     * Get the site context. Override that method for each separate site.
     * 
     * @return site context
     */
    protected Site getSite()
    {
        return Site.DEFAULT;
    }
}
