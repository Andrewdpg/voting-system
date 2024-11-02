package operation.interfaces;
import VotingSystem.QueryResult;

public interface QueryProcessor {
    QueryResult processQuery(int citizenId);
}