package james.games.connection.mapmaker;

import java.io.Serializable;

public class Player implements Cloneable, Serializable {
	private static final long serialVersionUID = 1L;
	public int numReverts = 5;
	public int showReverts = 1;
	public int declareVictories = 1;
	public String message="";
	public boolean side;
	@Override
	public Object clone() {
		try {
			Player out = (Player) super.clone();
			return out;
		} catch (CloneNotSupportedException e) {
			throw new Error(e);
		}
	}
	public Player(int num) {
		side=((num%2)==0);
	}
}
