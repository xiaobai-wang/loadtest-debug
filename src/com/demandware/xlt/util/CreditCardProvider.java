package com.demandware.xlt.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.RandomStringUtils;

import com.xceptance.xlt.api.data.GeneralDataProvider;
import com.xceptance.xlt.api.util.XltProperties;
import com.xceptance.xlt.api.util.XltRandom;

/**
 * Credit card provider.
 * 
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 */
public class CreditCardProvider
{
    /** List of available credit cards. */
    private final List<CreditCard> cards = new ArrayList<CreditCard>();

    /** Default constructor. Declared private to prevent external instantiation. */
    private CreditCardProvider()
    {
        // Access provider via singleton only.
    }

    /**
     * Returns the provider instance.
     * 
     * @return provider instance
     */
    public static CreditCardProvider getInstance()
    {
        return Singleton_Holder._INSTANCE;
    }

    /**
     * Returns a randomly chosen credit card.
     * 
     * @return credit card
     */
    public CreditCard getCreditCard()
    {
        return cards.get(XltRandom.nextInt(cards.size()));
    }

    /**
     * Initialize provider.
     */
    private void init()
    {
        // Since the credit card properties have a serial number this counter tracks the current serial while parsing
        // the cards.
        int i = 0;
        while (true)
        {
            // Get all properties belonging to the specific credit card configuration (identified by the serial number).
            final Map<String, String> props = XltProperties.getInstance().getPropertiesForKey("cc." + (i++));

            // Serial numbers have to be in steadily row. So if the expected (next) credit card is not present, all
            // configured credit cards must have been read.
            if (props.isEmpty())
            {
                break;
            }

            // Get the needed credit card attributes.
            final String type = props.get("type");
            final String number = props.get("number");
            final String issueNumber = props.get("issuenr");
            String cvn = props.get("cvn");
            String expYear = props.get("year");
            String expMonth = props.get("month");
            String owner = props.get("owner");

            // Check that the necessary attributes are given. Skip silently 'bad' cards.
            if (!(StringUtils.isEmpty(type) || StringUtils.isEmpty(number)))
            {
                // Create a credit card object described by the configured attributes. If an attribute is missing
                // generate a value on the fly.
                final CreditCard cc = new CreditCard();

                // Set the fix attributes.
                cc.setType(type.trim());
                cc.setNumber(number.trim());
                cc.setIssueNumber(issueNumber);

                // CVN. If no CVN is configured generate a random 3 digit number.
                if (StringUtils.isBlank(cvn))
                {
                    cvn = RandomStringUtils.randomNumeric(3);
                }
                cc.setCVN(cvn);

                // Year. If no year is configured take the very next year from NOW.
                if (StringUtils.isBlank(expYear))
                {
                    final Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(new Date().getTime());
                    expYear = String.valueOf(cal.get(Calendar.YEAR) + 2);
                }
                cc.setExpirationYear(expYear);

                // Month. If no moth is configured set a random month.
                if (StringUtils.isBlank(expMonth))
                {
                    expMonth = String.valueOf(XltRandom.nextInt(1, 12));
                }
                cc.setExpirationMonth(expMonth);

                // Owner. If no owner is configured generate a random owner name by first name and last name.
                if (StringUtils.isBlank(owner))
                {
                    owner = new StringBuffer().append(GeneralDataProvider.getInstance().getFirstName(false)).append(" ")
                                              .append(GeneralDataProvider.getInstance().getLastName(false)).toString();
                }
                cc.setOwner(owner);

                // Add the credit card to the card pool.
                cards.add(cc);
            }
        }
    }

    /**
     * Hold the singleton cart provider to enable lazy instantiation.
     */
    private static class Singleton_Holder
    {
        /** Singleton */
        private static final CreditCardProvider _INSTANCE;

        /** Initialize singleton */
        static
        {
            _INSTANCE = new CreditCardProvider();
            _INSTANCE.init();
        }
    }
}
