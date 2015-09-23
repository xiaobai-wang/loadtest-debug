package com.xceptance.xlt.api.util.elementLookup;

import java.util.List;

/**
 * Lookup strategy
 */
public interface Strategy
{
    /**
     * Lookup elements
     * 
     * @return list of result elements (never <code>null</code>).
     */
    List<?> lookup();

    /**
     * Get Locator description in format <code>{&lt;LookupStrategyName&gt;=&lt;locator&gt;}</code> and its predecessors
     * if any. If a predecessor failed to find elements the current locator description is not appended.
     * 
     * @return locator description
     */
    public String getLocatorDescription();
}
