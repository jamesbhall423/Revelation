package james.games.connection.action;

import james.games.connection.CAction;

public class Message_Action extends CAction {
	public static final Message_Action MESSAGE = new Message_Action();
	private String message = "No value";
	/**
	 * Method Message_Action
	 *
	 *
	 */
	private Message_Action() {
		super(0,0,"Message",true);
	}
	@Override
	public String toString() {
		String out = super.toString();
		out+=message+LN;
		return out;
	}
	public void setMessage(String message) {
		this.message=message;
	}
}
