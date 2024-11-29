package manager.interfaces;

public interface IdManager {
    void readIdFile(String filename);
    int getFileSize();
    String[] getBatch();
    void divideInto(int number);

}
