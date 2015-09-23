package com.demandware.xlt.extra.actions;

import com.demandware.xlt.actions.catalog.ProductDetailView;


/**
 * Opens a product detail page directly without need for browsing the catalog or searching.
 * 
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 * 
 */
public class DirectProductDetailView extends ProductDetailView
{
    private final String urlString;

    /**
     * Constructor. Used if product details page is called directly per URL.
     * 
     * @param urlString
     *            the URL which should be loaded.
     */
    public DirectProductDetailView(final String urlString)
    {
        this.urlString = urlString;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doExecute() throws Exception
    {
        loadPage(this.urlString);
    }
}
