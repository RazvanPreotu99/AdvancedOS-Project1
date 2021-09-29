package cs6378Project1;

public class Node {
	// node identifier
	private NodeID identifier;
	
	private Listener listener;
	private NodeID[] neighbors;
	
	// constructor
	public Node( NodeID identifier, String configFile, Listener listener) {
		
	}
	
	// methods
	public NodeID[] getNeighbors() {
		return neighbors;
	}
	public void send( Message message, NodeID destination) {
		
	}
	public void sendToAll( Message message) {
		
	}
	public void tearDown() {
		
	}
}
