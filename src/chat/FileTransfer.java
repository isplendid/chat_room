package chat;



import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class FileTransfer extends Thread {
   
	private JFrame frame = new JFrame();
	//private JTextField IPAddressFiled;
	private JTextField fileNameFiled;
	private JTextArea messageTextArea;
	private JButton sendButton;
	private JButton selectFileButton;
	private File sendFile;
	private File receiveFile;
	private JPanel fileNamePanel;

	private DatagramSocket messageSocket;
	private DatagramSocket fileSocket;
	private SocketAddress messageAddress;
	private SocketAddress fileAddress;

	private int port = 8888;
	private SocketAddress newMessageAddress;
	
	//���͵�ַ
	private String destinationIP;
	

	private final int SEND_FILE_REQUEST = 1;  //send_file_request
	private final int AGREE_SEND_REQUEST = 2;  //agree_send_request
	private final int REFUSE_ACCEPT_FILE = 3;  //refuse_accept_file

/*	public static void main(String[] args) {
		new FileTransfer("192.168.17.21");
	}*/
    
	public FileTransfer(String destinationIP) {
		this.destinationIP = destinationIP;
		try {
			messageSocket = new DatagramSocket(port);
			fileSocket = new DatagramSocket(port - 1);
		} catch (SocketException e) {
			e.printStackTrace();
		}

		// ��������
		initUI();
		// �����߳�
		this.start();

	}

	// ������������
	public void initUI() {
		//IPAddressFiled = new JTextField();
		fileNameFiled = new JTextField();
		messageTextArea = new JTextArea();
		sendButton = new JButton("����");
		selectFileButton = new JButton("ѡ���ļ�");

		fileNamePanel = new JPanel();
		fileNamePanel.setLayout(new BorderLayout());
		fileNamePanel.add(new JLabel("ѡ���ļ�:"), BorderLayout.WEST);
		fileNamePanel.add(fileNameFiled, BorderLayout.CENTER);
		fileNameFiled.setHorizontalAlignment(JTextField.LEFT);

		fileNamePanel.add(selectFileButton, BorderLayout.CENTER);
		selectFileButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				int n = fileChooser.showOpenDialog(null);

				if (n == JFileChooser.APPROVE_OPTION) {
					sendFile = fileChooser.getSelectedFile();
					if (sendFile == null) {
						JOptionPane.showMessageDialog(null, "��ѡ��һ���ļ�", "������ʾ",
								JOptionPane.ERROR_MESSAGE);
					} else {
						selectFileButton.setVisible(false);
						fileNameFiled.setVisible(true);
						fileNamePanel.add(fileNameFiled, BorderLayout.CENTER);
						fileNameFiled.setText(sendFile.getAbsolutePath());
						fileNameFiled.setEditable(false);

					}
				}
			}
		});
		fileNamePanel.add(sendButton, BorderLayout.EAST);

		frame.setLayout(new BorderLayout());
		//frame.add(IPAddressPanel, BorderLayout.NORTH);
		frame.add(fileNamePanel, BorderLayout.SOUTH);
		frame.add(new JScrollPane(messageTextArea), BorderLayout.CENTER);

		sendButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
			
				if (fileNameFiled.getText().trim().length()==0){
					JOptionPane.showMessageDialog(null, "��ѡ��һ�������ļ�", "������ʾ",
							JOptionPane.ERROR_MESSAGE);
				}
				else 
				{
					messageTextArea.append("׼�������ļ���"
							+ fileNameFiled.getText().trim() + "��"
							+ destinationIP + "\n");
					String sendFileName = fileNameFiled.getText().trim();
					//��ʼ�������ļ���Ϣ�ĵ�ַ
					messageAddress = new InetSocketAddress(destinationIP,port);
					//��ʼ���ļ����䵽�ĵ�ַ
					fileAddress = new InetSocketAddress(destinationIP,port-1);
					
					//��Է����͡��������ļ�����Ϣ
					sendFileMsg(SEND_FILE_REQUEST,sendFileName);
					
					//�ָ����Ͱ�ť
					fileNameFiled.setVisible(false);
					fileNameFiled.setText(null);
					selectFileButton.setVisible(true);
				}
			}

			
		
		});

		frame.setTitle("�ļ����乤��");
		frame.setSize(500, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
		frame.addWindowListener(new WindowAdapter(){
			public void windowClosing(){
				frame.setVisible(false);
			}
		});
	}

	@Override
	public void run() {
		super.run();
		while (true) {
			//�����Ƿ�����Ϣ����
		 receiveFileMsg();
		}

	}

	private void sendFileMsg(int requestType, String sendFileName) {
		// TODO Auto-generated method stub
		byte[] buf = new byte[1024];
		Arrays.fill(buf, (byte)0);
		
		try {
			if(messageAddress ==null){
				messageAddress = newMessageAddress;
			}
			
			DatagramPacket sendFileMsgPack = new DatagramPacket(buf,
					buf.length, messageAddress);
			//UDP��   
			sendFileMsgPack.setData((Integer.toString(requestType)
					+"->" + sendFileName).getBytes());
			sendFileMsgPack.setSocketAddress(messageAddress);
			
			messageSocket.send(sendFileMsgPack);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private void sendFileMsg(int requestType) {
		
		byte[] buf = new byte[1024];
		Arrays.fill(buf, (byte)0);
		
		try {
			if(messageAddress ==null){
				messageAddress = newMessageAddress;
			}
			
			DatagramPacket sendFileMsgPack = new DatagramPacket(buf,
					buf.length, messageAddress);
			//UDP��   
			sendFileMsgPack.setData(Integer.toString(requestType).getBytes());
			sendFileMsgPack.setSocketAddress(messageAddress);
			
			messageSocket.send(sendFileMsgPack);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
     private void receiveFileMsg() {
		byte[] buf = new byte[1024];
		Arrays.fill(buf, (byte)0);
		
		try {
			DatagramPacket receiveFileMsgPack = new DatagramPacket(buf, buf.length);
			messageSocket.receive(receiveFileMsgPack);	
			
			
			String receivedMessage = new String(buf).trim();
			//���͵��ļ���Ϣ����1,2,3
			int MsgType = Integer.parseInt(receivedMessage.substring(0, 1));
			//�����ļ���������Ϣ
			newMessageAddress = receiveFileMsgPack.getSocketAddress();
			String fromIP =  new String(receiveFileMsgPack.getAddress().getHostAddress());
			
			if(MsgType == SEND_FILE_REQUEST){
				String receiveFileName = receivedMessage.substring(4);
				int response  = JOptionPane.showConfirmDialog(null, fromIP + "��ͼ���㷢���ļ�" + receiveFileName+", �Ƿ�����ļ���");
				if(response == JOptionPane.YES_OPTION){
					messageTextArea.append("���Ѿ�ͬ������ļ���\n");
					JFileChooser chooser = new JFileChooser();
					int n = chooser.showSaveDialog(null);
					receiveFile = chooser.getSelectedFile();
					messageTextArea.append("�����ַ�ǣ� "
							+ receiveFile.getAbsolutePath()+ "\n");
					if( n == JFileChooser.APPROVE_OPTION && receiveFile !=null){
						sendFileMsg(AGREE_SEND_REQUEST);
						
						messageTextArea.append("��ʼ�����ļ�...\n");
						receiveFile(receiveFile);
						messageTextArea.append("������ϣ�\n");
					}
					
				}else{
					messageTextArea.append("��ܾ��˽����ļ���\n");
					sendFileMsg(REFUSE_ACCEPT_FILE);
				}
				
				
			}
			else if(MsgType == AGREE_SEND_REQUEST){
				messageTextArea.append("�Է�ͬ������ļ������ڿ�ʼ�����ļ���");
				
				//�����ļ�
				messageTextArea.append("��ʼ����...\n");
				sendFile(sendFile);
				messageTextArea.append("������ϣ�\n");
			}else if(MsgType == REFUSE_ACCEPT_FILE){
				messageTextArea.append("�Է��Ѿܾ��㴫���ļ���");
			}
			
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}

	private void sendFile(File file) {
		// TODO Auto-generated method stub
		try {
			InputStream in = new FileInputStream(file);
			byte[] buf = new byte[1024];
			int length = in.read(buf);
			in.close();
			DatagramPacket sendFilePacket = new DatagramPacket(buf, length, fileAddress);
			fileSocket.send(sendFilePacket);
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	private void receiveFile(File file) {
		
		try {
			OutputStream out = new FileOutputStream(file);
			byte[] buf = new byte[1024];
			DatagramPacket receiveFilePacket = new DatagramPacket(buf,
			    buf.length);
			fileSocket.receive(receiveFilePacket);
			
			out.write(receiveFilePacket.getData());
			out.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

}