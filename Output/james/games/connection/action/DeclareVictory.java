package james.games.connection.action;

import james.games.connection.CAction;

public class DeclareVictory extends CAction {
	public static final DeclareVictory DECLARE = new DeclareVictory();	
	/**
	 * Method DeclareVictory
	 *
	 *
	 */
	private DeclareVictory() {
		super(0,0,"declare win",true);
	}	
}
