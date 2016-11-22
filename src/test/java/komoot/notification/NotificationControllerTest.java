package komoot.notification;

import komoot.notification.jpa.NotificationDAO;
import komoot.notification.jpa.NotificationEntity;
import komoot.notification.jpa.SubscriberDAO;
import komoot.notification.jpa.SubscriberEntity;
import komoot.notification.model.sns.BaseSNS;
import komoot.notification.model.sns.Notification;
import komoot.notification.model.sns.SubscriptionConfirmation;
import komoot.notification.rest.SNSMessageDeserializer;
import komoot.notification.rest.cron.schedule.NotificationSummaryCron;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
public class NotificationControllerTest {

    private MediaType contentType = new MediaType(MediaType.TEXT_PLAIN.getType(),
            MediaType.TEXT_PLAIN.getSubtype(),
            Charset.forName("utf8"));

    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {

        this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream().filter(
                hmc -> hmc instanceof MappingJackson2HttpMessageConverter).findAny().get();

        Assert.assertNotNull("the JSON message converter must not be null",
                this.mappingJackson2HttpMessageConverter);
    }

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private NotificationDAO notificationDAO;

    @Autowired
    SNSMessageDeserializer deserializer;

    @Autowired
    private NotificationSummaryCron notificationSummaryCron;

    @Autowired
    private SubscriberDAO subscriberDAO;

    @Autowired
    private EmailSender mailSender;

    private MockMvc mockMvc;

