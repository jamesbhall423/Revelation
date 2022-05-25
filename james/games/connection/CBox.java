package james.games.connection;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.MenuItem;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JOptionPane;
import james.games.connection.action.*;
import james.games.connection.mapmaker.Player;
public abstract class CBox extends BaseMap {
	private static class Clicker extends MouseAdapter {
		private static final long serialVersionUID = 1L;
		private CBox box = null;
		public void setBox(CBox box) {
			this.box=box;
		}
		@Override
		public void mouseClicked(MouseEvent e) {
			if (!box.responsive) return;
			Square source = (Square)e.getSource();
			int time = source.time();
			if (time==0) return;
			if (box.roadLast!=null&&!source.getHighlight()) return;
			if (box.timeBlock>0) return;
			int answer = JOptionPane.NO_OPTION;
			if (source.getView()==0) {
				if (box.revertsLeft>0) answer = JOptionPane.showConfirmDialog(source,"Revert? "+box.revertsLeft+" left.","Revert?",JOptionPane.YES_NO_CANCEL_OPTION);
			}
			else answer = JOptionPane.showConfirmDialog(source,"Check Square?","Check?",JOptionPane.OK_CANCEL_OPTION);
			if (time==-2&&(answer==JOptionPane.YES_OPTION||answer==JOptionPane.NO_OPTION)) {
				if (box.roadLast!=null) {
					time=1;
					box.doRoads(false);
					box.roadLast=null;
				} else {
					time=0;
					box.roadLast=source;
					box.doRoads(true);
				}
			}
			if (answer==JOptionPane.YES_OPTION) box.doAction(new SquareAction(box.player,source.X,source.Y,true,box.side,box.time[box.player]));
			else if (answer==JOptionPane.NO_OPTION) box.doAction(new SquareAction(box.player,source.X,source.Y,false,box.side,box.time[box.player]));
			if (answer==JOptionPane.YES_OPTION||answer==JOptionPane.NO_OPTION) {
				box.timeBlock=time;
				box.autoTurns();
			}
		}
		public Clicker() {
			last=this;
		}
	}
	private static final long serialVersionUID = 2L;
	private static Clicker last;
	private boolean responsive=true;
	
	protected final boolean saver;
	
	private int player;
	private boolean finiteDeclares;
	private int declaresLeft;
	private int revertsLeft;
	private int showsLeft;
	private String message;
	private boolean side;
	
	private int[] time;
	private boolean[] blocked;
	protected ArrayList<CAction> notifications = new ArrayList<CAction>();
	protected HashMap<String,MenuItem> menuItems;
	protected Menu game;
	private String miniTitle;
	
