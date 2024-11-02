package ice;

import VotingSystem.ClientInfo;
import VotingSystem.Message;
import VotingSystem.QueryResult;
import com.zeroc.Ice.Current;
import lambda.OnRegister;

public class CallbackHandler implements VotingSystem.Client {

    private final OnRegister onRegister;

    public CallbackHandler(OnRegister onRegister) {
        this.onRegister = onRegister;
    }

    @Override
    public void receiveNotification(Message message, Current current) {
        if (message instanceof QueryResult queryResult) {
            System.out.println("Processing QueryResult: " + queryResult.citizenId);
        } else if (message instanceof ClientInfo clientInfo) {
            System.out.println("Processing ClientInfo: " + clientInfo.clientId);
            onRegister.onRegister(clientInfo);
        } else {
            System.out.println("Unknown message type");
        }
    }
}