import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TimeServer {
    public List<Thread> clientList = new ArrayList<>();
    private Boolean check = true;

    public TimeServer() {
        try {

            ServerSocket serverSocket = new ServerSocket(1237);
            System.out.println("[*] Server is running on 1237");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                String infoSocket = clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort();
                System.out.println("[+] Client connected: " + infoSocket);
                Thread clientThread = new Thread(new ClientHandler(clientSocket, infoSocket));
                clientThread.start();
                Thread msgThread = new Thread(new MessageReceiver(clientSocket, infoSocket, clientThread.getId()));
                msgThread.start();
                clientList.add(clientThread);
            }
        } catch (IOException e) {
            System.out.println("[-] Can't run server");
        }
    }

    public static void main(String[] args) throws Exception {
        new TimeServer();
    }
    
    private class MessageReceiver implements Runnable {
        private String infoSocket;
        private Socket clientSocket;
        private Long clientThreadId;

        public MessageReceiver(Socket clientSocket, String infoSocket, Long clientThread) {
            this.clientSocket = clientSocket;
            this.infoSocket = infoSocket;
            this.clientThreadId = clientThread;
        }

        @Override
        public void run() {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 while (true) {
                     try {
                         String option = reader.readLine();
                         if (option.equals("stop")) {
                            check = false;
                         }else if(option.equals("run")){
                            check = true;
                         }
                     } catch (Exception e) {
                        break;
                     }
                 }
            } catch (IOException e) {
                e.printStackTrace();
            } 
        }

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
                    if (check) {
                        try {
                            String time = new Date().toString();
                            dout.writeUTF(time);
                            Thread.sleep(1000);
                        } catch (Exception e) {
                            dout.writeBytes("Have some issues");
                        }  
                    }
                    else {
                        Thread.sleep(1000);
                    }
                }

            } catch (IOException e) {
                System.out.println("[-] " + infoSocket + " disconnected");
            } catch (InterruptedException e) {
                
            }
        }

    }

}