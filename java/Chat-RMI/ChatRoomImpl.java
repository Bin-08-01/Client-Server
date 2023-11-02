import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class ChatRoomImpl extends UnicastRemoteObject implements ChatRoom {
    private Map<String, ChatClient> clients = new HashMap<>();
    public ChatRoomImpl() throws RemoteException {
        super();
    }

    @Override
    public boolean registerClient(String nickname, ChatClient client) throws RemoteException {
        if (clients.containsKey(nickname) == false) {
            clients.put(nickname, client);
            return true;
        }
        return false;
    }

    @Override
    public void sendMessage(String sender, String message, String client) throws RemoteException {
        clients.get(client).receiveMessage(sender, message);
    }

    @Override
    public String getNicknames() throws RemoteException {
        return clients.keySet().toString();
    }

    @Override
    public ChatClient getUser(String nickname) throws RemoteException {
        return clients.get(nickname);
    }
}
