
import java.util.Arrays;

//helper class to keep info for each node
public class NodeStruct {

        public NodeID id;
        public String machine;
        public int port;
        public NodeID neighbors[];

        public NodeStruct() {
        }

        public NodeStruct(NodeID id, String machine, int port){
            this.id = id;
            this.machine = machine;
            this.port = port;
        }
            
        @Override
        public String toString() {
            return "Node " + id.getID() + " on machine " + machine + " on port " + port;
        }

}