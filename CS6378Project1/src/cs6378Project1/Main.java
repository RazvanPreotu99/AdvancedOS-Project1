package cs6378Project1;

public class Main {

	public static void main(String[] args) {
		
		NodeID identifier = new NodeID(64);
		String configFile = "src/cs6378Project1/config.txt";
		
		Application application = new Application(identifier, configFile);
		
		Node node = new Node(identifier, configFile, application);
		
		System.out.println("Number of nodes is: " + Integer.toString(node.getNumNodes()));
	}
}
