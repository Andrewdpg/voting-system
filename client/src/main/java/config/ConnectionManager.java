package config;

import VotingSystem.ClientInfo;
import VotingSystem.ClientPrx;
import VotingSystem.QueryException;
import VotingSystem.QueryServicePrx;
import VotingSystem.RegistrationServicePrx;
import com.zeroc.Ice.Communicator;

public class ConnectionManager {

    private final RegistrationServicePrx registrationServer;
    private final QueryServicePrx queryServer;

    public ConnectionManager(Communicator communicator) {
        this.registrationServer = RegistrationServicePrx.checkedCast(
            communicator.propertyToProxy("RegistrationService.Proxy"));
        this.queryServer = QueryServicePrx.checkedCast(
            communicator.propertyToProxy("QueryService.Proxy"));

        if (registrationServer == null || queryServer == null) {
            throw new Error("Invalid proxy");
        }
    }

    public void registerClient(ClientPrx callback) {
        registrationServer.registerClient(callback);
    }

    public void queryPollingStation(ClientInfo info, int stationId) {
        try {
            long time = System.currentTimeMillis();
            queryServer.queryPollingStation(info, stationId, time);
        } catch (QueryException e) {
            System.out.println("Query failed: " + e.reason);
        }
    }
}