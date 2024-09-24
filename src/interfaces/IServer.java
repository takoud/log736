package interfaces;

public interface IServer {
    void startServer(int port) throws java.io.IOException;

    void stopServer() throws java.io.IOException;

    long getTime();
}