	private Square roadLast;
	private int timeBlock = 0;
	private boolean autoTurn = true;
	public CBox(CMap map, int playerNum,boolean save,CAction[] gameActions) {
		super(map,new Clicker(),false);
		last.setBox(this);
		this.player=playerNum;
		Player player = map.players[playerNum];
		finiteDeclares = (player.declareVictories>0);
		declaresLeft = player.declareVictories;
		revertsLeft = player.numReverts;
		showsLeft = player.showReverts;
		message = player.message;
		side = player.side;
		time = new int[map.players.length];
		blocked = new boolean[map.players.length];
		saver=save;
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent ev) {
				exit();
			}
		});
		menuItems = new HashMap<String, MenuItem>(2*(4+gameActions.length));
		MenuBar bar = new MenuBar();
		
		Menu file = new Menu("file");
		if (save) addMenuItem(file,new MenuItem("save"),CAction.SAVE);
		addMenuItem(file,new MenuItem("exit"),CAction.EXIT);
		bar.add(file);
		
		game = new Menu("game");
		if (finiteDeclares) addMenuItem(game,new MenuItem("declare win - "+declaresLeft),CAction.DECLARE);
		if (showsLeft>0) addMenuItem(game,new MenuItem("show reverts - "+showsLeft),CAction.SHOW_REVERTS);
		addMenuItem(game,new MenuItem("end turn"),CAction.TURN);
		addMenuItem(game,new MenuItem("manual turns"),CAction.TOGGLE_TURNS);
		if (gameActions!=null) for (int i = 0; i < gameActions.length; i++) {
			addMenuItem(game,new MenuItem(gameActions[i].getName()),gameActions[i]);
		}
		bar.add(game);
		Menu view = new Menu("view");
		MenuItem showNotifications = new MenuItem("Notification Log");
		showNotifications.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ev) {
				new NotificationScreen(notifications);
			}
		});
		notifications.add(new StartNotification(this.player,message));
		view.add(showNotifications);
		bar.add(view);
		setMenuBar(bar);
		setMiniTitle("Pl: "+(this.player+1)+"  "+(side ? "up" : "flat"));
	}
	private void addMenuItem(Menu menu, MenuItem item, final CAction action) {
		String name = action.getName();
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ev) {
				doAction(action.create(player,time[player]));
			}
		});
		menu.add(item);
		menuItems.put(name,item);
	}
	private void doAction(CAction action) {
		if (responsive&&action.getClass()==CAction.TURN.getClass()) doTurn(action);
		else if (action.getClass()==CAction.EXIT.getClass()) exit();
		else if (action.getClass()==CAction.SAVE.getClass()) save();
		else if (!responsive) return;
		else if (action.getClass()==CAction.TOGGLE_TURNS.getClass()) toggleTurns(action);
		else proccessAction(action);
	}
	public void proccessAction2(CAction action) {
		//System.out.println("user: "+(player+1));
		//System.out.println("proccess2: "+action);
		Class clazz = action.getClass();
		if (clazz==SquareAction.class) doSquare((SquareAction) action);
		else if (clazz==DeclareVictory.class) { if (action.player()==player) testVictory((DeclareVictory) action); }
		else if (clazz==ShowReverts.class) { if (action.player()==player) showReverts((ShowReverts) action); }
		else if (clazz==DoTurn.class) turn();
		else if (clazz==Blocked.class)  {if (causesEnd((Blocked)action)) blocked();}
		else extraAction(action);
		if (action.player()==player&&action.typeVal()!=CAction.VAL_TURN||action.isPublic()) notifications.add(action);
	}
	public void turn() {
		if (!finiteDeclares) testVictory();
		if (!blocked[player]&&testBlocked()&&revertsLeft==0) proccessAction(CAction.BLOCKED.create(player,time[player]));
	}
	public void setResponsive(boolean newVal) {
		//System.out.println("Changing responsiveness "+(player+1)+": "+newVal);
		boolean change = responsive!=newVal;
		this.responsive=newVal;
		if (change) updateTitle();
	}
	public int time(int pl) {
		return time[pl];
	}
	public void doSquare(SquareAction action) {
		Square square = getSquare(action.x,action.y);
		int contents = square.player();
		int result=contents;
		boolean called = square.called(action.side);
		if ((!called||!action.revert)&&square.canPlace(action.side,action.revert)) result=Square.valueSide(action.side);
		if (action.player()==player) {
			square.setPlayer(result);
			action.setResponse(contents+" to "+result);
			if (!called&&action.revert) revertsLeft--;
		} else {
			square.updatePlayer(result);
		}
	}
	private void doRoads(boolean highlight) {
		int road = roadLast.getRoad();
		try {
			if ((road&Square.LEFT)>0) getSquare(roadLast.X-1,roadLast.Y).setHighlight(highlight);
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		try {
			if ((road&Square.RIGHT)>0) getSquare(roadLast.X+1,roadLast.Y).setHighlight(highlight);
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		try {
			if ((road&Square.UP)>0) getSquare(roadLast.X,roadLast.Y-1).setHighlight(highlight);
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		try {
			if ((road&Square.DOWN)>0) getSquare(roadLast.X,roadLast.Y+1).setHighlight(highlight);
		} catch (ArrayIndexOutOfBoundsException e) {
		}
	}
	public boolean testBlocked() {
		Pathfinder finder = new Pathfinder(board(),new SquareCondition() {
			public boolean conditionFulfilled(Square square) {
				return (square.getView()!=square.valueSide(!side));
			}
		});
		boolean out = !findPath(finder);
		return out;
	}
	public int testVictory() {
		Pathfinder finder = new Pathfinder(board(),new SquareCondition() {
			public boolean conditionFulfilled(Square square) {
				return square.isPlayer(side);
			}
		});
		boolean win = findPath(finder);
		if (win) {
			win();
			return 1;
		}
		else if (finiteDeclares&&--declaresLeft<=0) {
			loss();
			return -1;
		} else return 0;
	}
	public void testVictory(DeclareVictory action) {
		switch (testVictory()) {
			case -1:
			action.setResponse("Loss");
			break;
			case 0:
			action.setResponse("Fail but still in");
			break;
			case 1:
			action.setResponse("Victory");
			break;
			default:
			throw new Error("Invalid result from testVictory().");
		}
		decMenu(action,"declare win",--declaresLeft);
	}
	public void showReverts(ShowReverts action) {
		showsLeft--;
		int height = height();
		int width = width();
		String result = "revealed:";
		for (int y = 0; y < height; y++) for (int x = 0; x < width; x++) {
			Square square = getSquare(x,y);
			if (square.isView(side)&&!square.isPlayer(side)) {
				square.setView(square.player());
				result+= " "+Square.coordinates(square.X,square.Y);
			}
		}
		action.setResponse(result);
		decMenu(action,"show reverts",--showsLeft);
	}
	public void doTurn(CAction action) {
		timeBlock--;
		time[action.player()]=action.endTime();
		if (roadLast!=null) doRoads(false);
		roadLast=null;
		proccessAction(action);
	}
	public static int[] constArray(int val, int size) {
		int[] out = new int[size];
		for (int i = 0; i < out.length; i++) out[i]=val;
		return out;
	}
	public static int[] lineArray(int size) {
		int[] out = new int[size];
		for (int i = 0; i < out.length; i++) out[i]=i;
		return out;
	}
	private boolean findPath(Pathfinder finder) {
		if (side) return finder.path(lineArray(width()),constArray(0,width()),lineArray(width()),constArray(height()-1,width()));
		else return finder.path(constArray(0,height()),lineArray(height()),constArray(width()-1,height()),lineArray(height()));
	}
	private boolean causesEnd(Blocked action) {
		blocked[action.player()]=true;
		boolean end = true;
		for (int i = 0; i < blocked.length; i++) end = (end&&blocked[i]);
		return end;
	}
	public int[] getTimes() {
		return time.clone();
	}
	public int player() {
		return player;
	}
	public void setMiniTitle(String title) {
		miniTitle=title;
		updateTitle();
	}
	public String getMiniTitle() {
		return miniTitle;
	}
	private void updateTitle() {
		if (responsive)	setTitle(miniTitle);
		else setTitle(miniTitle+" (waiting)");
	}
	protected void decMenu(CAction dec, String baseText, int newVal) {
		MenuItem item = menuItems.get(dec.getName());
		if (newVal>0) item.setLabel(baseText+" - "+newVal);
		else game.remove(item);
	}
	private void autoTurns() {
		if (!autoTurn) return;
		while (timeBlock>0) {
			doTurn(CAction.TURN.create(player,time[player]));
		}
	}
	private void toggleTurns(CAction action) {
		autoTurn=!autoTurn;
		MenuItem access = menuItems.get(action.getName());
		if (autoTurn) {
			access.setLabel("manual turns");
			autoTurns();
		}
		else access.setLabel("auto turns");
	}
	public abstract void proccessAction(CAction action);
	protected abstract void extraAction(CAction action);
	public abstract void save();
	public abstract void exit();
	public abstract void win();
	public abstract void loss();
	public abstract void blocked();
}
