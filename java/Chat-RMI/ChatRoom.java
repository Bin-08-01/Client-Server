import java.rmi.Remote;
import java.rmi.RemoteException;


public interface ChatRoom extends Remote {
    boolean registerClient(String nickname, ChatClient client) throws RemoteException;
    
    void sendMessage(String sender, String message, String client) throws RemoteException;

    String getNicknames() throws RemoteException;

    ChatClient getUser(String client) throws RemoteException;
}
