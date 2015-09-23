package com.demandware.xlt.util;

import java.util.UUID;

import org.apache.commons.lang.RandomStringUtils;

import com.xceptance.common.util.CsvUtils;
import com.xceptance.xlt.api.data.GeneralDataProvider;

/**
 * Default implementation of {@link Account}.
 * 
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 */
public class Account
{
    /**
     * Email address
     */
    private final String email;

    /**
     * First name
     */
    private final String firstname;

    /**
     * Last name
     */
    private final String lastname;

    /**
     * Password
     */
    private String password;

    /**
     * Registered flag.
     */
    private boolean isRegistered;

    /**
     * Primary address.
     */
    private final Address address1;

    /**
     * Secondary address
     */
    private final Address address2;

    /**
     * Borderfree address
     */
    private final BorderfreeAddress borderfree;

    /**
     * Create generic generated account. This account will be based on the configured account (email address, password,
     * registration state) if any.
     */
    public Account()
    {
        this(//email
             Context.getConfiguration().getProperty("account.email",                                
                                                    RandomStringUtils.randomAlphanumeric(2)
                                                    + UUID.randomUUID().toString().replaceAll("-", "").substring(0, 12)
                                                        + "@dwtest.com"),
              //password
             Context.getConfiguration().getProperty("account.password", "Xc1-Demandware1"),//
             //firstname 
             GeneralDataProvider.getInstance().getFirstName(false),
             //lastname 
             GeneralDataProvider.getInstance().getLastName(false),
             //registered
             Context.getConfiguration().getProperty("account.isRegistered", false),
             //address1
             Address.getPrimaryAddress(),
             //address2             
             Address.getSecondaryAddress(),
             // borderfree address
             BorderfreeAddressProvider.getInstance().getAddress()
             );
    }

    /**
     * Create an account with the given properties.
     * 
     * @param email
     *            email address
     * @param password
     *            password
     * @param firstname
     *            first name
     * @param lastname
     *            last name
     * @param registered
     *            is it an account of an already registered customer
     * @param address1
     *            primary address
     * @param address2
     *            secondary address
     * @param borderfree
     *            address to use for Borderfree shipping
     */
    private Account(final String email, final String password, final String firstname, final String lastname, final boolean registered,
        final Address address1, final Address address2, final BorderfreeAddress borderfree)
    {
        this.firstname = firstname;
        this.lastname = lastname;
        this.password = password;
        this.isRegistered = registered;
        this.email = email;
        this.address1 = address1;
        this.address2 = address2;
        this.borderfree = borderfree;
    }

    /**
     * Create an account from CSV field elements:
     * <ol start="0">
     * <li>Email</li>
     * <li>Password</li>
     * <li>First name</li>
     * <li>Last name</li>
     * </ol>
     * such an account is always assumed to be an account of an already registered customer.
     * 
     * @param fields
     */
    private Account(final String[] fields)
    {
        this(fields[0],// email
             fields[1],// password
             fields[2],// firstname
             fields[3],// lastname
             true,// registered
             Address.getPrimaryAddress(), // address1
             Address.getSecondaryAddress(), // address2
             BorderfreeAddressProvider.getInstance().getAddress() // borderfree address
        );
    }

    /**
     * Get the email address.
     * 
     * @return the email address
     */
    public String getEmail()
    {
        return email;
    }

    /**
     * Get the first name.
     * 
     * @return the first name
     */
    public String getFirstName()
    {
        return firstname;
    }

    /**
     * Get the last name.
     * 
     * @return the last name
     */
    public String getLastName()
    {
        return lastname;
    }

    /**
     * Get the password.
     * 
     * @return the password
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
    public static Account fromCsv(String row)
    {
        // Decode the given string and check the minimal required length before
        // creating the account object.
        final String[] fields = CsvUtils.decode(row);
        if (fields.length > 3)
        {
            return new Account(fields);
        }

        // If the row doesn't have the minimal required length the account
        // creation is not possible
        return null;
    }

    /**
     * Convert account data to CSV format.
     * 
     * @return to CSV format converted account.
     */
    public String toCsv()
    {
        return CsvUtils.encode(new String[]
        {
            email, password, firstname, lastname
        });
    }

    /**
     * Set the account password.
     * 
     * @param password
     */
    public void setPassword(final String password)
    {
        this.password = password;
    }

    /**
     * Set registration state.
     * 
     * @param isRegistered
     *            is this an account of an already registered customer
     */
    public void setRegistered(boolean isRegistered)
    {
        this.isRegistered = isRegistered;
    }

    /**
     * Marks this account as registered.
     */
    public void setRegistered()
    {
        setRegistered(true);
    }

    /**
     * Returns whether or not this account is a registered one. Unregistered accounts are also known as guest accounts.
     * 
     * @return <code>true</code> if this account is a registered one, <code>false</code> otherwise
     */
    public boolean isRegistered()
    {
        return isRegistered;
    }

    /**
     * Get the primary address for this account.
     * 
     * @return primary address
     */
    public Address getAddress()
    {
        return address1;
    }

    /**
     * Get the secondary address for this account.
     * 
     * @return secondary address
     */
    public Address getAddress2()
    {
        return address2;
    }

    /**
     * Get the address used for Borderfree checkout
     * 
     * @return address used for Borderfree checkout
     */
    public BorderfreeAddress getBorderfreeAddress()
    {
        return borderfree;
    }
}
