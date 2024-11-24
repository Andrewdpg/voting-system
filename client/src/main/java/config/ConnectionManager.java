package config;

import VotingSystem.ClientPrx;
import VotingSystem.QueryServicePrx;
import com.zeroc.IceGrid.QueryPrx;

public class ConnectionManager {

    private final QueryServicePrx queryServer;

    public ConnectionManager(QueryPrx queryPrx) {
        this.queryServer = QueryServicePrx.checkedCast(queryPrx.findObjectByType("::VotingSystem::QueryService"));

        if (queryServer == null) {
            throw new Error("Invalid proxy");
        }
    }

    public void queryPollingStation(ClientPrx callback, int stationId) {
        long time = System.currentTimeMillis();
        queryServer.queryPollingStation(callback, stationId, time);
    }
}