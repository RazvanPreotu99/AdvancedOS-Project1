
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gvazq
 */
public class IDSocket {

    /**
     *
     */
    public NodeID id;

    /**
     *
     */
    public Socket socket;

    /**
     *
     */
    public ObjectOutputStream oos;

    /**
     *
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
     *
     * @param id
     */
    public IDSocket(NodeID id) {
        this.id = id;
        this.socket = null;
    }

    /**
     *
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
