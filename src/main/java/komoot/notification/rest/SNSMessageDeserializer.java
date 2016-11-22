package komoot.notification.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import komoot.notification.model.ErrorResponse;
import komoot.notification.model.sns.Notification;
import komoot.notification.model.sns.BaseSNS;
import komoot.notification.model.sns.SubscriptionConfirmation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;

@Component
public class SNSMessageDeserializer {

    private static final Logger logger = LoggerFactory.getLogger(SNSMessageDeserializer.class);


    private final ObjectMapper mapper;

    @Autowired
    public SNSMessageDeserializer(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public BaseSNS getGenericSNSMessage(String message, String messageType){
        if(Objects.equals(messageType, "SubscriptionConfirmation")){
            return getSubscriptionFromString(message);
        }else if(Objects.equals(messageType, "Notification")) {
            return getNotificationFromString(message);
        }
        return null;
    }

    private SubscriptionConfirmation getSubscriptionFromString(String message){
        try {
            return mapper.readValue(message, SubscriptionConfirmation.class);
        } catch (IOException e) {
            logger.error("Error unmarskalling message: " + message, e);
            throw new NotificationException( "Error unmarskalling message: " + message, ErrorResponse.Error.UNMARSHALLING);
        }
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
