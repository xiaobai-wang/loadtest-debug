package com.demandware.xlt.flows;

import com.demandware.xlt.actions.account.GoToSignUp;
import com.demandware.xlt.actions.account.Logout;
import com.demandware.xlt.actions.account.Register;
import com.demandware.xlt.util.Context;

/**
 * Registers a new customer.
 * 
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 */
public class RegisterFlow extends AbstractFlow
{
    /**
     * {@inheritDoc}
     */
    @Override
    public void run() throws Throwable
    {
        // create new account
        Context.initNewAccount();

        // go to login / sign up page
        // new GoToLoginSignUp().run();

        // Goto registration form.
        new GoToSignUp().run();

        // Fill and submit the registration form.
        new Register().run();

        // Logout finally.
        new Logout().run();
    }
}
