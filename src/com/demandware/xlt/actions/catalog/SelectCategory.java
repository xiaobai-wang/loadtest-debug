package com.demandware.xlt.actions.catalog;

import com.demandware.xlt.actions.AbstractHtmlPageAction;
import com.demandware.xlt.util.Page;
import com.demandware.xlt.validators.Validator;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.xceptance.xlt.api.util.elementLookup.Results;

/**
 * Selects a category from the top navigation menu.
 * 
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 * 
 */
public class SelectCategory extends AbstractHtmlPageAction
{
    /** Level 2 navigation sub categories. */
    private static final String LEVEL2_CATEGORY = ".level-1 .level-2 > li > a";

    /** Level 3 navigation sub categories. */
    private static final String LEVEL3_CATEGORY = ".level-1 .level-2 .level-3 > li > a";

    /** Custom link in category menu. */
    private static final String CUSTOM_CATEGORY = ".level-1 .custom > a";

    /** All category links. */
    private static final String CATEGORY_LINK = LEVEL2_CATEGORY + "," + LEVEL3_CATEGORY + "," + CUSTOM_CATEGORY;
    
    /**
     * Locate all category links.
     */
    public static Results getCategoryLinks()
    {
        return  Page.find().byId("navigation").byCss(CATEGORY_LINK);
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
        // Get a link from the Page and click it
        categoryLink = getCategoryLinks().asserted("No category link in navigation found!").random();
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
