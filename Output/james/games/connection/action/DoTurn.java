package james.games.connection.action;

import james.games.connection.CAction;

public class DoTurn extends CAction {
	public static final DoTurn TURN = new DoTurn();	
	/**
	 * Method DoTurn
	 *
	 *
	 */
	private DoTurn() {
		super(0,1,"next turn",false);
	}	
}
