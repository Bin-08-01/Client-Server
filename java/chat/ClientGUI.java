import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class ClientGUI extends JFrame {
    private JTextField ipField;
    private JTextField portField;
    private JTextArea chatArea;
    private JTextArea messageField;
    private JButton sendButton;
    private JButton connectButton;
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;

    public ClientGUI() {
        setTitle("Chat GUI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);
        JPanel connectionPanel = new JPanel();
        JLabel ipLabel = new JLabel("IP Address:");
        ipField = new JTextField(15);
        JLabel portLabel = new JLabel("Port:");
        portField = new JTextField(5);
        connectButton = new JButton("Connect");
        connectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                connectToServer();
            }
        });
        connectionPanel.add(ipLabel);
        connectionPanel.add(ipField);
        connectionPanel.add(portLabel);
        connectionPanel.add(portField);
        connectionPanel.add(connectButton);
        add(connectionPanel, BorderLayout.NORTH);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setRows(26);
        chatArea.setColumns(40);
        JScrollPane chatScrollPane = new JScrollPane(chatArea);
        add(chatScrollPane, BorderLayout.CENTER);


        JPanel sendMsgPanel = new JPanel();

        messageField = new JTextArea();
        messageField.setRows(3);
        messageField.setColumns(26);
        setResizable(false);
        messageField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        sendButton = new JButton("Send");
        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
        sendButton.setEnabled(false);
        sendMsgPanel.add(messageField);
        sendMsgPanel.add(sendButton);
        add(sendMsgPanel, BorderLayout.SOUTH);

        pack();
        setVisible(true);
    }

    private void connectToServer() {
        String ip = ipField.getText();
        int port = Integer.parseInt(portField.getText());
        try {
            socket = new Socket(ip, port);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);
            new Thread(new MessageReceiver()).start();
            connectButton.setEnabled(false);
            ipField.setEditable(false);
            portField.setEditable(false);
            sendButton.setEnabled(true);
            chatArea.append("Connected to " + ip + ":"+port + "\n");
        } catch (IOException e) {
            chatArea.append("Can't connect to " + ip + ":"+port + "\n");
        }
    }

    private void sendMessage() {
        String message = messageField.getText();
        chatArea.append("You: " + message + "\n");
        messageField.setText("");
        writer.println(message);
    }

    private class MessageReceiver implements Runnable {
        public void run() {
            try {
                String message;
                while ((message = reader.readLine()) != null) {
                    chatArea.append(message + "\n");
                }
            } catch (IOException e) {
                chatArea.append("Can't send message right now, please try again!");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ClientGUI();
            }
        });
    }
}