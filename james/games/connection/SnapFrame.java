package james.games.connection;

import james.games.connection.mapmaker.*;
import java.awt.Frame;
import java.awt.TextArea;
import java.awt.Button;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JRadioButton;
import javax.swing.JCheckBox;

public class SnapFrame extends Frame implements ActionListener {
	private static final long serialVersionUID = 1L;
	SuperGroup test;
	
	/**
	 * Method SnapFrame
	 *
	 *
	 */
	public SnapFrame() {
		setLayout(new GridLayout(0,1));
		String[] names = {"a","b","c","d"};
		test = new SuperGroup(JRadioButton.class,names,"character");
		Button button = new Button("Do Something");
		button.addActionListener(this);
		pack();
		add(test);
		add(button);
		setVisible(true);
	}
	public void actionPerformed(ActionEvent e) {
		try {
			System.out.println(test.getSelection());
		} catch (JConnectionException ex) {
			System.out.println(ex.getMessage());
		}
	}
}
