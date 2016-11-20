package komoot.notification.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface NotificationDAO extends JpaRepository<NotificationEntity, Long>{
    List<NotificationEntity> findByStatusAndOwnerName(NotificationEntity.Status status, String name);
    List<NotificationEntity> findByStatusAndOwnerEmail(NotificationEntity.Status status, String email);

}
