
public class ArithmeticTest {

        static int passed = 0;
        static int failed = 0;

        // -------------------------------------------------------
        // Tiny assertion helper
        // -------------------------------------------------------
        static void expect(String description, String expected, String actual) {
                if (expected.equals(actual)) {
                        System.out.println("  PASS  " + description);
                        passed++;
                } else {
                        System.out.println("  FAIL  " + description);
                        System.out.println("         Expected : " + expected);
                        System.out.println("         Actual   : " + actual);
                        failed++;
                }
        }

        // -------------------------------------------------------
        // ADDITION tests
        // -------------------------------------------------------
        static void testAddition() {
                System.out.println("\n--- Addition ---");
                expect("0 + 0 = 0",
                                "0", Arithmetic.add(new LargeNumber("0"), new LargeNumber("0")).toString());
                expect("1 + 0 = 1",
                                "1", Arithmetic.add(new LargeNumber("1"), new LargeNumber("0")).toString());
                expect("5 + 5 = 10 (single-digit carry)",
                                "10", Arithmetic.add(new LargeNumber("5"), new LargeNumber("5")).toString());
                expect("999 + 1 = 1000 (carry chain)",
                                "1000", Arithmetic.add(new LargeNumber("999"), new LargeNumber("1")).toString());
                expect("Large number addition",
                                "123913578246913578246913578246913578246913578246913578",
                                Arithmetic.add(
                                                new LargeNumber("123456789123456789123456789123456789123456789123456789"),
                                                new LargeNumber("456789123456789123456789123456789123456789123456789"))
                                                .toString());
                expect("(-5) + 3 = -2",
                                "-2", Arithmetic.add(new LargeNumber("-5"), new LargeNumber("3")).toString());
                expect("5 + (-3) = 2",
                                "2", Arithmetic.add(new LargeNumber("5"), new LargeNumber("-3")).toString());
                expect("(-5) + (-3) = -8",
                                "-8", Arithmetic.add(new LargeNumber("-5"), new LargeNumber("-3")).toString());
                expect("(-5) + 5 = 0 (no negative zero)",
                                "0", Arithmetic.add(new LargeNumber("-5"), new LargeNumber("5")).toString());
        }

        // -------------------------------------------------------
        // SUBTRACTION tests
        // -------------------------------------------------------
        static void testSubtraction() {
                System.out.println("\n--- Subtraction ---");
                expect("0 - 0 = 0",
                                "0", Arithmetic.subtract(new LargeNumber("0"), new LargeNumber("0")).toString());
                expect("10 - 1 = 9",
                                "9", Arithmetic.subtract(new LargeNumber("10"), new LargeNumber("1")).toString());
                expect("1000 - 1 = 999 (borrow chain)",
                                "999", Arithmetic.subtract(new LargeNumber("1000"), new LargeNumber("1")).toString());
                expect("5 - 5 = 0",
                                "0", Arithmetic.subtract(new LargeNumber("5"), new LargeNumber("5")).toString());
                expect("100 - 99 = 1",
                                "1", Arithmetic.subtract(new LargeNumber("100"), new LargeNumber("99")).toString());
                // Standard signed subtractions
                expect("3 - 7 = -4 (signed result)",
                                "-4", Arithmetic.subtract(new LargeNumber("3"), new LargeNumber("7")).toString());
                expect("(-3) - 2 = -5",
                                "-5", Arithmetic.subtract(new LargeNumber("-3"), new LargeNumber("2")).toString());
                expect("(-3) - (-2) = -1",
                                "-1", Arithmetic.subtract(new LargeNumber("-3"), new LargeNumber("-2")).toString());
        }

