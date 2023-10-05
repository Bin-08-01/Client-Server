import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    public DatagramSocket serverSocket;
    public List<String> accounts;
    public Server() {
        try {
            getAccount();
            int serverPort = 12345;

            serverSocket = new DatagramSocket(serverPort);
            System.out.println("[!] Server login is running on port " + serverPort);
            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            while (true) {
                

                serverSocket.receive(receivePacket);

                String loginInfo = new String(receivePacket.getData(), 0, receivePacket.getLength());

                String[] credentials = loginInfo.split(":");
                String username = credentials[0];
                String password = credentials[1];

                boolean isLogin = checkLogin(username, password);

                InetAddress clientAddress = receivePacket.getAddress();
                int clientPort = receivePacket.getPort();

                String response;
                if (isLogin) {
                    response = "Login successfully!";
                } else {
                    response = "Login failed, try again!";
                }

                System.out.println("[!] " + receivePacket.getAddress() + ":" + receivePacket.getPort() + ": " + response);
                byte[] sendData = response.getBytes();

                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
                serverSocket.send(sendPacket);
            }

        } catch (IOException e) {
            e.printStackTrace();
            serverSocket.close();
        }
    }
    public static void main(String[] args) {
        new Server();
    }

    private boolean checkLogin(String username, String password) {
        for (String account : accounts) {
            String[] credentials = account.split(":");
            String uname = credentials[0];
            String passwd = credentials[1];

            if (uname.equals(username) && passwd.equals(password)) {
                return true;
            }
        }

        return false;
    }

    private void getAccount() {
        accounts = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader("account.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                accounts.add(line);
            }
        } catch (IOException e) {
            System.out.println("[-] Can not connect to database!");
        }
    }
}