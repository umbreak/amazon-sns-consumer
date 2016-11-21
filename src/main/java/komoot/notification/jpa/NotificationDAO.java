package komoot.notification.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Set;


public interface NotificationDAO extends JpaRepository<NotificationEntity, Long>{
    List<NotificationEntity> findByStatusAndOwnerEmail(NotificationEntity.Status status, String email);
    List<NotificationEntity> findByStatusOrderByOwnerIdAsc(NotificationEntity.Status status);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE NotificationEntity n set n.status =:status, n.statusDate =:date WHERE n.id in :ids")
    int updateStatus(@Param("status") NotificationEntity.Status status, @Param("date")  Date date, @Param("ids") Set<Long> ids);

}
