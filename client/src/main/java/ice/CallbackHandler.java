package ice;

import VotingSystem.ClientInfo;
import VotingSystem.Message;
import VotingSystem.QueryResult;
import com.zeroc.Ice.Current;
import lambda.OnExport;
import lambda.OnRegister;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CallbackHandler implements VotingSystem.Client {

    private final OnRegister onRegister;
    private final OnExport onExport;

    private static final int THREAD_POOL_SIZE = 100;
    private static final ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

    private static final Queue<Message> messageQueue = new ConcurrentLinkedQueue<>();
    private static final String FILE_PATH = "out.csv";

    public CallbackHandler(OnRegister onRegister, OnExport onExport) {
        this.onRegister = onRegister;
        this.onExport = onExport;
    }

    @Override
    public void receiveNotification(Message message, Current current) {
        long endtime = System.currentTimeMillis();
        threadPool.submit(() -> {
            if (message instanceof ClientInfo clientInfo) {
                System.out.println("Client info received: " + clientInfo);
                onRegister.onRegister(clientInfo);
            } else if (message instanceof QueryResult queryResult) {
                queryResult.endTime = endtime;
                messageQueue.add(queryResult);
            } else {
                messageQueue.add(message);
            }
        });
    }

    @Override
    public void receiveExportSignal(Current current) {
        System.out.println("Export signal received");
        System.out.println("Length of message queue: " + messageQueue.size());
        exportToExcel();
        onExport.onExport(false);
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
                    } else if (message instanceof ClientInfo clientInfo) {
                        writer.append(clientInfo.clientId).append(',')
                            .append("ClientInfo").append('\n');
                        onRegister.onRegister(clientInfo);
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