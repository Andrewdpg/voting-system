package config.interfaces;

import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.Properties;
import com.zeroc.IceGrid.QueryPrx;

public interface ServiceManager {
    void initializeServices(Properties properties, ObjectAdapter adapter, QueryPrx queryPrx);
}