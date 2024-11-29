import VotingSystem.SubscriberPrx;
import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.InitializationData;
import com.zeroc.Ice.ObjectAdapter;
import VotingSystem.ClientPrx;
import com.zeroc.Ice.Util;
import com.zeroc.IceGrid.QueryPrx;
import config.ConnectionManager;
import ice.CallbackHandler;
import ice.SubscriberI;

public class Client {

    private static volatile boolean running = true;

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java -jar client.jar <host>");
            System.exit(1);
        }

        String host = args[0];

        InitializationData initData = new InitializationData();
        initData.properties = Util.createProperties();
        initData.properties.setProperty("Ice.Default.Locator", "IceGrid/Locator:tcp -h " + host + " -p 4061");
        initData.properties.setProperty("CallbackAdapter.Endpoints", "tcp -p 0");
        initData.properties.setProperty("Ice.ThreadPool.Server.Size", "20");
        initData.properties.setProperty("Ice.ThreadPool.Server.SizeMax", "50");
        initData.properties.setProperty("Ice.ThreadPool.Client.Size", "50");
        initData.properties.setProperty("Ice.ThreadPool.Client.SizeMax", "100");
        initData.properties.setProperty("Ice.ThreadPool.Server.StackSize", "131072");
        initData.properties.setProperty("Ice.ThreadPool.Server.Serialize", "0");

        try (Communicator communicator = com.zeroc.Ice.Util.initialize(initData)) {
            ObjectAdapter adapter = communicator.createObjectAdapter("CallbackAdapter");

            QueryPrx query = QueryPrx.checkedCast(communicator.stringToProxy("IceGrid/Query"));
            if (query == null) {
                throw new Error("Invalid proxy");
            }

            ConnectionManager serviceManager = new ConnectionManager(query, communicator);

            CallbackHandler callbackHandler = new CallbackHandler();
            ClientPrx callback = ClientPrx.checkedCast(adapter.addWithUUID(callbackHandler));

            SubscriberI subscriber = new SubscriberI(serviceManager, callback, Client::setRunning);
            SubscriberPrx subscriberProxy = SubscriberPrx.checkedCast(adapter.addWithUUID(subscriber));

            adapter.activate();

            serviceManager.registerClient(subscriberProxy);

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
