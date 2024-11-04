package voter;

import VotingSystem.ClientPrx;
import VotingSystem.Message;
import java.util.Map;

public class NotificationServiceImpl implements voter.interfaces.NotificationService {

    public void notifyClient(ClientPrx client, Message message) {
        try {
            client.receiveNotification(message);
        } catch (Exception ex) {
            System.err.println("Error notifying client: " + ex.getMessage());
        }
    }

    public void notifyAllClients(Map<String, ClientPrx> clients, Message message) {
        for (Map.Entry<String, ClientPrx> entry : clients.entrySet()) {
            try {
                entry.getValue().receiveNotification(message);
            } catch (Exception ex) {
                System.err.println("Error notifying client: " + ex.getMessage());
            }
        }
    }

    @Override
    public void sendExportSignal( Map<String, ClientPrx> clients) {
        for (Map.Entry<String, ClientPrx> entry : clients.entrySet()) {
            try {
                entry.getValue().receiveExportSignal();
            } catch (Exception ex) {
                System.err.println("Error notifying client: " + ex.getMessage());
            }
        }
    }
}
