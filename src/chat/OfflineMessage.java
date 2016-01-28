package chat;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class OfflineMessage {
	private JFrame frame;
	private JScrollPane scrollPane;
	private JTextArea textArea;
	
	
	public static void main(String[] args){
		new OfflineMessage();
	}
	
	public OfflineMessage(){
		initUI();
	}
	
	private void initUI(){
		frame = new JFrame("¿Îœﬂ–≈œ¢");
		textArea = new JTextArea();
		scrollPane = new JScrollPane(textArea);
		
		textArea.setEditable(false);
		frame.setLayout(new BorderLayout());
		frame.add(scrollPane, BorderLayout.CENTER);
		
		frame.setSize(500, 300);
//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(false);
	}
	
	public JTextArea getTextArea(){
		return textArea;
	}
	
	public JFrame getFrame(){
		return frame;
	}
}
