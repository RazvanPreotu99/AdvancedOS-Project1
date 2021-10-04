
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {

        int n = Integer.parseInt(args[0]);

        Application app = new Application(new NodeID(n), "config.txt");
        app.run();

        //Node node = new Node( new NodeID(n), "config.txt", null);
        //node.tearDown();
    }
}
