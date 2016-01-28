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
 * 接收client的目标IP信息，并在新的client加入连接后，向所有的client发送在线信息
 * 接收client发送的信息，并将信息转发给对应的client
 * 接收client的退出信息，并给所有连接中的client发送在线信息
 * 接收client发送的离线信息后，保存信息，待对应的client上线时，再进行转发。
 * 
 */
public class Server {
	private final int PORT = 8111;
	private JFrame frame;
	private JScrollPane scrollPane;
	private JTextArea textArea;
	
	//记录所有的在线用户
	private Map<String, Socket> onlineUserMap;
	//记录所有的发给离线用户的信息
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
				
				//获取用户的IP信息
				InetAddress userInetAddress = socket.getInetAddress();
				String userIP = userInetAddress.getHostAddress();
				textArea.append("用户: " + userIP + "接入服务器\n");
				
				
				//将该用户加入到在线用户
				onlineUserMap.put(userIP, socket);
				
				//创建一个处理client请求的任务
				HandleAClient task = new HandleAClient(socket, textArea, onlineUserMap, offLineMsgList, userIP);
				
				//创建一个处理client任务的线程，并启动它
				Thread handleAClientThread = new Thread(task);
				handleAClientThread.start();		
				
				
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 构建服务端的界面
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


 