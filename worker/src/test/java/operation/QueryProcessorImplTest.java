package operation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;

import VotingSystem.QueryResult;
import operation.interfaces.Validator;

/**
 * Unit tests for the QueryProcessorImpl class.
 */
public class QueryProcessorImplTest {

    private QueryProcessorImpl queryProcessor;
    private Validator validatorMock;

    /**
     * Sets up the test environment before each test.
     * Mocks the Validator and injects it into the QueryProcessorImpl.
     */
    @BeforeEach
    public void setUp() {
        validatorMock = Mockito.mock(Validator.class);
        queryProcessor = new QueryProcessorImpl();
        queryProcessor.executor = validatorMock; // Inject the mock validator
    }

    /**
     * Tests the processQuery method when the prime factors count is prime.
     * Verifies that the QueryResult is correctly populated.
     */
    @Test
    public void testProcessQuery_PrimeFactorsCountIsPrime() {
        String citizenId = "123456";
        long start = System.currentTimeMillis();

        when(validatorMock.isPrimeFactorCountPrime(anyString())).thenReturn(true);

        QueryResult result = queryProcessor.processQuery(citizenId, start);

        assertEquals(citizenId, result.citizenId);
        assertEquals(1, result.isPrime);
    }

    /**
     * Tests the processQuery method when the prime factors count is not prime.
     * Verifies that the QueryResult is correctly populated.
     */
    @Test
    public void testProcessQuery_PrimeFactorsCountIsNotPrime() {
        String citizenId = "123456";
        long start = System.currentTimeMillis();

        when(validatorMock.isPrimeFactorCountPrime(anyString())).thenReturn(false);

        QueryResult result = queryProcessor.processQuery(citizenId, start);

        assertEquals(citizenId, result.citizenId);
        assertEquals(0, result.isPrime);
    }
}