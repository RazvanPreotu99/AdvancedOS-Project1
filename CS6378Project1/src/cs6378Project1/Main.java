
public class Main {

    public static void main(String[] args) {
        int nodes = 5;
        for (int i = 0; i < nodes; ++i) {
            Node myNode = new Node(new NodeID(i), "config.txt", null);
            new Thread(myNode).start();
        }

    }
}
