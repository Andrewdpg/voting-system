package config;

import VotingSystem.DatabaseServicePrx;
import com.zeroc.Ice.Identity;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.Properties;
import com.zeroc.Ice.Util;
import com.zeroc.IceGrid.QueryPrx;
import ice.QueryServiceI;
import operation.QueryProcessorImpl;
import operation.interfaces.QueryProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the ServiceManagerImpl class.
 */
public class ServiceManagerImplTest {

    private ServiceManagerImpl serviceManager;
    private QueryProcessor queryProcessorMock;
    private Properties propertiesMock;
    private ObjectAdapter adapterMock;
    private QueryPrx queryPrxMock;
    private DatabaseServicePrx databaseServicePrxMock;

    /**
     * Sets up the test environment before each test.
     */
    @BeforeEach
    public void setUp() {
        queryProcessorMock = Mockito.mock(QueryProcessor.class);
        propertiesMock = Mockito.mock(Properties.class);
        adapterMock = Mockito.mock(ObjectAdapter.class);
        queryPrxMock = Mockito.mock(QueryPrx.class);
        databaseServicePrxMock = Mockito.mock(DatabaseServicePrx.class);

        serviceManager = new ServiceManagerImpl();
        serviceManager.queryProcessor = queryProcessorMock;

        when(propertiesMock.getProperty("IdentityQuery")).thenReturn("QueryService");
        when(queryPrxMock.findObjectByType("::VotingSystem::DatabaseService")).thenReturn(databaseServicePrxMock);
    }

    /**
     * Tests the initializeServices method to ensure it adds the QueryServiceI to the adapter.
     */
    @Test
    public void testInitializeServices() {
        serviceManager.initializeServices(propertiesMock, adapterMock, queryPrxMock);

        verify(adapterMock).add(any(QueryServiceI.class), any(Identity.class));
    }

    /**
     * Tests the initializeServices method to ensure it throws an Error when the proxy is invalid.
     */
    @Test
    public void testInitializeServicesInvalidProxy() {
        when(queryPrxMock.findObjectByType("::VotingSystem::DatabaseService")).thenReturn(null);

        assertThrows(Error.class, () -> {
            serviceManager.initializeServices(propertiesMock, adapterMock, queryPrxMock);
        });
    }
}