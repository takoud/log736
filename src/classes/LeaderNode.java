package classes;
import java.io.IOException;
public class LeaderNode {

    public static void main(String[] args) {
        Node leader = new Node();
        int[] ports = {25101, 25102};  // Ports des nœuds suiveurs

        try {
            // Le leader synchronise tous les nœuds
            leader.synchronizeNodes(ports);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
