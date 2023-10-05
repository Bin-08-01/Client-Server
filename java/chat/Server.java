import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;


public class Server {
    private List<Socket> clientList = new ArrayList<>();
    public static void main(String[] args) {
        Server server = new Server();
        server.startServer();
    }
    
    public void startServer() {
        try(ServerSocket serverSocket = new ServerSocket(1236)) {
            System.out.println("[*] Server is running on 1236");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                String infoSocket = clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort();
                System.out.println("[+] Client connected: " + infoSocket);
                Thread clientThread = new Thread(new ClientHandler(clientSocket, infoSocket, clientSocket.getInetAddress().getHostAddress(), clientSocket.getPort()));
                clientThread.start();
                clientList.add(clientSocket);
            }
        } catch (IOException e) {
            System.out.println("[-] Can't run server");
        }
    }

    private void sendMessage(String msg, String ip, Integer port) {
        for (Socket client : clientList) {
            if(port != client.getPort() ){
                try {
                    PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                    out.println(msg);
                } catch (IOException e) {
                    System.out.println("[-] Can't send message to " + ip + ":" + port);
                }
            }
        }
    }

    class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private final String infoSocket;
        private final String ip;
        private final Integer port;
        
        public ClientHandler(Socket socket, String infoSocket, String ip, Integer p) {
            this.clientSocket = socket;
            this.infoSocket = infoSocket;
            this.ip = ip;
            this.port = p;
        }

        @Override
        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println(infoSocket + " sended: " + message);
                    sendMessage(infoSocket + ": " + message, this.ip, this.port);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                    System.out.println("[-] " + this.infoSocket + " is disconnected");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

