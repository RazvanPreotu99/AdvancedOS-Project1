/*
*
* CS6378 - Fall 2021
* Project 1
* Razvan Preotu
* Guillermo Vazquez
*
*/

/*
 * Class that denotes the unique identifier associated with a node
 * identifier is a non-negative number
 */
public class NodeID implements java.io.Serializable {
    
    //ID of the node
    private int identifier;

    /**
     * Constructor
     * @param id
     */
    public NodeID(int id) {
        identifier = id;
    }

    public int getID() {
        return identifier;
    }

    @Override
    public String toString() {
        return "" + identifier;
    }
}
