import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.InitializationData;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.Util;

import VotingSystem.ClientPrx;
import config.ControllerImpl;
import config.ServiceManagerImpl;
import config.interfaces.Controller;
import config.interfaces.ServiceManager;
import ice.CallbackHandler;
import manager.ClientManagerImpl;
import manager.interfaces.ClientManager;

public class Publisher {

    private static volatile boolean running = true;

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java -jar publisher.jar <host>");
            System.exit(1);
        }

        String host = args[0];

        // Configuración de propiedades para Ice
        InitializationData initData = new InitializationData();
        initData.properties = Util.createProperties();
        initData.properties.setProperty("Ice.Default.Locator", "IceGrid/Locator:tcp -h " + host + " -p 4061");
        initData.properties.setProperty("Ice.ProgramName", "Publisher");
        initData.properties.setProperty("PublisherAdapter.Endpoints", "tcp -h *");
        initData.properties.setProperty("PublisherAdapter.AdapterId", "PublisherAdapter");
        initData.properties.setProperty("CallbackAdapter.Endpoints", "tcp -p 0");
        initData.properties.setProperty("Ice.ThreadPool.Server.Size", "20");
        initData.properties.setProperty("Ice.ThreadPool.Server.SizeMax", "50");
        initData.properties.setProperty("Ice.ThreadPool.Client.Size", "50");
        initData.properties.setProperty("Ice.ThreadPool.Client.SizeMax", "100");
        initData.properties.setProperty("Ice.ThreadPool.Server.StackSize", "131072");
        initData.properties.setProperty("Ice.ThreadPool.Server.Serialize", "0");

        try (Communicator communicator = com.zeroc.Ice.Util.initialize(initData)) {
            ClientManager clientManager = new ClientManagerImpl();
            ObjectAdapter adapter = communicator.createObjectAdapter("CallbackAdapter");

            ServiceManager serviceManager = new ServiceManagerImpl(clientManager);
            serviceManager.initializeServices(communicator.getProperties(), communicator.createObjectAdapter("PublisherAdapter"));

            CallbackHandler callbackHandler = new CallbackHandler();
            ClientPrx callback = ClientPrx.checkedCast(adapter.addWithUUID(callbackHandler));

            adapter.activate();

            Controller controller = new ControllerImpl(clientManager, callback, communicator);

            System.out.println("Publisher registered dynamically.");

            while (running) {
                running =  controller.run();
            }

            communicator.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}