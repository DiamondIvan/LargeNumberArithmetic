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
        Node pA = a.tail; // start from last digit of a
        Node pB = b.tail; // start from last digit of b
        int carry = 0;

        // Process digits from right to left
        while (pA != null || pB != null || carry != 0) {
            int digitA = (pA != null) ? pA.digit : 0;
            int digitB = (pB != null) ? pB.digit : 0;

            int sum = digitA + digitB + carry;
            carry = sum / 10; // carry for next position
            int currentDigit = sum % 10;

            result.prependDigit(currentDigit); // add to front since we go right to left

            if (pA != null)
                pA = pA.prev;
            if (pB != null)
                pB = pB.prev;
        }

        if (result.head == null)
            result.appendDigit(0);
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
                digitA += 10; // borrow from next position
                borrow = 1;
            } else {
                borrow = 0;
            }

            int diff = digitA - digitB;
            result.prependDigit(diff);

            pA = pA.prev;
            if (pB != null)
                pB = pB.prev;
        }

        result.removeLeadingZeros();
        return result;
    }

    // -------------------------------------------------------
    // MULTIPLICATION: multiplies a by b digit by digit
    // Traverses DLL nodes directly (tail -> head) to stay true
    // to the doubly linked list data structure requirement.
    // Uses the standard long multiplication approach.
    // -------------------------------------------------------
    public static LargeNumber multiply(LargeNumber a, LargeNumber b) {
        // Short-circuit: anything * 0 = 0
        if (a.isZero() || b.isZero()) {
            return new LargeNumber("0");
        }

        int lenA = a.size;
        int lenB = b.size;

        // Use an int array to accumulate partial products
        // Result can have at most lenA + lenB digits
        int[] resultDigits = new int[lenA + lenB];

        // Traverse a from least significant (tail) to most significant (head)
        Node pA = a.tail;
        for (int i = lenA - 1; i >= 0; i--) {
            // Traverse b from least significant (tail) to most significant (head)
            Node pB = b.tail;
            for (int j = lenB - 1; j >= 0; j--) {
                int mul = pA.digit * pB.digit;
                int pos1 = i + j; // carry position
                int pos2 = i + j + 1; // current position

                int sum = mul + resultDigits[pos2];
                resultDigits[pos2] = sum % 10;
                resultDigits[pos1] += sum / 10; // add carry

                pB = pB.prev;
            }
            pA = pA.prev;
        }

        LargeNumber result = new LargeNumber();
        int start = 0;
        while (start < resultDigits.length - 1 && resultDigits[start] == 0) {
            start++;
        }
        for (int i = start; i < resultDigits.length; i++) {
            result.appendDigit(resultDigits[i]);
        }
        return result;
    }

    // Helper: multiply a LargeNumber by a single digit (0-9)
    // Traverses the linked list from tail to head, tracking carry
    private static LargeNumber multiplyByDigit(LargeNumber a, int digit) {
        if (digit == 0)
            return new LargeNumber("0");

        LargeNumber result = new LargeNumber();
        Node pA = a.tail; // start from least significant digit
        int carry = 0;

        // Traverse each node of a from right to left
        while (pA != null || carry != 0) {
            int digitA = (pA != null) ? pA.digit : 0;
            int prod = digitA * digit + carry;
            carry = prod / 10;
            result.prependDigit(prod % 10);
            if (pA != null)
                pA = pA.prev;
        }

        if (result.head == null)
            result.appendDigit(0);
        return result;
    }

    // -------------------------------------------------------
    // DIVISION: divides a by b, returns quotient with decimal places
    // Uses long division approach with half-up rounding on the last digit.
    // -------------------------------------------------------
    public static String divide(LargeNumber a, LargeNumber b, int decimalPlaces) {
        if (b.isZero()) {
            return "Error: Division by zero";
        }

        StringBuilder quotient = new StringBuilder();
        LargeNumber current = new LargeNumber("0");

        // Step 1: Integer part — traverse dividend digit by digit using linked list
        Node pA = a.head; // start from most significant digit
        while (pA != null) {
            // Bring down next digit into current
            if (current.isZero()) {
                current = new LargeNumber(String.valueOf(pA.digit));
            } else {
                current.appendDigit(pA.digit);
            }

            // Find largest digit (0-9) such that digit * b <= current
            int count = findMultiple(current, b);
            quotient.append(count);

            // Subtract count * b from current (progressive subtraction)
            LargeNumber multiple = multiplyByDigit(b, count);
            current = subtract(current, multiple);

            pA = pA.next; // move to next digit of dividend
        }

        // Step 2: Decimal part (continue dividing the remainder)
        // We compute one extra digit beyond decimalPlaces for half-up rounding.
        int roundingDigit = 0;
        if (!current.isZero() && decimalPlaces > 0) {
            quotient.append(".");
            for (int i = 0; i <= decimalPlaces; i++) { // note: <= to get one extra digit
                // Multiply remainder by 10 (shift left by appending 0)
                current.appendDigit(0);

                int count = findMultiple(current, b);

                if (i == decimalPlaces) {
                    // This is the extra rounding digit — don't append, just save it
                    roundingDigit = count;
                } else {
                    quotient.append(count);
                }

                LargeNumber multiple = multiply(b, new LargeNumber(String.valueOf(count)));
                current = subtract(current, multiple);

                if (current.isZero() && i < decimalPlaces)
                    break; // exact division, stop early
            }
        }

        // Apply half-up rounding: if the extra digit >= 5, increment the last decimal
        // digit
        String result = quotient.toString();
        if (roundingDigit >= 5 && result.contains(".")) {
            result = applyRounding(result);
        }

        // Clean up leading zeros in integer part
        int dotIndex = result.indexOf('.');
        String intPart = (dotIndex == -1) ? result : result.substring(0, dotIndex);
        String decPart = (dotIndex == -1) ? "" : result.substring(dotIndex);
        intPart = intPart.replaceFirst("^0+(?!$)", "");
        if (intPart.isEmpty())
            intPart = "0";

        return intPart + decPart;
    }

    /**
     * Applies half-up rounding by incrementing the last character of a
     * decimal string, propagating carry leftward if needed.
     * Example: "3.999" -> "4.0" (if last digit rounds up with carry chain)
     */
    private static String applyRounding(String s) {
        char[] digits = s.toCharArray();
        int i = digits.length - 1;
        while (i >= 0) {
            if (digits[i] == '.') {
                i--;
                continue;
            } // skip decimal point
            if (digits[i] < '9') {
                digits[i]++; // simple increment, no carry needed
                return new String(digits);
            } else {
                digits[i] = '0'; // this digit rolls over, carry propagates left
                i--;
            }
        }
        // Carry propagated past the first digit: prepend '1'
        return "1" + new String(digits);
    }

    // Helper: find the largest digit (0-9) such that digit * b <= current
    private static int findMultiple(LargeNumber current, LargeNumber b) {
        for (int i = 9; i >= 1; i--) {
            LargeNumber multiple = multiplyByDigit(b, i);
            if (current.compareTo(multiple) >= 0) {
                return i;
            }
        }
        return 0;
    }
}
