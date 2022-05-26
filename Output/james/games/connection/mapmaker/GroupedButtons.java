package james.games.connection.mapmaker;

import java.awt.Panel;
import java.awt.GridLayout;
import javax.swing.JToggleButton;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import james.games.connection.JConnectionException;

public class GroupedButtons extends Panel {
	private static final long serialVersionUID = 1L;
	private LabeledButton[] buttons;
	public final boolean isRadio;	
	/**
	 * Method GroupedButtons
	 *
	 *
	 * @param clazz
	 * @param names
	 *
	 */
	public GroupedButtons(Class<? extends JToggleButton> clazz, String[] names) {
		try {
			buttons = new LabeledButton[names.length];
			isRadio = (clazz==JRadioButton.class);
			JToggleButton[] btns = new JToggleButton[names.length];
			for (int i = 0; i < btns.length; i++) btns[i] = clazz.newInstance();
			if (isRadio) {
				ButtonGroup group = new ButtonGroup();
				for (int i = 0; i < btns.length; i++) group.add(btns[i]);
			}
			for (int i = 0; i < buttons.length; i++) buttons[i]=new LabeledButton(btns[i],names[i]);
			setLayout(new GridLayout(1,0));
			for (int i = 0; i < buttons.length; i++) add(buttons[i]);
		} catch (InstantiationException e) {
			throw new Error(e);
		} catch (IllegalAccessException e) {
			throw new Error(e);
		}
	}

	/**
	 * Method select
	 *
	 *
	 * @param btn
	 *
	 */
	public void select(int btn) {
		buttons[btn].setSelected(true);
	}

	/**
	 * Method getSelection
	 *
	 *
	 * @return
	 *
	 */
	public int getSelection() throws JConnectionException {
		if (isRadio) return getRadioSelection();
		else return getCheckSelection();
	}

	/**
	 * Method getRadioSelection
	 *
	 *
	 * @return
	 *
	 */
	public int getRadioSelection() throws JConnectionException {
		int i;
		for (i = 0; i<buttons.length&&!buttons[i].isSelected(); i++);
		if (i==buttons.length) throw new JConnectionException("A button from the group must be selected.");
		return i;
	}
	/**
	 * Method getCheckSelection
	 *
	 *
	 * @return
	 *
	 */
	public int getCheckSelection() {
		int out = 0;
		for (int i = 0; i < buttons.length; i++) if (buttons[i].isSelected()) out+=(1<<i);
		return out;
	}	
}
