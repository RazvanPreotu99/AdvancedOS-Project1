
public class Main {

    public static void main(String[] args) {
        int N = 5;

        Node[] nodes = new Node[N];
        for (int i = 0; i < N; ++i) {
            System.out.println("Setting up node " + i);
            nodes[i] = new Node(new NodeID(i), "config.txt", null);
            new Thread(nodes[i]).start();
        }

        nodes[0].tearDown();
        nodes[1].tearDown();
    }
}
