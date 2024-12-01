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

public class RegistrationServiceITest {

    private RegistrationServiceI registrationService;
    private ClientManager clientManagerMock;
    private SubscriberPrx subscriberPrxMock;
    private Current currentMock;

    @BeforeEach
    public void setUp() {
        clientManagerMock = Mockito.mock(ClientManager.class);
        subscriberPrxMock = Mockito.mock(SubscriberPrx.class);
        currentMock = Mockito.mock(Current.class);

        registrationService = new RegistrationServiceI(clientManagerMock);
    }

    @Test
    public void testRegister() throws InterruptedException {
        registrationService.register(subscriberPrxMock, currentMock);

        // Wait for the thread to complete
        Thread.sleep(100);

        verify(clientManagerMock).registerClient(subscriberPrxMock);
    }

    @Test
    public void testRegisterWithNullClientProxy() throws InterruptedException {
        registrationService.register(null, currentMock);

        // Wait for the thread to complete
        Thread.sleep(100);

        verify(clientManagerMock, never()).registerClient(any());
    }
}