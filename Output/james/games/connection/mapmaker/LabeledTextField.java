package james.games.connection.mapmaker;

import james.games.connection.JConnectionException;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.Label;

public class LabeledTextField extends Panel {
	private static final long serialVersionUID = 1L;
	private TextField field;
	private Label label;	
	/**
	 * Method getValue
	 *
	 *
	 * @return
	 *
	 */
	public int getValue() throws JConnectionException {
		int out = 0;
		try {
			out = Integer.parseInt(field.getText());
		} catch (NumberFormatException e) {
			throw new JConnectionException(label.getText()+" must be a number. Value= \""+field.getText()+"\".");
		}
		if (out<0) throw new JConnectionException(label.getText()+" must be a number <= 0. Value= \""+field.getText()+"\".");
		return out;
	}


	/**
	 * Method LabeledTextField
	 *
	 *
	 * @param name
	 *
	 */
	public LabeledTextField(String name) {
		field=new TextField();
		addComponents(name);
	}
	/**
	 * Method LabeledTextField
	 *
	 *
	 * @param name
	 *
	 */
	public LabeledTextField(String name, int value) {
		field=new TextField(""+value);
		addComponents(name);
	}	
	private void addComponents(String name) {
		label=new Label(name,Label.CENTER);
		setLayout(new GridLayout(1,0));
		add(field);
		add(label);
	}
	public void setValue(int val) {
		field.setText(val+"");
	}
}
