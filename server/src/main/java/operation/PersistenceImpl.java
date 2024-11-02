package operation;

public class PersistenceImpl implements operation.interfaces.Persistence {

    public String getPollingStation(int citizenId) {
        return "Polling Station for " + citizenId;
    }
}
