package james.games.connection.action;

import james.games.connection.CAction;

public class Exit extends CAction {
	public static final Exit EXIT = new Exit();
	private Exit() {
		super(0,0,"exit",true);
	}
}
