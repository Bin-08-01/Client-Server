import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import javax.swing.JTextArea;

public class ChatClientImpl extends UnicastRemoteObject implements ChatClient {
    private String nickname;
    private JTextArea textArea;
    public ChatClientImpl(String nickname, JTextArea textArea) throws RemoteException {
        super();
        this.nickname = nickname;
        this.textArea = textArea;
    }

    @Override
    public void receiveMessage(String sender, String message) throws RemoteException {
        textArea.append("[" + sender + "]: " + message + "\n");
    }

    public String getNickname() {
        return nickname;
    }
}
