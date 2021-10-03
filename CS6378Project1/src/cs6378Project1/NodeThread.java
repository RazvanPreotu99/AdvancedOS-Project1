package cs6378Project1;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class NodeThread implements Runnable{
	
	private Node node;
	private Socket socket;
	private ObjectInputStream inputStream;
	
	boolean stopThread = false;
	
	// constructor
	public NodeThread(Node node, Socket socket) {
		this.node = node;
		this.socket = socket;
		
		// create inputStream to read Message objects
		try {
            inputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException ex) {
            Logger.getLogger(NodeThread.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(3);
        } 
	}	
	
	public void run() {
		// loop until tearDown is called
		while(stopThread == false) {
			try {
				// read message, will be blocked if there is no message to read
                Message message = (Message) inputStream.readObject();
                
                //check if it is a tearDown message
                String s = new String(message.data, StandardCharsets.UTF_8);
                
                if(s.equals("TERMINATE")) {
                	// create tearDown message and send it back as a response
                	Message newMessage = new Message(node.getNodeStruct().id, "TERMINATE".getBytes());
                	node.send(newMessage, message.source);
                	
                	// close stream and socket
                	inputStream.close();
                	socket.close();
                	
                	// end thread
                	stopThread = true;
                }
                else {
                	// call listener's receive as it is an application message
        			node.getListener().receive(message);
                }
            } catch (IOException ex) {
                Logger.getLogger(NodeThread.class.getName()).log(Level.SEVERE, null, ex);
            } catch(ClassNotFoundException ex) {
                Logger.getLogger(NodeThread.class.getName()).log(Level.SEVERE, null, ex);
            }
		}
	}
}
