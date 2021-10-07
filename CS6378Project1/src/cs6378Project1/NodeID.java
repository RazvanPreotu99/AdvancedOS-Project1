/*
*
* CS6378 - Fall 2021
* Project 1
* Razvan Preotu
* Guillermo Vazquez
*
 */

public class NodeID implements java.io.Serializable {
    
    //ID of the node
    private int identifier;

    //Constructor
    public NodeID(int id) {
        identifier = id;
    }

    //Getter function for ID
    public int getID() {
        return identifier;
    }

    @Override
    public String toString() {
        return "" + identifier;
    }
}
