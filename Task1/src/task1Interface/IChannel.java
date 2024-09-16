package task1Interface;

public interface IChannel {
int read(byte[] bytes, int offset, int length);
    int write(byte[] bytes, int offset, int length);
    void disconnect();
    boolean disconnected();
}
