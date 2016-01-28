package chat;

import java.io.Serializable;

public class Message implements Serializable {
	/**
	 * 为了使用ObjectInputStream和ObjectOutputStream
	 */
	private static final long serialVersionUID = 1L;
	
	private String sendIP;
	private String receiveIP;
	private String message;
	//type为0 表示发送信息(是否在线:是-发送信息，否-保存离线信息）
	//type为1表示传输在线用户信息（用户上线，更新在线用户）
	//type为2表示用户下线，更新在线用户信息
	//type为3表示用户不在线
	private int type;
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getSendIP() {
		return sendIP;
	}
	public void setSendIP(String sendIP) {
		this.sendIP = sendIP;
	}
	public String getReceiveIP() {
		return receiveIP;
	}
	public void setReceiveIP(String receiveIP) {
		this.receiveIP = receiveIP;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
}
