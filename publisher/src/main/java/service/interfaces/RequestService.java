package service.interfaces;

public interface RequestService {
    void sendBatch(String[] batch);
    boolean canHandle(int amount);
    void sendSingleQuery(String citizenId);
}
