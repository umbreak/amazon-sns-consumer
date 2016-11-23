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
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


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

    //Starts after 60sec and run every scheduler.email (specified in properties file
    @Scheduled(initialDelay = 60000,fixedRateString = "${scheduler.email}")
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
        } catch (AddressException | UnsupportedEncodingException e) {
            logger.error("Failed sending notifications: " + notifications + " to subscriber: " + subscriber, e);
            return false;
        }
    }

    private void updateNotificationsStatus(List<NotificationEntity> notifications){
        Set<Long> mapIDs = notifications.stream().map(notification -> notification.getId()).collect(Collectors.toSet());
        notificationDAO.updateStatus(NotificationEntity.Status.SENT, new Date(), mapIDs);
    }
}
