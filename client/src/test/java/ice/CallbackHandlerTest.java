package ice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.zeroc.Ice.Current;

import VotingSystem.Message;
import VotingSystem.QueryResult;

/**
 * Unit tests for the CallbackHandler class.
 */
public class CallbackHandlerTest {

    private CallbackHandler callbackHandler;
    private Current currentMock;

    /**
     * Sets up the test environment before each test.
     */
    @BeforeEach
    public void setUp() {
        callbackHandler = new CallbackHandler();
        currentMock = Mockito.mock(Current.class);
    }

    /**
     * Tests the receiveNotification method with a QueryResult message.
     * Verifies that the message is added to the message queue and the endTime is set correctly.
     */
    @Test
    public void testReceiveNotificationWithQueryResult() throws InterruptedException {
        QueryResult queryResult = new QueryResult();
        long beforeTime = System.currentTimeMillis();

        callbackHandler.receiveNotification(queryResult, currentMock);

        // Wait for the thread to complete
        Thread.sleep(100);

        assertFalse(SubscriberI.messageQueue.isEmpty());
        Message message = SubscriberI.messageQueue.poll();
        assertTrue(message instanceof QueryResult);
        assertEquals(queryResult, message);
        assertTrue(((QueryResult) message).endTime >= beforeTime);
    }

    /**
     * Tests the receiveNotification method with a generic Message.
     * Verifies that the message is added to the message queue.
     */
    @Test
    public void testReceiveNotificationWithMessage() throws InterruptedException {
        Message message = new Message();

        callbackHandler.receiveNotification(message, currentMock);

        // Wait for the thread to complete
        Thread.sleep(100);

        assertFalse(SubscriberI.messageQueue.isEmpty());
        Message resultMessage = SubscriberI.messageQueue.poll();
        assertEquals(message, resultMessage);
    }
}