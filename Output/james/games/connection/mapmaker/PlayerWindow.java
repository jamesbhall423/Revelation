package james.games.connection.mapmaker;

import james.games.connection.MapMaker;
import james.games.connection.JConnectionException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JOptionPane;

public class PlayerWindow extends Frame {
	private static final long serialVersionUID = 1L;
	private static final Dimension dimension = new Dimension(180,189);
	private ActionField num;
	private ActionField specific;
	private Player[] players;
	/**
	 * Method PlayerWindow
	 *
	 *
	 * @param players
	 *
	 */
	public PlayerWindow(Player[] pls) {
		players=pls;
		if (players==null) players = new Player[0];
		num = new ActionField("numPlayers","Change", new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				try {
					Player[] temp = new Player[num.getValue()];
					int i;
					for (i=0; i<temp.length&&i<players.length; i++) temp[i]=players[i];
					for (;i<temp.length; i++) temp[i]=new Player(i);
					players=temp;
				} catch (JConnectionException e) {
					JOptionPane.showMessageDialog(PlayerWindow.this,e.getMessage(),"Unable to change number of players",JOptionPane.ERROR_MESSAGE);
				}
			}
		}, players.length);
		specific = new ActionField("Player","Stats", new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				try {
					int pick = specific.getValue();
					num.setValue(players.length);
					if (pick<1||pick>players.length) throw new JConnectionException("Player must be be between 1 and "+players.length+", inclusive. Value = "+pick);
					setVisible(false);
					new PlayerStats(players[pick-1],pick,PlayerWindow.this);
				} catch (JConnectionException e) {
					JOptionPane.showMessageDialog(PlayerWindow.this,e.getMessage(),"Unable to change number of players",JOptionPane.ERROR_MESSAGE);
				}
			}
		}, 1);
		
		setLocation(MapMaker.current().getLocation());
		setMinimumSize(dimension);
		setTitle("Players");
		setLayout(new GridLayout(2,1));
		pack();
		add(num);
		add(specific);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent ev) {
				setVisible(false);
				MapMaker.current().setPlayers(players);
				MapMaker.current().setEnabled(true);
			}
		});
		setVisible(true);
	}	
}
