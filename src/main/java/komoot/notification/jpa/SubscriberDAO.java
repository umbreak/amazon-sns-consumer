package komoot.notification.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface SubscriberDAO extends JpaRepository<SubscriberEntity, Long>{
    Optional<SubscriberEntity> findByName(String name);
    Optional<SubscriberEntity> findByEmail(String email);

}
