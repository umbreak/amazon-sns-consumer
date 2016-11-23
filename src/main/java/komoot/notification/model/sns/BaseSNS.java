package komoot.notification.model.sns;

import com.amazonaws.services.sns.util.SignatureChecker;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.mail.util.BASE64DecoderStream;
import komoot.notification.NotificationUtils;
import lombok.Data;
import lombok.ToString;
import org.apache.tomcat.util.codec.binary.Base64;

import java.io.InputStream;
import java.net.URL;
import java.security.Signature;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

@Data
@ToString
public class BaseSNS {

    @JsonIgnore
    private static final SimpleDateFormat formatter = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    static{
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    @JsonProperty("SigningCertURL")
    private String signingCertURL;

    @JsonProperty("MessageId")
    private String messageId;

    @JsonProperty("Message")
    private String message;

    @JsonProperty("Type")
    private String type;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "GMT")
    @JsonProperty("Timestamp")
    private Date timestamp;

    @JsonProperty("TopicArn")
    private String topicArn;

    @JsonProperty("SignatureVersion")
    private String signatureVersion;

    @JsonProperty("Signature")
    private String signature;


    @JsonIgnore
    public boolean isMessageSignatureValid() throws Exception{
        SignatureChecker signatureChecker = new SignatureChecker();
        URL url = new URL(getSigningCertURL());
        InputStream inStream = url.openStream();
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509Certificate cert = (X509Certificate)cf.generateCertificate(inStream);
        inStream.close();
        return signatureChecker.verifySignature(getMessageBytesToSign(), cert.getPublicKey());
    }

    @JsonIgnore
    private Map<String,String> getMessageBytesToSign () {
        if (this instanceof Notification)
            return ((Notification) this).buildNotificationStringToSign();
        else if (this instanceof SubscriptionConfirmation)
            return ((SubscriptionConfirmation) this).buildNotificationStringToSign();
        return null;
    }

    @JsonIgnore
    public String getTimestampIntoString(){
        return formatter.format(getTimestamp());
    }
}
