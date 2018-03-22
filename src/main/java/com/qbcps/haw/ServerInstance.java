package com.qbcps.haw;
        /*
         * Copyright 3/21/18 by Stephen Beitzel
         */

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.dumbster.pop.POPServer;
import com.dumbster.smtp.SmtpServer;
import com.dumbster.smtp.mailstores.AbstractMailStore;
import com.dumbster.smtp.mailstores.EMLMailStore;
import com.dumbster.smtp.mailstores.FixedSizeMailStore;
import com.dumbster.smtp.mailstores.NullMailStore;
import javafx.beans.property.SimpleIntegerProperty;
import org.apache.commons.configuration2.AbstractConfiguration;
import org.apache.commons.configuration2.MapConfiguration;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Data object representing a mailstore and an SMTP/POP3 server pair. It takes on the role
 * of the factories for the servers as well as providing a shutdown hook for the servers.
 *
 * @author Stephen Beitzel &lt;sbeitzel@pobox.com&gt;
 */
public class ServerInstance {
    private static final Logger __l = LoggerFactory.getLogger(ServerInstance.class);

    public static final String PROP_NUM_SMTP_THREADS = "dumbster.SMTP.numThreads";
    public static final String PROP_NUM_POP3_THREADS = "dumbster.POP3.numThreads";
    public static final String PROP_SMTP_PORT = "dumbster.SMTP.port";
    public static final String PROP_POP3_PORT = "dumbster.POP3.port";
    public static final String PROP_MAILSTORE_CLASS = "dumbster.mailstore.impl";
    public static final String PROP_SERVER_SOCKET_TIMEOUT = "dumbster.serversocket.timeout";
    public static final String PROP_MAXSIZE = "dumbster.FixedSizeMailStore.size";
    public static final String PROP_MAIL_DIR = "dumbster.EMLMailStore.directory";
    public static final String PROP_NULL_SETTING = "dumbster.ignore";

    private final SmtpServer _smtpServer;
    private final POPServer _popServer;
    private final AbstractMailStore _mailStore;
    private SimpleIntegerProperty _popPort = new SimpleIntegerProperty();
    private SimpleIntegerProperty _smtpPort = new SimpleIntegerProperty();
    private ExecutorService _executor;

