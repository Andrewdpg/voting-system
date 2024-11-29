package config;

import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.Properties;
import com.zeroc.Ice.Util;
import config.interfaces.ServiceManager;
import ice.RegistrationServiceI;
import manager.interfaces.ClientManager;

public class ServiceManagerImpl implements ServiceManager {

    private final ClientManager clientManager;

    public ServiceManagerImpl(ClientManager clientManager) {
        this.clientManager = clientManager;
    }

    @Override
    public void initializeServices(Properties properties, ObjectAdapter adapter) {

        adapter.add(new RegistrationServiceI(clientManager), Util.stringToIdentity("RegistrationService"));

        adapter.activate();
    }
}
