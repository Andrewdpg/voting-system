package ice;

import java.io.File;
import java.io.IOException;

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

import com.zeroc.Ice.Current;

import VotingSystem.ClientPrx;
import VotingSystem.QueryResult;
import VotingSystem.QueryServicePrx;
import config.ConnectionManager;
import lambda.OnShutdown;

/**
 * Unit tests for the SubscriberI class.
 */
public class SubscriberITest {

    private SubscriberI subscriber;
    private ConnectionManager connectionManagerMock;
    private ClientPrx clientPrxMock;
    private OnShutdown onShutdownMock;
    private QueryServicePrx queryServicePrxMock;
    private Current currentMock;

    /**
     * Sets up the test environment before each test.
     */
    @BeforeEach
    public void setUp() {
        connectionManagerMock = Mockito.mock(ConnectionManager.class);
        clientPrxMock = Mockito.mock(ClientPrx.class);
        onShutdownMock = Mockito.mock(OnShutdown.class);
        queryServicePrxMock = Mockito.mock(QueryServicePrx.class);
        currentMock = Mockito.mock(Current.class);

        when(connectionManagerMock.getQueryServer()).thenReturn(queryServicePrxMock);

        subscriber = new SubscriberI(connectionManagerMock, clientPrxMock, onShutdownMock, -1);
    }

    /**
     * Tests the receiveBatch method when the service is not available.
     * Verifies that the queryPollingStation method is never called.
     */
    @Test
    public void testReceiveBatchServiceNotAvailable() {
        when(connectionManagerMock.getQueryServer()).thenReturn(null);
        String[] batch = {"id1", "id2", "id3"};

        subscriber.receiveBatch(batch, currentMock);

        verify(queryServicePrxMock, never()).queryPollingStation(any(), anyString(), anyLong());
    }

    /**
     * Tests the receiveExportSignal method.
     * Verifies that the exportToExcel method creates the output file.
     */
    @Test
    public void testReceiveExportSignal() throws IOException {
        QueryResult queryResult = new QueryResult();
        queryResult.citizenId = "id1";
        queryResult.queryTime = System.currentTimeMillis();
        queryResult.endTime = queryResult.queryTime + 100;
        SubscriberI.messageQueue.add(queryResult);

        subscriber.receiveExportSignal(currentMock);

        File file = new File("out.csv");
        assertTrue(file.exists());
        file.delete();
    }

    /**
     * Tests the shutdown method.
     * Verifies that the onShutdown.setRunning method is called with false.
     */
    @Test
    public void testShutdown() {
        subscriber.shutdown(currentMock);

        verify(onShutdownMock).setRunning(false);
    }

    /**
     * Tests the exportToExcel method.
     * Verifies that the exportToExcel method creates the output file.
     */
    @Test
    public void testExportToExcel() throws IOException {
        QueryResult queryResult = new QueryResult();
        queryResult.citizenId = "id1";
        queryResult.queryTime = System.currentTimeMillis();
        queryResult.endTime = queryResult.queryTime + 100;
        SubscriberI.messageQueue.add(queryResult);

        subscriber.exportToExcel();

        File file = new File("out.csv");
        assertTrue(file.exists());
        file.delete();
    }
}