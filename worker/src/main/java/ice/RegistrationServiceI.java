package ice;

import VotingSystem.ClientInfo;
import VotingSystem.ClientPrx;
import VotingSystem.RegistrationService;
import com.zeroc.Ice.Current;
import com.zeroc.Ice.Identity;
import voter.interfaces.ClientManager;

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
    public void registerClient(ClientPrx clientProxy, Current current) {
        System.out.println("Registering client");
        threadPool.submit(() -> clientManager.registerClient(clientProxy));
    }

    @Override
    public void unregisterClient(ClientInfo info, Current current) {
        threadPool.submit(() -> clientManager.unregisterClient(info));
    }
}
