import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientGui extends JFrame {
    private JTextField operand1Field;
    private JTextField operand2Field;
    private JTextField operatorField;
    private JButton calculateButton;
    private JLabel resultLabel;
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader serverReader;

    public ClientGui() {
        connect();
        setSize(600, 700);
        setTitle("Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        operand1Field = new JTextField(10);
        operand2Field = new JTextField(10);
        operatorField = new JTextField(5);
        calculateButton = new JButton("Calculate");
        resultLabel = new JLabel("");

        add(new JLabel("Operand 1: "));
        add(operand1Field);
        add(new JLabel("Operand 2: "));
        add(operand2Field);
        add(new JLabel("Operator: "));
        add(operatorField);
        add(calculateButton);
        add(resultLabel);

        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String operand1 = operand1Field.getText();
                String operand2 = operand2Field.getText();
                String operator = operatorField.getText();

                writer.println(operand1);
                writer.println(operand2);
                writer.println(operator);

                String result = "";
                try {
                    result = serverReader.readLine();
                } catch (IOException ex) {
                    result = "Have some issues";
                    ex.printStackTrace();
                }
                resultLabel.setText("Result: " + result);
            }
        });

        setSize(600, 400); 
        setResizable(false); 
        setLocationRelativeTo(null); 
        setVisible(true);
    }

    private void connect() {
        try{
            socket = new Socket("localhost", 1236);
            writer = new PrintWriter(socket.getOutputStream(), true);
            serverReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ClientGui();
            }
        });
    }
}