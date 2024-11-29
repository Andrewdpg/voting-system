package manager;

import manager.interfaces.IdManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class IdManagerImpl implements IdManager {

    List<String> ids;
    int batchSize = -1;
    int currentBatch = 0;

    public IdManagerImpl() {
        this.ids = new ArrayList<>();
    }

    @Override
    public void readIdFile(String fileName) {
        ids.clear();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            reader.lines().forEach(ids::add);
            reader.close();
            System.out.println("Size: " + ids.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getFileSize() {
        return this.ids.size();
    }

    @Override
    public String[] getBatch() {
        if(batchSize == -1){
            System.out.println("List not divided");
        }

        if(this.ids.size() <= batchSize * currentBatch) {
            currentBatch = 0;
            return null;
        }

        int start = currentBatch * batchSize;
        int end = Math.min(start + batchSize, ids.size());
        List<String> batch = ids.subList(start, end);
        currentBatch++;

        return batch.toArray(new String[0]);
    }

    @Override
    public void divideInto(int number) {
        this.batchSize = (this.ids.size() / number) + 1;
        this.currentBatch = 0;
    }
}
