package komoot.notification.model.sns;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
public class SubscriptionConfirmation {
    private String Type;
    private String MessageId;
    private String Token;
    private String TopicArn;
    private String Message;
    private String SubscribeURL;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Europe/Berlin")
    private Date Timestamp;
    private String SignatureVersion;
    private String Signature;
    private String SigningCertURL;
}
