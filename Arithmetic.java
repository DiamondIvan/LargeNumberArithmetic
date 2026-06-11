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
    // Traverses linked list nodes directly (no String/array conversion)
    // For each digit of b (right to left), multiply against each digit
    // of a (right to left), shift by position, then add partial results
    // -------------------------------------------------------
    public static LargeNumber multiply(LargeNumber a, LargeNumber b) {
        LargeNumber result = new LargeNumber("0");
 
        Node pB = b.tail;   // start from least significant digit of b
        int shift = 0;      // positional shift (how many zeros to append)
 
        // Traverse each digit of b from right to left
        while (pB != null) {
            int digitB = pB.digit;
 
            if (digitB != 0) {
                // Multiply every digit of a by digitB using linked list traversal
                LargeNumber partial = multiplyByDigit(a, digitB);
 
                // Shift left by appending 'shift' zeros (positional value)
                for (int i = 0; i < shift; i++) {
                    partial.appendDigit(0);
                }
 
                // Accumulate into result using add
                result = add(result, partial);
            }
 
            shift++;
            pB = pB.prev;   // move to next digit of b (right to left)
        }

        return result;
    }

    // Helper: multiply a LargeNumber by a single digit (0-9)
    // Traverses the linked list from tail to head, tracking carry
    private static LargeNumber multiplyByDigit(LargeNumber a, int digit) {
        if (digit == 0) return new LargeNumber("0");
 
        LargeNumber result = new LargeNumber();
        Node pA = a.tail;   // start from least significant digit
        int carry = 0;
 
        // Traverse each node of a from right to left
        while (pA != null || carry != 0) {
            int digitA = (pA != null) ? pA.digit : 0;
            int prod = digitA * digit + carry;
            carry = prod / 10;
            result.prependDigit(prod % 10);
            if (pA != null) pA = pA.prev;
        }
 
        if (result.head == null) result.appendDigit(0);
        return result;
    }

    // -------------------------------------------------------
    // DIVISION: divides a by b, returns quotient with decimal places
    // Uses long division: find suitable multiple, subtract progressively
    // -------------------------------------------------------
    public static String divide(LargeNumber a, LargeNumber b, int decimalPlaces) {
        if (b.isZero()) {
            return "Error: Division by zero";
        }

        StringBuilder quotient = new StringBuilder();
        LargeNumber current = new LargeNumber("0");
 
        // Step 1: Integer part — traverse dividend digit by digit using linked list
        Node pA = a.head;   // start from most significant digit
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
 
            pA = pA.next;   // move to next digit of dividend
        }

        // Step 2: Decimal part — keep multiplying remainder by 10
        if (!current.isZero() && decimalPlaces > 0) {
            quotient.append(".");
            for (int i = 0; i < decimalPlaces; i++) {
                // Shift remainder left by 1 (multiply by 10)
                current.appendDigit(0);
 
                // Find largest digit such that digit * b <= current
                int count = findMultiple(current, b);
                quotient.append(count);
 
                // Subtract progressively
                LargeNumber multiple = multiplyByDigit(b, count);
                current = subtract(current, multiple);
 
                if (current.isZero()) break; // exact division, no more remainder
            }
        }
 
        // Clean up leading zeros from integer part
        String result = quotient.toString();
        int dotIndex = result.indexOf('.');
        String intPart = (dotIndex == -1) ? result : result.substring(0, dotIndex);
        String decPart = (dotIndex == -1) ? "" : result.substring(dotIndex);
 
        intPart = intPart.replaceFirst("^0+(?!$)", "");
        if (intPart.isEmpty()) intPart = "0";

        return intPart + decPart;
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
