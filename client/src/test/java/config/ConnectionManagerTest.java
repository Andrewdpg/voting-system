package config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.zeroc.Ice.Communicator;
import com.zeroc.IceGrid.QueryPrx;

import VotingSystem.QueryServicePrx;
import VotingSystem.RegistrationServicePrx;
import VotingSystem.SubscriberPrx;

/**
 * Unit tests for the ConnectionManager class.
 */
public class ConnectionManagerTest {

    private ConnectionManager connectionManager;
    private QueryPrx queryPrxMock;
    private Communicator communicatorMock;
    private RegistrationServicePrx registrationServiceMock;

    /**
     * Sets up the test environment before each test.
     */
    @BeforeEach
    public void setUp() {
        queryPrxMock = Mockito.mock(QueryPrx.class);
        communicatorMock = Mockito.mock(Communicator.class);
        registrationServiceMock = Mockito.mock(RegistrationServicePrx.class);

        when(communicatorMock.stringToProxy("RegistrationService@PublisherAdapter"))
                .thenReturn(registrationServiceMock);

        connectionManager = new ConnectionManager(queryPrxMock, communicatorMock);
    }

    /**
     * Tests the registerClient method.
     * Verifies that the register method of RegistrationServicePrx is called with the correct client proxy.
     */
    @Test
    public void testRegisterClient() {
        SubscriberPrx clientProxyMock = Mockito.mock(SubscriberPrx.class);

        connectionManager.registerClient(clientProxyMock);

        verify(registrationServiceMock).register(clientProxyMock);
    }

    /**
     * Tests the getQueryServer method.
     * Verifies that the correct QueryServicePrx is returned.
     */
    @Test
    public void testGetQueryServer() {
        QueryServicePrx queryServicePrxMock = Mockito.mock(QueryServicePrx.class);
        when(queryPrxMock.findObjectByType("::VotingSystem::QueryService"))
                .thenReturn(queryServicePrxMock);

        QueryServicePrx result = connectionManager.getQueryServer();

        assertEquals(queryServicePrxMock, result);
    }

    /**
     * Tests the constructor of ConnectionManager.
     * Verifies that a RuntimeException is thrown when the RegistrationServicePrx is null.
     */
    @Test
    public void testConstructorThrowsExceptionWhenRegistrationServiceIsNull() {
        when(communicatorMock.stringToProxy("RegistrationService@PublisherAdapter"))
                .thenReturn(null);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            new ConnectionManager(queryPrxMock, communicatorMock);
        });

        assertEquals("No se pudo conectar al RegistrationService", exception.getMessage());
    }

    /**
     * Tests the registerClient method.
     * Verifies that a NullPointerException is thrown when the client proxy is null.
     */
    @Test
    public void testRegisterClientThrowsExceptionWhenClientProxyIsNull() {
        Exception exception = assertThrows(NullPointerException.class, () -> {
            connectionManager.registerClient(null);
        });

        assertEquals("clientProxy is marked non-null but is null", exception.getMessage());
    }

    /**
     * Tests the getQueryServer method.
     * Verifies that null is returned when the QueryService is not found.
     */
    @Test
    public void testGetQueryServerReturnsNullWhenQueryServiceNotFound() {
        when(queryPrxMock.findObjectByType("::VotingSystem::QueryService")).thenReturn(null);

        QueryServicePrx result = connectionManager.getQueryServer();

        assertNull(result);
    }

    /**
     * Tests the registerClient method with a valid client proxy.
     * Verifies that the register method of RegistrationServicePrx is called with the correct client proxy.
     */
    @Test
    public void testRegisterClientWithValidClientProxy() {
        SubscriberPrx clientProxyMock = Mockito.mock(SubscriberPrx.class);

        connectionManager.registerClient(clientProxyMock);

        verify(registrationServiceMock).register(clientProxyMock);
    }

    /**
     * Tests the getQueryServer method with a valid QueryService.
     * Verifies that the correct QueryServicePrx is returned.
     */
    @Test
    public void testGetQueryServerWithValidQueryService() {
        QueryServicePrx queryServicePrxMock = Mockito.mock(QueryServicePrx.class);
        when(queryPrxMock.findObjectByType("::VotingSystem::QueryService")).thenReturn(queryServicePrxMock);

        QueryServicePrx result = connectionManager.getQueryServer();

        assertNotNull(result);
        assertEquals(queryServicePrxMock, result);
    }
}