module VotingSystem {

    // Base class for all messages
    class Message {
    };

    // Class representing the result of a voting place query
    class QueryResult extends Message {
        int citizenId;
        string pollingStation;
        int isPrime;
        long processTime;
        long queryTime;
    };

    // Class containing client information
    class ClientInfo extends Message {
        string clientId;
    };

    // Client interface defining the callback that will be invoked by the server
    interface Client {
        // Callback method to receive messages (Results or Other types) from the server
        void receiveNotification(Message message);
    };

    // Interface allowing clients to perform queries on the server
    interface QueryService {
        // Method to query a polling station based on citizen ID
        void queryPollingStation(ClientInfo info, int citizenId, long queryTime);
    };

    // Interface for client registration and removal on the server
    interface RegistrationService {
        // Method to register a client as an observer
        void registerClient(Client* clientProxy);

        // Method to remove a client from the observer registry
        void unregisterClient(ClientInfo info);
    };
};