    public static ServerInstance startService(AbstractConfiguration config) {
        int smtpPort = config.getInt(PROP_SMTP_PORT);
        int pop3Port = config.getInt(PROP_POP3_PORT);
        int smtpThreads = config.getInt(PROP_NUM_SMTP_THREADS);
        int pop3Threads = config.getInt(PROP_NUM_POP3_THREADS);
        int socketTimeout = config.getInt(PROP_SERVER_SOCKET_TIMEOUT);
        String msClass = config.getString(PROP_MAILSTORE_CLASS);

        // need a MailStore first
        AbstractMailStore store;
        try {
            // first, see if there is a constructor that takes an AbstractConfiguration
            Constructor ctor;
            try {
                ctor = (Class.forName(msClass).getDeclaredConstructor(AbstractConfiguration.class));
                ctor.setAccessible(true);
                store = (AbstractMailStore) ctor.newInstance(config);
            } catch (Exception e) {
                __l.info("Unable to find constructor that takes a configuration; trying no-arg constructor");
                store = (AbstractMailStore) Class.forName(msClass).newInstance();
            }
        } catch (Exception e) {
            __l.error("Unable to set up mail store", e);
            store = new NullMailStore();
        }
        ServerInstance servers = new ServerInstance(smtpPort, pop3Port, smtpThreads, pop3Threads, store, socketTimeout);
        // now wait until they're up and running before returning the instance
        while (!servers._smtpServer.isReady() && !servers._popServer.isReady()) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                __l.error("Interrupted while starting POP3 server", e);
            }
        }
        return servers;
    }

    public static ServerInstance startService(JSONObject params) {
        HashMap<String, Object> pMap = new HashMap<>();
        MapConfiguration config = new MapConfiguration(pMap);
        config.setListDelimiterHandler(new DefaultListDelimiterHandler(','));

        config.addProperty(PROP_NUM_SMTP_THREADS, params.get(PROP_NUM_SMTP_THREADS));
        config.addProperty(PROP_NUM_POP3_THREADS, params.get(PROP_NUM_POP3_THREADS));
        config.addProperty(PROP_SMTP_PORT, params.get(PROP_SMTP_PORT));
        config.addProperty(PROP_POP3_PORT, params.get(PROP_POP3_PORT));
        final Object msClassName = params.get(PROP_MAILSTORE_CLASS);
        config.addProperty(PROP_MAILSTORE_CLASS, msClassName);
        config.addProperty(PROP_SERVER_SOCKET_TIMEOUT, params.get(PROP_SERVER_SOCKET_TIMEOUT));
        if (FixedSizeMailStore.class.getName().equals(msClassName)) {
            config.addProperty(PROP_MAXSIZE, params.get(PROP_MAXSIZE));
        } else if (EMLMailStore.class.getName().equals(msClassName)) {
            config.addProperty(PROP_MAIL_DIR, params.get(PROP_MAIL_DIR));
        }
        return startService(config);
    }

    public JSONObject getParameters() {
        JSONObject params = new JSONObject();
        params.put(PROP_SMTP_PORT, _smtpPort.get());
        params.put(PROP_NUM_SMTP_THREADS, _smtpServer.getThreadCount());
        params.put(PROP_POP3_PORT, _popPort.get());
        params.put(PROP_NUM_POP3_THREADS, _popServer.getThreadCount());
        params.put(PROP_SERVER_SOCKET_TIMEOUT, _smtpServer.getSocketTimeout());
        params.put(PROP_MAILSTORE_CLASS, _mailStore.getClass().getName());
        if (_mailStore instanceof FixedSizeMailStore) {
            params.put(PROP_MAXSIZE, ((FixedSizeMailStore)_mailStore).getMaxSize());
        } else if (_mailStore instanceof EMLMailStore) {
            params.put(PROP_MAIL_DIR, ((EMLMailStore) _mailStore).getDirectory().getAbsolutePath());
        }
        return params;
    }

    private ServerInstance(int smtpPort, int pop3Port, int smtpThreads, int pop3Threads, AbstractMailStore mailStore, int socketTimeout) {
        _mailStore = mailStore;
        _popPort.set(pop3Port);
        _smtpPort.set(smtpPort);
        _smtpServer = new SmtpServer(smtpPort, smtpThreads, mailStore, socketTimeout);
        _popServer = new POPServer(pop3Port, pop3Threads, mailStore, socketTimeout);

        _executor = new ThreadPoolExecutor(2, 2, 5, TimeUnit.MILLISECONDS,
                                           new LinkedBlockingDeque<>(), r -> {
                                               Thread t = new Thread(r);
                                               t.setDaemon(true);
                                               return t;
                                           });
        _executor.execute(_smtpServer);
        _executor.execute(_popServer);
    }

    public void stop() {
        _smtpServer.stop();
        __l.info("Dumbster SMTP Server on port {} stopped", Integer.valueOf(_smtpServer.getPort()));
        __l.info("Total messages received: {}", Integer.valueOf(_smtpServer.getEmailCount()));
        _popServer.stop();
        __l.info("Dumbster POP3 Server on port {} stopped", Integer.valueOf(_popServer.getPort()));
        _executor.shutdownNow();
    }

    @SuppressWarnings("unused")
    public Integer getSmtpPort() {
        return _smtpPort.getValue();
    }

    @SuppressWarnings("unused")
    public Integer getPopPort() {
        return _popPort.getValue();
    }

    @SuppressWarnings("unused")
    public Integer getMessageCount() {
        return _mailStore.getMessageCount();
    }

    @SuppressWarnings("unused")
    public Integer getTotalReceived() {
        return _mailStore.getTotalReceived();
    }
}
