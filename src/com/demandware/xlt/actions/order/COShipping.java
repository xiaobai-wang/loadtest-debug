package com.demandware.xlt.actions.order;

import com.demandware.xlt.actions.AbstractHtmlPageAction;
import com.demandware.xlt.util.Account;
import com.demandware.xlt.util.Address;
import com.demandware.xlt.util.AjaxUtils;
import com.demandware.xlt.util.Context;
import com.demandware.xlt.util.FormUtils;
import com.demandware.xlt.util.Page;
import com.demandware.xlt.validators.Validator;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.xceptance.xlt.api.util.elementLookup.Results;

/**
 * Fills in the shipping address form.
 * 
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 * 
 */
public class COShipping extends AbstractHtmlPageAction
{
    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() throws Exception
    {
        // Fill the shipping address form.
        fillAddressForm();

        // Submit the form.
        final HtmlElement submitbutton = Page.findAsserted("No submit button found in shipping address form.")
                                             .byId("dwfrm_singleshipping_shippingAddress")
                                             .byXPath("./fieldset/div/button[@id='btn_submit_shipping']")
                                             .single();
        loadPageByClick(submitbutton);
    }

    /**
     * Fill the shipping address form and perform the necessary XHR calls.
     */
    private void fillAddressForm() throws Exception
    {
        // Get necessary account and address data.
        final Account account = Context.getAccount();
        final Address address = account.getAddress();

        // Fill first name.
        FormUtils.setInputValueByID("dwfrm_singleshipping_shippingAddress_addressFields_firstName", account.getFirstName());
        // Fill last name.
        FormUtils.setInputValueByID("dwfrm_singleshipping_shippingAddress_addressFields_lastName", account.getLastName());
        // Fill street and house number.
        FormUtils.setInputValueByID("dwfrm_singleshipping_shippingAddress_addressFields_address1", address.getStreet());
        // Select country.
        FormUtils.selectByID("dwfrm_singleshipping_shippingAddress_addressFields_country", address.getCountryCode());
        // Select state.
        FormUtils.selectByID("dwfrm_singleshipping_shippingAddress_addressFields_states_state", address.getStateCode());
        // Fill city.
        FormUtils.setInputValueByID("dwfrm_singleshipping_shippingAddress_addressFields_city", address.getTown());

        // Update shipping methods.
        AjaxUtils.getApplicableShippingMethods();

        // Fill ZIP code.
        FormUtils.setInputValueByID("dwfrm_singleshipping_shippingAddress_addressFields_zip", address.getZipCode());

        // Update shipping methods.
        AjaxUtils.getApplicableShippingMethods();
        AjaxUtils.updateShippingMethodsList();

        // Fill phone number.
        FormUtils.setInputValueByID("dwfrm_singleshipping_shippingAddress_addressFields_phone", address.getPhoneNo());

        // Don't save address (for registered customers only).
        final Results addressBookCheckbox = Page.find().byId("dwfrm_singleshipping_shippingAddress_addToAddressBook");
        if (addressBookCheckbox.exists())
        {
            addressBookCheckbox.<HtmlCheckBoxInput> single().setChecked(false);
        }

        // Use same address for billing.
        FormUtils.checkCheckboxByID("dwfrm_singleshipping_shippingAddress_useAsBillingAddress", true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void postValidate() throws Exception
    {
        // Check it is the billing form page.
        Validator.validateBillingCheckoutStep();
    }
}
