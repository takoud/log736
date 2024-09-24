package interfaces;

public interface IClient {
    void requestTime(int serverPort, long currentTime, int numberOfTries) throws java.io.IOException;

    long getTime();

    void setTime(long newTime);

    int getAccuracy();
}
