package komoot.notification.model.sns;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class SubscriptionConfirmation extends BaseSNS{

    @JsonProperty("Token")
    private String token;

    @JsonProperty("SubscribeURL")
    private String subscribeURL;

    @JsonIgnore
    public String buildNotificationStringToSign() {
        String stringToSign = null;
        //Build the string to sign from the values in the message.
        //Name and values separated by newline characters
        //The name value pairs are sorted by name
        //in byte sort order.
        stringToSign = "Message\n";
        stringToSign += getMessage() + "\n";
        stringToSign += "MessageId\n";
        stringToSign += getMessageId() + "\n";
        stringToSign += "SubscribeURL\n";
        stringToSign += getSubscribeURL() + "\n";
        stringToSign += "Timestamp\n";
        stringToSign += getTimestamp() + "\n";
        stringToSign += "Token\n";
        stringToSign += getToken() + "\n";
        stringToSign += "TopicArn\n";
        stringToSign += getTopicArn() + "\n";
        stringToSign += "Type\n";
        stringToSign += getType() + "\n";
        return stringToSign;
    }

}
