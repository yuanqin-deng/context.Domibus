package eu.domibus.ext.rest;

import eu.domibus.ext.domain.MessageAttemptDTO;
import eu.domibus.ext.exceptions.MessageMonitorException;
import eu.domibus.ext.services.MessageMonitorService;
import eu.domibus.logging.DomibusLogger;
import eu.domibus.logging.DomibusLoggerFactory;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Cosmin Baciu
 * @since 3.3
 */
@RestController
@RequestMapping(value = "/ext/monitoring/messages")
public class MessageMonitoringResource {

    public static final DomibusLogger LOG = DomibusLoggerFactory.getLogger(MessageMonitoringResource.class);

    @Autowired
    MessageMonitorService messageMonitorService;

    @RequestMapping(path = "/failed", method = RequestMethod.GET)
    public List<String> getFailedMessages(@RequestParam(value = "finalRecipient", required = false) String finalRecipient) throws MessageMonitorException {
        if (StringUtils.isNotEmpty(finalRecipient)) {
            return messageMonitorService.getFailedMessages(finalRecipient);
        } else {
            return messageMonitorService.getFailedMessages();
        }
    }

    @RequestMapping(path = "/failed/{messageId:.+}/elapsedtime", method = RequestMethod.GET)
    public Long getFailedMessageInterval(@PathVariable(value = "messageId") String messageId) throws MessageMonitorException {
        return messageMonitorService.getFailedMessageInterval(messageId);
    }

    @RequestMapping(path = "/failed/{messageId:.+}/restore", method = RequestMethod.PUT)
    public void restoreFailedMessage(@PathVariable(value = "messageId") String messageId) throws MessageMonitorException {
        messageMonitorService.restoreFailedMessage(messageId);
    }

    @RequestMapping(path = "/failed/restore", method = RequestMethod.POST)
    public List<String> restoreFailedMessages(@RequestBody FailedMessagesCriteriaRO failedMessagesCriteriaRO) throws MessageMonitorException {
        return messageMonitorService.restoreFailedMessagesDuringPeriod(failedMessagesCriteriaRO.getFromDate(), failedMessagesCriteriaRO.getToDate());
    }

    @RequestMapping(path = "/failed/{messageId:.+}", method = RequestMethod.DELETE)
    public void deleteFailedMessage(@PathVariable(value = "messageId") String messageId) throws MessageMonitorException {
        messageMonitorService.deleteFailedMessage(messageId);
    }

    @RequestMapping(path = "/{messageId:.+}/attempts", method = RequestMethod.GET)
    public List<MessageAttemptDTO> getMessageAttempts(@PathVariable(value = "messageId") String messageId) throws MessageMonitorException {
        return messageMonitorService.getAttemptsHistory(messageId);
    }
}