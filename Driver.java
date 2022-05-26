
import james.games.connection.*;
import james.games.connection.mapmaker.*;
import james.games.connection.action.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.IOException;
import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;
public class Driver implements Runnable {
	private static final int PORT = 6789;
	/**
	 * Method main
	 *
	 *
	 * @param args
	 *
	 */
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		SwingUtilities.invokeLater(new Driver());
	}
	public void run() {
		//MapMaker.open(null);
		//testInetProgram();
		runProgram();
	}
	public void testProgram() {
		try {
			String load = JOptionPane.showInputDialog("Enter the file you wish to load.");
			CMap map = CMap.read(CMap.expand(load));
			SelfBuffer[] bs = new SelfBuffer[map.players.length];
			for (int i = 0; i < bs.length; i++) bs[i]=new SelfBuffer();
			SelfBuffer.setLinks(bs);
			IBox[] boxes = new IBox[map.players.length];
			for (int i = 0; i < boxes.length; i++) boxes[i]=new IBox((CMap)map.clone(),i,false,bs[i]);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	public void runProgram() {
		int option = JOptionPane.showConfirmDialog(null, "Host a game?", "Host?", JOptionPane.YES_NO_OPTION);
		if (option==JOptionPane.YES_OPTION) hostProgram();
		else clientProgram();
	}
	public void hostProgram() {
		try {
			String load = JOptionPane.showInputDialog("Enter the file you wish to load.");
			CMap map = CMap.read(CMap.expand(load));
			new RevelationServer(map.players.length,PORT);
			InternetBuffer bs =new InternetBuffer(InetAddress.getLocalHost(),PORT);
			JOptionPane.showMessageDialog(null, "The Inet Address is: "+InetAddress.getLocalHost().getHostAddress(), "Host Address", JOptionPane.INFORMATION_MESSAGE);
			IBox box = new IBox((CMap)map.clone(),0,true,bs);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	public InetAddress parseAddress(String in) throws UnknownHostException {
		System.out.println("parseAddress('"+in+"')");
		String[] parts = in.split("\\.");
		System.out.println(parts);
		System.out.println(parts.length);
		byte[] out = {(byte)Integer.parseInt(parts[0]),(byte)Integer.parseInt(parts[1]),(byte)Integer.parseInt(parts[2]),(byte)Integer.parseInt(parts[3])};
		return InetAddress.getByAddress((out));
	}
	public void clientProgram() {
		try {
			String load = JOptionPane.showInputDialog("Enter the file you wish to load.");
			CMap map = CMap.read(CMap.expand(load));
			String server_addr = JOptionPane.showInputDialog("Enter the server addr");
			//new RevelationServer(map.players.length,PORT);
			InternetBuffer bs =new InternetBuffer(parseAddress(server_addr),PORT);
			IBox box = new IBox((CMap)map.clone(),1,true,bs);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	public void testInetProgram() {
		try {
			System.out.println("Hello 1");
			int port = 6789;
			String load = JOptionPane.showInputDialog("Enter the file you wish to load.");
			CMap map = CMap.read(CMap.expand(load));
			InternetBuffer[] bs = new InternetBuffer[map.players.length];
			System.out.println("Hello 2");
			new RevelationServer(map.players.length,port);
			System.out.println("Hello 3");
			for (int i = 0; i < bs.length; i++) bs[i]=new InternetBuffer(InetAddress.getLocalHost(),port);
			IBox[] boxes = new IBox[map.players.length];
			System.out.println("Hello 4");
			for (int i = 0; i < boxes.length; i++) boxes[i]=new IBox((CMap)map.clone(),i,false,bs[i]);
			System.out.println("Hello 5");
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

}
