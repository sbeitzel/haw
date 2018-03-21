package com.dumbster.smtp.mailstores;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.dumbster.smtp.MailMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a holdover from the original Dumbster project. It is, essentially, a FixedSizeMailStore with a
 * size of 100.
 */
public class RollingMailStore extends AbstractMailStore {
    private static final Logger __l = LoggerFactory.getLogger(RollingMailStore.class);


    private List<MailMessage> receivedMail;

    public RollingMailStore() {
        receivedMail = Collections.synchronizedList(new ArrayList<MailMessage>());
    }

    @Override
    public int getEmailCount() {
        return receivedMail.size();
    }

    @Override
    public void addMessage(MailMessage message) {
        __l.info("\n\nReceived message:\n" + message);
        receivedMail.add(message);
        incrementTotalReceived();
        if (getEmailCount() > 100) {
            receivedMail.remove(0);
        }
        setMessageCount(receivedMail.size());
    }

    @Override
    public MailMessage[] getMessages() {
        return receivedMail.toArray(new MailMessage[receivedMail.size()]);
    }

    @Override
    public MailMessage getMessage(int index) {
        return receivedMail.get(index);
    }

    @Override
    public void clearMessages() {
        this.receivedMail.clear();
        setMessageCount(0);
    }

    @Override
    public void deleteMessage(int index) {
        try {
            receivedMail.remove(index);
            setMessageCount(receivedMail.size());
        } catch (Exception e) {
            __l.error("Exception deleting message", e);
        }
    }
}
