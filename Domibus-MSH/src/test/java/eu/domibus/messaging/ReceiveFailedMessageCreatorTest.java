package eu.domibus.messaging;

import eu.domibus.api.jms.JmsMessage;
import eu.domibus.common.NotificationType;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * Created by Cosmin Baciu on 02-Sep-16.
 */
@RunWith(JMockit.class)
public class ReceiveFailedMessageCreatorTest {

    @Test
    public void testCreateMessage() throws Exception {
        ReceiveFailedMessageCreator creator = new ReceiveFailedMessageCreator("myMessageId", "myEndpoint");
        JmsMessage message = creator.createMessage();
        assertEquals(message.getProperty(MessageConstants.MESSAGE_ID), "myMessageId");
        assertEquals(message.getProperty(MessageConstants.ENDPOINT), "myEndpoint");
        assertEquals(message.getProperty(MessageConstants.NOTIFICATION_TYPE), NotificationType.MESSAGE_RECEIVED_FAILURE.name());
    }
}