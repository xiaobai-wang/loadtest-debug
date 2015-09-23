package com.demandware.xlt.actions;


/**
 * Base class of all AJAX-only actions.
 * 
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 * 
 */
public abstract class AbstractAjaxAction extends AbstractHtmlPageAction
{
    /**
     * {@inheritDoc}
     */
    @Override
    protected void postExecute() throws Exception
    {
        // nothing to do.
    }
}
