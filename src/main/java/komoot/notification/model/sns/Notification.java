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

    @JsonProperty("default")
    private String defaultMessage;

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
}