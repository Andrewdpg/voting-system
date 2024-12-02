package service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.Mockito;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.zeroc.Ice.Communicator;
import com.zeroc.IceGrid.QueryPrx;

import VotingSystem.ClientPrx;
import VotingSystem.QueryServicePrx;

/**
 * Unit tests for the RequestServiceImpl class.
 */
class RequestServiceImplTest {

    private RequestServiceImpl requestService;
    private ClientPrx clientPrxMock;
    private Communicator communicatorMock;
    private QueryPrx queryPrxMock;
    private QueryServicePrx queryServicePrxMock;

    /**
     * Sets up the test environment before each test.
     */
    @BeforeEach
    public void setUp() {
        clientPrxMock = Mockito.mock(ClientPrx.class);
        communicatorMock = Mockito.mock(Communicator.class);
        queryPrxMock = Mockito.mock(QueryPrx.class);
        queryServicePrxMock = Mockito.mock(QueryServicePrx.class);

        when(communicatorMock.stringToProxy("IceGrid/Query")).thenReturn(queryPrxMock);
        when(queryPrxMock.findObjectByType("::VotingSystem::QueryService")).thenReturn(queryServicePrxMock);

        requestService = new RequestServiceImpl(clientPrxMock, communicatorMock);
    }

    /**
     * Tests the sendBatch method to ensure it handles the case when the service is not available.
     */
    @Test
    public void testSendBatchServiceNotAvailable() {
        when(queryPrxMock.findObjectByType("::VotingSystem::QueryService")).thenReturn(null);

        String[] batch = {"id1", "id2", "id3"};
        requestService.sendBatch(batch);

        verify(queryServicePrxMock, never()).queryPollingStation(any(ClientPrx.class), anyString(), anyLong());
    }

    /**
     * Tests the canHandle method to ensure it returns the correct result based on the amount.
     */
    @Test
    public void testCanHandle() {
        assertTrue(requestService.canHandle(100000));
        assertFalse(requestService.canHandle(200000));
    }

    /**
     * Tests the getQueryServer method to ensure it returns the correct QueryServicePrx.
     */
    @Test
    public void testGetQueryServer() {
        QueryServicePrx result = requestService.getQueryServer();
        assertNotNull(result);
        assertEquals(queryServicePrxMock, result);
    }
}