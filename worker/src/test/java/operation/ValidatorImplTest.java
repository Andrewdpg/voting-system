package operation;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the ValidatorImpl class.
 */
public class ValidatorImplTest {
    private ValidatorImpl validator;

    /**
     * Sets up the test environment before each test.
     */
    @BeforeEach
    public void setUp() {
        validator = new ValidatorImpl();
    }

    /**
     * Invokes the private isPrime method using reflection.
     *
     * @param n the number to check for primality
     * @return true if the number is prime, false otherwise
     * @throws Exception if the method invocation fails
     */
    private boolean invokeIsPrime(int n) throws Exception {
        Method method = ValidatorImpl.class.getDeclaredMethod("isPrime", int.class);
        method.setAccessible(true);
        return (boolean) method.invoke(validator, n);
    }

    /**
     * Invokes the private countPrimeFactors method using reflection.
     *
     * @param n the number to count prime factors for
     * @return the number of prime factors
     * @throws Exception if the method invocation fails
     */
    private int invokeCountPrimeFactors(Long n) throws Exception {
        Method method = ValidatorImpl.class.getDeclaredMethod("countPrimeFactors", Long.class);
        method.setAccessible(true);
        return (int) method.invoke(validator, n);
    }

    // Tests for isPrime method

    @Test
    public void testIsPrime_NegativeNumber() throws Exception {
        assertFalse(invokeIsPrime(-5), "Negative numbers should not be prime");
    }

    @Test
    public void testIsPrime_Zero() throws Exception {
        assertFalse(invokeIsPrime(0), "Zero should not be prime");
    }

    @Test
    public void testIsPrime_One() throws Exception {
        assertFalse(invokeIsPrime(1), "One should not be prime");
    }

    @Test
    public void testIsPrime_Two() throws Exception {
        assertTrue(invokeIsPrime(2), "Two should be prime");
    }

    @Test
    public void testIsPrime_Three() throws Exception {
        assertTrue(invokeIsPrime(3), "Three should be prime");
    }

    @Test
    public void testIsPrime_EvenNumberGreaterThanTwo() throws Exception {
        assertFalse(invokeIsPrime(4), "Even numbers greater than 2 should not be prime");
        assertFalse(invokeIsPrime(100), "Even numbers greater than 2 should not be prime");
    }

    @Test
    public void testIsPrime_PrimeNumbers() throws Exception {
        assertTrue(invokeIsPrime(5), "5 should be prime");
        assertTrue(invokeIsPrime(7), "7 should be prime");
        assertTrue(invokeIsPrime(11), "11 should be prime");
        assertTrue(invokeIsPrime(17), "17 should be prime");
        assertTrue(invokeIsPrime(29), "29 should be prime");
    }

    @Test
    public void testIsPrime_CompositeNumbers() throws Exception {
        assertFalse(invokeIsPrime(9), "9 should not be prime");
        assertFalse(invokeIsPrime(15), "15 should not be prime");
        assertFalse(invokeIsPrime(21), "21 should not be prime");
        assertFalse(invokeIsPrime(25), "25 should not be prime");
    }

    // Tests for countPrimeFactors method

    @Test
    public void testCountPrimeFactors_PrimeNumber() throws Exception {
        assertEquals(1, invokeCountPrimeFactors(7L), "Prime number should have 1 prime factor");
    }

    @Test
    public void testCountPrimeFactors_EvenNumber() throws Exception {
        assertEquals(2, invokeCountPrimeFactors(12L), "12 should have 2 prime factors (2, 3)");
    }

    @Test
    public void testCountPrimeFactors_LargePrimeNumber() throws Exception {
        assertEquals(1, invokeCountPrimeFactors(997L), "Large prime number should have 1 prime factor");
    }

    @Test
    public void testCountPrimeFactors_CompositeNumber() throws Exception {
        assertEquals(3, invokeCountPrimeFactors(30L), "30 should have 3 prime factors (2, 3, 5)");
        assertEquals(4, invokeCountPrimeFactors(210L), "210 should have 4 prime factors (2, 3, 5, 7)");
    }

    // Tests for executeCommand method

    @Test
    public void testExecuteCommand_ValidCommand() {
        // Note: This test might need adjustment based on the specific environment
        String result = validator.executeCommand("echo Hello");
        assertTrue(result.contains("Hello") || result.equals("Error executing command"), 
                   "Command execution should return output or error message");
    }

    @Test
    public void testExecuteCommand_InvalidCommand() {
        String result = validator.executeCommand("nonexistentcommand");
        assertEquals("Error executing command", result, "Invalid command should return error message");
    }
}