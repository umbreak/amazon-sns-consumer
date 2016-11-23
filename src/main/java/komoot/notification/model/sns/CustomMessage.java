package komoot.notification.model.sns;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

@ToString
@Data
public class CustomMessage {
    private String email;
    private String name;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "GMT")
    private Date timestamp;
    private String message;

    public CustomMessage() {}

    public CustomMessage(String email, String name, String message) {
        this.email = email;
        this.name = name;
        this.message = message;
        this.timestamp = new Date();
    }
}
