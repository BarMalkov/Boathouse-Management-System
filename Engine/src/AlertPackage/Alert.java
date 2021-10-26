package AlertPackage;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "alert")
public class Alert implements Serializable{
    @XmlAttribute(name = "message")
    String message;
    @XmlAttribute
    List<String> recipients;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Alert alert = (Alert) o;
        return message.equals(alert.message) && Objects.equals(recipients, alert.recipients);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, recipients);
    }

    public String getMessage(){
        return message;
    }

    public List<String> getRecipients(){
        return recipients;
    }

    public Boolean isManualAlert(){
        return recipients.isEmpty();
    }

    public Boolean isEmailInRecipientsList(String email){
        return recipients.contains(email);
    }

    public Alert(String message){
        this.message = message;
        this.recipients = new ArrayList<>();
    }

    public Alert(String message, List<String> recipients){
        this.message = message;
        this.recipients = new ArrayList<>(recipients);
    }

    public Alert(){
        message = "";
        recipients = new ArrayList<>();
    }
}
