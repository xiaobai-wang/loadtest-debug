package com.demandware.xlt.extra.tests;

import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.demandware.xlt.actions.Search;
import com.demandware.xlt.actions.catalog.InfiniteScroll;
import com.demandware.xlt.actions.catalog.Paging;
import com.demandware.xlt.actions.catalog.SelectCategory;
import com.demandware.xlt.actions.catalog.SelectTopCategory;
import com.demandware.xlt.extra.actions.SimpleURL;
import com.demandware.xlt.flows.VisitFlow;
import com.demandware.xlt.tests.AbstractTestCase;
import com.demandware.xlt.util.Page;
import com.demandware.xlt.util.SearchOption;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;

/**
 * Simple Test to find search phrases on the page.
 * 
 * @author Bernd Weigel (Xceptance Software Technologies GmbH)
 */
public class SearchPhraseCrawler extends AbstractTestCase
{
    /**
     * Indicates if the result list of valid search terms should be written to the config/data/search-phrases.txt. </br>
     * </br> true: written to file </br> false: printed in system out
     */
    private static final boolean SAVE_TO_FILE = false;

    /**
     * If results should get saved to file this constant specifies the file.
     */
    private static final String SEARCH_PHRASES_FILE = "config/data/search-phrases.txt";

    /**
     * Indicates if only category names should be used for search phrases. </br> </br> true: only (top)category names
     * are used (faster but less results) </br> false: also product names are used (slower but more results)
     */
    private static final boolean CATEGORIES_ONLY = true;

    /**
     * Stop searching after the list has reached MAX_PHRASES valid search terms.
     */
    private static final int MAX_PHRASES = 50;

    /**
     * Indicates if search terms with only one result should be used for the search term list
     */
    private static final boolean PHRASES_WITH_MULTIPLE_RESULTS_ONLY = true;

    @Override
    protected void test() throws Throwable
    {
        // Open start page.
        new VisitFlow().run();

        // Initialize phrases lists.
        final Set<String> searchTerms = new HashSet<String>();
        final Set<String> possiblePhrases = new HashSet<String>();

        // Collect phrases.
        collectPossiblePhrases(possiblePhrases);

        // Search previously collected phrases and remember the phrases that had a hit.
        for (final String phrase : possiblePhrases)
        {
            try
            {
                // Search for current phrase.
                new Search(phrase, SearchOption.HITS).run();

                // Remember search phrase depending on the decision to keep all positive or phrases with multiple
                // results only.
                if (!PHRASES_WITH_MULTIPLE_RESULTS_ONLY || Page.isProductGridPage())
                {
                    searchTerms.add(phrase);
                }

                // Stop if enough search phrases were collected.
                if (searchTerms.size() >= MAX_PHRASES)
                {
                    break;
                }

                // TODO is sleeping necessary here?
                // Sleep so we don't kill the server with lots of search requests.
                Thread.sleep(500);
            }
            catch (AssertionError e)
            {
                // No hit for this one. Continue with next phrase.
            }
        }

        // Print some statistics.
        System.out.println("Extracted" + searchTerms.size() + " out of " + possiblePhrases.size() + " initial phrases.");

        // Output search phrases.
        printOut(searchTerms);
    }

    /**
     * 
     */
    private void printOut(final Set<String> searchTerms) throws IOException
    {
        final StringBuilder sb = new StringBuilder();
        sb.append("#######################################\n");
        sb.append("Found ").append(searchTerms.size()).append(" valid search terms.\n");
        sb.append("#######################################");

        // Decide to save the phrases to file or print it to console.
        if (SAVE_TO_FILE)
        {
            // Write to file.
            try (final FileWriter writer = new FileWriter(SEARCH_PHRASES_FILE))
            {
                String prefix = "";
                for (final String searchTerm : searchTerms)
                {
                    writer.write(prefix + searchTerm);
                    prefix = "\n";
                }
                sb.append("Search phrases written to file.\n");
            }
        }
        else
        {
            // Print to console.
            for (final String serchTerm : searchTerms)
            {
                sb.append(serchTerm).append("\n");
            }

            sb.append("#######################################\n");
            sb.append("#######################################\n");
        }

        System.out.println(sb.toString());
    }

    /**
     * Add some search phrases to the collection of possible search phrases.
     * 
     * @param possibleSearchPhrases
     *            collection of possible search phrases
     */
    private void collectPossiblePhrases(final Set<String> possibleSearchPhrases) throws Throwable
    {
        // Collect by sub categories.
        addCategoryFindingsToList(possibleSearchPhrases, SelectCategory.getCategoryLinks().<HtmlAnchor> all());

        // collect by top categories.
        addCategoryFindingsToList(possibleSearchPhrases, SelectTopCategory.getTopCatLocator().<HtmlAnchor> all());
    }

    /**
     * Add search phrases extracted from sub category names to list of possible search phrases.
     * 
     * @param possibleSearchPhrases
     *            collection of possible search phrases
     * @param elements
     *            category links
     */
    private void addCategoryFindingsToList(final Set<String> possibleSearchPhrases, final List<HtmlAnchor> elements) throws Throwable
    {
        // Get the passed category names.
        for (final HtmlAnchor htmlElement : elements)
        {
            // Split into separate words and add each word.
            final String[] split = htmlElement.getTextContent().trim().split(" ");
            for (final String string : split)
            {
                // Add trimmed.
                final String trimmed = string.trim();
                if (trimmed.length() > 0)
                {
                    possibleSearchPhrases.add(trimmed.toLowerCase());
                }
            }

            // If we search for category phrases we can stop here. Otherwise we browse the category and try to extract
            // product names as well.
            if (!CATEGORIES_ONLY)
            {
                // Browse the category to extract product names.
                addProductGridFindingsToList(possibleSearchPhrases, htmlElement);
            }
        }
    }

    /**
     * Browse category and extract search phrases from found products.
     * 
     * @param possibleSearchPhrases
     *            collection of possible search phrases
     * @param anchor
     *            category link
     */
    private void addProductGridFindingsToList(final Set<String> possibleSearchPhrases, final HtmlAnchor anchor)
        throws MalformedURLException, Throwable
    {
        // Follow category link.
        new SimpleURL(anchor.getHrefAttribute()).run();

        // If we have infinite scroll just scroll and load more products.
        while (InfiniteScroll.isPossible())
        {
            new InfiniteScroll().run();
        }

        // If paging is possible do it once (since we choose random paging link and don't know how long to loop)
        if (Paging.isPossible())
        {
            new Paging().run();
        }

        // Extract search phrases.
        selectSearchPhrasesAction(possibleSearchPhrases);
    }

    private void selectSearchPhrasesAction(Set<String> list)
    {
        List<HtmlElement> all = getProductNames();
        for (HtmlElement htmlElement : all)
        {
            String[] split = htmlElement.getTextContent().trim().split(" ");
            for (String string : split)
            {
                if (string.trim().length() > 0)
                {
                    list.add(string.trim().toLowerCase());
                }
            }
        }
    }

    private static List<HtmlElement> getProductNames()
    {
        return Page.find().byCss("#search-result-items .product-tile > .product-name").all();
    }
}
