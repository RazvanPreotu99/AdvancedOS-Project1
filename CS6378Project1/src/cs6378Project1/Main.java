
public class Main {

    public static void main(String[] args) {
        int nodes = 5;
        for(int i = 0; i < nodes; ++i){
            new Helper(i).start();
        }
       
    }

    public static class Helper extends Thread {

        private int id;

        public Helper(int id) {
            this.id = id;
        }

        @Override
        public void start() {
            System.out.println("Starting node: " + id);
            Node myNode = new Node(new NodeID(id), "config.txt", null);
            new Thread(myNode).start();
        }

    }

}

