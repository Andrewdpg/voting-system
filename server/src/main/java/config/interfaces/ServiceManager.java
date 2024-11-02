package config.interfaces;

import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectAdapter;

public interface ServiceManager {
    void initializeServices(Communicator communicator, ObjectAdapter adapter);
}