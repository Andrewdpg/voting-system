package config;

import com.zeroc.Ice.Communicator;
import com.zeroc.IceGrid.QueryPrx;

import VotingSystem.QueryServicePrx;
import VotingSystem.RegistrationServicePrx;
import VotingSystem.SubscriberPrx;

public class ConnectionManager {

    private final RegistrationServicePrx registrationService;
    private final QueryPrx queryPrx;

    public ConnectionManager(QueryPrx queryPrx, Communicator communicator) {
        this.queryPrx = queryPrx;
        this.registrationService = RegistrationServicePrx.checkedCast(communicator.stringToProxy("RegistrationService@PublisherAdapter"));

        if (registrationService == null) {
            throw new RuntimeException("No se pudo conectar al RegistrationService");
        }
    }

    public void registerClient(SubscriberPrx clientProxy) {
        if (clientProxy == null) {
            throw new NullPointerException("clientProxy is marked non-null but is null");
        }
        registrationService.register(clientProxy);
    }

    public QueryServicePrx getQueryServer() {
        return QueryServicePrx.checkedCast(queryPrx.findObjectByType("::VotingSystem::QueryService"));
    }
}