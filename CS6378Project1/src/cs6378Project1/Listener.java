package cs6378Project1;

public interface Listener {
	public void receive( Message message);
	public void broken( NodeID neighbor);
}
