package service.interfaces;

public interface RequestService {
    void sendRequest(String id);
    void sendBatch(String[] batch);
    boolean canHandle(int amount);
}
