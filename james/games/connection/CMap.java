package james.games.connection;

import james.games.connection.mapmaker.*;
import java.io.Serializable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import javax.swing.JOptionPane;
public class CMap implements Cloneable, Serializable {
	private static final long serialVersionUID = 1L;
	public Square.State[][] squares;
	public Player[] players;	
	/**
	 * Method writeCMap
	 *
	 *
	 * @param name
	 *
	 */
	public void writeCMap(String name,boolean testOverwrite) throws IOException {
		File writeTo;
		writeTo = new File("james/games/connection/maps/"+name+".cmap");
		if (testOverwrite&&writeTo.exists()) {
			if (JOptionPane.showConfirmDialog(null,"File already exists. Overwrite?","Overwrite?",JOptionPane.YES_NO_OPTION)==JOptionPane.NO_OPTION) return;
		} 
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(writeTo));
		out.writeObject(this);
		out.close();
	}

	/**
	 * Method CMap
	 *
	 *
	 * @param name
	 *
	 *
	 */
	/*public CMap(String fileName) throws IOException {
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName));
			squares = (Square.State[][]) in.readObject();
			players = (Player[]) in.readObject();
			in.close();
		} catch (ClassNotFoundException e) {
			throw new IOException("Invalid Class in Inputing "+fileName,e);
		} catch (ClassCastException e) {
			throw new IOException("Invalid Class in Inputing "+fileName,e);
		}
	}*/
	public CMap(Square.State[][] squares, Player[] players) {
		this.squares=squares;
		this.players=players;
	}
	public static String expand(String name) {
		return "james/games/connection/maps/"+name+".cmap";
	}
	public Object clone() {
		try {
			CMap out = (CMap)super.clone();
			out.players=players.clone();
			for (int i = 0; i < players.length; i++) out.players[i]=(Player)players[i].clone();
			out.squares=squares.clone();
			for (int i = 0; i < squares.length; i++) out.squares[i]=squares[i].clone();
			for (int y = 0; y < squares.length; y++) for (int x = 0; x < squares[0].length; x++) out.squares[y][x]=(Square.State)(squares[y][x].clone());
			return out;
		} catch (CloneNotSupportedException e) {
			throw new Error(e);
		}
	}
	public static CMap read(String fileName) throws IOException {
		CMap out;
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName));
			out = (CMap) in.readObject();
			in.close();
		} catch (ClassNotFoundException e) {
			throw new IOException("Invalid Class in Inputing "+fileName,e);
		} catch (ClassCastException e) {
			throw new IOException("Invalid Class in Inputing "+fileName,e);
		}
		return out;
	}
}
