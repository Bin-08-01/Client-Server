import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;

public class TimeClient extends JFrame {
    private JButton runBtn, stopBtn;
    private JTextField timeTF;
    private PrintWriter writer;

    public TimeClient() {
        setTitle("TimeClient");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        timeTF = new JTextField();
        timeTF.setEditable(false);
        add(timeTF, BorderLayout.NORTH);

        JPanel btnPanel = new JPanel();
        runBtn = new JButton("Run");
        runBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                writer.println("run");
                stopBtn.setEnabled(true);
                runBtn.setEnabled(false);
            }
        });
        runBtn.setEnabled(false);
        stopBtn = new JButton("Stop");
        stopBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                writer.println("stop");
                runBtn.setEnabled(true);
                stopBtn.setEnabled(false);
            }
        });
        btnPanel.add(runBtn);
        btnPanel.add(stopBtn);
        add(btnPanel, BorderLayout.SOUTH);

        pack();
        setVisible(true);
    }
    
    public void Connect() throws Exception{
        Socket socket = new Socket("localhost", 1237);
        DataInputStream din = new
        DataInputStream(socket.getInputStream());
        writer = new PrintWriter(socket.getOutputStream(), true);

        while (true) {
            String time = din.readUTF();
            timeTF.setText(time);
        }
    }
    public static void main(String[] args) throws Exception {
        TimeClient timeClient = new TimeClient();
        timeClient.Connect();
    }
}