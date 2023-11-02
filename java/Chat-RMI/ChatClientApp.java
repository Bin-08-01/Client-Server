import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ChatClientApp extends JFrame {
    private String nickname;
    private JTextArea chatArea;
    private JTextField messageField;
    private JTextField UsernameField;
    private JTextField nicknameField;
    private ChatClient clientSend;
    private String clientNickname;

    public ChatClientApp() throws NotBoundException, MalformedURLException, RemoteException {
        ChatRoom chatRoom = (ChatRoom) Naming.lookup("rmi://localhost/ChatRoom");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Chat RMI");
        setSize(800, 600);
        setLocationRelativeTo(null);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);

        messageField = new JTextField(30);
        UsernameField = new JTextField(10);
        JButton sendButton = new JButton("Send");
        JButton listButton = new JButton("List User");

        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String message = messageField.getText();
                String username = UsernameField.getText();
                try {
                    chatRoom.sendMessage(nickname, message, username);
                    chatArea.append("[" + nickname + "](You): " + message + "\n");
                    clientNickname = username;
                    clientSend = chatRoom.getUser(username);
                } catch (Exception ex) {
                    if (clientNickname == username) {
                        try {
                            clientSend.receiveMessage(nickname, message);
                        } catch (Exception ex1) {
                            System.out.println("[!] Error: " + ex1.getMessage());
                            return;
                        }
                    } else {
                        System.out.println("[!] Error: " + ex.getMessage());
                        chatArea.append("[Server]: Can not connect to server.\n");
                    }
                }
            }
        });

        listButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    String list = chatRoom.getNicknames();
                    chatArea.append("[Server] List of users: " + list + "\n");
                } catch (Exception ex) {
                    System.out.println("[!] Error: " + ex.getMessage());
                    chatArea.append("[Server]: Can not connect to server.\n");
                }
            }
        });

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout());
        inputPanel.add(new JLabel("Username:"));
        inputPanel.add(UsernameField);
        inputPanel.add(new JLabel("Message:"));
        inputPanel.add(messageField);
        inputPanel.add(sendButton);
        inputPanel.add(listButton);
        UsernameField.setEditable(false);
        messageField.setEditable(false);

        nicknameField = new JTextField(10);
        JPanel nicknamePanel = new JPanel();
        nicknamePanel.setLayout(new FlowLayout());
        JButton sendNnameButton = new JButton("Send");
        nicknamePanel.add(new JLabel("Nickname:"));
        nicknamePanel.add(nicknameField);
        nicknamePanel.add(sendNnameButton);

        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);
        add(nicknamePanel, BorderLayout.NORTH);
        chatArea.append("[Server]: Enter your nickname and press 'Send' to register.\n");

        sendNnameButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    String nname = nicknameField.getText();
                    ChatClient client = new ChatClientImpl(nname, chatArea);
                    if (chatRoom.registerClient(nname, client)) {
                        nickname = nname;
                        chatArea.append("[Server]: Registration successful.\n");
                        nicknameField.setEditable(false);
                        sendNnameButton.setEnabled(false);
                        UsernameField.setEditable(true);
                        messageField.setEditable(true);
                    } else {
                        chatArea.append("[Server]: Nickname is already in use. Please choose another one.\n");
                    }
                } catch (Exception ex) {
                    System.out.println("[!] Error: " + ex.getMessage());
                    chatArea.append("[Server]: Can not connect to server.\n");
                }
            }
        });
    }
    

    public static void main(String[] args) {
        try {
            ChatClientApp app = new ChatClientApp();
            app.setVisible(true);
        } catch (Exception e) {
            System.out.println("[!] Error: " + e.getMessage());
        }
    }
}
