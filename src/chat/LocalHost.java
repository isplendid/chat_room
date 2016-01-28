package chat;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class LocalHost {
    
	public static String getLocalIP() {		
		Enumeration<NetworkInterface> allNetInterfaces = null;
		List<String> list = new ArrayList<String>();
		try {
			allNetInterfaces = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e) {
			
			e.printStackTrace();
		}
		InetAddress ip = null;
		while (allNetInterfaces.hasMoreElements()){	
		NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
		  //System.out.println(netInterface.getName());
		Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
		
		while (addresses.hasMoreElements()){	
		ip = (InetAddress) addresses.nextElement();
		if (ip != null && ip instanceof Inet4Address){	
		  //System.out.println("±¾»úµÄIP = " + ip.getHostAddress());
			 list.add(ip.getHostAddress());	
		} 
	   }
	  }	
		
		
		return list.get(1);
	}
	
	
}
	

