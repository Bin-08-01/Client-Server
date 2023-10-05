import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.Vector;

public class ServerCMD {

    public ServerCMD() {
        try(ServerSocket serverSocket = new ServerSocket(1236);) {
            Vector<Thread> clientList = new Vector<>();

            System.out.println("[*] Server is running on 1236");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                String infoSocket = clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort();
                System.out.println("[+] Client connected: " + infoSocket);
                Thread clientThread = new Thread(new ClientHandler(clientSocket, infoSocket));
                clientThread.start();
                clientList.add(clientThread);
                System.out.println(clientList);
            }
        } catch (IOException e) {
            System.out.println("[-] Can't run server");
        }
    }
    public static void main(String[] args) throws Exception {
        new ServerCMD();
    }
    
    private class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private final String infoSocket;
        
        public ClientHandler(Socket socket, String infoSocket) {
            this.clientSocket = socket;
            this.infoSocket = infoSocket;
        }

        @Override
        public void run() {
            try {
                DataOutputStream dout = new DataOutputStream(clientSocket.getOutputStream());

                while (true) {
                    try {
                        String time = new Date().toString();
                        dout.writeUTF(time);
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        dout.writeBytes("Have some issues");
                    }
                }

            } catch (IOException e) {
                System.out.println("[-] " + infoSocket + " disconnected");
            }
        }
    
    }

}