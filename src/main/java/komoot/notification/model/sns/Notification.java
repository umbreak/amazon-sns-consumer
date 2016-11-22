package komoot.notification.model.sns;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import komoot.notification.model.sns.BaseSNS;
import lombok.Data;
import lombok.ToString;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@ToString
@Data
public class Notification extends BaseSNS{

    private String email;

    @JsonProperty("Subject")
    private String subject;

    @JsonProperty("UnsubscribeURL")
    private String unsubscribeURL;

    private String name;

    public Notification() {}

    public Notification(String email, String name, String message) {
        this.email = email;
        this.name = name;
        this.message = message;
        this.setTimestamp(new Date());
        this.setSignatureVersion("1");
    }
    @JsonIgnore
    public Map<String, String> buildNotificationStringToSign() {
        Map<String, String> result = new HashMap<>();
        result.put("Message", getMessage());
        System.out.println("Message=" + getMessage());
        result.put("MessageId", getMessageId());
        System.out.println("MessageId=" + getMessageId());
        if(subject != null){
            result.put("Subject", subject);
        }
        result.put("UnsubscribeURL", unsubscribeURL);
        System.out.println("UnsubscribeURL=" + unsubscribeURL);
        result.put("Signature",signature);
        System.out.println("signature=" + signature);
        result.put("SignatureVersion", getSignatureVersion());
        System.out.println("SignatureVersion=" + getSignatureVersion() );
        result.put("Timestamp", (getTimestampString()));
        System.out.println("Timestamp===" + (getTimestampString()));
        result.put("TopicArn", getTopicArn());
        System.out.println("TopicArn=" + getTopicArn());
        result.put("Type", getType());
        System.out.println("Type=" + getType());
        return result;
    }
}