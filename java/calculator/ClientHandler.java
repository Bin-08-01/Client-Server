import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final String infoSocket;

    public ClientHandler(Socket socket, String infoSocket) {
        this.clientSocket = socket;
        this.infoSocket = infoSocket;
    }

    @Override
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);

            while (true){
                try{
                    String operand1 = reader.readLine();
                    String operand2 = reader.readLine();
                    String operator = reader.readLine();

                    double result = calculate(Double.parseDouble(operand1), Double.parseDouble(operand2), operator);

                    writer.println(result);
                } catch (Exception e) {
                    writer.println("Invalid data");
                }
            }

        } catch (IOException e) {
            System.out.println("[-] " + infoSocket + " disconnected");
            e.printStackTrace();
        }
    }

    private double calculate(double operand1, double operand2, String operator) {
        return switch (operator) {
            case "+" -> operand1 + operand2;
            case "-" -> operand1 - operand2;
            case "*" -> operand1 * operand2;
            case "/" -> operand1 / operand2;
            default -> throw new IllegalArgumentException("Operator not supported");
        };
    }
}