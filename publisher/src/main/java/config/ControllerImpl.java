package config;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.LinkedBlockingQueue;

import com.zeroc.Ice.Communicator;

import VotingSystem.ClientPrx;
import VotingSystem.Message;
import VotingSystem.QueryResult;
import VotingSystem.SubscriberPrx;
import config.interfaces.Controller;
import manager.IdManagerImpl;
import manager.interfaces.ClientManager;
import manager.interfaces.IdManager;
import service.RequestServiceImpl;
import service.interfaces.RequestService;

public class ControllerImpl implements Controller {

    private static final int MAX_BATCH_SIZE = 100000;

    private final ClientManager clientManager;
    private final IdManager idManager;
    private final RequestService requestService;
    
    public static final Queue<Message> messageQueue = new LinkedBlockingQueue<>();
    private static final String FILE_PATH = "out.csv";

    public ControllerImpl(ClientManager clientManager, ClientPrx callback, Communicator communicator) {
        this.clientManager = clientManager;
        this.idManager = new IdManagerImpl();
        this.requestService = new RequestServiceImpl(callback, communicator);
    }

    @Override
    public boolean run() {
        printMenu();
        int option = new Scanner(System.in).nextInt();
        switch (option) {
            case 1:
                readFile();
                break;
            case 2:
                sendBatch();
                break;
            case 3:
                System.out.println("Exporting data");
                exportToExcel();
                clientManager.activeClients().forEach(SubscriberPrx::receiveExportSignalAsync);
                break;
            case 4:
                clientManager.activeClients().forEach(SubscriberPrx::shutdownAsync);
                break;
            case 5:
                System.out.println("Enter citizen id: ");
                String citizenId = new Scanner(System.in).nextLine();
                requestService.sendSingleQuery(citizenId);
                break;
            case 6:
                return false;
            default:
                System.out.println("Invalid option");
        }

        return true;
    }

    private void readFile() {
        String filename = inputFilename();
        idManager.readIdFile(filename);
        if (idManager.getFileSize() <= 0) {
            System.out.println("Error reading file or empty file");
        } else {
            System.out.println("File read successfully");
        }
    }

    private void sendBatch() {
        if (idManager.getFileSize() <= 0) {
            System.out.println("No file read");
            return;
        }

        if (requestService.canHandle(idManager.getFileSize())) {
            idManager.divideInto(1);
            requestService.sendBatch(idManager.getBatch());
            return;
        }

        List<SubscriberPrx> clients = clientManager.activeClients();
        idManager.divideInto(clients.size());

        for (SubscriberPrx client : clients) {
            String[] batch = idManager.getBatch();
            for (int index = 0; index < batch.length; index+=MAX_BATCH_SIZE) {
                String[] subBatch = new String[Math.min(MAX_BATCH_SIZE, batch.length - index)];
                System.arraycopy(batch, index, subBatch, 0, subBatch.length);
                client.receiveBatchAsync(subBatch);
            }
        }
        System.out.println("All batches sent");
    }

    private String inputFilename() {
        System.out.println("Enter the filename: ");
        return new Scanner(System.in).nextLine();
    }

    private void printMenu() {
        System.out.println("1. Read file");
        System.out.println("2. Send batch");
        System.out.println("3. Export data");
        System.out.println("4. Shutdown clients");
        System.out.println("5. Query a single citizen");
        System.out.println("6. Exit");
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
                            .append(queryResult.pollingStation.state + " - " + queryResult.pollingStation.city + " - " +queryResult.pollingStation.address + " - " +queryResult.pollingStation.post + " - " ).append(",")
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
