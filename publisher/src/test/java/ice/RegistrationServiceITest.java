package ice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mockito;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.zeroc.Ice.Current;

import VotingSystem.SubscriberPrx;
import manager.interfaces.ClientManager;

/**
 * Unit tests for the RegistrationServiceI class.
 */
public class RegistrationServiceITest {

    private RegistrationServiceI registrationService;
    private ClientManager clientManagerMock;
    private SubscriberPrx subscriberPrxMock;
    private Current currentMock;

    /**
     * Sets up the test environment before each test.
     */
    @BeforeEach
    public void setUp() {
        clientManagerMock = Mockito.mock(ClientManager.class);
        subscriberPrxMock = Mockito.mock(SubscriberPrx.class);
        currentMock = Mockito.mock(Current.class);

        registrationService = new RegistrationServiceI(clientManagerMock);
    }

    /**
     * Tests the register method.
     * Verifies that the registerClient method of ClientManager is called with the correct client proxy.
     */
    @Test
    public void testRegister() throws InterruptedException {
        registrationService.register(subscriberPrxMock, currentMock);

        // Wait for the thread to complete
        Thread.sleep(100);

        verify(clientManagerMock).registerClient(subscriberPrxMock);
    }

    /**
     * Tests the register method with a null client proxy.
     * Verifies that the registerClient method of ClientManager is never called.
     */
    @Test
    public void testRegisterWithNullClientProxy() throws InterruptedException {
        registrationService.register(null, currentMock);

        // Wait for the thread to complete
        Thread.sleep(100);

        verify(clientManagerMock, never()).registerClient(any());
    }
}