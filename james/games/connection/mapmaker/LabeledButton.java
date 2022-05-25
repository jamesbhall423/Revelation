package james.games.connection.mapmaker;

import java.awt.Panel;
import java.awt.Label;
import java.awt.GridLayout;
import javax.swing.JToggleButton;

public class LabeledButton extends Panel {
	private static final long serialVersionUID = 1L;
	private final CenteredButton button;
	private final Label label;	
	/**
	 * Method LabeledButton
	 *
	 *
	 * @param button
	 * @param text
	 *
	 */
	public LabeledButton(JToggleButton btn, String text) {
		button = new CenteredButton(btn);
		label=new Label(text,Label.CENTER);
		setLayout(new GridLayout(2,1));
		add(button);
		add(label);
	}

	/**
	 * Method setSelected
	 *
	 *
	 * @param selected
	 *
	 */
	public void setSelected(boolean selected) {
		button.setSelected(selected);
	}

	/**
	 * Method isSelected
	 *
	 *
	 * @return
	 *
	 */
	public boolean isSelected() {
		return button.isSelected();
	}	
}
