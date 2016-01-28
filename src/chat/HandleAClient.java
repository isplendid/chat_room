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
			//�����ж��Ƿ��з����ҵ�������Ϣ
			for(int i = 0; i < offLineMsgList.size(); i++){
				System.out.println("enter the offline list");
				Message m = offLineMsgList.get(i);
				
				String receiveIP = m.getReceiveIP();
				
				textArea.append("userip:" + userIP + " ip :" + receiveIP + "\n");
				
				//���з��͸��ҵ�������Ϣ,������ת�����ң����������Ϣ��server��ɾ��
				if(receiveIP.trim().equals(userIP)){
					ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
					output.writeObject(m);
					output.flush();
					
					offLineMsgList.remove(i);
				}
			}
			
			//�����û���������Ϣ
			while(true){
				
				//�����û��������Ϣ
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
					
					//�û����ߣ�������������û��б�
					if(chartMessage.getType() == 1){
						//updateOnlineUserList();
					    onlineUserMap.put(chartMessage.getSendIP(), socket);
						updateOnlineUserList2();
					}
					//�û����ߣ�������������û��б�
					else if(chartMessage.getType() == 2){
						//�����û���ɾ�����û������������������û����б�
						String toRemoveUserIP = userIP;
						if(onlineUserMap.containsKey(toRemoveUserIP)){
							onlineUserMap.remove(toRemoveUserIP);
							//updateOnlineUserList();
						}
						//���������������û����б�
						updateOnlineUserList2();
						//�ر�socket���´��޷�ѭ����break
						socket.close();
						
					}
					//�û������ߣ�����������Ϣ
					else if(chartMessage.getType() == 3){
						offLineMsgList.add(chartMessage);
					}
					else{  //chartMessage.getType()==0
						//receive the message --- IP Address
						String chartIP = chartMessage.getReceiveIP();
						
						//�жϽ�̸�û��Ƿ�����
						Socket isOnlineSocket = isUserOnline(chartIP);
						textArea.append("isUserOnline :" + isOnlineSocket + "\n");
						//��̸�û�����ֱ��ת����Ϣ
						if(isOnlineSocket != null){
							ObjectOutputStream output = new ObjectOutputStream(isOnlineSocket.getOutputStream());
							output.writeObject(chartMessage);
							//����ˢ�� �����б��޷�����
							output.flush();
						}
						//��̸�û������ߣ������Ϳͻ���ת��һ����Message����������Ϣ�����û�����ʱ��ת��
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
	 *���µ������û��б��͸�client��
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
	//��onlineUserList���û�����ÿһ�������û�
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
	 * �����û���ip�жϸ��û��Ƿ�����
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
