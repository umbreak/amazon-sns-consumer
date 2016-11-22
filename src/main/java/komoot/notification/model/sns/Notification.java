package komoot.notification.model.sns;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import komoot.notification.model.sns.BaseSNS;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

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
        if (getSubject() != null) {
            stringToSign += "Subject\n";
            stringToSign += getSubject() + "\n";
        }
        stringToSign += "Timestamp\n";
        stringToSign += getTimestamp() + "\n";
        stringToSign += "TopicArn\n";
        stringToSign += getTopicArn() + "\n";
        stringToSign += "Type\n";
        stringToSign += getType() + "\n";
        return stringToSign;
    }
}