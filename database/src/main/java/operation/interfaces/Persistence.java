package operation.interfaces;

import VotingSystem.PollingStation;

public interface Persistence {
    PollingStation getPollingStation(String citizenId);
}