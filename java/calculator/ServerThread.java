import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
public class ServerThread {
    public static void main(String[] args) {
        try(ServerSocket serverSocket = new ServerSocket(1236)) {
            System.out.println("[*] Server is running on 1236");
            while (true){
                Socket clientSocket = serverSocket.accept();
                String infoSocket = clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort();
                System.out.println("[+] Client connected: " + infoSocket);
                Thread clientThread = new Thread(new ClientHandler(clientSocket, infoSocket));
                clientThread.start();
            }
        }catch (IOException e){
            System.out.println("[-] Can't run server");
        }
    }
}
