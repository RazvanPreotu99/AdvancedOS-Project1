
import java.io.IOException;

/**
 *
 * @author gvazq
 */
public class Main {

    /**
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

       int n = Integer.parseInt(args[0]);
//       int n = 0;
       // Application app = new Application(new NodeID(n), "config.txt");
        //app.run();

        Node node = new Node( new NodeID(n), "config.txt", null);
        node.tearDown();
    }
}
