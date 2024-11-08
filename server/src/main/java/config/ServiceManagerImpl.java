package config;

import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.Util;
import config.interfaces.ServiceManager;
import ice.QueryServiceI;
import ice.RegistrationServiceI;
import operation.QueryProcessorImpl;
import operation.interfaces.QueryProcessor;
import voter.ClientManagerImpl;
import voter.interfaces.ClientManager;

public class ServiceManagerImpl implements ServiceManager {

    QueryProcessor queryProcessor;
    ClientManager clientManager;

    public ServiceManagerImpl() {
        queryProcessor = new QueryProcessorImpl();
        clientManager = new ClientManagerImpl();
    }

    @Override
    public void initializeServices(Communicator communicator, ObjectAdapter adapter) {
        // Create the servant
        com.zeroc.Ice.Object registration = new RegistrationServiceI(clientManager);
        com.zeroc.Ice.Object query = new QueryServiceI(queryProcessor, clientManager);

        // Add the servant to the adapter
        adapter.add(registration, Util.stringToIdentity("RegistrationService"));
        adapter.add(query, Util.stringToIdentity("QueryService"));
    }

    @Override
    public void shutdownServices() {
        clientManager.exportAll();
    }
}
