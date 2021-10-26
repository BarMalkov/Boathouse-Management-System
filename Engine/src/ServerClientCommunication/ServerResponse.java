package ServerClientCommunication;

import java.io.Serializable;

public class ServerResponse implements Serializable {
    private String messageToUser;
    private Object returnValue;

    public String getMessageToUser() {
        return messageToUser;
    }

    public Object getReturnValue() {
        return returnValue;
    }

    public ServerResponse() {
    }
    public void setMessageToUser(String messageToUser) {
        this.messageToUser = messageToUser;
    }

    public void setReturnValue(Object returnValue) {
        this.returnValue = returnValue;
    }
}


