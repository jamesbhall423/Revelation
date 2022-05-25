package james.games.connection.mapmaker;

import james.games.connection.MapMaker;
import james.games.connection.Square;
import james.games.connection.JConnectionException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JCheckBox;
import javax.swing.JRadioButton;
import javax.swing.JOptionPane;

public class SquareWindow extends Frame {
	private static final long serialVersionUID = 1L;
	private static final Dimension dimension = new Dimension(260,360);
	private static final String[] TYPES = {"Empty","Mount","Forest","Road"};
	private static final String[] ROADS = {"West","East","North","South"};
	private static final String[] CONTENT = {"Player1","Empty","Player2"};
	private Square square;
	private Teleporter tele;
	private Label typeL;
	private GroupedButtons type;
	private Label roadL;
	private GroupedButtons road;
	private Label contentsL;
	private GroupedButtons contents;
	private boolean offBoard;
	/**
	 * Method SquareWindow
	 *
	 *
	 * @param square
	 *
	 */
	public SquareWindow(Square square, boolean offBoard) {
		this.offBoard=offBoard;
		square.setHighlight(true);
		this.square=square;
		setTitle("Square: "+Square.coordinates(square.X,square.Y));
		
		tele = new Teleporter(square);
		
		typeL = new Label("Type",Label.CENTER);
		typeL.setBackground(Color.red);
		type = new GroupedButtons(JRadioButton.class,TYPES);
		type.select(square.getType().ordinal());
		
		roadL = new Label("Road",Label.CENTER);
		roadL.setBackground(Color.yellow);
		road = new GroupedButtons(JCheckBox.class,ROADS);
		for (int i = 0; i < ROADS.length; i++) if ((square.getRoad()&(1<<i))>0) road.select(i);
		
		contentsL = new Label("Contents",Label.CENTER);
		contentsL.setBackground(Color.green);
		contents = new GroupedButtons(JRadioButton.class,CONTENT);
		contents.select((-square.player())+1);
		
		setLocation(MapMaker.current().getLocation());
		setMinimumSize(dimension);
		setResizable(false);
		setLayout(new GridLayout(0,1));
		pack();
		add(tele);
		add(typeL);
		add(type);
		add(roadL);
		add(road);
		add(contentsL);
		add(contents);
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent ev) {
				int execute = JOptionPane.showConfirmDialog(SquareWindow.this,"Record Changes?","record?", JOptionPane.YES_NO_CANCEL_OPTION);
				if (execute==JOptionPane.YES_OPTION) save();
				if (execute==JOptionPane.NO_OPTION) tele.cut();
				if (execute==JOptionPane.YES_OPTION||execute==JOptionPane.NO_OPTION) exit();
			}
		});
		
		setVisible(true);
	}
	private void save() {
		try {
			if (!offBoard) tele.loadValues();
			else tele.cut();
			Square.Type t = Square.Type.getType(type.getSelection());
			int rd = road.getSelection();
			int player = -(contents.getSelection()-1);
			square.setType(t);
			square.setRoad(rd);
			square.setPlayer(player);
		} catch (JConnectionException e) {
			throw new Error(e);
		}
	}
	private void exit() {
		setVisible(false);
		square.setHighlight(false);
		MapMaker.current().setEnabled(true);
		
	}
}
