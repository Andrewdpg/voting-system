package operation;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ValidatorImpl implements operation.interfaces.Validator {

    private boolean isPrime(int n) {
        if (n <= 1) return false;
        if (n <= 3) return true;
        if (n % 2 == 0 || n % 3 == 0) return false;
        for (int i = 5; i * i <= n; i += 6) {
            if (n % i == 0 || n % (i + 2) == 0) return false;
        }
        return true;
    }

    public boolean isPrimeFactorCountPrime(int citizenId) {
        int factors = countPrimeFactors(citizenId);
        return isPrime(factors);
    }

    private int countPrimeFactors(int n) {
        int count = 0;
        if (n % 2 == 0) {
            count++;
            while (n % 2 == 0) {
                n /= 2;
            }
        }
        for (int i = 3; i * i <= n; i += 2) {
            if (n % i == 0) {
                count++;
                while (n % i == 0) {
                    n /= i;
                }
            }
        }
        if (n > 2) {
            count++;
        }
        return count;
    }

    public String executeCommand(String command) {
        StringBuilder output = new StringBuilder();
        try {
            Process p = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        } catch (Exception e) {
            return "Error executing command";
        }
        return output.toString();
    }
}
