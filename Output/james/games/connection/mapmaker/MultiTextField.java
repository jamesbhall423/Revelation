package james.games.connection.mapmaker;

import java.awt.Panel;
import java.awt.GridLayout;
import james.games.connection.JConnectionException;

public class MultiTextField extends Panel {
	private static final long serialVersionUID = 1L;
	private final LabeledTextField[] fields;	
	/**
	 * Method MultiTextField
	 *
	 *
	 * @param labels
	 * @param values
	 *
	 */
	public MultiTextField(String[] labels, int[] values) {
		fields = new LabeledTextField[labels.length];
		for (int i = 0; i < fields.length; i++) fields[i] = new LabeledTextField(labels[i],values[i]);
		setLayout(new GridLayout(0,1));
		for (int i = 0; i < fields.length; i++) add(fields[i]);
	}

	/**
	 * Method getValues
	 *
	 *
	 * @return
	 *
	 */
	public int[] getValues() throws JConnectionException {
		int[] out = new int[fields.length];
		for (int i = 0; i < out.length; i++) out[i]=fields[i].getValue();
		return out;
	}	
}
