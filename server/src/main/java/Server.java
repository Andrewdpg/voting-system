import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.Util;
import com.zeroc.Ice.ObjectAdapter;
import config.DatabaseConfig;
import config.ServiceManagerImpl;
import config.interfaces.ServiceManager;

import java.util.Scanner;

public class Server {
    public static void main(String[] args) {
        java.util.List<String> extraArgs = new java.util.ArrayList<>();

        try (Communicator communicator = Util.initialize(args, "config.server", extraArgs)) {
            if (!extraArgs.isEmpty()) {
                System.err.println("too many arguments");
                for (String v : extraArgs) {
                    System.out.println(v);
                }
            }

            // Initialize the database
            DatabaseConfig.getDataSource();

            // Create the object adapter
            ObjectAdapter adapter = communicator.createObjectAdapter("VotingSystem");

            // Initialize services using ServiceManager
            ServiceManager serviceManager = new ServiceManagerImpl();
            serviceManager.initializeServices(communicator, adapter);

            // Activate the adapter
            adapter.activate();

            // Leer valor de cierre en consola
            new Thread(() -> {
                System.out.println("Enter 'e' to export data to Excel");
                while (true) {
                    int ch = new Scanner(System.in).next().charAt(0);
                    if (ch == 'e') {
                        serviceManager.shutdownServices();
                        break;
                    }
                }
            }).start();

            communicator.waitForShutdown();
        }
    }
}