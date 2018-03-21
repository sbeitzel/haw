package com.dumbster.smtp.mailstores;
        /*
         * Copyright 3/21/18 by Stephen Beitzel
         */

import com.dumbster.smtp.MailStore;
import javafx.beans.property.SimpleIntegerProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Stephen Beitzel &lt;sbeitzel@pobox.com&gt;
 */
public abstract class AbstractMailStore implements MailStore {
    private static final Logger __l = LoggerFactory.getLogger(AbstractMailStore.class);

    private SimpleIntegerProperty _messageCount = new SimpleIntegerProperty(0);
    private SimpleIntegerProperty _totalReceived = new SimpleIntegerProperty(0);

    public Integer getMessageCount() {
        return _messageCount.getValue();
    }

    public Integer getTotalReceived() {
        return _totalReceived.getValue();
    }

    void setMessageCount(int number) {
        _messageCount.set(number);
    }

    void incrementTotalReceived() {
        int r = _totalReceived.get() + 1;
        _totalReceived.set(r);
    }
}
