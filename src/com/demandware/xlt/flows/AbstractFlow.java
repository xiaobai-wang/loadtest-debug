package com.demandware.xlt.flows;

/**
 * Provide a unique interface for flow execution.
 * 
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 */
public abstract class AbstractFlow
{
    /**
     * Run the flow.
     * 
     * @throws Throwable
     *             if something bad happens
     */
    public abstract void run() throws Throwable;
}
