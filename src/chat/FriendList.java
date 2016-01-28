package chat;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class FriendList {
	private JFrame frame;
	private JScrollPane scrollPane;
	private JTable friendTable;
	
	private String serverIP = "192.168.17.20";
	private final int PORT = 8111;

	private Socket socket; 
	private OfflineMessage offlineMessage;
	
	public static void main(String[] args){
		new FriendList();
	}
	

	public FriendList(){
		
		offlineMessage = new OfflineMessage();
		
		try {
			//创建连接
			socket = new Socket(serverIP, PORT);
			
			ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
			//用户上线，发送上线信息（type为1）
			Message message = new Message();
			message.setType(1);
			output.writeObject(message);
			output.flush();
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		frame = new JFrame("聊天系统");
		
		friendTable = new JTable();
		String[] tableHeads = new String[]{"在线用户"};
		DefaultTableModel dtm = (DefaultTableModel) friendTable.getModel();
		dtm.setColumnIdentifiers(tableHeads);
		
		scrollPane = new JScrollPane(friendTable);
		frame.setLayout(new BorderLayout());
		frame.add(scrollPane, BorderLayout.CENTER);
		
		//点击用户列表聊天
		SelectionListener listener = new SelectionListener(friendTable, serverIP, PORT);
		friendTable.getSelectionModel().addListSelectionListener(listener);
		
		
		
		frame.setSize(200, 400);  
		
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e){
				
				try {
					ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
					//关闭在线用户窗口，用户下线，发送下线消息，message.type = 2
					Message message = new Message();
					message.setType(2);
					output.writeObject(message);
					output.flush();
					
					output.close();
					socket.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
				System.exit(0);
			}
		});
		frame.setVisible(true);
		
//		this.start();
		
	
		
		
		for(;;) {
			updateFriendList();
			
			   try {
			     Thread.sleep(3 * 1000);
			   } catch(Exception e) { System.out.println("Interrupted."); }
			 }
		
		
		
	}
	

	//更新在线用户列表
	private void updateFriendList() {
		try {
			ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
			
			Object object = input.readObject();
			//显示离线信息
			if(object instanceof Message){
				Message m = (Message) object;
				if(m != null){
					offlineMessage.getTextArea().append(m.getSendIP() + ":" + m.getMessage() + "\n");
					offlineMessage.getFrame().setVisible(true);
				}
				
			}else{
				@SuppressWarnings("unchecked")
				List<String> onlineIP = (List<String>) object;
				
				//将在线用户加入到界面中显示
				String[] tableHeads = new String[]{"在线用户"};
				DefaultTableModel dtm = (DefaultTableModel) friendTable.getModel();
				dtm.setColumnIdentifiers(tableHeads);
				//清空Jtable中DefaultTableModel数据
				dtm.setRowCount(0);
				
				for(int i = 0; i < onlineIP.size(); i++){
					Vector<String> onlineIPVector = new Vector<String>();
					//+" Hello "+String.valueOf(i)
					onlineIPVector.add(onlineIP.get(i));
					dtm.addRow(onlineIPVector);
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
