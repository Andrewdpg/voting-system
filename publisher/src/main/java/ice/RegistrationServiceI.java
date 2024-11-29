package ice;

import VotingSystem.*;
import com.zeroc.Ice.Current;
import manager.interfaces.ClientManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegistrationServiceI implements RegistrationService {

    private static final int THREAD_POOL_SIZE = 10;
    private static final ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

    private final ClientManager clientManager;

    public RegistrationServiceI(ClientManager clientManager) {
        this.clientManager = clientManager;
    }

    @Override
    public void register(SubscriberPrx clientProxy, Current current) {
        System.out.println("Client registered: " + clientProxy.toString());
        threadPool.submit(() -> clientManager.registerClient(clientProxy));
    }

}