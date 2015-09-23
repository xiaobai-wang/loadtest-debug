package com.demandware.xlt.util;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import com.xceptance.xlt.api.data.DataProvider;
import com.xceptance.xlt.api.data.GeneralDataProvider;
import com.xceptance.xlt.api.engine.Session;

/**
 * Default implementation of {@link Address}.
 * 
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 */
public class Address
{
    /**
     * Street
     */
    private final String street;

    /**
     * ZIP code
     */
    private final String zipCode;

    /**
     * Phone number
     */
    private final String phone;

    /**
     * Town
     */
    private final String town;

    /**
     * State code
     */
    private final String stateCode;

    /**
     * Country code
     */
    private final String countryCode;

    /**
     * Create an address from the given data.
     * 
     * @param street
     *            street and house number
     * @param town
     *            city
     * @param zipCode
     *            ZIP code
     * @param stateCode
     *            state code
     * @param countryCode
     *            country code
     * @param phone
     *            phone number
     */
    protected Address(final String street, final String town, final String zipCode, final String stateCode, final String countryCode,
        final String phone)
    {
        this.street = StringUtils.defaultString(street, GeneralDataProvider.getInstance().getStreet(false));

        this.town = StringUtils.defaultString(town, GeneralDataProvider.getInstance().getTown(true));

        this.zipCode = StringUtils.defaultString(zipCode, GeneralDataProvider.getInstance().getZip(5));

        this.stateCode = StringUtils.defaultString(stateCode, getRandomStateCode());

        this.countryCode = StringUtils.defaultString(countryCode, "US");

        this.phone = StringUtils.defaultString(phone, "333-333-3333");
    }

    /**
     * Get new primary address. This address is taken from the configured address.1.* in 'project.properties' or filled
     * with random values if no such address is configured.
     * 
     * @return primary address
     */
    protected static Address getPrimaryAddress()
    {
        return new Address(Context.getConfiguration().getStreet(), Context.getConfiguration().getTown(), Context.getConfiguration()
                                                                                                                .getZipCode(),
                           Context.getConfiguration().getStateCode(), Context.getConfiguration().getCountryCode(),
                           Context.getConfiguration().getPhone());
    }

    /**
     * Get new secondary address. This address is taken from the configured address.2.* in 'project.properties' or
     * filled with random values if no such address is configured.
     * 
     * @return secondary address
     */
    protected static Address getSecondaryAddress()
    {
        return new Address(Context.getConfiguration().getStreet2(), Context.getConfiguration().getTown2(), Context.getConfiguration()
                                                                                                                  .getZipCode2(),
                           Context.getConfiguration().getStateCode2(), Context.getConfiguration().getCountryCode2(),
                           Context.getConfiguration().getPhone2());
    }

    /**
     * Get a random state code from predefined list.
     * 
     * @return state code or <code>null</code> if no state code is available
     */
    private String getRandomStateCode()
    {
        try
        {
            return DataProvider.getInstance("default/stateCodesUS.txt").getRandomRow();
        }
        catch (IOException e)
        {
            Session.logEvent("Cannot read state codes from file.", e.getMessage());
        }

        return null;
    }

    /**
     * Get the country code (for example 'US').
     * 
     * @return country code
     */
    public String getCountryCode()
    {
        return countryCode;
    }

    /**
     * Get the phone number.
     * 
     * @return phone number
     */
    public String getPhoneNo()
    {
        return phone;
    }

    /**
     * Get the house number and street.
     * 
     * @return house number and street
     */
    public String getStreet()
    {
        return street;
    }

    /**
     * Get the town.
     * 
     * @return town
     */
    public String getTown()
    {
        return town;
    }

    /**
     * Get the ZIP code.
     * 
     * @return ZIP code
     */
    public String getZipCode()
    {
        return zipCode;
    }

    /**
     * Get the state code (for example 'MA').
     * 
     * @return state code
     */
    public String getStateCode()
    {
        return stateCode;
    }
}
