package komoot.notification.jpa;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@Table(uniqueConstraints= @UniqueConstraint(columnNames = {"email"}))
public class SubscriberEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    long id;

    String name;

    String email;

    public SubscriberEntity() {}

    public SubscriberEntity(String email, String name) {
        this.email = email;
        this.name = name;
    }

}
