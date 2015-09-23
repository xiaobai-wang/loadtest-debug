package com.demandware.xlt.actions.catalog;

import org.junit.Assert;

import com.demandware.xlt.actions.AbstractAjaxAction;
import com.demandware.xlt.util.AjaxUtils;
import com.demandware.xlt.util.Context;
import com.demandware.xlt.util.Page;
import com.demandware.xlt.validators.Validator;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.xceptance.xlt.api.engine.Session;
import com.xceptance.xlt.api.util.elementLookup.Results;

/**
 * Changes result page size.
 * 
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 */
public class ChangeItemsPerPage extends AbstractAjaxAction
{
    /** Locate the items-per-page section. */
    private static Results locateItemsPerPageSection()
    {
        return Page.find().byId("grid-paging-header");
    }

    /** Locate any unselected items-per-page option on the product grid page. */
    private static Results locateUnselectedItemsPerPageOptions()
    {
        return locateItemsPerPageSection().byXPath("./option[not(@selected)]");
    }

    /** Locate the currently selected items-per-page-option. */
    private static Results locateSelectedItemsPerPageOption()
    {
        return locateItemsPerPageSection().byXPath("./option[@selected]");
    }

    /**
     * Checks if changing of the displayed items per grid page is possible. </br><b>NOTE: Only works on a product grid
     * page</b>
     * <p>
     * <b>Page:</b> use this on a product grid page.
     * </p>
     * 
     * @return <code>true</code> if changing the page size is possible, <code>false</code> otherwise.
     */
    public static boolean isPossible()
    {
        return Page.checkExistance(locateUnselectedItemsPerPageOptions());
    }

    /**
     * The options name.
     */
    private String targetOptionName;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() throws Exception
    {
        // Get a random unselected page size option.
        final HtmlElement itemsPerPageOption = locateUnselectedItemsPerPageOptions().asserted("No change item per page option found in page.")
                                                                                    .random();
        // Remember the option name.
        targetOptionName = itemsPerPageOption.getTextContent().trim();

        // Select the option. This performs an XHR and updates the page.
        AjaxUtils.callAndUpdate(itemsPerPageOption.getAttribute("value"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void postValidate() throws Exception
    {
        // Check that it is a result grid page since changing the size will not
        // change the type.
        Validator.validateCommonPage();
        Assert.assertTrue("Change items per page doesn't result in product grid page.", Page.isProductGridPage());

        // Check that the desired page size is the selected one.
        final String currentOptionName = locateSelectedItemsPerPageOption().asserted("No selected change item per page option found in page.")
                                                                           .random().getTextContent().trim();
        if (!targetOptionName.equals(currentOptionName))
        {
            Session.logEvent("Unexpected results size.",
                             "expected:" + targetOptionName + ", current:" + currentOptionName + " -> " + Context.getPage().getUrl());
        }
    }
}
