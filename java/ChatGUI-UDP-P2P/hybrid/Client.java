import java.io.*;
import java.net.*;

public class Client {
    public static DatagramSocket clientSocket;
    public MulticastSocket multicastSocket;

    public Client() {
        try {
            InetAddress serverAddress = InetAddress.getByName("localhost");
            int serverPort = 1234;
            multicastSocket = new MulticastSocket(8888);

            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));

            clientSocket = new DatagramSocket();
            byte[] sendData = "new user".getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, serverPort);
            clientSocket.send(sendPacket);
            Thread threadRec = new Thread(() -> {
                try {
                    byte[] buffer = new byte[1024];

                    while (true) {
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                        multicastSocket.receive(packet);
                        String message = new String(packet.getData(), 0, packet.getLength());
                        System.out.println("\n" + message + "\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            Thread receiverThread = new Thread(() -> {
                try {
                    byte[] buffer = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                    while (true) {
                        clientSocket.receive(packet);
                        String message = new String(packet.getData(), 0, packet.getLength());
                        System.out.println("[+] " + packet.getAddress() + ":" + packet.getPort() + " sended:"
                                + message + "\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            Thread sendThread = new Thread(() -> {
                try {
                    while (true) {
                        System.out.print("[!] Enter IP: ");
                        String ip = userInput.readLine();
                        ip = ip.equals("") ? "localhost" : ip;
                        System.out.print("[!] Enter port: ");
                        int port = Integer.parseInt(userInput.readLine());
                        System.out.print("[!] Enter message: ");
                        String message = userInput.readLine();

                        byte[] sendData1 = message.getBytes();
                        DatagramPacket sendPacket1 = new DatagramPacket(sendData1, sendData1.length,
                                InetAddress.getByName(ip), port);
                        clientSocket.send(sendPacket1);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            threadRec.start();
            receiverThread.start();
            sendThread.start();


            while (true) {
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


    public static void main(String[] args) {
        new Client();
    }
}