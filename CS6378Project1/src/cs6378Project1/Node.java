/*
*
* CS6378 - Fall 2021
* Project 1
* Razvan Preotu
* Guillermo Vazquez
*
*/

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

//Object to represent a node in the distributed system
final class Node {

    // set DEBUG to true to show messages regarding status
    public static final boolean DEBUG = true;

    // node identifier
    private NodeID identifier;

    //Listener of the Node
    private Listener listener;

    //own info: id, machine, neighbors IDs
    private NodeStruct myInfo;

    //info for all other nodes, to be used to find neighbors
    private NodeStruct allNodes[];

    private ServerSocket ss;

    // array of triplets of NodeID, Socket, OutputStream
    // one entry per neighbor
    private final IDSocket conns[];

    // number of connections to neighbors left to establish
    private int NconnsLeft = -1;

    /**
     * Constructor
     * @param identifier
     * @param configFile
     * @param listener
     */
    public Node(NodeID identifier, String configFile, Listener listener) {
        this.identifier = identifier;
        this.listener = listener;

        myInfo = new NodeStruct(identifier, "", -1);
        createFromFile(configFile);

        try {
            ss = new ServerSocket(myInfo.port);
        } catch (IOException ex) {
            Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
            if (DEBUG) {
                System.out.println("Cannot establish server socket for node: " + myInfo.id.getID());
            }
            System.exit(3);
        }

        NconnsLeft = myInfo.neighbors.length;
        conns = new IDSocket[NconnsLeft];
        for (int i = 0; i < NconnsLeft; ++i) {
            conns[i] = new IDSocket(myInfo.neighbors[i]);
        }
        connectToNeighbors();
        if (DEBUG) {
            System.out.println("Node " + myInfo.id.getID() + " is listening for incoming clients.");
        }
        listen();
    }

    /*
    listen for connections, stops when there are as many connections as neighbors
     */
    public void listen() {
        while (NconnsLeft > 0) {
            Socket s = null;
            try {
                s = ss.accept();
                DataInputStream dis = new DataInputStream(s.getInputStream());
                if (DEBUG) {
                    System.out.println("New connection comes");
                }
                NodeID clientid = new NodeID(Integer.parseInt(dis.readUTF()));

                if (DEBUG) {
                    System.out.println("Node " + myInfo.id.getID() + " receives connection from node" + clientid);
                }
                int i = findSocketIndex(clientid);
                if (i != -1) {

                    if (conns[i].socket != null) {
                        System.out.println("Something is wrong, repeated node");
                        System.exit(4);
                    }

                    conns[i].setSocket(s);
                    NconnsLeft--;
                }
            } catch (IOException e) {
            }
        }
        if (DEBUG) {
            System.out.println("Node " + myInfo.id.getID() + " has connected to all neighbors. Stop listening for new conns");
        }
        //create NodeThreads to check for messages incoming on channels
        threads = new Thread[myInfo.neighbors.length];
        try {
            for (int i = 0; i < threads.length; ++i) {
                threads[i] = new Thread(new NodeThread(this, conns[i].socket));
                threads[i].start();
            }
        } catch (Exception e) {
            System.out.println("Something went wrong");
        }
    }

    @Override
    public String toString() {
        return myInfo.toString();
    }

    /*
     returns the NodeIDs of the neighbors with an open socket to the Node
     */
    public NodeID[] getNeighbors() {

        NodeID ids[] = new NodeID[myInfo.neighbors.length];
        int c = 0;
        for (int i = 0; i < ids.length; ++i) {
            if (conns[i].socket != null && !conns[i].socket.isClosed()) {
                ids[c] = conns[i].id;
                ++c;
            }
        }
        NodeID neigh[] = new NodeID[c];
        for (int i = 0; i < c; ++i) {
            neigh[i] = ids[i];
        }

        return neigh;
    }

    public Listener getListener() {
        return listener;
    }

    public NodeStruct getNodeStruct() {
        return myInfo;
    }
    
