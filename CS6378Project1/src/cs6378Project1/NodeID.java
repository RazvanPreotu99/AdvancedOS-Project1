package cs6378Project1;

import java.io.Serializable;

public class NodeID implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private int identifier;
	
	//constructor
	public NodeID(int identifier) {
		this.identifier = identifier;
	}
	
	public int getID() {
		return identifier;
	}
}