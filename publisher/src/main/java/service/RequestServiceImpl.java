package service;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import com.zeroc.Ice.Communicator;
import com.zeroc.IceGrid.QueryPrx;

import VotingSystem.ClientPrx;
import VotingSystem.QueryServicePrx;
import service.interfaces.RequestService;

public class RequestServiceImpl implements RequestService {
    
    private final ClientPrx callback; 
    private final QueryPrx query;

    public RequestServiceImpl(ClientPrx callback, Communicator communicator) {
        this.callback = callback;
        query = QueryPrx.checkedCast(communicator.stringToProxy("IceGrid/Query"));
            if (query == null) {
                throw new Error("Invalid proxy");
            }
    }

    @Override
    public void sendBatch(String[] batch) {
        int numberOfThreads = Math.min(Runtime.getRuntime().availableProcessors() * 8, batch.length);
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(numberOfThreads);

        int totalRequests = batch.length;
        final int batchSize = Math.round((float) totalRequests / numberOfThreads) + 1;

        System.out.println("Submitting queries locally");
        
        for (int i = 0; i < numberOfThreads; i++) {
            QueryServicePrx service = getQueryServer();
            if (service == null) {
                System.out.println("Service not available");
                return;
            }
            int finalI = i;
            executor.submit(() -> {
                for (int j = 0; j < batchSize && j < batch.length; j++) {
                    if (finalI * batchSize + j >= totalRequests) {
                        break;
                    }
                    service.queryPollingStationAsync(callback, batch[finalI * batchSize + j], System.currentTimeMillis());
                }
            });
        }

        executor.shutdown();
        while (!executor.isTerminated()) {
            Thread.onSpinWait();
        }

        System.out.println("All local queries completed");
    }

    @Override
    public boolean canHandle(int amount) {
        return amount <= 150000;
    }

    @Override
    public void sendSingleQuery(String citizenId) {
        QueryServicePrx service = getQueryServer();
        if (service == null) {
            System.out.println("Service not available");
            return;
        }
        service.queryPollingStation(callback, citizenId, System.currentTimeMillis());
    }

    public QueryServicePrx getQueryServer () {
        return QueryServicePrx.checkedCast(query.findObjectByType("::VotingSystem::QueryService"));
    }

}
