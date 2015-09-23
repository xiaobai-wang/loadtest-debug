package com.demandware.xlt.util;

/**
 * Credit Cart
 * 
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 */
public class CreditCard
{
    /** Credit card CVN. */
    private String cvn;

    /** Credit card owner. */
    private String owner;

    /** Credit card number. */
    private String number;

    /** Month of credit card expiration. */
    private String expirationMonth;

    /** Year of credit card expiration. */
    private String expirationYear;

    /** Issue number. */
    private String issueNumber;

    /** Credit card type. */
    private String type;

    /**
     * Returns the credit card CVN.
     * 
     * @return credit card CVN
     */
    public String getCVN()
    {
        return cvn;
    }

    /**
     * Returns the month of credit card expiration.
     * 
     * @return month of expiration
     */
    public String getExpirationMonth()
    {
        return expirationMonth;
    }

    /**
     * Returns the year of credit card expiration.
     * 
     * @return year of expiration
     */
    public String getExpirationYear()
    {
        return expirationYear;
    }

    /**
     * Returns the credit card number.
     * 
     * @return credit card number
     */
    public String getNumber()
    {
        return number;
    }

    /**
     * Returns the credit card owner.
     * 
     * @return credit card owner
     */
    public String getOwner()
    {
        return owner;
    }

    /**
     * Set the credit card number.
     * 
     * @param number
     *            credit card number
     */
    public void setNumber(final String number)
    {
        this.number = number;
    }

    /**
     * Set the CVN.
     * 
     * @param cvn
     */
    public void setCVN(final String cvn)
    {
        this.cvn = cvn;
    }

    /**
     * Set the card owner.
     * 
     * @param owner
     */
    public void setOwner(final String owner)
    {
        this.owner = owner;
    }

    /**
     * Set the exiration month.
     * 
     * @param month
     */
    public void setExpirationMonth(final String month)
    {
        expirationMonth = month;
    }

    /**
     * Set the expiration year.
     * 
     * @param year
     */
    public void setExpirationYear(final String year)
    {
        expirationYear = year;
    }

    /**
     * Returns the issue number to be used for payment.
     * 
     * @return issue number
     */
    public String getIssueNumber()
    {
        return issueNumber;
    }

    /**
     * @param issueNumber
     *            the issueNumber to set
     */
    public void setIssueNumber(String issueNumber)
    {
        this.issueNumber = issueNumber;
    }

    /**
     * Returns the credit card type.
     * 
     * @return type of credit card.
     */
    public String getType()
    {
        return type;
    }

    /**
     * Set the type (for example 'Visa', 'Master' or 'Amex');
     * 
     * @param type
     *            credit card type
     */
    public void setType(final String type)
    {
        this.type = type;
    }
}
