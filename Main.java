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
        System.out.println("Addition        = " + Arithmetic.add(numM, numN));

        // Subtraction 
        System.out.println("Subtraction     = " + Arithmetic.subtract(numM, numN));

        // Multiplication
        System.out.println("Multiplication  = " + Arithmetic.multiply(numM, numN));

        // Division (with up to 20 decimal places)
        System.out.println("Division        = " + Arithmetic.divide(numM, numN, 20));

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

        // ------- Negative operand test -------
        String m4 = "-42";
        String n4 = "10";
        displayResults(m4, n4);

        // ------- Both negative test -------
        String m5 = "-50";
        String n5 = "-30";
        displayResults(m5, n5);

        // ------- Interactive mode: let the user enter their own numbers -------
        Scanner sc = new Scanner(System.in);
        System.out.println("----------------------------------------------");
        System.out.println("Enter your own numbers (press Enter with no");
        System.out.println("input to quit):");

        while (true) {
            System.out.print("m = ");
            String m = sc.nextLine().trim();
            if (m.isEmpty()) break;

            System.out.print("n = ");
            String n = sc.nextLine().trim();
            if (n.isEmpty()) break;

            try {
                // Validate both inputs before running any operations
                new LargeNumber(m);
                new LargeNumber(n);

                // Warn user clearly if n is zero (division not possible)
                if (new LargeNumber(n).isZero()) {
                    System.out.println("  Note: n = 0, division will show an error.\n");
                }

                displayResults(m, n);

            } catch (IllegalArgumentException e) {
                System.out.println("  Invalid input: " + e.getMessage());
                System.out.println("  Please enter integers only (e.g. 123 or -456).\n");
            }
        }

        sc.close();
        System.out.println("Goodbye!");
    }
}
