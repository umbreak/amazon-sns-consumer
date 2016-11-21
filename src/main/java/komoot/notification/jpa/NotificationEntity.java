package komoot.notification.jpa;
import komoot.notification.model.Notification;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
public class NotificationEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    long id;

    @ManyToOne(fetch=FetchType.LAZY,cascade = CascadeType.PERSIST)
    @JoinColumn(name="subscriber_id")
    SubscriberEntity owner;

    String message;

    Date timestamp;

    Status status;

    Date statusDate;

    public NotificationEntity() {}

    public NotificationEntity(Notification notification, SubscriberEntity subscriber){
        message = notification.getMessage();
        timestamp = notification.getTimestamp();
        owner = subscriber;
        status = Status.NOT_SENT;
        statusDate = new Date();
    }

    public enum Status{
        NOT_SENT, SENDING, SENT
    }

}
