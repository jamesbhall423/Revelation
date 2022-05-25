package james.games.connection;

import java.awt.Frame;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.TextArea;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import javax.swing.SwingUtilities;

public class NotificationScreen extends Frame {
	private ArrayList<CAction> notifications;
	private int sizeLast;
	private TextArea area;
	private boolean responsive = true;
	private Runnable updater = new Runnable() {
		public void run() {
			while (responsive) {
				try {
					Thread.sleep(100);
					SwingUtilities.invokeAndWait(new Runnable() {
						public void run() {
							updateNotifications();
						}
					});
				} catch (Exception e) {
					responsive=false;
					e.printStackTrace();
				}
			}
		}
	};
	/**
	 * Method NotificationScreen
	 *
	 *
	 * @param notifications
	 *
	 */
	public NotificationScreen(ArrayList<CAction> notifications) {
		setLayout(new GridLayout(1,1));
		area = new TextArea();
		area.setEditable(false);
		this.notifications=notifications;
		updateNotifications();
		add(area);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent ev) {
				responsive=false;
				setVisible(false);
			}
		});
		pack();
		setLocation(100,100);
		setMinimumSize(new Dimension(250,250));
		setTitle("Notifications");
		setVisible(true);
		Thread t = new Thread(updater);
		t.setPriority(2);
		t.start();
	}
	private void updateNotifications() {
		while (notifications.size()>sizeLast) area.setText(area.getText()+notifications.get(sizeLast++));
	}	
}
