package james.games.connection.mapmaker;

import java.awt.Panel;
import java.awt.Button;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import james.games.connection.JConnectionException;

public class ActionField extends Panel {
	private static final long serialVersionUID = 1L;
	private final LabeledTextField field;
	private final Button button;	
	/**
	 * Method getValue
	 *
	 *
	 * @return
	 *
	 */
	public int getValue() throws JConnectionException {
		return field.getValue();
	}

	/**
	 * Method ActionField
	 *
	 *
	 * @param label
	 * @param action
	 * @param listener
	 * @param initialValue
	 *
	 */
	public ActionField(String label, String action, ActionListener listener, int initialValue) {
		field = new LabeledTextField(label,initialValue);
		button = new Button(action);
		button.addActionListener(listener);
		setLayout(new GridLayout(2,1));
		add(field);
		add(button);
	}
	public void setValue(int val) {
		field.setValue(val);
	}
}
