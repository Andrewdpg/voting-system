package manager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import VotingSystem.SubscriberPrx;
import manager.interfaces.ClientManager;

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
        Iterator<SubscriberPrx> iterator = clients.iterator();
        while (iterator.hasNext()) {
            SubscriberPrx client = iterator.next();
            try {
                client.ice_ping();
            } catch (Exception e) {
                iterator.remove();
            }
        }
        return clients;
    }
}