package voter.interfaces;

import VotingSystem.ClientPrx;
import VotingSystem.Message;
import java.util.Map;

public interface NotificationService {
    void notifyClient(ClientPrx client, Message message);
    void notifyAllClients(Map<String, ClientPrx> clients, Message message);
    void sendExportSignal(Map<String, ClientPrx> clients);
}
