package com.dumbster.smtp.mailstores;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.dumbster.smtp.MailMessage;
import com.dumbster.smtp.MailStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RollingMailStore implements MailStore {
    private static final Logger __l = LoggerFactory.getLogger(RollingMailStore.class);


    private List<MailMessage> receivedMail;

    public RollingMailStore() {
        receivedMail = Collections.synchronizedList(new ArrayList<MailMessage>());
    }

    public int getEmailCount() {
        return receivedMail.size();
    }

    public void addMessage(MailMessage message) {
        __l.info("\n\nReceived message:\n" + message);
        receivedMail.add(message);
        if (getEmailCount() > 100) {
            receivedMail.remove(0);
        }
    }

    public MailMessage[] getMessages() {
        return receivedMail.toArray(new MailMessage[receivedMail.size()]);
    }

    public MailMessage getMessage(int index) {
        return receivedMail.get(index);
    }

    @Override
    public void clearMessages() {
        this.receivedMail.clear();
    }

    @Override
    public void deleteMessage(int index) {
        try {
            receivedMail.remove(index);
        } catch (Exception e) {
            __l.error("Exception deleting message", e);
        }
    }
}
