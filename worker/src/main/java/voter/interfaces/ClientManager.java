package voter.interfaces;

import VotingSystem.ClientInfo;
import VotingSystem.ClientPrx;
import VotingSystem.QueryResult;

public interface ClientManager {
    void registerClient(ClientPrx clientProxy);
    void unregisterClient(ClientInfo info);
    void sendResult(String clientId, QueryResult result);
    boolean isRegistered(ClientInfo info);
    void sendError(ClientInfo info, String error);
}