package chat;

import java.awt.BorderLayout;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * ����client��Ŀ��IP��Ϣ�������µ�client�������Ӻ������е�client����������Ϣ
 * ����client���͵���Ϣ��������Ϣת������Ӧ��client
 * ����client���˳���Ϣ���������������е�client����������Ϣ
 * ����client���͵�������Ϣ�󣬱�����Ϣ������Ӧ��client����ʱ���ٽ���ת����
 * 
 */
public class Server {
	private final int PORT = 8111;
	private JFrame frame;
	private JScrollPane scrollPane;
	private JTextArea textArea;
	
	//��¼���е������û�
	private Map<String, Socket> onlineUserMap;
	//��¼���еķ��������û�����Ϣ
	private List<Message> offLineMsgList;
	
	public static void main(String[] args){
		new Server();
	}
	
	public Server(){
		initUI();
		
		onlineUserMap = new HashMap<String, Socket>();
		offLineMsgList = new ArrayList<Message>();
		
		try {
			ServerSocket serverSocket = new ServerSocket(PORT);
			
			while(true){
				Socket socket = serverSocket.accept();
				
				//��ȡ�û���IP��Ϣ
				InetAddress userInetAddress = socket.getInetAddress();
				String userIP = userInetAddress.getHostAddress();
				textArea.append("�û�: " + userIP + "���������\n");
				
				
				//�����û����뵽�����û�
				onlineUserMap.put(userIP, socket);
				
				//����һ������client���������
				HandleAClient task = new HandleAClient(socket, textArea, onlineUserMap, offLineMsgList, userIP);
				
				//����һ������client������̣߳���������
				Thread handleAClientThread = new Thread(task);
				handleAClientThread.start();		
				
				
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * ��������˵Ľ���
	 */
	private void initUI(){
		frame = new JFrame("Server");
		textArea = new JTextArea();
		scrollPane = new JScrollPane(textArea);
		
		textArea.setEditable(false);
		frame.setLayout(new BorderLayout());
		frame.add(scrollPane, BorderLayout.CENTER);
		
		frame.setSize(500, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}


 