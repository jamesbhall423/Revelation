package james.games.connection;

import james.games.connection.mapmaker.Player;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.MouseListener;

public abstract class BaseMap extends Frame {
	private static final long serialVersionUID = 1L;
	public static final int XBUMP = 7;
	public static final int YBUMP = 53;
	public static final int SCALE = 32;
	private Square[][] squares;	
	public BaseMap(int xEnd, int yEnd,MouseListener listener, boolean show) {
		squares = new Square[yEnd][xEnd];
		for (int y = 0; y < yEnd; y++) for (int x = 0; x < xEnd; x++) squares[y][x]=new Square(x,y);
		setValues(xEnd,yEnd,listener,show);
	}
	public BaseMap(CMap map, MouseListener listener, boolean show) {
		int yEnd = map.squares.length;
		int xEnd = map.squares[0].length;
		squares = new Square[yEnd][xEnd];
		for (int y = 0; y < yEnd; y++) for (int x = 0; x < xEnd; x++) squares[y][x]=new Square(x,y).setState(map.squares[y][x]);
		setValues(xEnd,yEnd,listener,show);
	}
	private void setValues(int xEnd, int yEnd, MouseListener listener, boolean show) {
		for (int y = 0; y < yEnd; y++) for (int x = 0; x < xEnd; x++) squares[y][x].addMouseListener(listener);
		setBackground(Color.white);
		setLayout(new GridLayout(yEnd,xEnd,2,2));
		for (int y = 0; y < yEnd; y++) for (int x = 0; x < xEnd; x++) add(squares[y][x]);
		pack();
		setMinimumSize(new Dimension(SCALE*xEnd+XBUMP,SCALE*yEnd+YBUMP));
		setResizable(false);
		if (show) setVisible(true);
	}
	public CMap map(Player[] players) {
		Square.State[][] states = new Square.State[squares.length][squares[0].length];
		for (int y = 0; y < states.length; y++) for (int x = 0; x < states[y].length; x++) states[y][x]=squares[y][x].getState();
		return new CMap(states,players);
	}
	public Square getSquare(int x, int y) {
		return squares[y][x];
	}
	public int height() {
		return squares.length;
	}
	public int width() {
		return squares[0].length;
	}
	public Square[][] board() {
		return squares;
	}
}
