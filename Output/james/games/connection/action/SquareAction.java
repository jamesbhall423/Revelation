package james.games.connection.action;

import james.games.connection.CAction;
import james.games.connection.Square;
public class SquareAction extends CAction {
	public final int x;
	public final int y;
	public final boolean revert;
	public final boolean side;
	/**
	 * Method SquareAction
	 *
	 *
	 * @param player
	 * @param time
	 * @param x
	 * @param y
	 *
	 */
	public SquareAction(int player, int x, int y, boolean revert,boolean side,int startTime) {
		super (player,1, revert ? "revert" : "place",false);
		this.x=x;
		this.y=y;
		this.revert=revert;
		this.side=side;
		setStartTime(startTime);
	}
	@Override
	public String toString() {
		String out = LN+getName()+"- player: "+(player()+1)+" time: "+getStartTime()+" at "+Square.coordinates(x,y);
		if (getResponse()!=null) out+=LN+"result: "+getResponse();
		out+=LN;
		return out;
	}
	/*@Override
	public int delay() {
		return 0;
	}*/
}
