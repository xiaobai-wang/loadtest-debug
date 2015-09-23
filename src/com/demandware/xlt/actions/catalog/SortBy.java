package com.demandware.xlt.actions.catalog;

import org.junit.Assert;

import com.demandware.xlt.actions.AbstractAjaxAction;
import com.demandware.xlt.util.AjaxUtils;
import com.demandware.xlt.util.Page;
import com.demandware.xlt.validators.Validator;
import com.gargoylesoftware.htmlunit.html.HtmlElement;

/**
 * Sort results on a grid page.
 * 
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 */
public class SortBy extends AbstractAjaxAction
{
    /** XPath for a sort option that is currently NOT selected. */
    private static final String SORT_OPTION_NOT_SELECTED_XPATH = "id('grid-sort-header')/option[not(@selected)]";

    /** XPath for a sort option that is currently selected. */
    private static final String SORT_OPTION_SELECTED_XPATH = "id('grid-sort-header')/option[@selected]";

    /**
     * Checks if sorting is possible. </br><b>NOTE: Only works on a product grid page</b>
     * <p>
     * <b>Page:</b> use this on a product grid page.
     * </p>
     * 
     * @return <code>true</code> if sorting is possible, <code>false</code> otherwise
     */
    public static boolean isPossible()
    {
        return Page.checkXPathExistanceInPage(SORT_OPTION_NOT_SELECTED_XPATH);
    }

    /**
     * The chosen sort option.
     */
    private HtmlElement sortOption;

    /**
     * The chosen sort option's name.
     */
    private String targetSortOptionName;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() throws Exception
    {
        // Get a random sort option.
        sortOption = Page.find().byXPath(SORT_OPTION_NOT_SELECTED_XPATH).asserted("No sorting option found in page.").random();

        // Remember the option's name.
        targetSortOptionName = sortOption.getTextContent().trim();

        // Perform the AJAX call to update the page.
        AjaxUtils.callAndUpdate(sortOption.getAttribute("value"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void postValidate() throws Exception
    {
        Validator.validateCommonPage();

        // Check the resulting page has the desired sort option marked as selected. Do this by comparing the desired
        // option's name with the current selected one.
        final String currentSortOptionName = Page.find().byXPath(SORT_OPTION_SELECTED_XPATH)
                                                 .asserted("No selected sorting option found in page.").single().getTextContent().trim();
        Assert.assertEquals("Unexpected sort result.", targetSortOptionName, currentSortOptionName);

        // Check that there's still a product results grid page. Sorting should not change the type of page.
        Assert.assertTrue("Sorting didn't result in product grid page.", Page.isProductGridPage());
    }
}
