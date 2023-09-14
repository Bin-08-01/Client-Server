import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Objects;

public class Client {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 1236);

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

            while (true){
                System.out.print("Operand 1: ");
                String operand1 = reader.readLine();
                System.out.print("Operand 2: ");
                String operand2 = reader.readLine();
                System.out.print("Operator (+, -, *, /): ");
                String operator = reader.readLine();

                writer.println(operand1);
                writer.println(operand2);
                writer.println(operator);

                BufferedReader serverReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String result = serverReader.readLine();
                System.out.println("Result: " + result);
                System.out.print("Do you want to continue? (Type y/n)");
                String option = reader.readLine();
                if(!Objects.equals(option, "y")){
                    break;
                }
            }

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}