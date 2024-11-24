package operation;

import VotingSystem.QueryResult;
import operation.interfaces.QueryProcessor;
import operation.interfaces.Validator;

public class QueryProcessorImpl implements QueryProcessor {

    Validator executor = new ValidatorImpl();

    public QueryResult processQuery(int citizenId, long start) {
        QueryResult result = new QueryResult();
        result.processTime = System.currentTimeMillis() - start;
        result.citizenId = citizenId;
        result.isPrime = executor.isPrimeFactorCountPrime(citizenId)  ? 1 : 0;
        return result;
    }

}
