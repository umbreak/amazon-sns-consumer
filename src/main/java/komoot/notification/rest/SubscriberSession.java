package komoot.notification.rest;

import komoot.notification.jpa.SubscriberDAO;
import komoot.notification.jpa.SubscriberEntity;
import komoot.notification.model.sns.CustomMessage;
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

    SubscriberEntity createOrGetSubscriber(CustomMessage customMessage){
        Optional<SubscriberEntity> optionEmail = subcriberDao.findByEmail(customMessage.getEmail());
        if(optionEmail.isPresent()) return optionEmail.get();
        return createSubscriber(customMessage);
    }

    private SubscriberEntity createSubscriber(CustomMessage customMessage){
        SubscriberEntity subscriberEntity = new SubscriberEntity(customMessage.getEmail(), customMessage.getName());
        subcriberDao.save(subscriberEntity);
        return subscriberEntity;
    }
}
