package com.demandware.xlt.tests;

import com.demandware.xlt.flows.SearchFlow;
import com.demandware.xlt.flows.VisitFlow;

/**
 * Open the landing page and search for predefined key words as well as for random string. If there are search results
 * open a random product's quick or detail view.
 * 
 * @author Xiaobai Wang
 */
public class TSearch extends AbstractTestCase
{
    /**
     * {@inheritDoc}
     */
    @Override
    public void test() throws Throwable
    {
        // Start at the landing page.
        new VisitFlow().run();

        // Start the search.
        new SearchFlow().run();
    }
}
