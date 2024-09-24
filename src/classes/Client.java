package classes;

import interfaces.IClient;

import java.io.*;
import java.net.Socket;

public class Client implements IClient {

    private long time;        // Heure locale du client
    private int accuracy;     // Précision de la synchronisation

    @Override
    public void requestTime(int serverPort, long currentTime, int numberOfTries) throws IOException {
        String serverAddress = "localhost"; // Vous pouvez remplacer par une autre adresse IP si nécessaire

        // Variables pour stocker les temps
        long T0, T1, T2, T3;
        long totalAdjustment = 0;

        for (int i = 0; i < numberOfTries; i++) {
            // Connexion au serveur
            try (Socket socket = new Socket(serverAddress, serverPort);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                // T0 : temps avant d'envoyer la requête
                T0 = System.currentTimeMillis();

                // Envoyer une requête au serveur
                out.println("GET time");

                // Recevoir l'heure du serveur
                T2 = Long.parseLong(in.readLine());

                // T3 : temps après avoir reçu la réponse
                T3 = System.currentTimeMillis();

                // Calcul du délai aller-retour
                long roundTripTime = T3 - T0;

                // Calcul du délai réseau unidirectionnel (on suppose que le délai aller = délai retour)
                long oneWayDelay = roundTripTime / 2;

                // Ajustement du temps
                long correctedTime = T2 + oneWayDelay;

                // Ajouter l'ajustement total
                totalAdjustment += correctedTime - currentTime;

                // Mettre à jour le temps courant
                this.time = correctedTime;

                System.out.println("Tentative " + (i + 1) + ": Temps estimé du serveur = " + correctedTime);
            }
        }

        // Calcul de l'accuracy (précision)
        this.accuracy = (int) (totalAdjustment / numberOfTries);
        System.out.println("Précision moyenne : ±" + this.accuracy + " ms");
    }

    @Override
    public long getTime() {
        return this.time;
    }

    @Override
    public void setTime(long newTime) {
        this.time = newTime;
    }

    @Override
    public int getAccuracy() {
        return this.accuracy;
    }

    public static void main(String[] args) {
        Client client = new Client();
        try {
            // Essayons de synchroniser l'heure avec 5 tentatives
            client.requestTime(25000, System.currentTimeMillis(), 5);
            System.out.println("Heure synchronisée du client : " + client.getTime());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
