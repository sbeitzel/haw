package com.dumbster.pop.action;

import com.dumbster.pop.POPState;
import com.dumbster.pop.Response;
import com.dumbster.smtp.MailStore;

/**
 * POP3 action interface
 */
public interface Action {
    
    public String getCommand();

    public Response response(POPState popState, MailStore mailStore);
}
