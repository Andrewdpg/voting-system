package ice;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.zeroc.Ice.Current;

import VotingSystem.Message;
import VotingSystem.QueryResult;
import config.ControllerImpl;

public class CallbackHandler implements VotingSystem.Client {

    private static final ExecutorService threadPool = Executors.newCachedThreadPool();

    @Override
    public void receiveNotification(Message message, Current current) {
        long endtime = System.currentTimeMillis();
        threadPool.submit(() -> {
            if (message instanceof QueryResult queryResult) {
                queryResult.endTime = endtime;
                ControllerImpl.messageQueue.add(queryResult);
            } else {
                ControllerImpl.messageQueue.add(message);
            }
        });
    }
}