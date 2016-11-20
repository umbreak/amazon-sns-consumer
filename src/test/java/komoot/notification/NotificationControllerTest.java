package komoot.notification;

import komoot.notification.jpa.NotificationDAO;
import komoot.notification.jpa.NotificationEntity;
import komoot.notification.jpa.SubscriberDAO;
import komoot.notification.jpa.SubscriberEntity;
import komoot.notification.model.Notification;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
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
        String email = "aaa@example.com";
        String name = "Didac";
        String message = "This is a message sent by Merkel";
        Notification notification = new Notification(email, name, message);

        System.out.println(json(notification));
        mockMvc.perform(post("/notification")
                .header("x-amz-sns-message-type", "Notification")
                .contentType(contentType)
                .content(json(notification)).with(user("user1").password("secret1")))
                .andExpect(status().isOk());

        checkDBConsistency(notification);
    }

    @Test
    public void testPostNotificationWithoutHeader() throws Exception {
        String email = "aaa@example.com";
        String name = "Didac";
        String message = "This is a message sent by Merkel";
        Notification notification = new Notification(email, name, message);
        mockMvc.perform(post("/notification")
                .contentType(contentType)
                .content(json(notification)).with(user("user1").password("secret1")))
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
        Assert.assertEquals(1, listNotification.size());
        NotificationEntity entity = listNotification.get(0);
        Assert.assertEquals(notification.getMessage(), entity.getMessage());
        Assert.assertEquals(subscriptor.getId(), entity.getOwner().getId());
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
