
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

//Object to reprsents a node in the distributed system
class Node implements Runnable {

    // node identifier
    private NodeID identifier;
    private Listener listener;

    private NodeStruct myInfo;
    private NodeStruct allNodes[];

    private ServerSocket ss;

    private Pair conns[];
    private int NconnsLeft = -1;

    // constructor
    public Node(NodeID identifier, String configFile, Listener listener) {
        this.identifier = identifier;
        this.listener = listener;

        myInfo = new NodeStruct(identifier, "", -1);
        createFromFile(configFile);

        try {
            ss = new ServerSocket(myInfo.port);
        } catch (IOException ex) {
            Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Cannot establish server socket for node: " + myInfo.id.getID());
            System.exit(3);
        }

        NconnsLeft = myInfo.neighbors.length;
        conns = new Pair[NconnsLeft];
        for (int i = 0; i < NconnsLeft; ++i) {
            conns[i] = new Pair(myInfo.neighbors[i]);
        }
        connectToNeighbors();
        System.out.println("Node " + myInfo.id.getID() + " has been set up");
    }

    @Override
    public void run() {
        while (NconnsLeft > 0) {
            Socket s = null;
            try {
                s = ss.accept();
                DataInputStream dis = new DataInputStream(s.getInputStream());
                System.out.println("New connection comes");
                NodeID clientid = new NodeID(Integer.parseInt(dis.readUTF()));
                System.out.println("Node " + myInfo.id.getID() + "receives connection from node" + clientid);
                int i = findSocketIndex(clientid);
                if (i != -1) {
                    conns[i].socket = s;
                    NconnsLeft--;
                }
                //  Thread t = new ClientHandler(s, dis, dos);
                // t.start();
            } catch (IOException e) {
            }
        }
        System.out.println("Node " + myInfo.id.getID() + " has connected to all neighbors. Stop listening for new conns");
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
        message.source = myInfo.id;
        int i = findSocketIndex(destination);
        if (i != -1) {
            try {
                new ObjectOutputStream(conns[i].socket.getOutputStream()).writeObject(message);
            } catch (IOException ex) {
                Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void sendToAll(Message message) {
        message.source = myInfo.id;
        for (Pair p : conns) {
            if (p.socket != null) {
                try {
                    new ObjectOutputStream(p.socket.getOutputStream()).writeObject(message);
                } catch (IOException ex) {
                    Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public void tearDown() {
        //Your code goes here
    }

    private void connectToNeighbors() {
        for (int i = 0; i < myInfo.neighbors.length; ++i) {
            if (conns[i].socket == null) {
                Socket s = connectTo(myInfo.neighbors[i]);
                if (s != null) {
                    conns[i].socket = s;
                    NconnsLeft--;
                }
            }
        }
    }

    private Socket connectTo(NodeID nid) {
        NodeStruct ns = findNodeStruct(nid);

        try {
            Socket socket = new Socket(ns.machine, ns.port);
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            dos.writeUTF(myInfo.id.getID() + "");
            System.out.println("Node " + myInfo.id.getID() + " connected to " + ns);
            return socket;
        } catch (Exception ex) {
            System.out.println("Host not found " + ns);
        }
        return null;
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
    private NodeStruct findNodeStruct(NodeID id) {
        for (NodeStruct ns : allNodes) {
            if (ns.id.getID() == id.getID()) {
                return ns;
            }
        }
        return null; // we should not be here
    }

    private int findSocketIndex(NodeID id) {
        for (int i = 0; i < conns.length; ++i) {
            if (conns[i].id.getID() == id.getID()) {
                return i;
            }
        }
        return -1;
    }

    private void printNeighborInfo() {
        System.out.println("Neighbors are: ");
        for (NodeID nid : myInfo.neighbors) {
            System.out.println(findNodeStruct(nid));
        }
    }
}
