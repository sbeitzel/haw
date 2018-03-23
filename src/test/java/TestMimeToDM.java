/*
 * Copyright 3/23/18 by Stephen Beitzel
 */

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import com.dumbster.smtp.eml.EMLMailMessage;
import org.apache.commons.io.IOUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author Stephen Beitzel &lt;sbeitzel@pobox.com&gt;
 */
public class TestMimeToDM {

    @Test
    public void testSpamToDumbsterMessage() throws Exception {
        InputStream is = Thread.currentThread().getContextClassLoader()
                               .getResourceAsStream("sampleMIMEmessage.txt");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        IOUtils.copy(is, baos);
        ByteArrayInputStream bis = new ByteArrayInputStream(baos.toByteArray());
        EMLMailMessage dumbsterMessage = new EMLMailMessage(bis);
        Assert.assertNotNull(dumbsterMessage.getHeaderValues("From"));
    }
}
