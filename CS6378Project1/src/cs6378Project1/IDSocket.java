/*
*
* CS6378 - Fall 2021
* Project 1
* Razvan Preotu
* Guillermo Vazquez
*
*/

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

 /*
 Class to keep track of a NodeID, its socket with the local Node and its output stream
*/
public class IDSocket {

    public NodeID id;
    public Socket socket;
    public ObjectOutputStream oos;

    /**
     * Constructor
     * @param id
     * @param socket
     */
    public IDSocket(NodeID id, Socket socket) {
        this.id = id;
        this.socket = socket;
        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(IDSocket.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Constructor
     * @param id
     */
    public IDSocket(NodeID id) {
        this.id = id;
        this.socket = null;
    }

    /**
     * set the socket and the corresponding output stream
     * @param socket
     */
    public void setSocket(Socket socket) {
        this.socket = socket;
        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(IDSocket.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
