package komoot.notification.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import komoot.notification.jpa.NotificationDAO;
import komoot.notification.jpa.SubscriberDAO;
import komoot.notification.jpa.SubscriberEntity;
import komoot.notification.model.ErrorResponse;
import komoot.notification.model.Notification;
import komoot.notification.rest.cron.schedule.NotificationSummaryCron;
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
    private final ObjectMapper mapper;

    @Autowired
    public NotificationController(SubscriberSession subscriberSession, NotificationSession notificationSession, ObjectMapper mapper) {
        this.subscriberSession = subscriberSession;
        this.notificationSession = notificationSession;
        this.mapper = mapper;
    }



    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity notificationConsumer(
            @RequestHeader(value="x-amz-sns-message-type") String messageType,
            @RequestBody String message) {

        //Ignore not notification messages
        if(!Objects.equals(messageType, "Notification")) return ResponseEntity.ok(null);

        Notification notification = getNotificationFromString(message);
        SubscriberEntity subscriber = subscriberSession.createOrGetSubscriber(notification);
        notificationSession.storeNotificationForSubscriber(notification, subscriber);

        return ResponseEntity.ok(null);

    }

    private Notification getNotificationFromString(String message){
        try {
            return mapper.readValue(message, Notification.class);
        } catch (IOException e) {
            logger.error("Error unmarskalling message: " + message, e);
            throw new NotificationException( "Error unmarskalling message: " + message, ErrorResponse.Error.UNMARSHALLING);
        }
    }
}
