
public class Main {
    public static void main(String[] args) {
        Node myNode = new Node( new NodeID(0), "config.txt", null);
        System.out.println(myNode);
        myNode.printNeighborInfo();
    }
}