        // -------------------------------------------------------
        // MULTIPLICATION tests
        // -------------------------------------------------------
        static void testMultiplication() {
                System.out.println("\n--- Multiplication ---");
                expect("0 * 99999 = 0 (zero short-circuit)",
                                "0", Arithmetic.multiply(new LargeNumber("0"), new LargeNumber("99999")).toString());
                expect("99999 * 0 = 0 (zero short-circuit)",
                                "0", Arithmetic.multiply(new LargeNumber("99999"), new LargeNumber("0")).toString());
                expect("1 * 1 = 1",
                                "1", Arithmetic.multiply(new LargeNumber("1"), new LargeNumber("1")).toString());
                expect("55 * 2 = 110",
                                "110", Arithmetic.multiply(new LargeNumber("55"), new LargeNumber("2")).toString());
                expect("999 * 999 = 998001",
                                "998001",
                                Arithmetic.multiply(new LargeNumber("999"), new LargeNumber("999")).toString());
                expect("12 * 34 = 408",
                                "408", Arithmetic.multiply(new LargeNumber("12"), new LargeNumber("34")).toString());
                expect("Spec Example 1 multiplication",
                                "56393718488493483920593249352693014784792780216892530351019811918200464868202810547205156226207501"
                                                +
                                                "90521",
                                Arithmetic.multiply(
                                                new LargeNumber("123456789123456789123456789123456789123456789123456789"),
                                                new LargeNumber("456789123456789123456789123456789123456789123456789"))
                                                .toString());
                expect("(-5) * 3 = -15",
                                "-15", Arithmetic.multiply(new LargeNumber("-5"), new LargeNumber("3")).toString());
                expect("5 * (-3) = -15",
                                "-15", Arithmetic.multiply(new LargeNumber("5"), new LargeNumber("-3")).toString());
                expect("(-5) * (-3) = 15",
                                "15", Arithmetic.multiply(new LargeNumber("-5"), new LargeNumber("-3")).toString());
        }

        // -------------------------------------------------------
        // DIVISION tests
        // -------------------------------------------------------
        static void testDivision() {
                System.out.println("\n--- Division ---");
                expect("Division by zero",
                                "Error: Division by zero",
                                Arithmetic.divide(new LargeNumber("10"), new LargeNumber("0"), 5));
                expect("0 / 5 = 0",
                                "0", Arithmetic.divide(new LargeNumber("0"), new LargeNumber("5"), 5));
                expect("10 / 2 = 5 (exact)",
                                "5", Arithmetic.divide(new LargeNumber("10"), new LargeNumber("2"), 5));
                expect("55 / 2 = 27.5 (spec example)",
                                "27.5", Arithmetic.divide(new LargeNumber("55"), new LargeNumber("2"), 5));
                expect("1 / 3 = 0.33333 (truncated to 5 dp, rounded)",
                                "0.33333", Arithmetic.divide(new LargeNumber("1"), new LargeNumber("3"), 5));
                expect("2 / 3 = 0.66667 (rounded up from 0.666666...)",
                                "0.66667", Arithmetic.divide(new LargeNumber("2"), new LargeNumber("3"), 5));
                expect("999 / 1 = 999 (exact)",
                                "999", Arithmetic.divide(new LargeNumber("999"), new LargeNumber("1"), 5));
                expect("(-10) / 2 = -5",
                                "-5", Arithmetic.divide(new LargeNumber("-10"), new LargeNumber("2"), 5));
                expect("10 / (-2) = -5",
                                "-5", Arithmetic.divide(new LargeNumber("10"), new LargeNumber("-2"), 5));
                expect("(-10) / (-2) = 5",
                                "5", Arithmetic.divide(new LargeNumber("-10"), new LargeNumber("-2"), 5));
        }

