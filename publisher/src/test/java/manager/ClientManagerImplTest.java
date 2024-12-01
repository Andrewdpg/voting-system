package manager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.doThrow;

import VotingSystem.SubscriberPrx;

/**
 * Unit tests for the ClientManagerImpl class.
 */
class ClientManagerImplTest {

    private ClientManagerImpl clientManager;
    private SubscriberPrx subscriberPrxMock;

    /**
     * Sets up the test environment before each test.
     */
    @BeforeEach
    public void setUp() {
        clientManager = new ClientManagerImpl();
        subscriberPrxMock = Mockito.mock(SubscriberPrx.class);
    }

    /**
     * Tests the registerClient method.
     * Verifies that the client is added to the list of active clients.
     */
    @Test
    public void testRegisterClient() {
        clientManager.registerClient(subscriberPrxMock);

        List<SubscriberPrx> clients = clientManager.activeClients();
        assertEquals(1, clients.size());
        assertEquals(subscriberPrxMock, clients.get(0));
    }

    /**
     * Tests the activeClients method.
     * Verifies that the method returns the correct list of active clients and removes clients that do not respond to ping.
     */
    @Test
    public void testActiveClients() {
        clientManager.registerClient(subscriberPrxMock);

        List<SubscriberPrx> clients = clientManager.activeClients();
        assertEquals(1, clients.size());
        assertEquals(subscriberPrxMock, clients.get(0));

        // Simulate client not responding to ping
        doThrow(new RuntimeException()).when(subscriberPrxMock).ice_ping();

        clients = clientManager.activeClients();
        assertTrue(clients.isEmpty());
    }

    /**
     * Tests the activeClients method with multiple clients.
     * Verifies that the method handles multiple clients correctly and removes only the clients that do not respond to ping.
     */
    @Test
    public void testActiveClientsWithMultipleClients() {
        SubscriberPrx subscriberPrxMock2 = Mockito.mock(SubscriberPrx.class);
        clientManager.registerClient(subscriberPrxMock);
        clientManager.registerClient(subscriberPrxMock2);

        List<SubscriberPrx> clients = clientManager.activeClients();
        assertEquals(2, clients.size());
        assertTrue(clients.contains(subscriberPrxMock));
        assertTrue(clients.contains(subscriberPrxMock2));

        // Simulate one client not responding to ping
        doThrow(new RuntimeException()).when(subscriberPrxMock).ice_ping();

        clients = clientManager.activeClients();
        assertEquals(1, clients.size());
        assertEquals(subscriberPrxMock2, clients.get(0));
    }
}