package ice;

import VotingSystem.*;
import com.zeroc.Ice.Current;
import operation.interfaces.QueryProcessor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QueryServiceI implements QueryService {

    private static final int THREAD_POOL_SIZE = 200;
    private static final ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

    private final QueryProcessor queryProcessor;
    private final DatabaseServicePrx database;

    public QueryServiceI(QueryProcessor queryProcessor, DatabaseServicePrx database) {
        this.queryProcessor = queryProcessor;
        this.database = database;
    }

    @Override
    public void queryPollingStation(ClientPrx callback, String citizenId, long queryTime, Current current) {
        long start = System.currentTimeMillis();
        threadPool.submit(() ->{
            if (callback == null) {
                return;
            }
            QueryResult result = queryProcessor.processQuery(citizenId, start);
            result.queryTime = queryTime;
            database.queryPollingStationAsync(callback, result);
            }
        );
    }
}
