package james.games.connection;

import james.games.connection.mapmaker.*;
import java.io.File;
import java.io.IOException;
import java.awt.Frame;
import java.awt.MenuBar;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JOptionPane;

public class MapMaker extends BaseMap {
	private static final long serialVersionUID = 1L;
	public static final String FILES = "james/games/connection/maps/";
	private static MapMaker current;
	private Window window;
	private Player[] players;
	private String lastFile = null;
	private Square offSquare = new Square(-1,-1);
	private boolean sEnabled = false;
	private MenuItem singleClick;
	private static class SquareClick extends MouseAdapter {
		private MapMaker box;
		public void setBox(MapMaker box) {
			this.box=box;
		}
		@Override
		public void mouseClicked(MouseEvent ev) {
			Square square = (Square) ev.getSource();
			if (box.sEnabled) {
				int[] tele = square.getTeleporter();
				square.setState((Square.State)(box.offSquare.getState().clone()));
				square.setTeleporter(tele);
				return;
			}
			if (Teleporter.current()) {
				try {
					Teleporter.addTeleporter(square);
				} catch (JConnectionException e) {
					JOptionPane.showMessageDialog(square,e.getMessage(),"Unable to add teleporter",JOptionPane.ERROR_MESSAGE);
				}
			} else {
				current.setEnabled(false);
				new SquareWindow(square,false);
			}
		}
	};
	/**
	 * Method MapMaker
	 *
	 *
	 */
	private MapMaker(int x, int y, int numPlayers, Window window,SquareClick click) {
		super(x,y, click,false);
		setTitle("MapMaker - Untitled");
		click.setBox(this);
		players = new Player[numPlayers];
		for (int i = 0; i < players.length; i++) players[i]=new Player(i);
		showMapMaker(window);
	}
	private MapMaker(CMap map, Window window, String file, SquareClick click) {
		super(map, click,false);
		players=map.players;
		lastFile=file;
		setTitle("MapMaker - "+file);
		click.setBox(this);
		showMapMaker(window);
	}
	private void showMapMaker(Window window) {
		this.window=window;
		if (window!=null) window.setVisible(false);
		current=this;
		MenuBar bar = new MenuBar();
		Menu file = new Menu("file");
		MenuItem save = new MenuItem("save");
		MenuItem exit = new MenuItem("exit");
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				save();
			}
		});
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exit();
			}
		});
		file.add(save);
		file.add(exit);
		Menu edit = new Menu("edit");
		MenuItem player = new MenuItem("player");
		player.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				int num = 0;
				boolean err = false;
				try {
					num = Integer.parseInt(JOptionPane.showInputDialog(MapMaker.this,"Enter the player you want to access","Player?",JOptionPane.QUESTION_MESSAGE));
					if (num<1||num>players.length) err=true;
				} catch (NumberFormatException e) {
					err=true;
				}
				if (err) JOptionPane.showMessageDialog(MapMaker.this,"Player must be a number between 1 and "+players.length+" inclusive","Invalid Argument",JOptionPane.ERROR_MESSAGE);
				else new PlayerStats(players[num-1],num,MapMaker.this);
			}
		});
		edit.add(player);
		MenuItem mOffSquare = new MenuItem("off square");
		mOffSquare.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setEnabled(false);
				new SquareWindow(offSquare,true);
			}
		});
		edit.add(mOffSquare);
		singleClick = new MenuItem("enable single click");
		singleClick.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sEnabled = !sEnabled;
				if (sEnabled) singleClick.setLabel("disable single click");
				else singleClick.setLabel("enable single click");
			}
		});
		edit.add(singleClick);
		bar.add(file);
		bar.add(edit);
		setMenuBar(bar);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				exit();
			}
		});
		setVisible(true);
	}
	public static MapMaker open(Window caller) {
		SquareClick click = new SquareClick();
		int option = JOptionPane.showConfirmDialog(caller,"Load from file?","Load or create new?",JOptionPane.YES_NO_CANCEL_OPTION);
		if (option == JOptionPane.YES_OPTION) {
			String file = JOptionPane.showInputDialog(caller,"Enter the file name: ","FileName:",JOptionPane.PLAIN_MESSAGE);
			try {
				CMap map = CMap.read(CMap.expand(file));
				new MapMaker(map,caller,file,click);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(caller,e.getMessage(),"Could not read file",JOptionPane.ERROR_MESSAGE);
			}
		} else if (option == JOptionPane.NO_OPTION) {
			boolean err = false;
			int width = 0;
			int height = 0;
			int numPlayers = 0;
			try {
				width = Integer.parseInt(JOptionPane.showInputDialog(caller,"Enter the width of the board.","Width?",JOptionPane.QUESTION_MESSAGE));
				if (width<1||width>30) err=true;
			} catch (NumberFormatException e) {
				err=true;
			}
			if (err) {
				JOptionPane.showMessageDialog(caller,"Width must be a number between 1 and 30 inclusive","Invalid Argument",JOptionPane.ERROR_MESSAGE);
			} else {
				try {
					height = Integer.parseInt(JOptionPane.showInputDialog(caller,"Enter the height of the board.","Height?",JOptionPane.QUESTION_MESSAGE));
					if (height<1||height>20) err=true;
				} catch (NumberFormatException e) {
					err=true;
				}
				if (err) {
					JOptionPane.showMessageDialog(caller,"Height must be a number between 1 and 20 inclusive","Invalid Argument",JOptionPane.ERROR_MESSAGE);
				} else {
					try {
						numPlayers = Integer.parseInt(JOptionPane.showInputDialog(caller,"Enter the number of players.","Num Players?",JOptionPane.QUESTION_MESSAGE));
						if (numPlayers<1) err=true;
					} catch (NumberFormatException e) {
						err=true;
					}
					if (err) JOptionPane.showMessageDialog(caller,"number of players must be a number greater than 1.","Invalid Argument",JOptionPane.ERROR_MESSAGE);
					else new MapMaker(width,height,numPlayers,caller,click);
				}
			}
		}
		return current;
	}
	public static MapMaker current() {
		return current;
	}
	public void exit() {
		int option = JOptionPane.showConfirmDialog(this,"Do you wish save before exiting?","Save?",JOptionPane.YES_NO_CANCEL_OPTION);
		boolean success = true;
		if (option == JOptionPane.YES_OPTION) {
			success = save();
		}
		if ((option == JOptionPane.YES_OPTION||option == JOptionPane.NO_OPTION)&&(success)) {
			setVisible(false);
			if (window!=null) window.setVisible(true);
			else System.exit(0);
		}
	}
	public boolean save() {
		int option = JOptionPane.NO_OPTION;
		if (lastFile!=null) option = JOptionPane.showConfirmDialog(this,"Do you wish save to "+lastFile+"?","Save?",JOptionPane.YES_NO_OPTION);
		if (option!=JOptionPane.YES_OPTION) lastFile=JOptionPane.showInputDialog(this,"Enter the file to write to:","Save?",JOptionPane.PLAIN_MESSAGE);
		int option2 = JOptionPane.YES_OPTION;
		if (lastFile!=null) {
			if (option!=JOptionPane.YES_OPTION&&new File(CMap.expand(lastFile)).exists()) option2 = JOptionPane.showConfirmDialog(this,lastFile+ "already exists. Overwrite?","Overwrite?",JOptionPane.YES_NO_OPTION);
			if (option2 == JOptionPane.YES_OPTION) {
				try {
					CMap map = map(players);
					map.writeCMap(lastFile,false);
					setTitle("MapMaker - "+lastFile);
					return true;
				} catch (IOException e) {
					JOptionPane.showMessageDialog(this,e.getMessage(),"Err writing to file",JOptionPane.ERROR_MESSAGE);
				}
			}
		}
		return false;
	}
	public void setPlayers(Player[] players) {
		this.players=players;
	}
}
