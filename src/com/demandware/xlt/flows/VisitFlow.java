package com.demandware.xlt.flows;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;

import com.demandware.xlt.actions.Homepage;
import com.demandware.xlt.util.Context;

public class VisitFlow extends AbstractFlow
{
    /**
     * {@inheritDoc}
     */
    @Override
    public void run() throws Throwable
    {
        // Get the configured start URL (see project.properties) and build the
        // first action (Homepage).
        final String startUrlString = Context.getConfiguration().getStartURL();
        Assert.assertFalse("Start URL is empty", StringUtils.isBlank(startUrlString));

        // Open the start URL
        new Homepage(startUrlString).run();
    }
}
