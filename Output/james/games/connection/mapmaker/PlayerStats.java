package james.games.connection.mapmaker;

import james.games.connection.JConnectionException;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.TextArea;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Window;
import javax.swing.JRadioButton;
import javax.swing.JOptionPane;

public class PlayerStats extends Frame {
	private static final long serialVersionUID = 1L;
	private static final Dimension dimension = new Dimension(222,254);
	private static final String[] FIELD_NAMES = {"numReverts","showReverts","Declare Win"};
	private static final String[] SIDE_NAMES = {"Side1","Side2"};
	private MultiTextField field;
	private Window window;
	private TextArea area;
	private SuperGroup side;
	private Player player;	
	/**
	 * Method PlayerStats
	 *
	 *
	 * @param player
	 * @param order
	 *
	 */
	public PlayerStats(Player player, int order, Window window) {
		this.window=window;
		this.player=player;
		setTitle("Stats for player "+order);
		int[] stats = new int[3];
		stats[0]=player.numReverts;
		stats[1]=player.showReverts;
		stats[2]=player.declareVictories;
		field = new MultiTextField(FIELD_NAMES,stats);
		
		if (player.message==null) area = new TextArea("Write the player's instructions here.");
		else area = new TextArea(player.message);
		
		side = new SuperGroup(JRadioButton.class,SIDE_NAMES,"Side");
		if (player.side) side.select(0);
		else side.select(1);
		
		setMinimumSize(dimension);
		setLocation(window.getLocation());
		setLayout(new GridLayout(0,1));
		pack();
		add(field);
		add(area);
		add(side);
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent ev) {
				int execute = JOptionPane.showConfirmDialog(PlayerStats.this,"Record Changes?","record?", JOptionPane.YES_NO_CANCEL_OPTION);
				if (execute==JOptionPane.YES_OPTION) save();
				if (execute==JOptionPane.YES_OPTION||execute==JOptionPane.NO_OPTION) exit();
			}
		});
		
		setVisible(true);
	}

	/**
	 * Method save
	 *
	 *
	 */
	private boolean save() {
		try {
			int[] values = field.getValues();
			player.numReverts=values[0];
			player.showReverts=values[1];
			player.declareVictories=values[2];
			player.message=area.getText();
			player.side = (side.getSelection()==0);
		} catch (JConnectionException e) {
			JOptionPane.showMessageDialog(this,e.getMessage(),"Unable to change data",JOptionPane.ERROR_MESSAGE);
		}
		return false;
	}

	/**
	 * Method exit
	 *
	 *
	 */
	private void exit() {
		setVisible(false);
		window.setVisible(true);
	}	
}
