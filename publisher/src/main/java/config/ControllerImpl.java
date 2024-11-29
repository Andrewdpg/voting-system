package config;

import VotingSystem.SubscriberPrx;
import config.interfaces.Controller;
import manager.IdManagerImpl;
import manager.interfaces.ClientManager;
import manager.interfaces.IdManager;
import service.RequestServiceImpl;
import service.interfaces.RequestService;

import java.util.List;
import java.util.Scanner;

public class ControllerImpl implements Controller {

    private static final int MAX_BATCH_SIZE = 100000;

    private final ClientManager clientManager;
    private final IdManager idManager;
    private final RequestService requestService;

    public ControllerImpl(ClientManager clientManager) {
        this.clientManager = clientManager;
        this.idManager = new IdManagerImpl();
        this.requestService = new RequestServiceImpl();
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
                clientManager.activeClients().forEach(SubscriberPrx::receiveExportSignalAsync);
                break;
            case 4:
                clientManager.activeClients().forEach(SubscriberPrx::shutdownAsync);
                break;
            case 5:
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
        System.out.println("5. Exit");
    }
}