        // -------------------------------------------------------
        // INPUT VALIDATION tests
        // -------------------------------------------------------
        static void testInputValidation() {
                System.out.println("\n--- Input Validation ---");

                // null input
                try {
                        new LargeNumber((String) null);
                        System.out.println("  FAIL  null input should throw");
                        failed++;
                } catch (IllegalArgumentException e) {
                        System.out.println("  PASS  null input throws IllegalArgumentException");
                        passed++;
                }

                // empty string
                try {
                        new LargeNumber("   ");
                        System.out.println("  FAIL  blank input should throw");
                        failed++;
                } catch (IllegalArgumentException e) {
                        System.out.println("  PASS  blank input throws IllegalArgumentException");
                        passed++;
                }

                // non-numeric
                try {
                        new LargeNumber("abc");
                        System.out.println("  FAIL  'abc' input should throw");
                        failed++;
                } catch (IllegalArgumentException e) {
                        System.out.println("  PASS  'abc' throws IllegalArgumentException");
                        passed++;
                }

                // leading '-' only
                try {
                        new LargeNumber("-");
                        System.out.println("  FAIL  '-' input should throw");
                        failed++;
                } catch (IllegalArgumentException e) {
                        System.out.println("  PASS  '-' throws IllegalArgumentException");
                        passed++;
                }

                // valid negative number parses without crash
                try {
                        LargeNumber neg = new LargeNumber("-42");
                        System.out.println("  PASS  '-42' parses correctly, negative=" + neg.isNegative()
                                        + ", value=" + neg);
                        passed++;
                } catch (Exception e) {
                        System.out.println("  FAIL  '-42' should not throw: " + e.getMessage());
                        failed++;
                }

                // -0 should not be negative
                LargeNumber negZero = new LargeNumber("-0");
                if (!negZero.isNegative()) {
                        System.out.println("  PASS  '-0' is not flagged as negative");
                        passed++;
                } else {
                        System.out.println("  FAIL  '-0' should not be negative");
                        failed++;
                }
        }

        // -------------------------------------------------------
        // COMPARISON HELPER tests
        // -------------------------------------------------------
        static void testComparisons() {
                System.out.println("\n--- Comparison Helpers ---");
                LargeNumber ten = new LargeNumber("10");
                LargeNumber five = new LargeNumber("5");
                LargeNumber alsoTen = new LargeNumber("10");

                expect("10.isGreaterThan(5) = true", "true", String.valueOf(ten.isGreaterThan(five)));
                expect("5.isLessThan(10) = true", "true", String.valueOf(five.isLessThan(ten)));
                expect("10.isEqualTo(10) = true", "true", String.valueOf(ten.isEqualTo(alsoTen)));
                expect("5.isGreaterThan(10) = false", "false", String.valueOf(five.isGreaterThan(ten)));
                expect("10.isLessThan(5) = false", "false", String.valueOf(ten.isLessThan(five)));
                expect("10.isEqualTo(5) = false", "false", String.valueOf(ten.isEqualTo(five)));
        }

        // ----------------------------------------------------------
        // COPY CONSTRUCTOR tests
        // ----------------------------------------------------------
        static void testCopyConstructor() {
                System.out.println("\n--- Copy Constructor ---");
                LargeNumber original = new LargeNumber("12345");
                LargeNumber copy = new LargeNumber(original);

                expect("Copy equals original", "12345", copy.toString());
                expect("Copy is a separate object", "true",
                                String.valueOf(original != copy));

                // Mutate copy and confirm original is unchanged
                copy.appendDigit(6);
                expect("Original unchanged after mutating copy", "12345", original.toString());
                expect("Copy reflects mutation", "123456", copy.toString());
        }

        // ----------------------------------------------------------
        // SIZE ACCURACY tests
        // ----------------------------------------------------------
        static void testSizeAccuracy() {
                System.out.println("\n--- getSize() Accuracy ---");
                expect("'123' has size 3", "3", String.valueOf(new LargeNumber("123").getSize()));
                expect("'007' strips to '7', size 1", "1", String.valueOf(new LargeNumber("007").getSize()));
                expect("'0' has size 1", "1", String.valueOf(new LargeNumber("0").getSize()));
                expect("'1000' has size 4", "4", String.valueOf(new LargeNumber("1000").getSize()));
        }

        // -------------------------------------------------------
        // Entry point
        // -------------------------------------------------------
        public static void main(String[] args) {
                System.out.println("========================================");
                System.out.println("  ArithmeticTest — Running all tests");
                System.out.println("========================================");

                testAddition();
                testSubtraction();
                testMultiplication();
                testDivision();
                testInputValidation();
                testComparisons();

                System.out.println("\n========================================");
                System.out.println("  Results: " + passed + " passed, " + failed + " failed");
                System.out.println("========================================");
        }
}
