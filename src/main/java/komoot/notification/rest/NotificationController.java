package komoot.notification.rest;

import komoot.notification.URLUtils;
import komoot.notification.jpa.SubscriberEntity;
import komoot.notification.model.ErrorResponse;
import komoot.notification.model.Notification;
import komoot.notification.model.sns.SubscriptionConfirmation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Objects;

@RestController
@RequestMapping("/notification")
public class NotificationController {

    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);


    private final SubscriberSession subscriberSession;
    private final NotificationSession notificationSession;
    private final SNSMessageDeserializer deserializer;

    @Autowired
    public NotificationController(SubscriberSession subscriberSession, NotificationSession notificationSession, SNSMessageDeserializer deserializer) {
        this.subscriberSession = subscriberSession;
        this.notificationSession = notificationSession;
        this.deserializer = deserializer;
    }



    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity notificationConsumer(
            @RequestHeader(value="x-amz-sns-message-type") String messageType,
            @RequestBody String message) {

        logger.info("Message type==" + messageType + " message==" + message);

        //Ignore not notification messages
        if(Objects.equals(messageType, "SubscriptionConfirmation")){
            handleSunscription(message);
        }else if(Objects.equals(messageType, "Notification")) {
            handleNotification(message);
        }
        return ResponseEntity.ok(null);
    }


    private void handleSunscription(String message){
        SubscriptionConfirmation subscription = deserializer.getSubscriptionFromString(message);
        logger.info("subscription==" + subscription);
        try {
            URLUtils.getStringFromUrl(subscription.getSubscribeURL());
        } catch (IOException e) {
            logger.error("Error fetching URL: " + subscription.getSubscribeURL(), e);
            throw new NotificationException("Error fetching URL: " + subscription.getSubscribeURL(), ErrorResponse.Error.SUBSCRIBING_PROCESS);
        }

    }

    private void handleNotification(String message){
        Notification notification = deserializer.getNotificationFromString(message);
        SubscriberEntity subscriber = subscriberSession.createOrGetSubscriber(notification);
        notificationSession.storeNotificationForSubscriber(notification, subscriber);
    }

}
