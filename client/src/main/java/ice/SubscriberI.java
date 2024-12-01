package ice;

import VotingSystem.ClientPrx;
import VotingSystem.Message;
import VotingSystem.QueryResult;
import VotingSystem.QueryServicePrx;
import com.zeroc.Ice.Current;
import config.ConnectionManager;
import lambda.OnShutdown;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

public class SubscriberI implements VotingSystem.Subscriber {

    private final int threadPoolSize;
    private final OnShutdown onShutdown;
    private final ConnectionManager serviceManager;
    private final ClientPrx callback;

    public static final Queue<Message> messageQueue = new LinkedBlockingQueue<>();
    private static final String FILE_PATH = "out.csv";

    public SubscriberI(ConnectionManager serviceManager, ClientPrx callback, OnShutdown onShutdown, int numberOfThreads) {
        this.serviceManager = serviceManager;
        this.callback = callback;
        this.onShutdown = onShutdown;

        this.threadPoolSize = numberOfThreads != -1 ? numberOfThreads : Runtime.getRuntime().availableProcessors() * 8;
    }

    @Override
    public void receiveBatch(String[] batch, Current current) {

        System.out.println("Batch received - Size: " + batch.length);
        int numberOfThreads = Math.min(threadPoolSize, batch.length);
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(numberOfThreads);

        int totalRequests = batch.length;
        final int batchSize = Math.round((float) totalRequests / numberOfThreads)+1;

        System.out.println("Submitting queries");
        for (int i = 0; i < numberOfThreads; i++) {
            QueryServicePrx service = serviceManager.getQueryServer();
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
                    service.queryPollingStation(callback, batch[finalI * batchSize + j], System.currentTimeMillis());
                }
            });
        }

        System.out.println("All queries submitted");

        executor.shutdown();
        while (!executor.isTerminated()) {
            Thread.onSpinWait();
        }

        System.out.println("All queries completed");
    }

    @Override
    public void receiveExportSignal(Current current) {
        System.out.println("Export signal received");
        System.out.println("Length of message queue: " + messageQueue.size());
        exportToExcel();
    }

    @Override
    public void shutdown(Current current) {
        System.out.println("Shutting down");
        onShutdown.setRunning(false);
    }

    public void exportToExcel() {
        final int BATCH_SIZE = 1000;
        int totalRequests = 0;
        long totalResponseTime = 0;
        long startTime = 0;
        long endTime = 0;
        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            writer.append("CitizenId,Test,IsPrime,DbTime,ProcessTime,QueryTime,EndTime,ResponseTime\n");
            while (!messageQueue.isEmpty()) {
                for (int i = 0; i < BATCH_SIZE && !messageQueue.isEmpty(); i++) {
                    Message message = messageQueue.poll();
                    if (message instanceof QueryResult queryResult) {
                        if(startTime == 0) {
                            startTime = queryResult.queryTime;
                        }
                        endTime = queryResult.queryTime;
                        long responseTime = queryResult.endTime - queryResult.queryTime;
                        totalRequests++;
                        totalResponseTime += responseTime;
                        writer.append(String.valueOf(queryResult.citizenId)).append(",")
                            .append(queryResult.pollingStation != null ? "true" : "false").append(",")
                            .append(String.valueOf(queryResult.isPrime)).append(",")
                            .append(String.valueOf(queryResult.dbTime)).append(",")
                            .append(String.valueOf(queryResult.processTime)).append(",")
                            .append(String.valueOf(queryResult.queryTime)).append(",")
                            .append(String.valueOf(queryResult.endTime)).append(",")
                            .append(String.valueOf(responseTime)).append('\n');
                    } else {
                        writer.append("Unknown message type").append('\n');
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            long totalTime = endTime - startTime;
            double requestsPerSecond = totalRequests / (totalTime / 1000.0);
            double averageResponseTime = totalRequests > 0 ? totalResponseTime / (double) totalRequests : 0;

            System.out.println("Exported to " + FILE_PATH);
            System.out.println("Total Requests: " + totalRequests);
            System.out.println("Total Time (ms): " + totalTime);
            System.out.println("Requests per Second: " + requestsPerSecond);
            System.out.println("Average Response Time (ms): " + averageResponseTime);
        }
    }
}
