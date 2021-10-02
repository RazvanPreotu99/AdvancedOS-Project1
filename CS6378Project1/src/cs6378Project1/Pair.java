
import java.net.Socket;

public class Pair {
 
    public NodeID id;
    public Socket socket;

    public Pair(NodeID id, Socket socket) {
        this.id = id;
        this.socket = socket;
    }

    public Pair(NodeID id) {
        this.id = id;
        this.socket = null;
    }
    
    
}
