package com.demandware.xlt.extra.tests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;

import com.demandware.xlt.actions.Search;
import com.demandware.xlt.flows.VisitFlow;
import com.demandware.xlt.tests.AbstractTestCase;
import com.demandware.xlt.util.SearchOption;
import com.xceptance.xlt.api.data.DataProvider;
import com.xceptance.xlt.api.util.XltLogger;

/**
 * Helper class to check all predefined search phrases for having results.
 * 
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 */
public class TSearchAllKeywords extends AbstractTestCase
{
    @Override
    public void test() throws Throwable
    {
        // This list will contains all the problematic search phrases.
        final List<String> misses = new ArrayList<String>();
        final List<String> hits = new ArrayList<String>();

        // Start at the landing page.
        new VisitFlow().run();

        // Then we query for each search phrase one by one.
        for (final String searchPhrase : DataProvider.getInstance("search-phrases" + getSite().getSuffix() + ".txt").getAllRows())
        {
            // Since the script checks the predefined phrases that are
            // assumed to end in one or more results we pass the search
            // option HITS (expects results[s]) to the search action.

            try
            {
                // Perform the search.
                new Search(searchPhrase, SearchOption.HITS).run();
                hits.add(searchPhrase);
            }
            catch (AssertionError ae)
            {
                // If there was a problem we collect the phrase and move on.
                misses.add(searchPhrase);
            }
        }
        // If we have to renew the list, print the working phrases to std.out so we can copy&paste it to the
        // config/data/search-phrases.txt
        if (!misses.isEmpty())
        {
            XltLogger.runTimeLogger.info("The following phrases worked:");
            System.out.println();

            for (String hit : hits)
            {
                System.out.println(hit);
            }

            System.out.println();
            XltLogger.runTimeLogger.info("(copy&paste the list to the search-phrase file)");
        }
        // Print the problematic phrases if any.
        Assert.assertTrue("The following phrases result in a search miss: " + Arrays.toString(misses.toArray()), misses.isEmpty());
    }
}
