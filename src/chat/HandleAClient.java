package chat;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JTextArea;
 
public class HandleAClient implements Runnable{

	private Socket socket;
	private JTextArea textArea;
	private Map<String, Socket> onlineUserMap;
	private List<Message> offLineMsgList;
	private String userIP;
	
	public HandleAClient(Socket socket, JTextArea textArea, Map<String, Socket> onlineUserMap, List<Message> offLineMsgList, String userIP) {
		this.socket = socket;
		this.textArea = textArea;
		this.onlineUserMap = onlineUserMap;
		this.offLineMsgList = offLineMsgList;
		this.userIP = userIP;
		
	}

	@Override
	public void run() {
		try {
			//首先判断是否有发给我的离线信息
			for(int i = 0; i < offLineMsgList.size(); i++){
				System.out.println("enter the offline list");
				Message m = offLineMsgList.get(i);
				
				String receiveIP = m.getReceiveIP();
				
				textArea.append("userip:" + userIP + " ip :" + receiveIP + "\n");
				
				//若有发送给我的离线信息,服务器转发给我，并将相关信息从server上删除
				if(receiveIP.trim().equals(userIP)){
					ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
					output.writeObject(m);
					output.flush();
					
					offLineMsgList.remove(i);
				}
			}
			
			//处理用户的聊天信息
			while(true){
				
				//接收用户输入的信息
				try {
					
					if(!socket.isConnected() ||socket.isInputShutdown()|| socket.isClosed()){
						System.out.println("closed!!!!!!!!!!!!!!!!!"); 
						break;
					}		
					ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
					/*ObjectInputStream input =  null;
					try {
						input = new ObjectInputStream(socket.getInputStream());
					} catch (IOException e) {
						break;
						// TODO: handle exception
					}*/
					Message chartMessage = (Message) input.readObject();
					System.out.println("Message is "+chartMessage);
					textArea.append("From client chartMessage type: " + chartMessage.getType() + "\n");
					
					//用户上线，请求更新在线用户列表
					if(chartMessage.getType() == 1){
						//updateOnlineUserList();
					    onlineUserMap.put(chartMessage.getSendIP(), socket);
						updateOnlineUserList2();
					}
					//用户下线，请求更新在线用户列表
					else if(chartMessage.getType() == 2){
						//从在用户中删除该用户，并更新所有在线用户的列表
						String toRemoveUserIP = userIP;
						if(onlineUserMap.containsKey(toRemoveUserIP)){
							onlineUserMap.remove(toRemoveUserIP);
							//updateOnlineUserList();
						}
						//并更新所有在线用户的列表
						updateOnlineUserList2();
						//关闭socket，下次无法循环，break
						socket.close();
						
					}
					//用户不在线，保存离线信息
					else if(chartMessage.getType() == 3){
						offLineMsgList.add(chartMessage);
					}
					else{  //chartMessage.getType()==0
						//receive the message --- IP Address
						String chartIP = chartMessage.getReceiveIP();
						
						//判断交谈用户是否在线
						Socket isOnlineSocket = isUserOnline(chartIP);
						textArea.append("isUserOnline :" + isOnlineSocket + "\n");
						//交谈用户在线直接转发消息
						if(isOnlineSocket != null){
							ObjectOutputStream output = new ObjectOutputStream(isOnlineSocket.getOutputStream());
							output.writeObject(chartMessage);
							//必须刷新 否则列表无法更新
							output.flush();
						}
						//交谈用户不在线，给发送客户端转发一个空Message，并保存消息，待用户上线时再转发
						else{
							ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
							output.writeObject(null);
							output.flush();
							offLineMsgList.add(chartMessage);
						}
					}
					
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 *将新的在线用户列表发送给client端
	 */
	private void updateOnlineUserList(){
		try {
			ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
			Set<String> keys = onlineUserMap.keySet();
			System.out.println("keys: " + keys.toString());
			List<String> onlineList = new ArrayList<String>();
			for(Iterator<String> it = keys.iterator(); it.hasNext();){
				onlineList.add(it.next());
			}
			System.out.println("keys1:" + onlineList.toString());
			
			output.writeObject(onlineList);
			output.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	//将onlineUserList的用户发给每一个在线用户
	private void updateOnlineUserList2(){
		Set<String> keys = onlineUserMap.keySet();
		keys.remove(null);
		System.out.println("keys: "+ keys.toString());
		List<String> onlineList  = new ArrayList<String>();
		for(Iterator<String> it = keys.iterator(); it.hasNext();){
			onlineList.add(it.next());
		}
		System.out.println("keys1:" + onlineList.toString());
		for(Iterator<String> ip= onlineList.iterator(); ip.hasNext(); ){
			Socket socket = onlineUserMap.get(ip.next());
			
			try {
				ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
				output.writeObject(onlineList);				
				output.flush();
//				output.close();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}
	
	/**
	 * 根据用户的ip判断该用户是否在线
	 * @param ip
	 * @return
	 */
	private Socket isUserOnline(String ip){
		if(onlineUserMap.containsKey(ip)){
			return onlineUserMap.get(ip);
		}else{
			return null;
		}
	}

}