    /*
     * sends a message to a given neighbor
     */
    public void send(Message message, NodeID destination) {
        message.source = myInfo.id;
        int i = findSocketIndex(destination);
        if (i != -1) {
            if (conns[i].socket != null && !(conns[i].socket.isClosed())) {
                try {
                    conns[i].oos.writeObject(message);
                } catch (IOException ex) {
                    Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                if (DEBUG) {
                    System.out.println("Node " + identifier + ": connection to Node " + Integer.toString(destination.getID()) + "is already closed");
                    System.out.println("Node " + identifier + ": not sending message");
                }
            }
        } else {
            if (DEBUG) {
                System.out.println("Node " + identifier + ": connection to Node " + Integer.toString(destination.getID()) + " doesn't exist");
                System.out.println("Node " + identifier + ": unable to send message");
            }
        }
    }

    /*
     * sends a given message to all neighbors
     */
    public void sendToAll(Message message) {
        message.source = myInfo.id;
        for (IDSocket p : conns) {
            if (p.socket != null && !p.socket.isClosed()) {
                try {
                    p.oos.writeObject(message);
                } catch (IOException ex) {
                    Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    /*
     * closes connections to all neighbors
     */
    public void tearDown() {
        // send tearDown message to all neighbors
        Message message = new Message(identifier, "TERMINATE".getBytes());
        sendToAll(message);
        // notify listener about tearDown
        if (listener != null) {
            listener.broken(identifier);
        }

        if (DEBUG) {
            System.out.println("Node " + identifier + ": finished tearDown");
        }
    }

    /*
    * close connection with a specific node, after that node sends a termination message
    */
    public void closeConnection(NodeID source) {

        if (DEBUG) {
            System.out.println("Node " + identifier + " attempting to close socket with Node " + Integer.toString(source.getID()));
        }
        int i = 0;
        boolean isClosed = false;

        while (i < conns.length && isClosed == false) {
            if (source.getID() == conns[i].id.getID()) {
                try {
                    conns[i].socket.close();
                    conns[i].socket = null;
                    if (DEBUG) {
                        System.out.println("Node " + identifier + " succesfully closed socket with Node " + Integer.toString(source.getID()));
                    }
                    isClosed = true;
                } catch (IOException ex) {
                    Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            i++;
        }

        // check if socket was closed. shouldn't be here as source should be in conns array
        if (isClosed == false) {
            System.out.println("Error: Node " + identifier + " is not a neighbor so there is no connection to close");
        }

        if (DEBUG) {
            System.out.println("Node " + identifier + " has neighbors: " + Arrays.toString(getNeighbors()));
        }

    }

    /*
    try to connect to all neighbors that have not been connected to yet
     */
    private void connectToNeighbors() {
        for (int i = 0; i < myInfo.neighbors.length; ++i) {
            if (conns[i].socket == null) {
                Socket s = connectTo(myInfo.neighbors[i]);
                if (s != null) {
                    conns[i].setSocket(s);
                    NconnsLeft--;
                }
            }
        }
    }

    /*
    try to connect to a node given its NodeID
     */
    private Socket connectTo(NodeID nid) {
        NodeStruct ns = findNodeStruct(nid);

        try {
            Socket socket = new Socket(ns.machine, ns.port);
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            dos.writeUTF(myInfo.id.getID() + "");
            if (DEBUG) {
                System.out.println("Node " + myInfo.id.getID() + " connected to " + ns);
            }
            return socket;
        } catch (Exception ex) {
            if (DEBUG) {
                System.out.println("Host not found " + ns);
            }
        }
        return null;
    }

    /*
     * read from the configuration file and set the necessary variables
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
            if (DEBUG) {
                System.out.println("There are " + n + " nodes");
            }
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

    /*
     * checks if a line in the configuration file is valid
     * a valid line should start with an unsigned integer
     */
    private boolean isValidLine(String line) {

        if (line.length() == 0) {
            return false;
        }

        String firstToken = trimComments(line).split("\\s+")[0];

        return isUnsignedInteger(firstToken);
    }

    /*
     * checks if the first token of the line is an unsigned integer
     */
    private boolean isUnsignedInteger(String input) {
        try {
            int num = Integer.parseInt(input);
            return num >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /*
     * return a line with all characters after # removed
     */
    private String trimComments(String line) {
        int i = line.indexOf('#');
        if (i == -1) {
            return line;
        }
        return line.substring(0, i);
    }

    /*
    find the NodeStruct given the NodeID
     */
    private NodeStruct findNodeStruct(NodeID id) {
        for (NodeStruct ns : allNodes) {
            if (ns.id.getID() == id.getID()) {
                return ns;
            }
        }
        return null; // we should not be here
    }

    /*
    find the index of the Socket given the NodeID
     */
    private int findSocketIndex(NodeID id) {
        for (int i = 0; i < conns.length; ++i) {
            if (conns[i].id.getID() == id.getID()) {
                return i;
            }
        }
        return -1;	// no index with the given NodeID
    }
}
