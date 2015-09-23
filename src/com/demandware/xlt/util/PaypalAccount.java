package com.demandware.xlt.util;

import com.xceptance.common.util.CsvUtils;

/**
 * PayPal account data.
 * 
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 */
public class PaypalAccount
{
    /**
     * Email address
     */
    private final String email;

    /**
     * Password
     */
    private final String password;

    /**
     * Field elements:
     * <ol start="0">
     * <li>Email</li>
     * <li>Password</li>
     * </ol>
     * 
     * @param fields
     */
    private PaypalAccount(final String[] fields)
    {
        email = fields[0];
        password = fields[1];
    }

    /**
     * Get the account's email address.
     * 
     * @return email address
     */
    public String getEmail()
    {
        return email;
    }

    /**
     * Get the account password.
     * 
     * @return account password
     */
    public String getPassword()
    {
        return password;
    }

    /**
     * Generates an account from a CSV row.
     * 
     * @param row
     *            a CSV row
     * @return the account generated from the given CSV row or <code>null</code> if account cannot be created from given
     *         string.
     */
    public static PaypalAccount fromCsv(final String row)
    {
        // Decode the given string and check the minimal required length before creating the account object.
        final String[] fields = CsvUtils.decode(row);
        if (fields.length >= 2)
        {
            return new PaypalAccount(fields);
        }

        // If the row doesn't have the minimal required length the account creation is not possible.
        return null;
    }

    /**
     * Convert the current PayPal account data into CSV values.
     * 
     * @return CVS values representing the PayPal account
     */
    public String toCsv()
    {
        return CsvUtils.encode(new String[]
            {
             email, password
            });
    }
}
