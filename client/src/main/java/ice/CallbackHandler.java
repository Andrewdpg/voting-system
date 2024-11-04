package ice;

import VotingSystem.ClientInfo;
import VotingSystem.Message;
import VotingSystem.QueryResult;
import com.zeroc.Ice.Current;
import lambda.OnExport;
import lambda.OnRegister;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
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
    private static final String FILE_PATH = "output.xlsx";

    public CallbackHandler(OnRegister onRegister, OnExport onExport) {
        this.onRegister = onRegister;
        this.onExport = onExport;
    }

    @Override
    public void receiveNotification(Message message, Current current) {
        threadPool.submit( ()->{
            if (message instanceof ClientInfo clientInfo) {
                System.out.println("Client info received: " + clientInfo);
                onRegister.onRegister(clientInfo);
                return;
            } else if (message instanceof QueryResult queryResult) {
                QueryResult result = (QueryResult) message;
                result.endTime = System.currentTimeMillis();
                messageQueue.add(result);
                return;
            }
            messageQueue.add(message);
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
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Messages");
            int rowNum = 0;

            for (Message message : messageQueue) {
                Row row = sheet.createRow(rowNum++);
                if (message instanceof QueryResult queryResult) {
                    row.createCell(0).setCellValue(queryResult.citizenId);
                    row.createCell(1).setCellValue(queryResult.pollingStation);
                    row.createCell(2).setCellValue(queryResult.isPrime);
                    row.createCell(3).setCellValue(queryResult.dbTime);
                    row.createCell(4).setCellValue(queryResult.processTime);
                    row.createCell(5).setCellValue(queryResult.queryTime);
                    row.createCell(6).setCellValue(queryResult.endTime);
                } else if (message instanceof ClientInfo clientInfo) {
                    row.createCell(0).setCellValue(clientInfo.clientId);
                    onRegister.onRegister(clientInfo);
                } else {
                    row.createCell(0).setCellValue("Unknown message type");
                }
            }

            try (FileOutputStream fileOut = new FileOutputStream(FILE_PATH)) {
                workbook.write(fileOut);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}