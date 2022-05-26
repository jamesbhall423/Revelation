package james.games.connection.action;

import james.games.connection.CAction;

public abstract class Request extends CAction {
	public Request(String name) {
		super(0,0,name,false);
	}
}
