import java.io.IOException;

/**
 *
 * @author gvazq
 */
public class Main {

    /**
     *
     * @param args
     * 
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

    	// first argument is the node identifier number
    	int n = Integer.parseInt(args[0]);
    	
    	if(n < 0) {
    		System.out.println("Invalid first argument: Node identifier must be greater or equal to 0");
    	}
    	
    	// second argument is the config file path
    	String config = args[1];
    	
    	// start application
        Application app = new Application(new NodeID(n), config);
        app.run();

        //Node node = new Node( new NodeID(n), "config.txt", null);
        //node.tearDown();
    }
}
