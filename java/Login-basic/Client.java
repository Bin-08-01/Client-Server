import java.io.*;
import java.net.*;

public class Client {
    public static DatagramSocket clientSocket;
    public static void main(String[] args) {
        try {
            InetAddress serverAddress = InetAddress.getByName("localhost");
            int serverPort = 12345;

            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));

            clientSocket = new DatagramSocket();

            while (true) {
                System.out.print("Your username: ");
                String username = userInput.readLine();
                System.out.print("Your password: ");
                String password = userInput.readLine();

                String loginInfo = username + ":" + password;
                byte[] sendData = loginInfo.getBytes();

                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, serverPort);
                clientSocket.send(sendPacket);

                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                clientSocket.receive(receivePacket);

                String response = new String(receivePacket.getData(), 0, receivePacket.getLength());
                System.out.println("[!] " + response);
                if (response.contains("success")) {
                    break;
                }
            }

            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}