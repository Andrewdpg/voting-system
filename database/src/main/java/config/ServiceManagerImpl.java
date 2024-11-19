package config;

import com.zeroc.Ice.Identity;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.Properties;
import com.zeroc.Ice.Util;
import config.interfaces.ServiceManager;
import ice.DatabaseServiceI;
import operation.PersistenceImpl;
import operation.interfaces.Persistence;

public class ServiceManagerImpl implements ServiceManager {

    Persistence persistence;

    public ServiceManagerImpl() {
        persistence = new PersistenceImpl();
    }

    @Override
    public void initializeServices(Properties properties, ObjectAdapter adapter) {
        Identity idQuery = Util.stringToIdentity(properties.getProperty("IdentityDatabase"));

        // Create the servant
        com.zeroc.Ice.Object query = new DatabaseServiceI(persistence);

        // Add the servant to the adapter
        adapter.add(query, idQuery);
    }
}
