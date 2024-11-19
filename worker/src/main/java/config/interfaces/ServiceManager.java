package config.interfaces;

import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.Properties;

public interface ServiceManager {
    void initializeServices(Properties properties, ObjectAdapter adapter);
}