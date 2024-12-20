package ice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.zeroc.Ice.Current;

import VotingSystem.ClientPrx;
import VotingSystem.DatabaseServicePrx;
import VotingSystem.QueryResult;
import operation.interfaces.QueryProcessor;

/**
 * Unit tests for the QueryServiceI class.
 */
public class QueryServiceITest {

    private QueryServiceI queryService;
    private QueryProcessor queryProcessor;
    private DatabaseServicePrx databaseServicePrx;
    private ClientPrx clientPrx;

    /**
     * Sets up the test environment before each test.
     * Mocks the QueryProcessor, DatabaseServicePrx, and ClientPrx.
     */
    @BeforeEach
    public void setUp() {
        queryProcessor = mock(QueryProcessor.class);
        databaseServicePrx = mock(DatabaseServicePrx.class);
        clientPrx = mock(ClientPrx.class);
        queryService = new QueryServiceI(queryProcessor, databaseServicePrx);
    }

    /**
     * Tests the queryPollingStation method with a null callback.
     * Verifies that no interactions occur with the query processor or database service.
     */
    @Test
    public void testQueryPollingStationWithNullCallback() {
        String citizenId = "12345";
        long queryTime = System.currentTimeMillis();

        queryService.queryPollingStation(null, citizenId, queryTime, mock(Current.class));

        verifyNoInteractions(queryProcessor);
        verifyNoInteractions(databaseServicePrx);
    }
}