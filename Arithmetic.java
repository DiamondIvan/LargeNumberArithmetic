/**
 * Arithmetic class that performs addition, subtraction,
 * multiplication, and division on LargeNumber objects.
 */
public class Arithmetic {

    // -------------------------------------------------------
    // ADDITION: adds two large numbers digit by digit
    // Works from tail (least significant) to head (most significant)
    // -------------------------------------------------------
    public static LargeNumber add(LargeNumber a, LargeNumber b) {
        LargeNumber result = new LargeNumber();
        Node pA = a.tail;   // start from last digit of a
        Node pB = b.tail;   // start from last digit of b
        int carry = 0;

        // Process digits from right to left
        while (pA != null || pB != null || carry != 0) {
            int digitA = (pA != null) ? pA.digit : 0;
            int digitB = (pB != null) ? pB.digit : 0;

            int sum = digitA + digitB + carry;
            carry = sum / 10;           // carry for next position
            int currentDigit = sum % 10;

            result.prependDigit(currentDigit); // add to front since we go right to left

            if (pA != null) pA = pA.prev;
            if (pB != null) pB = pB.prev;
        }

        if (result.head == null) result.appendDigit(0);
        return result;
    }

    // -------------------------------------------------------
    // SUBTRACTION: subtracts b from a (assumes a >= b)
    // Works from tail (least significant) to head (most significant)
    // -------------------------------------------------------
    public static LargeNumber subtract(LargeNumber a, LargeNumber b) {
        // Make sure a >= b
        if (a.compareTo(b) < 0) {
            System.out.println("Note: a < b, returning 0 for subtraction.");
            return new LargeNumber("0");
        }

        LargeNumber result = new LargeNumber();
        Node pA = a.tail;
        Node pB = b.tail;
        int borrow = 0;

        while (pA != null) {
            int digitA = pA.digit - borrow;
            int digitB = (pB != null) ? pB.digit : 0;

            if (digitA < digitB) {
                digitA += 10;   // borrow from next position
                borrow = 1;
            } else {
                borrow = 0;
            }

            int diff = digitA - digitB;
            result.prependDigit(diff);

            pA = pA.prev;
            if (pB != null) pB = pB.prev;
        }

        result.removeLeadingZeros();
        return result;
    }

    // -------------------------------------------------------
    // MULTIPLICATION: multiplies a by b digit by digit
    // Uses the standard long multiplication approach
    // -------------------------------------------------------
    public static LargeNumber multiply(LargeNumber a, LargeNumber b) {
        // Convert to string arrays for easier indexed access
        String strA = a.toString();
        String strB = b.toString();
        int lenA = strA.length();
        int lenB = strB.length();

        // Use an int array to accumulate partial products
        // Result can have at most lenA + lenB digits
        int[] resultDigits = new int[lenA + lenB];

        // Multiply each digit of b with each digit of a (right to left)
        for (int i = lenA - 1; i >= 0; i--) {
            for (int j = lenB - 1; j >= 0; j--) {
                int mul = (strA.charAt(i) - '0') * (strB.charAt(j) - '0');
                int pos1 = i + j;       // carry position
                int pos2 = i + j + 1;   // current position

                int sum = mul + resultDigits[pos2];
                resultDigits[pos2] = sum % 10;
                resultDigits[pos1] += sum / 10;  // add carry
            }
        }

        // Build LargeNumber from result array
        LargeNumber result = new LargeNumber();
        for (int digit : resultDigits) {
            result.appendDigit(digit);
        }
        result.removeLeadingZeros();
        return result;
    }

    // -------------------------------------------------------
    // DIVISION: divides a by b, returns quotient with decimal places
    // Uses long division approach
    // -------------------------------------------------------
    public static String divide(LargeNumber a, LargeNumber b, int decimalPlaces) {
        if (b.isZero()) {
            return "Error: Division by zero";
        }

        String dividend = a.toString();
        StringBuilder quotient = new StringBuilder();
        LargeNumber current = new LargeNumber("0");

        // Step 1: Integer part of division (long division)
        for (int i = 0; i < dividend.length(); i++) {
            // Bring down next digit into current
            if (current.isZero()) {
                current = new LargeNumber(String.valueOf(dividend.charAt(i) - '0'));
            } else {
                current.appendDigit(dividend.charAt(i) - '0');
            }

            // Find how many times b goes into current
            int count = findMultiple(current, b);
            quotient.append(count);

            // Subtract count * b from current
            LargeNumber multiple = multiply(b, new LargeNumber(String.valueOf(count)));
            current = subtract(current, multiple);
        }

        // Step 2: Decimal part (continue dividing the remainder)
        if (!current.isZero() && decimalPlaces > 0) {
            quotient.append(".");
            for (int i = 0; i < decimalPlaces; i++) {
                // Multiply remainder by 10 (shift left by appending 0)
                if (current.isZero()) {
                    current.appendDigit(0);
                } else {
                    current.appendDigit(0);
                }

                int count = findMultiple(current, b);
                quotient.append(count);

                LargeNumber multiple = multiply(b, new LargeNumber(String.valueOf(count)));
                current = subtract(current, multiple);

                if (current.isZero()) break; // no more remainder
            }
        }

        // Clean up leading zeros in integer part
        String result = quotient.toString();
        int dotIndex = result.indexOf('.');
        String intPart = (dotIndex == -1) ? result : result.substring(0, dotIndex);
        String decPart = (dotIndex == -1) ? "" : result.substring(dotIndex);

        // Remove leading zeros from integer part
        intPart = intPart.replaceFirst("^0+(?!$)", "");
        if (intPart.isEmpty()) intPart = "0";

        return intPart + decPart;
    }

    // Helper: find the largest digit (0-9) such that digit * b <= current
    private static int findMultiple(LargeNumber current, LargeNumber b) {
        for (int i = 9; i >= 1; i--) {
            LargeNumber multiple = multiply(b, new LargeNumber(String.valueOf(i)));
            if (current.compareTo(multiple) >= 0) {
                return i;
            }
        }
        return 0;
    }
}
