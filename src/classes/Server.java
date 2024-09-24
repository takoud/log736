package classes;

import interfaces.IServer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements IServer {
    private ServerSocket serverSocket;
    private boolean isRunning;

    @Override
    public void startServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        isRunning = true;
        System.out.println("Serveur de temps démarré sur le port " + port);

        // Boucle pour écouter et répondre aux clients
        while (isRunning) {
            try (Socket clientSocket = serverSocket.accept();
                 BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                // Lire la requête du client
                String clientRequest = in.readLine();
                if ("GET time".equals(clientRequest)) {
                    // Envoyer l'heure actuelle du serveur
                    long serverTime = getTime();
                    out.println(serverTime);
                    System.out.println("Heure du serveur envoyée : " + serverTime + " ms");
                }
            } catch (IOException e) {
                System.out.println("Erreur lors de la connexion au client : " + e.getMessage());
            }
        }
    }
    @Override
    public void stopServer() throws IOException {
        isRunning = false;
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
            System.out.println("Serveur arrêté.");
        }
    }
    @Override
    public long getTime() {
        return System.currentTimeMillis();
    }

    public static void main(String[] args) {
        Server server = new Server();
        try {
            server.startServer(25000); // Démarrer le serveur sur le port 25000
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
