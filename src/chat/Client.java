package chat;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;


public class Client extends Thread {
	private JFrame frame;
	private JPanel userMsgPanel;
	private JPanel sendPanel;
	private JScrollPane chartAreaScrollPane;
	private JTextArea chartTextArea;
	private JTextField sendMsgTextField;
	private JButton sendButton;
	private Socket socket;
	private JTable friendTable;
	private String myIP;
	private String destinationIP;
	
	public Client(String destinationIP, String serverIP, int PORT,
			JTable friendTable) {
		this.friendTable = friendTable;
		this.destinationIP = destinationIP;
		
		frame = new JFrame("聊天室");
		try {
			// 构建界面
			initUI();

			// 创建连接 跟server连接SERVER_IP
			socket = new Socket(serverIP, PORT);

			// 发送信息
			/*InetAddress addr = InetAddress.getLocalHost();
			myIP = addr.getHostAddress().toString();*/
			
			myIP = LocalHost.getLocalIP();
			chartTextArea.append("本机IP:" + myIP + "\n");

			 final Message message = new Message();
			message.setSendIP(myIP);
			message.setReceiveIP(destinationIP);
			sendMsgTextField.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (e.getSource() == sendMsgTextField) {
						new SendMsg(socket, message, sendMsgTextField,
								chartTextArea);
						sendMsgTextField.setText("");
					}
				}
			});
			sendButton.addActionListener(new SendMsg(socket, message,
					sendMsgTextField, chartTextArea));

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.start();
	}

	/**
	 * 构建一个用户接受server信息的线程
	 */
	@Override
	public void run() {
		super.run();
		while (true) {
			receiveServerMsg();
		}
	}

	/**
	 * 处理接收到得用户信息
	 */
	private void receiveServerMsg() {
		try {

			ObjectInputStream serverInput = new ObjectInputStream(
					socket.getInputStream());
			Object object = serverInput.readObject();
			// 判断对方是否在线
			if (object == null) {   //type为3表示用户不在线,转离线消息
				ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
				Message m = new Message();
				m.setType(3);
				m.setSendIP(myIP);
				m.setMessage(sendMsgTextField.getText().trim());
				m.setReceiveIP(destinationIP);
				output.writeObject(m);
				chartTextArea.append("对方不在线，信息已经转入离线信息。\n");
			} else {
				// 用户收到的是正常的聊天信息
				if (object instanceof Message) {
					Message m = (Message) object;
					/*String destinationIP = m.getSendIP();					
					new Client(destinationIP, serverIP, PORT, table);*/
					chartTextArea.append(m.getSendIP() + ":\n" + m.getMessage()
							+ "\n");
				}
				// 用户收到的是更新在线用户的信息
				else {
					@SuppressWarnings("unchecked")
					List<String> onlineIP = (List<String>) object;

					System.out.println("friend list:" + onlineIP.toString());

					// 将在线用户加入到界面中显示
					String[] tableHeads = new String[] { "在线用户" };
					DefaultTableModel dtm = (DefaultTableModel) friendTable
							.getModel();
					dtm.setColumnIdentifiers(tableHeads);

					for (int i = 0; i < onlineIP.size(); i++) {
						Vector<String> onlineIPVector = new Vector<String>();
						onlineIPVector.add(onlineIP.get(i));
						dtm.addRow(onlineIPVector);
					}

				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 构建客户端的聊天界面
	 */
	private void initUI() {

		sendButton = new JButton("发送");

		userMsgPanel = new JPanel();
		sendMsgTextField = new JTextField("enter sth to sent");
		sendPanel = new JPanel();
		chartTextArea = new JTextArea();

		userMsgPanel.setLayout(new BorderLayout());

		sendPanel.setLayout(new BorderLayout());
		sendPanel.add(sendMsgTextField, BorderLayout.CENTER);
		sendPanel.add(sendButton, BorderLayout.EAST);

		chartTextArea.setEditable(false);
		chartAreaScrollPane = new JScrollPane(chartTextArea);

		frame.setLayout(new BorderLayout());
		frame.add(userMsgPanel, BorderLayout.NORTH);
		frame.add(chartAreaScrollPane, BorderLayout.CENTER);
		frame.add(sendPanel, BorderLayout.SOUTH);

		frame.setSize(500, 300);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				frame.setVisible(false);
				
				
			}
		});
		frame.setVisible(true);
	}

}
