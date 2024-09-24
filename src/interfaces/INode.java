package interfaces;

import java.io.IOException;
import java.net.Socket;

public interface INode {

    /**
     * Demande l'heure aux autres nœuds dans le réseau et retourne un tableau avec les heures collectées.
     * @param ports Les ports des autres nœuds à contacter.
     * @param seuil Un seuil de temps (ex. timeout) pour la collecte des heures.
     * @return Un tableau de long représentant les heures collectées auprès des nœuds.
     * @throws IOException Si une erreur de connexion survient lors de la collecte des heures.
     */
    long[] requestTime(int[] ports, long seuil) throws IOException;

    /**
     * Démarre un nœud et écoute sur le port spécifié.
     * @param port Le port sur lequel le nœud doit écouter les requêtes.
     * @return Un objet Socket pour la communication.
     * @throws IOException Si une erreur survient lors du démarrage du nœud.
     */
    Socket startNoeud(int port) throws IOException;

    /**
     * Retourne l'heure locale actuelle du nœud.
     * @return L'heure locale actuelle sous forme de long (millisecondes depuis l'époque UNIX).
     */
    long getTime();

    /**
     * Ajuste l'heure locale du nœud avec un décalage donné.
     * @param offset Le décalage en millisecondes à ajouter ou à soustraire à l'heure locale.
     */
    void setTime(long offset);
}
