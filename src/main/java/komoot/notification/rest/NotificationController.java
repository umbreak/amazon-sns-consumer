package komoot.notification.rest;

import komoot.notification.NotificationUtils;
import komoot.notification.jpa.SubscriberEntity;
import komoot.notification.model.ErrorResponse;
import komoot.notification.model.sns.Notification;
import komoot.notification.model.sns.BaseSNS;
import komoot.notification.model.sns.SubscriptionConfirmation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    private final Boolean verifySignature;

    @Autowired
    public NotificationController(SubscriberSession subscriberSession, NotificationSession notificationSession, SNSMessageDeserializer deserializer, @Value("${verifySignature}") Boolean verifySignature) {
        this.subscriberSession = subscriberSession;
        this.notificationSession = notificationSession;
        this.deserializer = deserializer;
        this.verifySignature = verifySignature;
    }



    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity notificationConsumer(
            @RequestHeader(value="x-amz-sns-message-type") String messageType,
            @RequestBody String message) {

        logger.info("Message type==" + messageType + " message==" + message);

        BaseSNS snsMessage = deserializer.getGenericSNSMessage(message, messageType);


        checkValidMessage(snsMessage, message);

        //Ignore not notification messages
        if(snsMessage instanceof SubscriptionConfirmation){
            handleSunscription(snsMessage);
        }else if(snsMessage instanceof Notification) {
            handleNotification(snsMessage);
        }
        return ResponseEntity.ok(null);
    }

    private void checkValidMessage(BaseSNS snsMessage, String originalMessage){
        if(!Objects.equals(snsMessage.getSignatureVersion(), "1"))
            throw new NotificationException("Wrong version signature" , ErrorResponse.Error.WRONG_SIGNATURE);
        if(verifySignature){
            try {
                if(!snsMessage.isMessageSignatureValid())
                    wrongSignature(null);
            } catch (Exception e) {
                wrongSignature(e);
            }
        }
    }

    private void wrongSignature(Exception e){
        logger.error("Wrong signature", e);
        throw new NotificationException("Wrong signature" , ErrorResponse.Error.WRONG_SIGNATURE);

    }


    private void handleSunscription(BaseSNS message){
        SubscriptionConfirmation subscription = (SubscriptionConfirmation)message;
        try {
            NotificationUtils.getStringFromUrl(subscription.getSubscribeURL());
        } catch (IOException e) {
            logger.error("Error fetching URL: " + subscription.getSubscribeURL(), e);
            throw new NotificationException("Error fetching URL: " + subscription.getSubscribeURL(), ErrorResponse.Error.SUBSCRIBING_PROCESS);
        }

    }

    private void handleNotification(BaseSNS message){
        Notification notification = (Notification)message;
        SubscriberEntity subscriber = subscriberSession.createOrGetSubscriber(notification);
        notificationSession.storeNotificationForSubscriber(notification, subscriber);
    }

}
