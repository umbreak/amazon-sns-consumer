package komoot.notification.model.sns;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
public class SubscriptionConfirmation {

    @JsonProperty("SigningCertURL")
    private String signingCertURL;

    @JsonProperty("MessageId")
    private String messageId;

    @JsonProperty("Message")
    private String message;

    @JsonProperty("Type")
    private String type;

    @JsonProperty("Token")
    private String token;

    @JsonProperty("SignatureVersion")
    private String signatureVersion;

    @JsonProperty("SubscribeURL")
    private String subscribeURL;

    @JsonProperty("Signature")
    private String signature;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Europe/Berlin")
    private Date Timestamp;

    @JsonProperty("TopicArn")
    private String topicArn;
}
