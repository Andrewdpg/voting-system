package operation.interfaces;
import VotingSystem.QueryResult;

public interface QueryProcessor {
    QueryResult processQuery(String citizenId, long start);
}