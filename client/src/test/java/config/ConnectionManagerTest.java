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

public class ConnectionManagerTest {

    private ConnectionManager connectionManager;
    private QueryPrx queryPrxMock;
    private Communicator communicatorMock;
    private RegistrationServicePrx registrationServiceMock;

    @BeforeEach
    public void setUp() {
        queryPrxMock = Mockito.mock(QueryPrx.class);
        communicatorMock = Mockito.mock(Communicator.class);
        registrationServiceMock = Mockito.mock(RegistrationServicePrx.class);

        when(communicatorMock.stringToProxy("RegistrationService@PublisherAdapter"))
                .thenReturn(registrationServiceMock);

        connectionManager = new ConnectionManager(queryPrxMock, communicatorMock);
    }

    @Test
    public void testRegisterClient() {
        SubscriberPrx clientProxyMock = Mockito.mock(SubscriberPrx.class);

        connectionManager.registerClient(clientProxyMock);

        verify(registrationServiceMock).register(clientProxyMock);
    }

    @Test
    public void testGetQueryServer() {
        QueryServicePrx queryServicePrxMock = Mockito.mock(QueryServicePrx.class);
        when(queryPrxMock.findObjectByType("::VotingSystem::QueryService"))
                .thenReturn(queryServicePrxMock);

        QueryServicePrx result = connectionManager.getQueryServer();

        assertEquals(queryServicePrxMock, result);
    }

    @Test
    public void testConstructorThrowsExceptionWhenRegistrationServiceIsNull() {
        when(communicatorMock.stringToProxy("RegistrationService@PublisherAdapter"))
                .thenReturn(null);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            new ConnectionManager(queryPrxMock, communicatorMock);
        });

        assertEquals("No se pudo conectar al RegistrationService", exception.getMessage());
    }

    @Test
    public void testRegisterClientThrowsExceptionWhenClientProxyIsNull() {
        Exception exception = assertThrows(NullPointerException.class, () -> {
            connectionManager.registerClient(null);
        });

        assertEquals("clientProxy is marked non-null but is null", exception.getMessage());
    }

    @Test
    public void testGetQueryServerReturnsNullWhenQueryServiceNotFound() {
        when(queryPrxMock.findObjectByType("::VotingSystem::QueryService")).thenReturn(null);

        QueryServicePrx result = connectionManager.getQueryServer();

        assertNull(result);
    }

    @Test
    public void testRegisterClientWithValidClientProxy() {
        SubscriberPrx clientProxyMock = Mockito.mock(SubscriberPrx.class);

        connectionManager.registerClient(clientProxyMock);

        verify(registrationServiceMock).register(clientProxyMock);
    }

    @Test
    public void testGetQueryServerWithValidQueryService() {
        QueryServicePrx queryServicePrxMock = Mockito.mock(QueryServicePrx.class);
        when(queryPrxMock.findObjectByType("::VotingSystem::QueryService")).thenReturn(queryServicePrxMock);

        QueryServicePrx result = connectionManager.getQueryServer();

        assertNotNull(result);
        assertEquals(queryServicePrxMock, result);
    }
}