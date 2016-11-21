package komoot.notification.rest.cron.schedule;

import komoot.notification.jpa.NotificationEntity;
import komoot.notification.jpa.SubscriberEntity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


public class NotificationsSplitter {


    private Queue<NotificationEntity> notifications;

    List<List<NotificationEntity>> finalList;

    private NotificationsSplitter(List<NotificationEntity> notifications) {
        this.notifications = new LinkedList<>(notifications);
        finalList = new ArrayList<>();
    }

    public static List<List<NotificationEntity>> generate(List<NotificationEntity> notifications){
        NotificationsSplitter splitter = new NotificationsSplitter(notifications);
        splitter.getList();
        return splitter.finalList;
    }

    private void getList(){
        if(!notifications.isEmpty()){
            SubscriberEntity newSubscriber = notifications.peek().getOwner();
            List<NotificationEntity> currentSubscriberNotifications = new ArrayList<>();
            finalList.add(currentSubscriberNotifications);
            addNotificationToCurrentSubscriber(newSubscriber, currentSubscriberNotifications);
        }
    }


    private boolean addNotificationToCurrentSubscriber(SubscriberEntity subscriber, List<NotificationEntity> currentSubscriberNotifications){
        if(notifications.isEmpty()) return true;
        SubscriberEntity newSubscriber = notifications.peek().getOwner();
        if(newSubscriber.getId() == subscriber.getId()){
            currentSubscriberNotifications.add(notifications.poll());
        }else{
            currentSubscriberNotifications = new ArrayList<>();
            finalList.add(currentSubscriberNotifications);
        }
        return addNotificationToCurrentSubscriber(newSubscriber, currentSubscriberNotifications);
    }
}
