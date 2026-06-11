/**
 * Main class to demonstrate Large Number Arithmetic.
 * Tests the two examples from the project specification,
 * then lets the user enter their own numbers interactively.
 */
import java.util.Scanner;

public class Main {

    // Display the results of all four operations neatly
    public static void displayResults(String m, String n) {
        System.out.println("==============================================");
        System.out.println("Input:");
        System.out.println("  m = " + m);
        System.out.println("  n = " + n);
        System.out.println("----------------------------------------------");

        LargeNumber numM = new LargeNumber(m);
        LargeNumber numN = new LargeNumber(n);

        // Addition
        LargeNumber sum = Arithmetic.add(numM, numN);
        System.out.println("Addition        = " + sum);

        // Subtraction (m - n, assuming m >= n)
        LargeNumber diff = Arithmetic.subtract(numM, numN);
        System.out.println("Subtraction     = " + diff);

        // Multiplication
        LargeNumber product = Arithmetic.multiply(numM, numN);
        System.out.println("Multiplication  = " + product);

        // Division (with up to 20 decimal places)
        String quotient = Arithmetic.divide(numM, numN, 20);
        System.out.println("Division        = " + quotient);

        System.out.println("==============================================\n");
    }

    public static void main(String[] args) {

        // ------- Example 1 from the project spec -------
        String m1 = "123456789123456789123456789123456789123456789123456789";
        String n1 = "456789123456789123456789123456789123456789123456789";
        displayResults(m1, n1);

        // ------- Example 2 from the project spec -------
        String m2 = "55";
        String n2 = "2";
        displayResults(m2, n2);

        // ------- Additional small test -------
        String m3 = "999";
        String n3 = "1";
        displayResults(m3, n3);

        // ------- Interactive mode: let the user enter their own numbers -------
        Scanner sc = new Scanner(System.in);
        System.out.println("----------------------------------------------");
        System.out.println("Enter your own numbers (press Enter with no");
        System.out.println("input to quit):");
        while (true) {
            System.out.print("  m = ");
            String m = sc.nextLine().trim();
            if (m.isEmpty()) break;

            System.out.print("  n = ");
            String n = sc.nextLine().trim();
            if (n.isEmpty()) break;

            try {
                displayResults(m, n);
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid input: " + e.getMessage());
                System.out.println("Please enter positive integers only.\n");
            }
        }
        sc.close();
        System.out.println("Goodbye!");
    }
}
