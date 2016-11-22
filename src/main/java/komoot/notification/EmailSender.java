package komoot.notification;

import com.google.common.collect.Lists;
import it.ozimov.springboot.templating.mail.model.Email;
import it.ozimov.springboot.templating.mail.model.impl.EmailImpl;
import it.ozimov.springboot.templating.mail.service.EmailService;
import komoot.notification.jpa.NotificationEntity;
import komoot.notification.jpa.SubscriberEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

@Component
public class EmailSender {

    @Autowired
    private EmailService emailService;

    @Autowired @Value("${emailFrom}")
    private String emailFrom;

    @Autowired @Value("${emailTo}")
    private String emailTo;

    @Autowired @Value("${emailSalutation}")
    private String emailSalutation;


    private SimpleDateFormat formatter;

    public EmailSender() {
        this.formatter = new SimpleDateFormat("EEEE, HH:mm", Locale.ENGLISH);
    }

    public void sendEmail(SubscriberEntity subscriber, List<NotificationEntity> notifications) throws AddressException {
        String salutation = salutation(subscriber);
        String text = buildText(salutation, notifications);
        final Email email = EmailImpl.builder()
                .from(new InternetAddress(emailFrom))
                .replyTo(new InternetAddress(emailFrom))
                .to(Lists.newArrayList(new InternetAddress(emailTo)))
                .subject(salutation)
                .body(text)
                .encoding(Charset.forName("UTF-8")).build();

        emailService.send(email);
    }

    private String salutation(SubscriberEntity subscriber){
        String name = subscriber == null ? "Anonymous" : subscriber.getName();
        return emailSalutation.replace("{0}", name);
    }

    private String buildText(String salutation, List<NotificationEntity> notifications){
        StringBuilder textBuilder = new StringBuilder().append(salutation)
                .append("\n\n");
        for (NotificationEntity notification : notifications) {
            textBuilder.append(capilalize(formatter.format(notification.getTimestamp())))
                    .append("\t\t" + notification.getMessage()).append("\n");
        }
        return textBuilder.toString();
    }

    private String capilalize(String string){
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }
}
