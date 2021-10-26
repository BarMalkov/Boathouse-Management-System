package ServerClientCommunication;

import RowerPackage.Rower;

import java.io.Serializable;

public class ClientRequest implements Serializable {


    private Rower requestingUser;
    private String methodToInvoke;
    private Object[] arguments;

    public String getMethodToInvoke() {
        return methodToInvoke;
    }

    public Object[] getArguments() {
        return arguments;
    }

    public ClientRequest(Rower requestingUser, String methodToInvoke, Object... arguments) {
        this.methodToInvoke = methodToInvoke;
        this.arguments = arguments;
        this.requestingUser = requestingUser;
    }

    public Rower getRequestingUser() {
        return requestingUser;
    }

    public void setRequestingUser(Rower requestingUser) {
        this.requestingUser = requestingUser;
    }
}
