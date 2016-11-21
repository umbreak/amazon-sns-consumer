package komoot.notification.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface NotificationDAO extends JpaRepository<NotificationEntity, Long>{
    List<NotificationEntity> findByStatusAndOwnerEmail(NotificationEntity.Status status, String email);
    List<NotificationEntity> findByStatusOrderByOwnerIdAsc(NotificationEntity.Status status);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE NotificationEntity n set n.status =:status WHERE n.id =:id")
    int updateStatus(@Param("status") NotificationEntity.Status status, @Param("id") long id);

}
