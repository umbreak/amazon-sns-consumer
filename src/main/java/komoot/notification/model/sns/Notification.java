package komoot.notification.model.sns;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import komoot.notification.model.ErrorResponse;
import komoot.notification.rest.NotificationController;
import komoot.notification.rest.NotificationException;
import lombok.Data;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@ToString
@Data
public class Notification extends BaseSNS{

    @JsonIgnore
    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);

    @JsonProperty("Subject")
    private String subject;

    @JsonProperty("UnsubscribeURL")
    private String unsubscribeURL;

    @JsonIgnore
    private CustomMessage customMessage;

    public Notification() {}

    public Notification(CustomMessage message) throws JsonProcessingException {
        customMessage = message;
        this.setMessage(new ObjectMapper().writeValueAsString(customMessage));
        this.setTimestamp(new Date());
        this.setSignatureVersion("1");
    }

    @JsonIgnore
    public Map<String, String> buildNotificationStringToSign() {
        Map<String, String> result = new HashMap<>();
        result.put("Message", getMessage());
        result.put("MessageId", getMessageId());
        if(subject != null){
            result.put("Subject", subject);
        }
        result.put("UnsubscribeURL", unsubscribeURL);
        result.put("Signature",getSignature());
        result.put("SignatureVersion", getSignatureVersion());
        result.put("Timestamp", (getTimestampIntoString()));
        result.put("TopicArn", getTopicArn());
        result.put("Type", getType());
        return result;
    }

    @JsonIgnore
    public CustomMessage getCustomMessage() {
        if(customMessage != null) return customMessage;
        try {
            customMessage = new ObjectMapper().readValue(getMessage(), CustomMessage.class);
            return customMessage;
        } catch (IOException e) {
            logger.error("Error unmarshalling custom message " + getMessage(), e);
            throw new NotificationException("Error unmarshalling custom message " + getMessage(), ErrorResponse.Error.UNMARSHALLING);
        }
    }
}