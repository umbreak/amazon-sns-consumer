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

}
