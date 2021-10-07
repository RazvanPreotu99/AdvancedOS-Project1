/*
*
* CS6378 - Fall 2021
* Project 1
* Razvan Preotu
* Guillermo Vazquez
*
*/

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/*
 * Class to read messages from a specific channel and let Node know about it
 */
public class NodeThread implements Runnable {

    public static final boolean DEBUG = Node.DEBUG;
    
    private Node node;
    private ObjectInputStream inputStream;

    boolean stopThread = false;

    /**
     * Constructor
     * @param node
     * @param socket
     */
    public NodeThread(Node node, Socket socket){
        this.node = node;

        // create inputStream to read Message objects
        inputStream = new ObjectInputStream(socket.getInputStream());
    }

    @Override
    public void run() {
        // loop until tearDown is called
        while (stopThread == false) {
            try {
                // read message, will be blocked if there is no message to read
                Message message = (Message) inputStream.readObject();

                String s = new String(message.data, StandardCharsets.UTF_8);

                //check if it is a terminate message
                if (s.equals("TERMINATE")) {

                    if (DEBUG) {
                        System.out.println("Thread for Node " + Integer.toString(node.getNodeStruct().id.getID()) + " received terminate message from Node "
                            + message.source.getID());
                    }
                    // create terminate done message and send it back as a response
                    Message newMessage = new Message(node.getNodeStruct().id, "TERMINATEDONE".getBytes());
                    node.send(newMessage, message.source);

                    // close input stream
                    inputStream.close();

                    // tell Node to close socket
                    node.closeConnection(message.source);

                    // end thread
                    stopThread = true;
                } // check if it is a terminate done message
                else if (s.equals("TERMINATEDONE")) {

                    if (DEBUG) {
                        System.out.println("Thread for Node " + Integer.toString(node.getNodeStruct().id.getID()) + " received terminate done message from Node "
                            + message.source.getID());
                    }
                    // close input stream
                    inputStream.close();

                    // tell Node to close socket
                    node.closeConnection(message.source);

                    // end thread
                    stopThread = true;
                } else {
                    // call listener's receive as it is an application message
                    node.getListener().receive(message);
                }
            } catch (IOException ex) {
                Logger.getLogger(NodeThread.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(NodeThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
