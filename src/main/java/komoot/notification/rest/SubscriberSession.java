package komoot.notification.rest;

import komoot.notification.jpa.SubscriberDAO;
import komoot.notification.jpa.SubscriberEntity;
import komoot.notification.model.sns.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SubscriberSession {


    private final SubscriberDAO subcriberDao;


    @Autowired
    public SubscriberSession(SubscriberDAO subcribedDao) {
        this.subcriberDao = subcribedDao;
    }

    SubscriberEntity createOrGetSubscriber(Notification notification){
        Optional<SubscriberEntity> optionEmail = subcriberDao.findByEmail(notification.getEmail());
        if(optionEmail.isPresent()) return optionEmail.get();
        return createSubscriber(notification);
    }

    private SubscriberEntity createSubscriber(Notification notification){
        SubscriberEntity subscriberEntity = new SubscriberEntity(notification.getEmail(), notification.getName());
        subcriberDao.save(subscriberEntity);
        return subscriberEntity;
    }
}
