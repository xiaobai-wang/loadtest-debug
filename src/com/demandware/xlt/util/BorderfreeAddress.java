package com.demandware.xlt.util;

import com.xceptance.common.util.CsvUtils;

/**
 * Default implementation of {@link BorderfreeAddress}.
 * 
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 */
public class BorderfreeAddress extends Address
{
    /** State */
    private final String state;

    /** Country */
    private final String country;

    /**
     * Create a address for Borderfree shipping based on the given data.
     * 
     * @param street
     *            street
     * @param town
     *            city
     * @param zipCode
     *            ZIP code
     * @param state
     *            state (e.g. Massachusetts)
     * @param stateCode
     *            state code (e.g. MA)
     * @param country
     *            country (e.g. United State)
     * @param countryCode
     *            country code (e.g. US)
     * @param phone
     *            phone number
     */
    protected BorderfreeAddress(final String street, final String town, final String zipCode, final String state, final String stateCode,
        final String country, final String countryCode,
        final String phone)
    {
        super(street, town, zipCode, stateCode, countryCode, phone);

        this.state = state;
        this.country = country;
    }

    /**
     * Get the country (for example 'United States').
     * 
     * @return country
     */
    public String getCountry()
    {
        return country;
    }

    /**
     * Get the state.
     * 
     * @return state
     */
    public String getState()
    {
        return state;
    }

    public static BorderfreeAddress fromCsv(final String csvRow)
    {
        final String[] cols = CsvUtils.decode(csvRow);
        if (cols.length == 8)
        {
            return new BorderfreeAddress(cols[0], cols[1], cols[2], cols[3], cols[4], cols[5], cols[6], cols[7]);
        }
        return null;
    }
}
