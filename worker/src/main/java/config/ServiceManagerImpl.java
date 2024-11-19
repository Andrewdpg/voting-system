package config;

import VotingSystem.DatabaseServicePrx;
import com.zeroc.Ice.Identity;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.Properties;
import com.zeroc.Ice.Util;
import com.zeroc.IceGrid.QueryPrx;
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
    public void initializeServices(Properties properties, ObjectAdapter adapter, QueryPrx queryPrx) {
        Identity idRegister = Util.stringToIdentity(properties.getProperty("IdentityRegister"));
        Identity idQuery = Util.stringToIdentity(properties.getProperty("IdentityQuery"));

        DatabaseServicePrx service = DatabaseServicePrx.checkedCast(queryPrx.findObjectByType("::VotingSystem::DatabaseService"));

        // Create the servant
        com.zeroc.Ice.Object registration = new RegistrationServiceI(clientManager);
        com.zeroc.Ice.Object query = new QueryServiceI(queryProcessor, clientManager, service);

        // Add the servant to the adapter
        adapter.add(registration, idRegister);
        adapter.add(query, idQuery);
    }
}
