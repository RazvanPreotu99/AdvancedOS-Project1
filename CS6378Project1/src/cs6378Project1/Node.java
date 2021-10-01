package cs6378Project1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

public class Node{
	// node identifier
	private NodeID identifier;
	
	private Listener listener;
	private int numNodes;
	private NodeID [] neighbors;
	
	// constructor
	public Node(NodeID identifier, String configFile, Listener listener){
		
		this.identifier = identifier;
		this.listener = listener;
		
		int lineCounter = 0;
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(configFile));
			String line;
			
			boolean readNumNodes = false;
				
			while((line = reader.readLine()) != null && !(readNumNodes)) {
				lineCounter ++;
				StringTokenizer tokenizer = new StringTokenizer(line, " ", false);
				int firstToken = toUnsignedInteger(tokenizer.nextToken());
				if(firstToken != -1){
					{
						if((tokenizer.countTokens() == 0) || (Character.compare('#', tokenizer.nextToken().charAt(0))) == 0){
							numNodes = firstToken;
							break;
						}
					}
				}
			}
			
			reader.close();
			
			System.out.println("Number of nodes was read on line " + Integer.toString(lineCounter));
			
			for(int i = 0; i < numNodes; i++) {
				NodeID id = new NodeID(i);
				new Thread(new NodeThread(id, configFile));
			}
			
		} catch (IOException e) {
			System.out.println(e.toString());
		}
	}
	
	// checks if input string is an unsigned integer
	public int toUnsignedInteger(String input) {
		try {
			int num = Integer.parseInt(input);
			if(num >= 0)
				return num;
			else
				return -1;
		}
		catch (Exception e) {
			return -1;
		}
	}
	
	public NodeID[] getNeighbors() {
		return neighbors;
	}
	
	public int getNumNodes(){
		return numNodes;
	}
		
	public void send( Message message, NodeID destination) {
		
	}
	public void sendToAll( Message message) {
		
	}
	public void tearDown() {
		
	}
}
