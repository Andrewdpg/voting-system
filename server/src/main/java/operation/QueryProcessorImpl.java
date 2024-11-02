package operation;

import VotingSystem.QueryResult;
import operation.interfaces.Persistence;
import operation.interfaces.QueryProcessor;
import operation.interfaces.Validator;

public class QueryProcessorImpl implements QueryProcessor {

    Validator executor = new ValidatorImpl();
    Persistence persistence = new PersistenceImpl();

    public QueryResult processQuery(int citizenId) {
        long start = System.currentTimeMillis();

        QueryResult result = new QueryResult();
        result.citizenId = citizenId;
        result.pollingStation = persistence.getPollingStation(citizenId);
        result.isPrime = executor.isPrimeFactorCountPrime(citizenId)  ? 1 : 0;
        result.processTime = System.currentTimeMillis() - start;

        return result;
    }

}
