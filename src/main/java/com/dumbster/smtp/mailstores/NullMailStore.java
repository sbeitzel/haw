package com.dumbster.smtp.mailstores;

import com.dumbster.smtp.MailMessage;

/**
 * Do-nothing implementation of MailStore
 */
public class NullMailStore extends AbstractMailStore {
    @Override
    public void addMessage(MailMessage message) {
        incrementTotalReceived();
    }

    @Override
    public int getEmailCount() {
        return 0;
    }

    @Override
    public MailMessage[] getMessages() {
        return new MailMessage[0];
    }

    @Override
    public MailMessage getMessage(int index) {
        return null;
    }

    @Override
    public void clearMessages() {
    }

    @Override
    public void deleteMessage(int index) {
    }
}
