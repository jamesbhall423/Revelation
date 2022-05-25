package james.games.connection.mapmaker;

import java.awt.Panel;
import java.awt.Label;
import java.awt.GridLayout;
import javax.swing.JToggleButton;
import james.games.connection.JConnectionException;

public class SuperGroup extends Panel {
	private static final long serialVersionUID = 1L;
	private final Label label;
	private final GroupedButtons group;
	/**
	 * Method GroupedButtons
	 *
	 *
	 * @param clazz
	 * @param names
	 *
	 */
	public SuperGroup(Class<? extends JToggleButton> clazz, String[] names, String top) {
		group=new GroupedButtons(clazz,names);
		label = new Label(top,Label.CENTER);
		setLayout(new GridLayout(2,1));
		add(label);
		add(group);
	}

	/**
	 * Method select
	 *
	 *
	 * @param btn
	 *
	 */
	public void select(int btn) {
		group.select(btn);
	}

	/**
	 * Method getSelection
	 *
	 *
	 * @return
	 *
	 */
	public int getSelection() throws JConnectionException {
		return group.getSelection();
	}

	/**
	 * Method getRadioSelection
	 *
	 *
	 * @return
	 *
	 */
	public int getRadioSelection() throws JConnectionException {
		return group.getRadioSelection();
	}
	/**
	 * Method getCheckSelection
	 *
	 *
	 * @return
	 *
	 */
	public int getCheckSelection() {
		return group.getCheckSelection();
	}		
}
