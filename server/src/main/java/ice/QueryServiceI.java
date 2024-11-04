package ice;

import VotingSystem.ClientInfo;
import VotingSystem.QueryResult;
import VotingSystem.QueryService;
import com.zeroc.Ice.Current;
import operation.interfaces.QueryProcessor;
import voter.interfaces.ClientManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QueryServiceI implements QueryService {

    private static final int THREAD_POOL_SIZE = 100;
    private static final ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

    private final QueryProcessor queryProcessor;
    private final ClientManager clientManager;

    public QueryServiceI(QueryProcessor queryProcessor, ClientManager clientManager) {
        this.queryProcessor = queryProcessor;
        this.clientManager = clientManager;
    }


    @Override
    public void queryPollingStation(ClientInfo info, int citizenId, long queryTime, Current current) {
        threadPool.submit(() ->{
            if (!clientManager.isRegistered(info)) {
                return;
            }
            QueryResult result = queryProcessor.processQuery(citizenId);
            result.queryTime = queryTime;
            clientManager.sendResult(info.clientId, result);
            }
        );

    }
}
