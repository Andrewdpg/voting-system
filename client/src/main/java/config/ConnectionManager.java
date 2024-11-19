package config;

import VotingSystem.ClientInfo;
import VotingSystem.ClientPrx;
import VotingSystem.QueryServicePrx;
import VotingSystem.RegistrationServicePrx;
import com.zeroc.Ice.Communicator;
import com.zeroc.IceGrid.QueryPrx;

public class ConnectionManager {

    private final RegistrationServicePrx registrationServer;
    private final QueryServicePrx queryServer;

    public ConnectionManager(QueryPrx queryPrx) {
        this.registrationServer = RegistrationServicePrx.checkedCast(queryPrx.findObjectByType("::VotingSystem::RegistrationService"));
        this.queryServer = QueryServicePrx.checkedCast(queryPrx.findObjectByType("::VotingSystem::QueryService"));

        if (registrationServer == null || queryServer == null) {
            throw new Error("Invalid proxy");
        }
    }

    public void registerClient(ClientPrx callback) {
        registrationServer.registerClient(callback);
    }

    public void queryPollingStation(ClientInfo info, int stationId) {
        long time = System.currentTimeMillis();
        queryServer.queryPollingStation(info, stationId, time);
    }
}