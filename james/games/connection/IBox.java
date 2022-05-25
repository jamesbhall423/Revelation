package james.games.connection;

import javax.swing.JOptionPane;

import james.games.connection.action.Exit;
import james.games.connection.action.Message_Action;
public class IBox extends CBox {
	private ActionPipeline line;
	private boolean host;
	private static CAction[] gActions = {CAction.MESSAGE};
	private final CBuffer buffer;
	public IBox(CMap map, int playerNum,boolean host, CBuffer buffer) {
		super(map,playerNum,false,gActions);
		this.buffer = buffer;
		line = new ActionPipeline(this,buffer);
		setVisible(true);
		this.host=host;
	}
	public void proccessAction(CAction action) {
		//System.out.println("proccess1: "+action);
		if (action.getClass()==Message_Action.class) ((Message_Action)action).setMessage(JOptionPane.showInputDialog(this,"Enter the message you want to send."));
		line.distribute(action);
	}
	public void extraAction(CAction action) {
		if (action.getClass()==Exit.class) {
			exit();
		}
	}
	public void save() {
		System.out.println("save "+(player()+1));
	}
	public void exit() {
		if (host) {
			//post save request
			//line.distribute()
			//receive save profile
			//write save profile
		} else {
			//Post exit request
		}
		try {
			InternetBuffer ibuffer = (InternetBuffer) buffer;
			ibuffer.sendObject(Exit.EXIT, CBuffer.ECHO);
			ibuffer.signalEnd();
			System.out.print("Hello There");
			setVisible(false);
		} catch (ClassCastException e) {
			System.exit(0);
		}
		//exit
	}
	public void win() {
		System.out.println("win "+(player()+1));
	}
	public void loss() {
		System.out.println("loss "+(player()+1));
	}
	public void blocked() {
		System.out.println("blocked "+(player()+1));
	}
}
