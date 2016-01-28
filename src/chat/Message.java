package chat;

import java.io.Serializable;

public class Message implements Serializable {
	/**
	 * Ϊ��ʹ��ObjectInputStream��ObjectOutputStream
	 */
	private static final long serialVersionUID = 1L;
	
	private String sendIP;
	private String receiveIP;
	private String message;
	//typeΪ0 ��ʾ������Ϣ(�Ƿ�����:��-������Ϣ����-����������Ϣ��
	//typeΪ1��ʾ���������û���Ϣ���û����ߣ����������û���
	//typeΪ2��ʾ�û����ߣ����������û���Ϣ
	//typeΪ3��ʾ�û�������
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
