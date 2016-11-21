package komoot.notification.rest.cron.schedule;

import komoot.notification.EmailSender;
import komoot.notification.jpa.NotificationDAO;
import komoot.notification.jpa.NotificationEntity;
import komoot.notification.jpa.SubscriberEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.internet.AddressException;
import java.util.List;


@Component
@ComponentScan
public class NotificationSummaryCron {
    private static final Logger logger = LoggerFactory.getLogger(NotificationSummaryCron.class);

    private final EmailSender mailSender;
    private final NotificationDAO notificationDAO;

    @Autowired
    public NotificationSummaryCron(EmailSender mailSender, NotificationDAO notificationDAO) {
        this.notificationDAO = notificationDAO;
        this.mailSender = mailSender;
    }


    @Scheduled(initialDelay = 0,fixedRateString = "${scheduler.email}")
    @Transactional
    public void subscriptionTaskChecker(){
        logger.info("Start checking subcriptions");

        List<NotificationEntity> notificationsEntities = notificationDAO.findByStatusOrderByOwnerIdAsc(NotificationEntity.Status.NOT_SENT);

        List<List<NotificationEntity>> notificationsGroupedBySubscriber = NotificationsSplitter.generate(notificationsEntities);

        if(notificationsGroupedBySubscriber != null){
            for (List<NotificationEntity> notifications : notificationsGroupedBySubscriber) {
                boolean result = sendNotificationGroupThroughEmail(notifications);
                if(result){
                    updateNotificationsStatus(notifications);
                }
            }
        }
    }

    private boolean sendNotificationGroupThroughEmail(List<NotificationEntity> notifications){
        if(notifications == null || notifications.isEmpty()) return false;

        SubscriberEntity subscriber = notifications.get(0).getOwner();
        try {
            mailSender.sendEmail(subscriber, notifications);
            return true;
        } catch (AddressException e) {
            logger.error("Failed sending notifications: " + notifications + " to subscriber: " + subscriber, e);
            return false;
        }
    }

    private void updateNotificationsStatus(List<NotificationEntity> notifications){
        for (NotificationEntity notification : notifications) {
            notification.setStatus(NotificationEntity.Status.SENT);
            notificationDAO.updateStatus(notification.getStatus(), notification.getId());
        }
    }
}
