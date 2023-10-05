import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class ServerDevideToTwoPackets {
    public List<String> accounts;

    public ServerDevideToTwoPackets() {
        try {
            int serverPort = 12345;

            DatagramSocket serverSocket = new DatagramSocket(serverPort);

            getAccount();
            System.out.println("Server is running on port " + serverPort);

            while (true) {
                byte[] receiveData1 = new byte[1024];
                DatagramPacket usernamePacket = new DatagramPacket(receiveData1, receiveData1.length);
                serverSocket.receive(usernamePacket);

                byte[] receiveData2 = new byte[1024];
                DatagramPacket passwordPacket = new DatagramPacket(receiveData2, receiveData2.length);
                serverSocket.receive(passwordPacket);

                ClientHandler clientHandler = new ClientHandler(serverSocket, usernamePacket, passwordPacket);
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private class ClientHandler implements Runnable {
        private DatagramSocket serverSocket;
        private DatagramPacket usernamePacket;
        private DatagramPacket passwordPacket;

        public ClientHandler(DatagramSocket serverSocket, DatagramPacket usernamePacket, DatagramPacket passwordPacket) {
            this.serverSocket = serverSocket;
            this.usernamePacket = usernamePacket;
            this.passwordPacket = passwordPacket;
        }

        @Override
        public void run() {
            try {
                String username = new String(usernamePacket.getData(), 0, usernamePacket.getLength());

                String password = new String(passwordPacket.getData(), 0, passwordPacket.getLength());

                boolean isAuthenticated = checkLogin(username, password);

                String response = isAuthenticated ? "Login successful" : "Login failed";
                byte[] sendData = response.getBytes();
                InetAddress clientAddress = usernamePacket.getAddress();
                int clientPort = usernamePacket.getPort();
                System.out.println("[!] "+clientAddress + ":" + clientPort + " " + response);

                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
                serverSocket.send(sendPacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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

        
    public static void main(String[] args) {
        new ServerDevideToTwoPackets();
    }
}
