
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

//Object to reprsents a node in the distributed system
class Node {

    // node identifier
    private NodeID identifier;
    private Listener listener;

    private NodeStruct myInfo;
    private NodeStruct allNodes[];
    // constructor

    public Node(NodeID identifier, String configFile, Listener listener) {
        this.identifier = identifier;
        this.listener = listener;

        myInfo = new NodeStruct(identifier, "", -1);
        createFromFile(configFile);

        //System.out.println("Finished parsing info");
        
        try {
            ServerSocket ss = new ServerSocket(myInfo.port);
        } catch (IOException ex) {
            Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
        }

        
        // TO DO
        // start listening 
        
        
        // TO DO
        // how to establish connection to neighbor?
        // neighbor might not be up, so either
        // 1) keep trying until it finds it is up
        // 2) only try when there is a message for that neighbor, give error if neighbor is down
        // 3) ???
    }

    @Override
    public String toString() {
        return myInfo.toString();
    }

    // methods
    public NodeID[] getNeighbors() {
        return myInfo.neighbors;
    }

    public void send(Message message, NodeID destination) {
        //Your code goes here
    }

    public void sendToAll(Message message) {
        //Your code goes here
    }

    public void tearDown() {
        //Your code goes here
    }

    /*
        methods to check the configfile
     */
    private void createFromFile(String configFile) {

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(new File(configFile)));
            String line;
            int n = -1;

            // find the first line for the number of nodes
            while (n == -1 && (line = br.readLine().trim()) != null) {
                if (isValidLine(line)) {
                    n = Integer.parseInt(trimComments(line));
                }
            }

            //System.out.println("There are " + nodes + " nodes");
            //getting ids and other info
            allNodes = new NodeStruct[n];
            int c = 0, ownInfoIndex = -1;
            while (c < n && (line = br.readLine().trim()) != null) {
                if (isValidLine(line)) {
                    String[] info = trimComments(line).split("\\s+");
                    allNodes[c] = new NodeStruct();
                    allNodes[c].id = new NodeID(Integer.parseInt(info[0]));
                    if (allNodes[c].id.getID() == identifier.getID()) {
                        ownInfoIndex = c;
                        myInfo.machine = info[1];
                        myInfo.port = Integer.parseInt(info[2]);

                    }
                    allNodes[c].machine = info[1];
                    allNodes[c].port = Integer.parseInt(info[2]);
                    ++c;
                }
            }

            // get neighbors 
            c = 0;
            while (c < n && (line = br.readLine().trim()) != null) {
                if (isValidLine(line)) {
                    if (c == ownInfoIndex) {
                        String temp[] = trimComments(line).split("\\s+");
                        myInfo.neighbors = new NodeID[temp.length];
                        for (int i = 0; i < temp.length; ++i) {
                            myInfo.neighbors[i] = new NodeID(Integer.parseInt(temp[i]));
                        }
                    }
                    ++c;
                }
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private boolean isValidLine(String line) {
        int n = line.charAt(0) - '0';
        return n >= 0 && n <= 9;
    }

    private String trimComments(String line) {
        int i = line.indexOf('#');
        if (i == -1) {
            return line;
        }
        return line.substring(0, i);
    }

    // TO DO change to private
    // TO DO binary search or sth  faster
    public NodeStruct findNodeStruct(NodeID id){
        for( NodeStruct ns: allNodes){
            if ( ns.id.getID() == id.getID())
                return ns;
        }
        return null; // we should not be here
    }
    
    // TO DO delete this or make private, only public
    public void printNeighborInfo(){
        System.out.println("Neighbors are: ");
        for(NodeID nid: myInfo.neighbors){
            System.out.println(findNodeStruct(nid));
        }
    }
}
