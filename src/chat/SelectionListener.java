package chat;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class SelectionListener implements ListSelectionListener{

	private JTable table;
	private String serverIP;
	private int PORT;
	
	public SelectionListener(JTable friendTable, String serverIP, int PORT) {
		this.table = friendTable;
		this.serverIP = serverIP;
		this.PORT = PORT;
		
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (e.getValueIsAdjusting()) {
			int rowNO = table.getSelectedRow();
			String destinationIP = table.getValueAt(rowNO, 0).toString();
			new Client(destinationIP, serverIP, PORT, table);
			new FileTransfer(destinationIP);
		}else{
			//取消选中行
			table.clearSelection();
		}
	}

}
