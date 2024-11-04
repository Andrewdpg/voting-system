import VotingSystem.ClientInfo;
import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectAdapter;
import VotingSystem.ClientPrx;
import config.ConnectionManager;
import ice.CallbackHandler;

import java.util.List;

public class Client {

    private static volatile boolean running = true;
    private static volatile ClientInfo info = null;

    public static void main(String[] args) {
        List<String> extraArgs = new java.util.ArrayList<>();

        try (Communicator communicator = com.zeroc.Ice.Util.initialize(args, "config.client", extraArgs)) {
            ObjectAdapter adapter = communicator.createObjectAdapter("CallbackAdapter");

            CallbackHandler callbackHandler = new CallbackHandler(Client::setInfo, Client::setRunning);
            com.zeroc.Ice.ObjectPrx proxy = adapter.addWithUUID(callbackHandler);
            adapter.activate();

            ClientPrx callback = ClientPrx.checkedCast(proxy);

            ConnectionManager serviceManager = new ConnectionManager(communicator);
            serviceManager.registerClient(callback);

            while (info == null) {
                Thread.onSpinWait();
            }


            for (int i = 0; i < 1000; i++) {
                new Thread(() -> serviceManager.queryPollingStation(info, (int)(Math.random() * 100000))).start();
            }

            System.out.println("All queries sent");

            while (running) {
                Thread.onSpinWait();
            }
        }
    }

    public static void setInfo(ClientInfo info) {
        Client.info = info;
    }

    public static void setRunning(boolean running) {
        Client.running = running;
    }
}