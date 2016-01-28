package chat;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * 实现用户发送信息
 *
 */
public class SendMsg implements ActionListener {

	private Socket socket;
	private Message message;
	private JTextField sendMsgTextField; 
	private JTextArea chartTextArea;
	
	public SendMsg(Socket socket, Message message, JTextField sendMsgTextField, JTextArea chartTextArea) {
		this.socket = socket;
		this.message = message;
		this.sendMsgTextField = sendMsgTextField;
		this.chartTextArea = chartTextArea;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		try {
			ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
			message.setMessage(sendMsgTextField.getText().trim());
			message.setType(0);
			output.writeObject(message);
			output.flush();
			chartTextArea.append( message.getSendIP() + ":\n" + message.getMessage()+ "\n");
			sendMsgTextField.setText("");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
	}

}
