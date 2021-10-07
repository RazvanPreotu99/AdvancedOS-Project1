/*
*
* CS6378 - Fall 2021
* Project 1
* Razvan Preotu
* Guillermo Vazquez
*
 */

 /*
 Class to keep track of the node Info
 id, machine, port and neighbors ids
 */
public class NodeStruct {

    public NodeID id;
    public String machine;
    public int port;
    public NodeID neighbors[];

    public NodeStruct() {
    }

    /**
     * Constructor
     *
     * @param id
     * @param machine
     * @param port
     */
    public NodeStruct(NodeID id, String machine, int port) {
        this.id = id;
        this.machine = machine;
        this.port = port;
    }

    @Override
    public String toString() {
        return "Node " + id.getID() + " on machine " + machine + " on port " + port;
    }

}
