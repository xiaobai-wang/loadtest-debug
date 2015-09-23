package com.demandware.xlt.actions;

import java.net.SocketTimeoutException;
import java.text.MessageFormat;

import com.demandware.xlt.util.AjaxUtils;
import com.demandware.xlt.util.Context;
import com.xceptance.xlt.api.engine.Session;
import com.xceptance.xlt.api.util.XltProperties;

/**
 * Base class for all actions that perform a page call.
 * 
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 * 
 */
public abstract class AbstractHtmlPageAction extends com.xceptance.xlt.api.actions.AbstractHtmlPageAction
{
    /**
     * Create new action that is based on the action before (if any).
     */
    public AbstractHtmlPageAction()
    {
        // Let this action base on the action before.
        this(Context.getCurrentAction());
    }

    /**
     * Create a new action that is based on the passed action.
     * 
     * @param lastAction
     *            action used as ancestor for the action to create
     */
    public AbstractHtmlPageAction(final com.xceptance.xlt.api.actions.AbstractHtmlPageAction lastAction)
    {
        super(lastAction, null);

        /*
         * Sometimes the received response needs to be modified. This can be done easily by response processors. Find
         * here some examples that
         */

        // Drop 'iframe' sections.
        // addResponseProcessor(new ResponseContentProcessor("<noscript\\s*>.*?</noscript>", ""));
        // Drop 'noscript' sections.
        // addResponseProcessor(new ResponseContentProcessor("<iframe[^>]+>", "<iframe>"));
        
        // Drop ISML tags (Use this workaround for script DEVELOPMENT only)
        // addResponseProcessor(new ResponseContentProcessor("<is.*?>", ""));
        // addResponseProcessor(new ResponseContentProcessor("</is.*?>", ""));

        // Take over the app resources from last action (if available). This will be update if a new page gets loaded
        // later on.

        // Make THIS action the current one.
        Context.setCurrentAction(this);

        // Adjust action name if necessary.
        setTimerName(getTimerName() + Context.getSite().getSuffix());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() throws Exception
    {
        // If a retry on timeout is configured execute the action in a more relaxed way.
        final int retryCount = XltProperties.getInstance().getProperty("retry.execute.ontimeout", 0);
        if (retryCount > 0)
        {
            doExecuteTolerateTimeout(retryCount);
        }
        else
        {
            // Otherwise just execute it and break if a timeout is detected.
            doExecute();
        }

        // Do the post execution steps.
        postExecute();
    }

    /**
     * Execute the commands. If a timeout is detected the commands will be repeated. If the repetitions have reached the
     * given maximum the method just returns. A SocketTimeoutException will never be thrown.
     * 
     * @param retryCount
     *            How many repetitions are allowed at maximum.
     * @throws Exception
     *             In case of an error but a SocketTimeoutException
     */
    private void doExecuteTolerateTimeout(final int retryCount) throws Exception
    {
        // Repeat if necessary
        for (int i = 0; i < retryCount; i++)
        {
            try
            {
                // Execute the action.
                doExecute();

                // No timeout, no retry. We are done.
                break;
            }
            catch (final SocketTimeoutException e)
            {
                // Nothing to do. Catch it and continue. Action will be executed again if possible.
            }
            catch (final RuntimeException rte)
            {
                // Check for the wrapped exception. Do not throw a SocketTimeoutException.
                final Throwable cause = rte.getCause();
                if (cause == null || cause.getClass() != SocketTimeoutException.class)
                {
                    // Propagate it.
                    throw rte;
                }
            }

            // If we reach that, we've caught the exception and will try again. Log it.
            Session.logEvent("TimeoutRetry",
                             MessageFormat.format("{0} - Try: {1} failed.", this.getTimerName(), (i + 1)));
        }
    }

    /**
     * Executes Commands which needs to be done after the main execution (like the analytics call).
     * 
     * @throws Exception
     */
    protected void postExecute() throws Exception
    {
        Context.loadAppResources();

        // Do the analytics call.
        if (Context.getConfiguration().loadAnalytics())
        {
            AjaxUtils.loadAnalytics();
        }
    }

    /**
     * Execute the action's main part. What is done here is determined by the sub classes.
     * 
     * @throws Exception
     *             if an error occurred while executing the action.
     */
    protected abstract void doExecute() throws Exception;

    /**
     * {@inheritDoc}
     */
    @Override
    public void preValidate() throws Exception
    {
    }
}
