module VotingSystem {

    class PollingStation {
        string post;
        string address;
        string city;
        string state;
    };

    // Base class for all messages
    class Message {
    };

    // Class representing the result of a voting place query
    class QueryResult extends Message {
        int citizenId;
        PollingStation pollingStation;
        int isPrime;
        long processTime;
        long dbTime;
        long queryTime;
        long endTime;
    };

    // Class containing client information
    class ClientInfo extends Message {
        string clientId;
    };

    // Class representing an error message
    class Error extends Message {
        string message;
    };

    // Client interface defining the callback that will be invoked by the server
    interface Client {
        // Callback method to receive messages (Results or Other types) from the server
        void receiveNotification(Message message);
        void receiveExportSignal();
    };

    // Interface allowing clients to perform queries on the server
    interface QueryService {
        // Method to query a polling station based on citizen ID
        void queryPollingStation(ClientInfo info, int citizenId, long queryTime);
    };

    interface DatabaseService {
       void queryPollingStation(Client* client, QueryResult partialResult);
    };

    // Interface for client registration and removal on the server
    interface RegistrationService {
        // Method to register a client as an observer
        void registerClient(Client* clientProxy);

        // Method to remove a client from the observer registry
        void unregisterClient(ClientInfo info);
    };
};
