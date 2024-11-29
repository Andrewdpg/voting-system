package ice;

import VotingSystem.Message;
import VotingSystem.QueryResult;
import com.zeroc.Ice.Current;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CallbackHandler implements VotingSystem.Client {

    private static final ExecutorService threadPool = Executors.newCachedThreadPool();

    @Override
    public void receiveNotification(Message message, Current current) {
        long endtime = System.currentTimeMillis();
        threadPool.submit(() -> {
            if (message instanceof QueryResult queryResult) {
                queryResult.endTime = endtime;
                SubscriberI.messageQueue.add(queryResult);
            } else {
                SubscriberI.messageQueue.add(message);
            }
        });
    }
}