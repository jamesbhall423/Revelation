package james.games.connection;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Panel;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serializable;
public class Square extends Panel {
	private static final long serialVersionUID = 1L;
	public static enum Type {
		Empty,Mountain,Forest,Road;
		public static Type getType(int ordinal) {
			switch (ordinal) {
				case 0:
				return Empty;
				case 1:
				return Mountain;
				case 2:
				return Forest;
				case 3:
				return Road;
				default:
				throw new IllegalArgumentException("Ordinal ("+ordinal+") does not reflect the range of the enum. (0-3)");
			}
			
		}
	}
	public static final int LEFT = 1;
	public static final int RIGHT = 2;
	public static final int UP = 4;
	public static final int DOWN = 8;
	public static class State implements Cloneable, Serializable {
		private static final long serialVersionUID = 1L;
		Type type = Type.Empty;
		int road;
		int[] teleporter = null;
		int contents = 0;
		@Override
		public Object clone() {
			try {
				State out = (State) super.clone();
				if (teleporter!=null) out.teleporter=teleporter.clone();
				return out;
			} catch (CloneNotSupportedException e) {
				throw new Error(e);
			}
		}
	}
	private State state = new State();
	private boolean showSpace = false;
	private int view = 0;
	private boolean called1 = false;
	private boolean called2 = false;
	private boolean highlight = false;
	public final int X;
	public final int Y;
	/**
	 * Method Square
	 *
	 *
	 * @param x
	 * @param y
	 * @param type
	 *
	 */
	public Square(int x, int y) {
		X=x;
		Y=y;
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				showSpace=true;
				paint(getGraphics());
			}
			@Override
			public void mouseExited(MouseEvent e) {
				showSpace=false;
				paint(getGraphics());
			}
		});
	}

	/**
	 * Method isEmpty
	 *
	 *
	 * @return
	 *
	 */
	public boolean isEmpty() {
		return state.contents==0;
	}

	/**
	 * Method isPlayer
	 *
	 *
	 * @param player1
	 *
	 * @return
	 *
	 */
	public boolean isPlayer(boolean player1) {
		if (player1) return state.contents==1;
		else return state.contents==-1;
	}
	/**
	 * Method isView
	 *
	 *
	 * @param player1
	 *
	 * @return
	 *
	 */
	public boolean isView(boolean player1) {
		if (player1) return view==1;
		else return view==-1;
	}

	/**
	 * Method player
	 *
	 *
	 * @return
	 *
	 */
	public int player() {
		return state.contents;
	}

	/**
	 * Method setPlayer
	 *
	 *
	 * @param player
	 *
	 */
	public void setPlayer(boolean player) {
		if (player) {
			state.contents=1;
			view=1;
		} else {
			state.contents=-1;
			view=-1;
		}
		paint(getGraphics());
	}

	/**
	 * Method getTeleporter
	 *
	 *
	 * @return
	 *
	 */
	public int[] getTeleporter() {
		if (state.teleporter!=null) return (int[]) state.teleporter.clone();
		else return null;
	}

	/**
	 * Method setTeleporter
	 *
	 *
	 * @param dest
	 *
	 */
	public void setTeleporter(int[] dest) {
		state.teleporter=dest;
		if (dest!=null) state.teleporter = (int[]) dest.clone();
		paint(getGraphics());
	}

	/**
	 * Method time
	 *
	 *
	 * @return
	 *
	 */
	public int time() {
		switch (state.type) {
			case Empty:
			return 1;
			case Mountain:
			return 0;
			case Forest:
			return 2;
			case Road:
			return -2;
		}
		throw new Error("Type is immposible: "+state.type);
	}
	/**
	 * Method canPlace
	 *
	 *
	 * @param player
	 *
	 * @return
	 *
	 */
	public boolean canPlace(boolean player, boolean revert) {
		if (player) called1=true;
		else called2=true;
		if (state.contents==0) return !revert;
		else return revert&&(player!=(state.contents>0));
	}

	/**
	 * Method getView
	 *
	 *
	 * @return
	 *
	 */
	public int getView() {
		return view;
	}

	/**
	 * Method setView
	 *
	 *
	 * @param observed
	 *
	 */
	public void setView(int observed) {
		view=observed;
		paint(getGraphics());
	}
	public void setHighlight(boolean highlight) {
		this.highlight=highlight;
		paint(getGraphics());
	}
	public boolean getHighlight() {
		return highlight;
	}
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		if (g==null) return;
		Graphics2D g2 = (Graphics2D) g;
		Dimension frame = getSize();
		if (frame==null) {
			new Exception().printStackTrace();
			System.exit(0);
		}
		double height = frame.getHeight();
		double width = frame.getWidth();
		Color color;
		switch (state.type) {
			case Empty:
			case Road:
			color=Color.orange;
			break;
			case Mountain:
			color=Color.gray;
			break;
			case Forest:
			color=Color.green;
			break;
			default:
			throw new Error("Invalid type");
		}
		if (highlight) g2.setColor(color);
		else g2.setColor(color.darker());
		g2.fill(new Rectangle(0,0,(int)width,(int)height));
		if (state.teleporter!=null) {
			g2.setColor(Color.blue);
			g2.fill(new Rectangle(0,0,12,12));
			g2.setColor(Color.black);
			g2.drawString(""+state.teleporter[2],0,11);
		}
		if (state.type==Type.Road) {
			g2.setColor(Color.yellow);
			if ((state.road&LEFT)!=0) g2.fill(new Rectangle(0,(int)height/2-3,(int)width/2,6));
			if ((state.road&RIGHT)!=0) g2.fill(new Rectangle((int)width/2,(int)height/2-3,(int)width/2,6));
			if ((state.road&UP)!=0) g2.fill(new Rectangle((int)width/2-3,0,6,(int)height/2));
			if ((state.road&DOWN)!=0) g2.fill(new Rectangle((int)width/2-3,(int)height/2,6,(int)height/2));
		}
		if (view==1) g2.setColor(Color.cyan);
		else if (view==-1) g2.setColor(Color.magenta);
		if (view!=0) g2.fill(new Ellipse2D.Double(width/2-5,height/2-5,10,10));
		g2.setColor(Color.black);
		if (showSpace) g2.drawString(coordinates(X,Y),(int)width/2-10,(int)height/2+5);
	}
	public Type getType() {
		return state.type;
	}
	public void setType(Type type) {
		state.type=type;
		paint(getGraphics());
	}
	public int getRoad() {
		return state.road;
	}
	public void setRoad(int road) {
		state.road=road;
		paint(getGraphics());
	}
	public boolean called(boolean player) {
		if (time()==0) return true;
		if (player) return called1;
		else return called2;
	}
	public static String coordinates(int X, int Y) {
		char first = (char)('a'+Y);
		int x = X+1;
		if (x<10) return first+"0"+x;
		else return first+""+x;
	}
	public State getState() {
		return state;
	}
	public Square setState(State state) {
		this.state=state;
		view=state.contents;
		if (view!=0) {
			called1=true;
			called2=true;
		}
		return this;
	}
	public void setPlayer(int player) {
		view=player;
		state.contents=player;
		paint(getGraphics());
	}
	public void updatePlayer(int player) {
		state.contents=player;
	}
	public static int valueSide(boolean side) {
		if (side) return 1;
		else return -1;
	}
	public static String valueOf(int player) {
		switch (player) {
			case -1:
			return "magenta";
			case 0:
			return "empty";
			case 1:
			return "cyan";
			default:
			throw new IllegalArgumentException("Player must be -1,0,or 1.");
		}
	}
}
