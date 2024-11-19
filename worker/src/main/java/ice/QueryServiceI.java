package ice;

import VotingSystem.*;
import com.zeroc.Ice.Current;
import operation.interfaces.QueryProcessor;
import voter.interfaces.ClientManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QueryServiceI implements QueryService {

    private static final int THREAD_POOL_SIZE = 70;
    private static final ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

    private final QueryProcessor queryProcessor;
    private final ClientManager clientManager;
    private final DatabaseServicePrx database;

    public QueryServiceI(QueryProcessor queryProcessor, ClientManager clientManager, DatabaseServicePrx database) {
        this.queryProcessor = queryProcessor;
        this.clientManager = clientManager;
        this.database = database;
    }

    @Override
    public void queryPollingStation(ClientInfo info, int citizenId, long queryTime, Current current) {
        long start = System.currentTimeMillis();
        threadPool.submit(() ->{
            if (!clientManager.isRegistered(info)) {
                return;
            }
            QueryResult result = queryProcessor.processQuery(citizenId, start);
            result.queryTime = queryTime;
            database.queryPollingStation(clientManager.getCallback(info), result);
            }
        );

    }
}
