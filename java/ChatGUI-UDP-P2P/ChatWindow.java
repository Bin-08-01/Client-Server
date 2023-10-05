import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class ChatWindow extends JFrame {
    private JTextArea chatArea;
    private JTextField messageField;
    private JTextField ipField;
    private JTextField portField;
    private static int PORT;
    public MulticastSocket multicastSocket;
    public InetAddress group;
    private String ds = "";
    private DatagramSocket socket;
    
    public ChatWindow() throws IOException {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Chat Window");
        setSize(800, 600);
        setLocationRelativeTo(null);
        
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);

        messageField = new JTextField(30);
        ipField = new JTextField(15);
        portField = new JTextField(5);
        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)  {
                try {
                    sendMessage();
                } catch (UnknownHostException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout());
        inputPanel.add(new JLabel("IP:"));
        inputPanel.add(ipField);
        inputPanel.add(new JLabel("Port:"));
        inputPanel.add(portField);
        inputPanel.add(messageField);
        inputPanel.add(sendButton);

        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);

        try {

            socket = new DatagramSocket(0);
            PORT = socket.getLocalPort();
            try {
                multicastSocket = new MulticastSocket(8888);
                group = InetAddress.getByName("233.0.0.1");
                multicastSocket.joinGroup(new InetSocketAddress(group, PORT), null);
                String info = "New 127.0.0.1:" + socket.getLocalPort();
                byte[] buffer = info.getBytes();

                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, 8888);
                multicastSocket.send(packet);
                Thread threadRec = new Thread(new recMsg());
                threadRec.start();
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            Thread receiverThread = new Thread(() -> {
                try {
                    byte[] buffer = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                    while (true) {
                        socket.receive(packet);
                        String message = new String(packet.getData(), 0, packet.getLength());
                        chatArea.setText(chatArea.getText() + packet.getAddress()+ ":" + packet.getPort()+ " sended:" +message + "\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            receiverThread.start();

        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    class recMsg implements Runnable {

        public recMsg() throws IOException {
        }

        @Override
        public void run() {
            try {
                byte[] buffer = new byte[1024];

                while (true) {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    multicastSocket.receive(packet);
                    String message = new String(packet.getData(), 0, packet.getLength());
                    if (message.contains("New")) {
                        ds = "";
                        String infoPerson = "Info 127.0.0.1:" + PORT;
                        byte[] b = infoPerson.getBytes();
                        DatagramPacket newP = new DatagramPacket(b, b.length, InetAddress.getByName("255.255.255.255"), 8888);
                        multicastSocket.send(newP);
                    } else if (message.contains("Info")){
                        String info = message.split(" ")[1];
                        ds += info + "\n";
                        System.out.println("[+] Have new user, List User: \n" + ds);
                        chatArea.setText(chatArea.getText() + "[+] Have new user, List User:\n" + ds + "\n");
                    } else {
                        chatArea.setText(chatArea.getText() + packet.getAddress() + ":" + packet.getPort() + " sended: " +message + "\n" );
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendMessage() throws IOException {
        String ip = ipField.getText();

        String message = messageField.getText();
        if (ip.equals("*")) {
            byte[] byteMsg = message.getBytes();

            DatagramPacket data = new DatagramPacket(byteMsg, byteMsg.length, InetAddress.getByName("255.255.255.255"), 8888);
            multicastSocket.send(data);
        } else {
            try {
                int port = Integer.parseInt(portField.getText());

                byte[] buffer = message.getBytes();
                InetAddress receiverAddress = InetAddress.getByName(ip);
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, receiverAddress, port);
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        chatArea.append("You: " + message + "\n");
        messageField.setText("");
    }

    public static void main(String[] args) throws IOException {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() throws RuntimeException {
                ChatWindow chatWindow;
                try {
                    chatWindow = new ChatWindow();
                    chatWindow.setVisible(true);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}