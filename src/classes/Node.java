package classes;

import interfaces.INode;

import java.io.*;
import java.net.*;

public class Node implements INode {
    private long time;

    @Override
    public long[] requestTime(int[] ports, long seuil) throws IOException {
        long[] times = new long[ports.length];  // Tableau pour stocker les heures des autres nœuds

        for (int i = 0; i < ports.length; i++) {
            try (Socket socket = new Socket(InetAddress.getLocalHost(), ports[i]);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                // Envoyer une requête pour obtenir le temps
                out.println("GET time");

                // Lire l'heure reçue
                times[i] = Long.parseLong(in.readLine());
            } catch (IOException e) {
                System.out.println("Erreur lors de la connexion au nœud sur le port " + ports[i] + ": " + e.getMessage());
                times[i] = -1;  // Marquer une erreur si la connexion échoue
            }
        }
        return times;
    }

    @Override
    public Socket startNoeud(int port) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Nœud démarré et écoute sur le port " + port);

        while (true) {
            try (Socket clientSocket = serverSocket.accept();
                 BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                String request = in.readLine();
                if ("GET time".equals(request)) {
                    out.println(getTime());
                    System.out.println("Heure locale envoyée au leader: " + getTime());
                } else if (request.startsWith("SET offset")) {
                    long offset = Long.parseLong(request.split(" ")[2]);
                    setTime(offset);
                    System.out.println("Heure locale ajustée avec un décalage de " + offset + " ms.");
                }
            } catch (IOException e) {
                System.out.println("Erreur de communication avec le leader: " + e.getMessage());
            }
        }
    }

    @Override
    public long getTime() {
        return System.currentTimeMillis();  // Retourne l'heure actuelle en millisecondes
    }

    @Override
    public void setTime(long offset) {
        time += offset;  // Ajuster l'heure locale avec le décalage reçu
    }

    public void synchronizeNodes(int[] ports) throws IOException {
        long[] times = requestTime(ports, 1000);  // Récupérer les heures des autres nœuds
        long sum = 0;
        int validResponses = 0;

        for (long time : times) {
            if (time != -1) {
                sum += time;
                validResponses++;
            }
        }

        long leaderTime = getTime();
        sum += leaderTime;
        validResponses++;

        long averageTime = sum / validResponses;
        System.out.println("Temps moyen calculé: " + averageTime);

        for (int i = 0; i < ports.length; i++) {
            if (times[i] != -1) {
                long offset = averageTime - times[i];
                sendOffsetToNode(ports[i], offset);
            }
        }

        long leaderOffset = averageTime - leaderTime;
        setTime(leaderOffset);
        System.out.println("Décalage appliqué au leader: " + leaderOffset + " ms");
    }

    private void sendOffsetToNode(int port, long offset) throws IOException {
        try (Socket socket = new Socket(InetAddress.getLocalHost(), port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            out.println("SET offset " + offset);
        }
    }
}
