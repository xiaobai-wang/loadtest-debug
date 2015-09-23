package com.demandware.xlt.actions.catalog;

import org.junit.Assert;

import com.demandware.xlt.actions.AbstractHtmlPageAction;
import com.demandware.xlt.util.Page;
import com.demandware.xlt.validators.Validator;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.xceptance.xlt.api.util.elementLookup.Results;

/**
 * Clicks a category link at the refinements panel.
 * 
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 * 
 */
public class RefineByCategory extends AbstractHtmlPageAction
{
    /** Links of navigation side menu level 3. */
    private static Results getLevel3Links()
    {
        return Page.find().byId("category-level-3").byCss("a");
    }

    /** Links of navigation side menu level 2. */
    private static Results getLevel2Links()
    {
        return Page.find().byCss("#category-level-2 > li:not(.active) > a");
    }

    /** Links of navigation side menu level 1. */
    private static Results getLevel1Links()
    {
        return Page.find().byCss("#category-level-1 > li:not(.active) > a");
    }

    /**
     * Checks if a category refining is possible.
     * 
     * @return <code>true</code> if category refining is possible, <code>false</code> otherwise
     */
    public static boolean isPossible()
    {
        return Page.checkExistance(getLevel3Links()) || Page.checkExistance(getLevel2Links()) || Page.checkExistance(getLevel1Links());
    }

    /**
     * Chosen category link.
     */
    private HtmlElement categoryLink;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() throws Exception
    {
        // Get a random sub category link, start most specific
        categoryLink = getLevel3Links().random();
        if (categoryLink == null)
        {
            // If no level-3 link is available try to get one of level-2
            categoryLink = getLevel2Links().random();
            if (categoryLink == null)
            {
                // if also no level-2 link is available try to change on level-1
                categoryLink = getLevel1Links().asserted("No category for Refinement available").random();
                Assert.assertNotNull("No category refinement link found.", categoryLink);
            }
        }

        loadPageByClick(categoryLink);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void postValidate() throws Exception
    {
        Validator.validateCategoryPage(categoryLink);
    }
}
