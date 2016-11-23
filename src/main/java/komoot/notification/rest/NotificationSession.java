package komoot.notification.rest;

import komoot.notification.jpa.NotificationDAO;
import komoot.notification.jpa.NotificationEntity;
import komoot.notification.jpa.SubscriberEntity;
import komoot.notification.model.sns.CustomMessage;
import komoot.notification.model.sns.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class NotificationSession {

    private final NotificationDAO notificationDAO;

    @Autowired
    public NotificationSession(NotificationDAO notificationDAO) {
        this.notificationDAO = notificationDAO;
    }

    NotificationEntity storeNotificationForSubscriber(CustomMessage customMessage, SubscriberEntity subscriber){
        NotificationEntity notificationEntity = new NotificationEntity(customMessage, subscriber);
        notificationDAO.save(notificationEntity);
        return notificationEntity;
    }

}
