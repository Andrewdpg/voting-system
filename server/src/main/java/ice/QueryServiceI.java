package ice;

import VotingSystem.*;
import operation.interfaces.QueryProcessor;
import com.zeroc.Ice.Current;
import voter.interfaces.ClientManager;

public class QueryServiceI implements QueryService {

    private final QueryProcessor queryProcessor;
    private final ClientManager clientManager;

    public QueryServiceI(QueryProcessor queryProcessor, ClientManager clientManager) {
        this.queryProcessor = queryProcessor;
        this.clientManager = clientManager;
    }


    @Override
    public void queryPollingStation(ClientInfo info, int citizenId, Current current) throws QueryException {
        if(!clientManager.isRegistered(info)) {
            throw new VotingSystem.QueryException("Client not registered");
        }
        QueryResult result = queryProcessor.processQuery(citizenId);
        clientManager.sendResult(info.clientId, result);
    }
}
