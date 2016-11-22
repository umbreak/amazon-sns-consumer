package komoot.notification.model.sns;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import komoot.notification.NotificationUtils;
import lombok.Data;
import lombok.ToString;
import org.apache.tomcat.util.codec.binary.Base64;

import java.io.InputStream;
import java.net.URL;
import java.security.Signature;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;

@Data
@ToString
public class BaseSNS {
    @JsonProperty("SigningCertURL")
    String signingCertURL;

    @JsonProperty("MessageId")
    String messageId;

    @JsonProperty("Message")
    String message;

    @JsonProperty("Type")
    String type;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Europe/Berlin")
    Date Timestamp;

    @JsonProperty("TopicArn")
    String topicArn;

    @JsonProperty("SignatureVersion")
    private String signatureVersion;

    @JsonProperty("Signature")
    String signature;

    @JsonIgnore
    public boolean isMessageSignatureValid() throws Exception{
        URL url = new URL(getSigningCertURL());
        InputStream inStream = url.openStream();
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509Certificate cert = (X509Certificate)cf.generateCertificate(inStream);
        inStream.close();

        Signature sig = Signature.getInstance("SHA1withRSA");
        sig.initVerify(cert.getPublicKey());
        sig.update(getMessageBytesToSign());
        return sig.verify(Base64.decodeBase64(getSignature()));
    }

    @JsonIgnore
    private byte [] getMessageBytesToSign () {
        String stringToSign = null;
        if (this instanceof Notification)
            stringToSign = ((Notification) this).buildNotificationStringToSign();
        else if (this instanceof SubscriptionConfirmation)
            stringToSign = ((SubscriptionConfirmation) this).buildNotificationStringToSign();
        else
            return null;
        return stringToSign.getBytes();
    }
}
