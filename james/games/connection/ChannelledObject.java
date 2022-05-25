package james.games.connection;

import java.io.Serializable;

public class ChannelledObject implements Serializable {
	private static long serialVersionUID = 1L;
	public Object channel;
	public Object message;
	public ChannelledObject() {
		
	}	
	public ChannelledObject(Object channel, Object message) {
		this.channel=channel;
		this.message=message;
	}
}
