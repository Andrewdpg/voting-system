package ice;

import VotingSystem.ClientPrx;
import VotingSystem.DatabaseService;
import VotingSystem.QueryResult;
import com.zeroc.Ice.Current;
import operation.interfaces.Persistence;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DatabaseServiceI implements DatabaseService {

    private static final int THREAD_POOL_SIZE = 200;
    private static final ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

    private final Persistence persistence;

    public DatabaseServiceI(Persistence persistence) {
        this.persistence = persistence;
    }

    @Override
    public void queryPollingStation(ClientPrx client, QueryResult partialResult, Current current) {
        long start = System.currentTimeMillis();
        threadPool.submit(() -> {
            if (client == null) {
                return;
            }
            partialResult.pollingStation = persistence.getPollingStation(partialResult.citizenId);
            long time = System.currentTimeMillis();
            partialResult.dbTime = time - start;
            client.receiveNotificationAsync(partialResult);
            }
        );
    }
}
