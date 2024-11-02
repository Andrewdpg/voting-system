package ice;

import VotingSystem.*;
import com.zeroc.Ice.Current;
import voter.interfaces.ClientManager;

public class RegistrationServiceI implements RegistrationService {

    private final ClientManager clientManager;

    public RegistrationServiceI(ClientManager clientManager) {
        this.clientManager = clientManager;
    }

    @Override
    public void registerClient(ClientPrx clientProxy, Current current) {
        clientManager.registerClient(clientProxy);
    }

    @Override
    public void unregisterClient(ClientInfo info, Current current) {
        clientManager.unregisterClient(info);
    }
}
