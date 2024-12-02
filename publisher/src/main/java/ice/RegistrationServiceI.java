package ice;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.zeroc.Ice.Current;

import VotingSystem.RegistrationService;
import VotingSystem.SubscriberPrx;
import manager.interfaces.ClientManager;

public class RegistrationServiceI implements RegistrationService {

    private static final int THREAD_POOL_SIZE = 10;
    private static final ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

    private final ClientManager clientManager;

    public RegistrationServiceI(ClientManager clientManager) {
        this.clientManager = clientManager;
    }

    @Override
    public void register(SubscriberPrx clientProxy, Current current) {
        if (clientProxy == null) {
            System.out.println("Client proxy is null, registration aborted.");
            return;
        }
        System.out.println("Client registered: " + clientProxy);
        threadPool.submit(() -> clientManager.registerClient(clientProxy));
    }
}