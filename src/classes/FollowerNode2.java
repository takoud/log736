package classes;
import java.io.IOException;
public class FollowerNode2 {
    public static void main(String[] args) {
        Node follower = new Node();
        int port = 25102;  // Port de ce nœud suiveur

        try {
            follower.startNoeud(port);  // Ce nœud écoute sur le port 25101
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
