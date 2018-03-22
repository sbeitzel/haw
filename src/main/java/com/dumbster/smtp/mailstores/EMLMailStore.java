package com.dumbster.smtp.mailstores;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.dumbster.smtp.MailMessage;
import com.dumbster.smtp.eml.EMLMailMessage;
import org.apache.commons.configuration2.AbstractConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.qbcps.haw.ServerInstance.PROP_MAIL_DIR;

/**
 * Store messages as EML files.
 * <br/>This class makes no guarantees as to the order of the received messages.
 * The messages are stored in order but getMessages won't return messages in the same order they were received.
 */
public class EMLMailStore extends AbstractMailStore {
    private static final Logger __l = LoggerFactory.getLogger(EMLMailStore.class);

    private boolean initialized;
    private int count = 0;
    private File directory = new File("eml_store");
    private List<MailMessage> messages = new ArrayList<MailMessage>();

    public EMLMailStore(AbstractConfiguration config) {
        setDirectory(config.getString(PROP_MAIL_DIR));
    }

    /**
     * Checks if mail mailStore is initialized and initializes it if it's not.
     */
    private void checkInitialized() {
        if (!initialized) {
            if (!directory.exists()) {
                directory.mkdirs();
            } else {
                loadMessages();
            }
            initialized = true;
        }
    }

    /**
     * Load previous messages from directory.
     */
    private void loadMessages() {
        File[] files = loadMessageFiles();

        for (File file : files) {
            MailMessage message = new EMLMailMessage(file);
            messages.add(message);
            incrementTotalReceived();
        }
        count = files.length;
        setMessageCount(count);
    }

    /**
     * Load message files from mailStore directory.
     * @return an array of {@code File}
     */
    private File[] loadMessageFiles() {
        File[] files = this.directory.listFiles(new EMLFilenameFilter());
        if (files == null) {
            __l.error("Unable to load messages from eml mailStore directory: " + directory);
            return new File[0];
        }
        return files;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getEmailCount() {
        checkInitialized();
        return count;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addMessage(MailMessage message) {
        checkInitialized();
        count++;
        messages.add(message);
        incrementTotalReceived();
        setMessageCount(count);

        __l.info("Received message: " + count);

        try {
            if (!directory.exists()) {
                System.out.println("Directory created: " + directory);
                directory.mkdirs();
            }
            String filename = getFilename(message, count);
            File file = new File(directory, filename);
            FileWriter writer = new FileWriter(file);

            for (Iterator<String> i = message.getHeaderNames(); i.hasNext();) {
                String name = i.next();
                String[] values = message.getHeaderValues(name);
                for (String value : values) {
                    writer.append(name);
                    writer.append(": ");
                    writer.append(value);
                    writer.append('\n');
                }
            }
            writer.append('\n');
            writer.append(message.getBody());
            writer.append('\n');

            writer.close();

        } catch (Exception e) {
            __l.error("Exception receiving message", e);
        }
    }

    public String getFilename(MailMessage message, int count) {
        String filename = new StringBuilder().append(count).append("_")
                .append(message.getFirstHeaderValue("Subject"))
                .append(".eml").toString();
        filename = filename.replaceAll("[\\\\/<>\\?>\\*\"\\|]", "_");
        return filename;
    }

    /**
     * Return a list of messages stored by this mail mailStore.
     * @return a list of {@code EMLMailMessage}
     */
    @Override
    public MailMessage[] getMessages() {
        checkInitialized();

        return messages.toArray(new MailMessage[messages.size()]);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MailMessage getMessage(int index) {
        return getMessages()[index];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearMessages() {
        for (File file : this.directory.listFiles(new EMLFilenameFilter())) {
            file.delete();
            count--;
        }
        messages.clear();
        setMessageCount(0);
    }

    @Override
    public void deleteMessage(int index) {
        throw new UnsupportedOperationException("EMLMailStore does not support deleting single messages.");
    }

    public void setDirectory(String directory) {
        setDirectory(new File(directory));
    }

    public void setDirectory(File directory) {
        this.directory = directory;
    }

    public File getDirectory() {
        return this.directory;
    }

    /**
     * Filter only files matching name of files saved by EMLMailStore.
     */
    public static class EMLFilenameFilter implements FilenameFilter {
        private final Pattern PATTERN = Pattern.compile("\\d+_.*\\.eml");
        private final Matcher MATCHER = PATTERN.matcher("");

        @Override
        public boolean accept(File dir, String name) {
            MATCHER.reset(name);
            return MATCHER.matches();
        }

    }
}
