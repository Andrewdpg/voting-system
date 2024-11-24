package config;

import VotingSystem.DatabaseServicePrx;
import com.zeroc.Ice.Identity;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.Properties;
import com.zeroc.Ice.Util;
import com.zeroc.IceGrid.QueryPrx;
import config.interfaces.ServiceManager;
import ice.QueryServiceI;
import operation.QueryProcessorImpl;
import operation.interfaces.QueryProcessor;

public class ServiceManagerImpl implements ServiceManager {

    QueryProcessor queryProcessor;

    public ServiceManagerImpl() {
        queryProcessor = new QueryProcessorImpl();
    }

    @Override
    public void initializeServices(Properties properties, ObjectAdapter adapter, QueryPrx queryPrx) {
        Identity idQuery = Util.stringToIdentity(properties.getProperty("IdentityQuery"));

        DatabaseServicePrx service = DatabaseServicePrx.checkedCast(queryPrx.findObjectByType("::VotingSystem::DatabaseService"));
        if (service == null) {
            throw new Error("Invalid proxy for db");
        }

        com.zeroc.Ice.Object query = new QueryServiceI(queryProcessor, service);

        // Add the servant to the adapter
        adapter.add(query, idQuery);
    }
}
