package manager.interfaces;

import VotingSystem.SubscriberPrx;

import java.util.List;

public interface ClientManager {
    void registerClient(SubscriberPrx clientProxy);
    List<SubscriberPrx> activeClients();
}
