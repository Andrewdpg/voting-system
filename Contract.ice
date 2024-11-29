module VotingSystem {
    sequence<string> Batch;

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

    // Class representing an error message
    class Error extends Message {
        string message;
    };

    // Client interface defining the callback that will be invoked by the server
    interface Client {
        // Callback method to receive messages (Results or Other types) from the server
        void receiveNotification(Message message);
    };

    interface Subscriber {
        void receiveBatch(Batch batch);
        void receiveExportSignal();
        void shutdown();
    };

    interface RegistrationService {
        void register(Subscriber* client);
    };

    // Interface allowing clients to perform queries on the server
    interface QueryService {
        // Method to query a polling station based on citizen ID
        void queryPollingStation(Client* client, int citizenId, long queryTime);
    };

    interface DatabaseService {
       void queryPollingStation(Client* client, QueryResult partialResult);
    };
};
