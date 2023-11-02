import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class ChatServer {
    public static void main(String[] args) {
        try {
            ChatRoom chatRoom = new ChatRoomImpl();
            LocateRegistry.createRegistry(1099);
            Naming.rebind("ChatRoom", chatRoom);
            System.out.println("[+] Chat server is running.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
