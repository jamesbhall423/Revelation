package james.games.connection.mapmaker;

import java.awt.Panel;
import java.awt.Label;
import java.awt.GridLayout;
import javax.swing.JCheckBox;
import james.games.connection.*;

public class Teleporter extends Panel {
	private static final long serialVersionUID = 1L;
	private static Square last;
	private static boolean[] labels = new boolean[10];
	private Label front;
	private CenteredButton box;
	private Label back;	
	/**
	 * Method Teleporter
	 *
	 *
	 * @param square
	 *
	 */
	public Teleporter(Square square) {
		last=square;
		setLayout(new GridLayout(1,0));
		front = new Label("tele",Label.RIGHT);
		box = new CenteredButton(new JCheckBox());
		String text;
		int[] dest = square.getTeleporter();
		if (dest==null) text = "Next Sq";
		else {
			box.setSelected(true);
			text = Square.coordinates(dest[0],dest[1]);
		}
		back = new Label(text,Label.LEFT);
		add(front);
		add(box);
		add(back);
	}

	/**
	 * Method current
	 *
	 *
	 * @return
	 *
	 */
	public static boolean current() {
		return last!=null;
	}

	/**
	 * Method addTeleporter
	 *
	 *
	 * @param square
	 *
	 */
	public static void addTeleporter(Square square) throws JConnectionException {
		try {
			if (square!=last) {
				if (square.getTeleporter()!=null) throw new JConnectionException("Destination already has teleporter");
				int label;
				for (label=0; label<labels.length&&labels[label]; label++);
				if (label==labels.length) throw new JConnectionException("Reached maximum number of teleporters (10)");
				labels[label]=true;
				int[] source = new int[3];
				source[0]=last.X;
				source[1]=last.Y;
				source[2]=label;
				square.setTeleporter(source);
				int[] dest = new int[3];
				dest[0]=square.X;
				dest[1]=square.Y;
				dest[2]=label;
				last.setTeleporter(dest);
			}
		} catch (JConnectionException e) {
			throw (e);
		} finally {
			last=null;
		}
	}

	/**
	 * Method loadValues
	 *
	 *
	 * @return
	 *
	 */
	public boolean loadValues() {
		boolean b = box.isSelected();
		int[] cur = last.getTeleporter();
		if (!b&&(cur!=null)) {
			MapMaker.current().getSquare(cur[0],cur[1]).setTeleporter(null);
			last.setTeleporter(null);
			labels[cur[2]]=false;
		}
		if ((!b)||(cur!=null)) last=null;
		return b;
	}
	public boolean isSelected() {
		return box.isSelected();
	}
	public void cut() {
		last=null;
	}
}
