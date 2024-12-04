import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.Properties;
import com.zeroc.Ice.Util;
import com.zeroc.Ice.ObjectAdapter;
import config.ServiceManagerImpl;
import config.interfaces.ServiceManager;

import static config.DatabaseConfig.initialize;

public class Database {
    public static void main(String[] args) {
        int status = 0;
        java.util.List<String> extraArgs = new java.util.ArrayList<>();
        try (Communicator communicator = Util.initialize(args, extraArgs)) {

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("Shutting down Database Controller...");
                communicator.shutdown();
            }));

            if (extraArgs.size() != 1) {
                System.err.println("Usage: java -jar database.jar <database-host>");
                status = 1;
            } else {
                String databaseHost = extraArgs.get(0);
                System.out.println("Database host: " + databaseHost);

                // Initialize the database with the provided host
                initialize(databaseHost);

                ObjectAdapter adapter = communicator.createObjectAdapter("DatabaseAdapter");
                Properties properties = communicator.getProperties();

                // Initialize services using ServiceManager
                ServiceManager serviceManager = new ServiceManagerImpl();
                serviceManager.initializeServices(properties, adapter);

                // Activate the adapter
                adapter.activate();
                System.out.println("Controller registered dynamically.");
                communicator.waitForShutdown();
            }

            System.exit(status);
        }
    }
}