import com.zeroc.Ice.*;
import com.zeroc.IceGrid.QueryPrx;
import config.ServiceManagerImpl;
import config.interfaces.ServiceManager;

public class Worker {
    public static void main(String[] args) {
        int status = 0;
        java.util.List<String> extraArgs = new java.util.ArrayList<>();
        try (Communicator communicator = Util.initialize(args, extraArgs)) {

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("Shutting down WorkerServer...");
                communicator.shutdown();
            }));

            if (!extraArgs.isEmpty()) {
                System.err.println("too many arguments");
                status = 1;
            }else{
                ObjectAdapter adapter = communicator.createObjectAdapter("WorkerAdapter");
                Properties properties = communicator.getProperties();

                QueryPrx query = QueryPrx.checkedCast(communicator.stringToProxy("IceGrid/Query"));
                if (query == null) {
                    throw new Error("Invalid proxy");
                }

                // Initialize services using ServiceManager
                ServiceManager serviceManager = new ServiceManagerImpl();
                serviceManager.initializeServices(properties, adapter, query);

                // Activate the adapter
                adapter.activate();
                System.out.println("WorkerServer registered dynamically.");
                communicator.waitForShutdown();
            }

            System.exit(status);
        }
    }
}
