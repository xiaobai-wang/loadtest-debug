package com.xceptance.xlt.api.util.elementLookup;

import com.xceptance.xlt.api.util.elementLookup.asserted.InAsserted;

/**
 * HPU is just an abbreviation for HtmlPageUtils.
 */
public class HPU
{
    /**
     * Private constructor to prevent instantiation.
     */
    private HPU()
    {
        // nothing
    }

    /**
     * Initializes finder.
     * 
     * @return lookup base setter
     */
    public static In find()
    {
        return new In();
    }

    /**
     * Initializes finder and enables result assertions.
     * 
     * @return lookup base setter
     */
    public static InAsserted findAsserted()
    {
        return findAsserted(null);
    }

    /**
     * Initializes finder and enables result assertions with custom assertion-failed message.
     * 
     * @param customAssertionMessage
     *            custom assertion-failed message
     * @return lookup base setter
     */
    public static InAsserted findAsserted(final String customAssertionMessage)
    {
        return new InAsserted(customAssertionMessage);
    }
}
