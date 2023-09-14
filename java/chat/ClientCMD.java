import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientCMD {
    private static Socket socket;
    private static BufferedReader in;
    private static BufferedReader userInput;
    private static PrintWriter out;
    public static void main(String[] args) {
        ClientCMD client = new ClientCMD();
        client.start();
    }
    
    private void start() {
        String serverHost = "localhost";
        int serverPort = 1236;

        try {
            socket = new Socket(serverHost, serverPort);
            System.out.println("Connected to server");

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            userInput = new BufferedReader(new InputStreamReader(System.in));
            
            Thread threadRec = new Thread(new recMsg(serverHost + ":" + serverPort));
            threadRec.start();
            Thread threadSend = new Thread(new sendMsg());
            threadSend.start();
        
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class recMsg implements Runnable {
        String name;

        public recMsg(String name) {
            this.name = name;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    String serverResponse = in.readLine();
                    System.out.println(serverResponse);
                } catch (IOException e) {
                    System.out.println("Error");
                }
            }
        }
    }
    
    class sendMsg implements Runnable {

        @Override
        public void run() {
            while (true) {
                try {
                    String message = userInput.readLine();
                    out.println(message);
                } catch (Exception e) {
                    System.out.println("Error");
                }
            }
        }
        
    }
}