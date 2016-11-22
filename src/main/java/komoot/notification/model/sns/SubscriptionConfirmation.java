package komoot.notification.model.sns;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

@Data
@ToString
public class SubscriptionConfirmation extends BaseSNS{

    @JsonProperty("Token")
    private String token;

    @JsonProperty("SubscribeURL")
    private String subscribeURL;

    @JsonIgnore
    public Map<String, String> buildNotificationStringToSign() {
        Map<String, String> result = new HashMap<>();
        result.put("Message", getMessage());
        result.put("MessageId", getMessageId());
        result.put("SubscribeURL", getSubscribeURL());
        result.put("SubscribeURL", subscribeURL);
        result.put("Signature",signature);
        result.put("SignatureVersion", getSignatureVersion());
        result.put("Timestamp", getTimestampString());
        result.put("Token", getToken());
        result.put("TopicArn", getTopicArn());
        result.put("Type", getType());
        return result;
    }

}
