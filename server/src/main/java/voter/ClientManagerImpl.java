package voter;

import VotingSystem.ClientInfo;
import VotingSystem.ClientPrx;
import VotingSystem.QueryResult;
import voter.interfaces.NotificationService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ClientManagerImpl implements voter.interfaces.ClientManager {

    NotificationService notificationService = new NotificationServiceImpl();
    private static final Map<String, ClientPrx> clients = new ConcurrentHashMap<>();

    public void registerClient(ClientPrx clientProxy) {
        ClientInfo info = new ClientInfo(generateClientId());
        clients.put(info.clientId, clientProxy);
        notificationService.notifyClient(clientProxy, info);
    }

    public void unregisterClient(ClientInfo info) {
        clients.remove(info.clientId);
    }

    public void sendResult(String clientId, QueryResult result) {
        ClientPrx clientProxy = clients.get(clientId);
        if (clientProxy != null) {
            notificationService.notifyClient(clientProxy, result);
        }
    }

    @Override
    public boolean isRegistered(ClientInfo info) {
        return clients.containsKey(info.clientId);
    }

    @Override
    public void sendError(ClientInfo info, String error) {
        ClientPrx clientProxy = clients.get(info.clientId);
        if (clientProxy != null) {
            notificationService.notifyClient(clientProxy, new VotingSystem.Error(error));
        }
    }

    @Override
    public void exportAll() {
        notificationService.sendExportSignal(clients);
    }

    private static String generateClientId() {
        return "Client-" + UUID.randomUUID() + "-" + System.currentTimeMillis();
    }
}

