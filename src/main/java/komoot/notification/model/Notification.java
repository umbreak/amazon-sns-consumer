package komoot.notification.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

@ToString
@Data
public class Notification {

    private String email;

    private String name;

    private String message;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Europe/Berlin")
    private Date timestamp;

    public Notification() {}

    public Notification(String email, String name, String message) {
        this.email = email;
        this.name = name;
        this.message = message;
        this.timestamp = new Date();
    }


}