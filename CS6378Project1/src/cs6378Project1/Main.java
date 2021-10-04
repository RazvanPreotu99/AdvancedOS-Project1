
import java.io.IOException;

public class Main {
    public static final int n = 0;
    
    public static void main(String[] args) throws IOException {
        Application app = new Application(new NodeID(n), "config.txt");
        app.run();
    }
}
