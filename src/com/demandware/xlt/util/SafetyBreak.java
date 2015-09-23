package com.demandware.xlt.util;

/**
 * Handling class for safety break checks
 * 
 * @author Bernd Weigel (Xceptance Software Technologies GmbH)
 */
public class SafetyBreak
{
    /** Current value */
    private int currentValue;

    /** Threshold */
    private final int max;

    /**
     * Initialize the {@link SafetyBreak}
     * 
     * @param maxTries
     *            the safety break's threshold
     */
    public SafetyBreak(final int maxTries)
    {
        this.max = maxTries;
        reset();
    }

    /**
     * Updates the number of remaining tries and returns <b>true</b> if the configured limit has been reached. Note:
     * every call of this method modifies the tries-counter value.
     * 
     * @param key
     *            - a unique key to identify this special safety break
     * @return <code>true</code> if safety break is reached, <code>false</code> otherwise
     */
    public boolean reached()
    {
        return reached("");
    }

    /**
     * Updates the number of remaining tries and returns <b>true</b> if the configured limit has been reached. Note:
     * every call of this method modifies the tries-counter value.
     * 
     * @param key
     *            - a unique key to identify this special safety break
     * @param additionalMessage
     *            custom message that should be added to the log entry
     * @return <code>true</code> if safety break is reached, <code>false</code> otherwise
     */
    public boolean reached(final String additionalMessage)
    {
        if (currentValue-- <= 0)
        {
            Context.logForDebug(getLogMessage(additionalMessage));
            return true;
        }
        return false;
    }

    /**
     * Updates the number of remaining tries throws an Exception if so. Note: every call of this method modifies the
     * tries-counter value.
     * 
     * @throws FlowStoppedException
     *             if security break limit is reached
     */
    public void check() throws FlowStoppedException
    {
        check("");
    }

    /**
     * Updates the number of remaining tries throws an Exception if so. Note: every call of this method modifies the
     * tries-counter value.
     * 
     * @param additionalMessage
     *            custom message that should be part of the exception and the log entry
     * @throws FlowStoppedException
     *             if security break limit is reached
     */
    public void check(final String additionalMessage) throws FlowStoppedException
    {
        if (this.currentValue-- <= 0)
        {
            final String message = getLogMessage(additionalMessage);
            Context.logForDebug(message);
            throw new FlowStoppedException(message);
        }
    }

    /**
     * Resets the safety break counter to the pre-configured value.
     */
    public void reset()
    {
        this.currentValue = max;
    }

    /**
     * Retrieves the class and method name of the calling method for logging purpose.
     * 
     * @return class and method name of the calling method or <code>null</code> if class and method could not be parsed
     *         from stack trace.
     */
    private String getCaller()
    {
        // Parse all stack trace elements.
        final StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        for (int i = 0; i < stackTraceElements.length; i++)
        {
            // This will filter out the Page.checkExistanceInPage() method and will give us the real method which calls
            // it.
            if (!stackTraceElements[i].getClassName().equals(this.getClass().getName())
                && !stackTraceElements[i].getClassName().equals(Thread.class.getName()))
            {
                final String className = stackTraceElements[i].getClassName();
                return className.substring(className.lastIndexOf('.') + 1) + "." + stackTraceElements[i + 1].getMethodName();
            }
        }

        return "";
    }

    /**
     * Get a message based on the given custom message part.
     * 
     * @param additionalMessage
     *            custom part of log message
     * @return message based on the given custom message part
     */
    private String getLogMessage(final String additionalMessage)
    {
        return "SafetyBreak for " + getCaller() + " reached! " + additionalMessage;
    }
}
