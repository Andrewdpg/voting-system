package ice;

import VotingSystem.Message;
import VotingSystem.QueryResult;
import com.zeroc.Ice.Current;
import lambda.OnExport;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class CallbackHandler implements VotingSystem.Client {

    private final OnExport onExport;

    private static final ExecutorService threadPool = Executors.newCachedThreadPool();

    private static final Queue<Message> messageQueue = new LinkedBlockingQueue<>();
    private static final String FILE_PATH = "out.csv";

    public CallbackHandler(OnExport onExport) {
        this.onExport = onExport;
    }

    @Override
    public void receiveNotification(Message message, Current current) {
        long endtime = System.currentTimeMillis();
        threadPool.submit(() -> {
            if (message instanceof QueryResult queryResult) {
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