import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    public List<String> clientList = new ArrayList<>();

    public Server() {
        try (DatagramSocket socket = new DatagramSocket(1234);MulticastSocket multicastSocket = new MulticastSocket(8888);) {
            System.out.println("[*] Server is running on 1234");
            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            while (true) {
                socket.receive(receivePacket);

                String data = new String(receivePacket.getData(), 0, receivePacket.getLength());
                InetAddress clientAddress = receivePacket.getAddress();
                int clientPort = receivePacket.getPort();

                clientList.add(clientAddress + ":" + clientPort);
                System.out.println("[+] New client: " + data.split(" ")[1]);
                System.out.println("[+] Client list: " + clientList);
                byte[] b = ("[+] Have new user, list user is: " + clientList).getBytes();
                DatagramPacket dataGram = new DatagramPacket(b, b.length, InetAddress.getByName("255.255.255.255"),
                        8888);
                multicastSocket.send(dataGram);
            }
        } catch (IOException e) {
            System.out.println("[-] Can't run server");
        } 
    }
    
    public static void main(String[] args) {
        new Server();
    }
}
