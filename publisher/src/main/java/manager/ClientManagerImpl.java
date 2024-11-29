package manager;

import VotingSystem.SubscriberPrx;
import manager.interfaces.ClientManager;

import java.util.ArrayList;
import java.util.List;

public class ClientManagerImpl implements ClientManager {

    List<SubscriberPrx> clients;

    public ClientManagerImpl() {
        this.clients = new ArrayList<>();
    }

    @Override
    public void registerClient(SubscriberPrx clientProxy) {
        clients.add(clientProxy);
    }

    @Override
    public List<SubscriberPrx> activeClients() {
        for (SubscriberPrx client : clients) {
            try {
                client.ice_ping();
            } catch (Exception e) {
                clients.remove(client);
            }
        }
        return clients;
    }
}
