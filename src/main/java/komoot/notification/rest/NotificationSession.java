package komoot.notification.rest;

import komoot.notification.jpa.NotificationDAO;
import komoot.notification.jpa.NotificationEntity;
import komoot.notification.jpa.SubscriberEntity;
import komoot.notification.model.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class NotificationSession {

    private final NotificationDAO notificationDAO;

    @Autowired
    public NotificationSession(NotificationDAO notificationDAO) {
        this.notificationDAO = notificationDAO;
    }

    NotificationEntity storeNotificationForSubscriber(Notification notification, SubscriberEntity subscriber){
        NotificationEntity notificationEntity = new NotificationEntity(notification, subscriber);
        notificationDAO.save(notificationEntity);
        return notificationEntity;
    }

}
