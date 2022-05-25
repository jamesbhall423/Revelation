package james.games.connection.action;

import james.games.connection.CAction;

public class Blocked extends CAction {
	public static final Blocked BLOCKED = new Blocked();	
	/**
	 * Method Blocked
	 *
	 *
	 */
	private Blocked() {
		super(0,0,"Blocked",false);
	}	
}
