import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectAdapter;
import VotingSystem.ClientPrx;
import com.zeroc.IceGrid.QueryPrx;
import config.ConnectionManager;
import ice.CallbackHandler;

import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Client {

    private static volatile boolean running = true;

    public static void main(String[] args) {
        List<String> extraArgs = new java.util.ArrayList<>();

        try (Communicator communicator = com.zeroc.Ice.Util.initialize(args, "config.client", extraArgs)) {
            ObjectAdapter adapter = communicator.createObjectAdapter("CallbackAdapter");

            QueryPrx query = QueryPrx.checkedCast(communicator.stringToProxy("IceGrid/Query"));
            if (query == null) {
                throw new Error("Invalid proxy");
            }

            CallbackHandler callbackHandler = new CallbackHandler(Client::setRunning);
            com.zeroc.Ice.ObjectPrx proxy = adapter.addWithUUID(callbackHandler);
            adapter.activate();

            ClientPrx callback = ClientPrx.checkedCast(proxy);

            int numberOfThreads = Runtime.getRuntime().availableProcessors() * 8;
            ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(numberOfThreads);

            int totalRequests = 1_000_000;
            final int batchSize = totalRequests / numberOfThreads;

            new Thread(() -> {
                while (running) {
                    new Scanner(System.in).nextLine();
                    System.out.println("Exporting to Excel");
                    callbackHandler.exportToExcel();
                }
            }).start();

            System.out.println("Submitting queries");
            for (int i = 0; i < numberOfThreads; i++) {
                ConnectionManager serviceManager = new ConnectionManager(query);
                executor.submit(() -> {
                    for (int j = 0; j < batchSize; j++) {
                        final int stationId = 100_000_000 + ((int) (Math.random() * 100_000_000));
                        serviceManager.queryPollingStation(callback, stationId);
                    }
                });
            }

            System.out.println("All queries submitted");

            executor.shutdown();
            while (!executor.isTerminated()) {
                Thread.onSpinWait();
            }

            System.out.println("All queries completed");

            while (running) {
                Thread.onSpinWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setRunning(boolean running) {
        Client.running = running;
    }
}
