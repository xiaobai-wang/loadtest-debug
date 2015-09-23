package com.demandware.xlt.flows;

import org.junit.Assert;

import com.demandware.xlt.actions.order.paypal.paypal1.Paypal1Agree;
import com.demandware.xlt.actions.order.paypal.paypal1.Paypal1Login;
import com.demandware.xlt.actions.order.paypal.paypal1.Paypal1PayNow;
import com.demandware.xlt.actions.order.paypal.paypal2.Paypal2Login;
import com.demandware.xlt.actions.order.paypal.paypal2.Paypal2PayNow;
import com.demandware.xlt.util.PaypalAccount;
import com.demandware.xlt.util.PaypalAccountManager;

/**
 * Checkout with PayPal.
 * 
 * @author Bernd Weigel (Xceptance Software Technologies GmbH)
 */
public class PaypalFlow extends AbstractFlow
{
    /**
     * {@inheritDoc}
     */
    @Override
    public void run() throws Throwable
    {
        // Get exclusive PayPal account.
        final PaypalAccount paypalAccount = PaypalAccountManager.getInstance().getAccount();

        try
        {
            // PayPal sometimes returns 2 variants of their site that look the same but behave different below the
            // surface. We have to decide what variant we've got.
            if (Paypal1Login.isPossible())
            {
                // Login.
                new Paypal1Login(paypalAccount).run();

                // Press "Pay Now" button.
                new Paypal1PayNow().run();

                // Agree to terms and conditions if necessary.
                if (Paypal1Agree.isPossible())
                {
                    new Paypal1Agree().run();
                }
            }
            else if (Paypal2Login.isPossible())
            {
                // // Login.
                final Paypal2Login ppLogin = new Paypal2Login(paypalAccount);
                ppLogin.run();

                // Press "Pay Now" button.
                new Paypal2PayNow(ppLogin.getLoginJson()).run();
            }
            else
            {
                // Page did not match do either variant 1 or variant 2.
                Assert.fail("Unsupported PayPal login page.");
            }
        }
        finally
        {
            // Release PayPal account so it can be used by another test user.
            PaypalAccountManager.getInstance().addAccount(paypalAccount);
        }
    }
}