    @Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
    }


    @Test
    @Transactional
    public void testPostNotification() throws Exception {
        Notification notification = new Notification("aaa@example.com", "Didac", "This is a message sent by Merkel");
        sendNotification(notification);
        checkDBConsistency(notification);
    }

    @Test
    @Transactional
    public void testPostNotifications() throws Exception {
        List<Notification> notifications = Arrays.asList(
                new Notification("aaa@example.com", "Didac", "Hey, do you fanc partying tonight"),
                new Notification("aaa@example.com", "Didac", "YOLO!"),
                new Notification("bbb@example.com", "Mike", "I would like to have a beer"),
                new Notification("ccc@example.com", "Stephan", "No beer before 4 8english version sucks)"),
                new Notification("aaa@example.com", "Didac", "End of sadness == end of winter")
        );
        for (Notification notification : notifications) {
            sendNotification(notification);
            checkDBConsistency(notification);
        }
        Assert.assertEquals(3, subscriberDAO.count());
        Assert.assertEquals(notifications.size(), notificationDAO.count());
    }

    @Test
    @Transactional
    public void testPostNotificationsAndSendEmail() throws Exception {
        Date init = new Date();
        testPostNotifications();
        notificationSummaryCron.subscriptionTaskChecker();

        //Check that now all the status have been changed to sent
        Map<Long, Date> sentDate=new HashMap<>();
        Date now = new Date();
        notificationDAO.findAll().stream().forEach(notification -> {
                Assert.assertEquals(NotificationEntity.Status.SENT, notification.getStatus());
                Assert.assertTrue(notification.getStatusDate().after(init) && notification.getStatusDate().before(now));
                sentDate.put(notification.getId(), notification.getStatusDate());
            }
        );

        Thread.sleep(1000);
        notificationSummaryCron.subscriptionTaskChecker();

        //Check that the emails have not been sent again
        notificationDAO.findAll().stream().forEach(notification -> {
                Assert.assertEquals(NotificationEntity.Status.SENT, notification.getStatus());
                Assert.assertEquals(sentDate.get(notification.getId()), notification.getStatusDate());
            }
        );
    }

    @Test
    public void deserializeSubscription(){
        String jsonSubscription="{ \"Type\" : \"SubscriptionConfirmation\", \"MessageId\" : \"bf1e7813-2dda-4de1-8a03-c5d9ab9633e6\", \"Token\" : \"2336412f37fb687f5d51e6e241d44a2dc0dc1be77f70e0f7808517b9078d0de38fe0df916b101b6373c920912128b416703290f03cef4e206858e3edf52ca829cef08bf4d451ca8b88df3bd322ac73eebbddf5fbdc65b4640fdb8ddb82b4fed72bbf9d16da43b361ab801ac56fce011c5aa5d663359f53baff7242adf26087ceec7e999e7fd4cda7548b75e7c992b555\", \"TopicArn\" : \"arn:aws:sns:eu-west-1:374647430309:komoot-challenge-notifications\", \"Message\" : \"You have chosen to subscribe to the topic arn:aws:sns:eu-west-1:374647430309:komoot-challenge-notifications.\\nTo confirm the subscription, visit the SubscribeURL included in this message.\", \"SubscribeURL\" : \"https://sns.eu-west-1.amazonaws.com/?Action=ConfirmSubscription&TopicArn=arn:aws:sns:eu-west-1:374647430309:komoot-challenge-notifications&Token=2336412f37fb687f5d51e6e241d44a2dc0dc1be77f70e0f7808517b9078d0de38fe0df916b101b6373c920912128b416703290f03cef4e206858e3edf52ca829cef08bf4d451ca8b88df3bd322ac73eebbddf5fbdc65b4640fdb8ddb82b4fed72bbf9d16da43b361ab801ac56fce011c5aa5d663359f53baff7242adf26087ceec7e999e7fd4cda7548b75e7c992b555\", \"Timestamp\" : \"2016-11-22T20:11:44.050Z\", \"SignatureVersion\" : \"1\", \"Signature\" : \"RwCmkPvlKP6Ceh2VdrZXUiMNhQSLd8asj7WuEnoCchP7pPCzT9FXh0/z/NNXXkcGB4LXOXDRa2gTBqUQWWQdZ+rQXrUX53pi9a29okoGmhTQL9dAyILwGrfsrhUAqvh86J37PRt/bFZT4lq7oIG+ag6SYvVvZ0TEn5JQK921OCr2onDJOQ9eLlJVAP/dmA2c0UFWj1DGh5gtvcXLi8poh0zRvlCQNpVw7yn/xEvITF5iHwORARTsT2ZSIw6PbqE4fRPROEP/TjOUusAHp+pMbvHFHbm3izQRQkYnavX+LpjKpynKs7sMpN/RLxIJOuXaaM5zqHZHxwzE63iTZxaI5A==\", \"SigningCertURL\" : \"https://sns.eu-west-1.amazonaws.com/SimpleNotificationService-b95095beb82e8f6a046b3aafc7f4149a.pem\" }";
        BaseSNS baseSNS = deserializer.getGenericSNSMessage(jsonSubscription, "SubscriptionConfirmation");
        Assert.assertTrue(baseSNS instanceof SubscriptionConfirmation);
        SubscriptionConfirmation subscription = (SubscriptionConfirmation) baseSNS;
        Assert.assertEquals("SubscriptionConfirmation", subscription.getType());
        Assert.assertEquals("2016-11-22T20:11:44.050Z", subscription.getTimestampString());
        Assert.assertEquals("https://sns.eu-west-1.amazonaws.com/?Action=ConfirmSubscription&TopicArn=arn:aws:sns:eu-west-1:374647430309:komoot-challenge-notifications&Token=2336412f37fb687f5d51e6e241d44a2dc0dc1be77f70e0f7808517b9078d0de38fe0df916b101b6373c920912128b416703290f03cef4e206858e3edf52ca829cef08bf4d451ca8b88df3bd322ac73eebbddf5fbdc65b4640fdb8ddb82b4fed72bbf9d16da43b361ab801ac56fce011c5aa5d663359f53baff7242adf26087ceec7e999e7fd4cda7548b75e7c992b555", subscription.getSubscribeURL());

    }

    private void sendNotification(Notification notification) throws Exception {
        mockMvc.perform(post("/notification")
                .header("x-amz-sns-message-type", "Notification")
                .contentType(contentType)
                .content(json(notification)))
                .andExpect(status().isOk());
    }

    @Test
    public void testPostNotificationWithoutHeader() throws Exception {
        String email = "aaa@example.com";
        String name = "Didac";
        String message = "This is a message sent by Merkel";
        Notification notification = new Notification(email, name, message);
        mockMvc.perform(post("/notification")
                .contentType(contentType)
                .content(json(notification)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    public void testSendEmail() throws Exception {
        testPostNotification();
        SubscriberEntity subscriberEntity = subscriberDAO.findAll().get(0);
        List<NotificationEntity> notifications = notificationDAO.findAll();
        mailSender.sendEmail(subscriberEntity, notifications);

    }


    private void checkDBConsistency(Notification notification){
        SubscriberEntity subscriberEntity = checkBDSubscription(notification);
        checkDBNotification(notification, subscriberEntity);
    }

    private void checkDBNotification(Notification notification, SubscriberEntity subscriptor){
        List<NotificationEntity> listNotification = notificationDAO.findByStatusAndOwnerEmail(NotificationEntity.Status.NOT_SENT, notification.getEmail());
        Optional<NotificationEntity> dbNotificationOption = listNotification.stream().filter(not -> not.getMessage().equals(notification.getMessage())).findFirst();
        Assert.assertTrue(dbNotificationOption.isPresent());
        NotificationEntity dbNotification = dbNotificationOption.get();
        Assert.assertEquals(notification.getMessage(), dbNotification.getMessage());
        Assert.assertEquals(subscriptor.getId(), dbNotification.getOwner().getId());
    }

    private SubscriberEntity checkBDSubscription(Notification notification){
        Optional<SubscriberEntity> subscriptionOption = subscriberDAO.findByEmail(notification.getEmail());
        Assert.assertTrue(subscriptionOption.isPresent());

        SubscriberEntity subscriberEntity = subscriptionOption.get();
        Assert.assertEquals(notification.getName(), subscriberEntity.getName());
        return subscriberEntity;
    }


    protected String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }

}
