
//Arithmetic class that performs addition, subtraction,
//multiplication, and division on LargeNumber objects.

public class Arithmetic {

    // -------------------------------------------------------
    // ADDITION: adds two large numbers digit by digit
    // Works from tail (least significant) to head (most significant)
    // -------------------------------------------------------
    public static LargeNumber add(LargeNumber a, LargeNumber b) {
        // Both positive: plain addition
        if (!a.isNegative() && !b.isNegative()) {
            return absAdd(a, b);
        }
        // Both negative: add magnitudes, mark negative
        if (a.isNegative() && b.isNegative()) {
            LargeNumber result = absAdd(a, b);
            if (!result.isZero())
                result.negative = true;
            return result;
        }
        // Different signs: subtract smaller magnitude from larger
        if (!a.isNegative() && b.isNegative()) {
            // (+a) + (-b) = a - b
            if (a.compareTo(b) >= 0) {
                return absSubtract(a, b); // result >= 0
            } else {
                LargeNumber result = absSubtract(b, a); // result < 0
                if (!result.isZero())
                    result.negative = true;
                return result;
            }
        }
        // (-a) + (+b) = b - a
        if (b.compareTo(a) >= 0) {
            return absSubtract(b, a); // result >= 0
        } else {
            LargeNumber result = absSubtract(a, b); // result < 0
            if (!result.isZero())
                result.negative = true;
            return result;
        }
    }

    // -------------------------------------------------------
    // SUBTRACTION: subtracts b from a
    // -------------------------------------------------------
    public static LargeNumber subtract(LargeNumber a, LargeNumber b) {
        // Flip b's sign and reuse signed add()
        LargeNumber negB = new LargeNumber(b); // copy constructor — don't mutate b
        if (!negB.isZero())
            negB.negative = !negB.negative;
        return add(a, negB);
    }

    // -------------------------------------------------------
    // MULTIPLICATION: multiplies a by b digit by digit
    // -------------------------------------------------------
    public static LargeNumber multiply(LargeNumber a, LargeNumber b) {
        // Short-circuit: anything * 0 = 0
        if (a.isZero() || b.isZero()) {
            return new LargeNumber("0");
        }

        // Determine result sign before computing magnitude
        boolean resultNegative = a.isNegative() != b.isNegative();

        int lenA = a.getSize(); // FIX 1: use getter
        int lenB = b.getSize(); // FIX 1: use getter

        int[] resultDigits = new int[lenA + lenB];

        // Traverse a from least significant (tail) to most significant (head)
        Node pA = a.tail;
        for (int i = lenA - 1; i >= 0; i--) {
            Node pB = b.tail;
            for (int j = lenB - 1; j >= 0; j--) {
                int mul = pA.digit * pB.digit;
                int pos1 = i + j;
                int pos2 = i + j + 1;

                int sum = mul + resultDigits[pos2];
                resultDigits[pos2] = sum % 10;
                resultDigits[pos1] += sum / 10;

                pB = pB.prev;
            }
            pA = pA.prev;
        }

        // Build result LargeNumber, skipping leading zeros
        LargeNumber result = new LargeNumber();
        int start = 0;
        while (start < resultDigits.length - 1 && resultDigits[start] == 0)
            start++;
        for (int i = start; i < resultDigits.length; i++) {
            result.appendDigit(resultDigits[i]);
        }

        if (resultNegative && !result.isZero())
            result.negative = true;
        return result;
    }

    // -------------------------------------------------------
    // DIVISION: divides a by b, returns quotient with decimal places
    // -------------------------------------------------------
    public static String divide(LargeNumber a, LargeNumber b, int decimalPlaces) {
        if (b.isZero()) {
            return "Error: Division by zero";
        }

        boolean resultNegative = a.isNegative() != b.isNegative();

        StringBuilder quotient = new StringBuilder();
        LargeNumber current = new LargeNumber("0");

        // ── Integer part ──────────────────────────────────────────────────
        Node pA = a.head;
        while (pA != null) {
            if (current.isZero()) {
                current = new LargeNumber(String.valueOf(pA.digit));
            } else {
                current.appendDigit(pA.digit);
            }
            int count = findMultiple(current, b);
            quotient.append(count);
            current = absSubtract(current, multiplyByDigit(b, count)); // FIX 2
            pA = pA.next;
        }

        // ── Decimal part with half-up rounding ────────────────────────────
        int roundingDigit = 0;
        if (!current.isZero() && decimalPlaces > 0) {
            quotient.append(".");
            for (int i = 0; i <= decimalPlaces; i++) {
                current.appendDigit(0);
                int count = findMultiple(current, b);
                if (i == decimalPlaces) {
                    roundingDigit = count;
                } else {
                    quotient.append(count);
                }
                current = absSubtract(current, multiplyByDigit(b, count)); // FIX 2
                if (current.isZero() && i < decimalPlaces)
                    break;
            }
        }

        // Apply half-up rounding
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

        // Prepend sign if negative and result is not zero
        String finalResult = intPart + decPart;
        if (resultNegative && !finalResult.equals("0")) {
            finalResult = "-" + finalResult;
        }
        return finalResult;
    }

    // ==========================================================
    // UNSIGNED (absolute value) helpers — used internally
    // ==========================================================

    /**
     * Adds the absolute values of two LargeNumbers (ignores sign).
     * Traverses both lists from tail to head with carry tracking.
     */
    static LargeNumber absAdd(LargeNumber a, LargeNumber b) {
        LargeNumber result = new LargeNumber();
        Node pA = a.tail;
        Node pB = b.tail;
        int carry = 0;

        while (pA != null || pB != null || carry != 0) {
            int digitA = (pA != null) ? pA.digit : 0;
            int digitB = (pB != null) ? pB.digit : 0;
            int sum = digitA + digitB + carry;
            carry = sum / 10;
            result.prependDigit(sum % 10);
            if (pA != null)
                pA = pA.prev;
            if (pB != null)
                pB = pB.prev;
        }

        if (result.head == null)
            result.appendDigit(0);
        return result;
    }

    /**
     * Subtracts the absolute value of b from a (assumes |a| >= |b|).
     * Traverses from tail to head with borrow tracking.
     */
    static LargeNumber absSubtract(LargeNumber a, LargeNumber b) {
        if (a.compareTo(b) < 0)
            return new LargeNumber("0");

        LargeNumber result = new LargeNumber();
        Node pA = a.tail;
        Node pB = b.tail;
        int borrow = 0;

        while (pA != null) {
            int digitA = pA.digit - borrow;
            int digitB = (pB != null) ? pB.digit : 0;
            if (digitA < digitB) {
                digitA += 10;
                borrow = 1;
            } else {
                borrow = 0;
            }
            result.prependDigit(digitA - digitB);
            pA = pA.prev;
            if (pB != null)
                pB = pB.prev;
        }

        result.removeLeadingZeros();
        return result;
    }

    /**
     * Multiplies a LargeNumber by a single digit (0-9).
     * Traverses from tail to head with carry tracking. O(n).
     */
    private static LargeNumber multiplyByDigit(LargeNumber a, int digit) {
        if (digit == 0)
            return new LargeNumber("0");
        LargeNumber result = new LargeNumber();
        Node pA = a.tail;
        int carry = 0;
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

    /**
     * Finds the largest digit q (0-9) such that |b| * q <= |current|.
     */
    private static int findMultiple(LargeNumber current, LargeNumber b) {
        for (int i = 9; i >= 1; i--) {
            if (current.compareTo(multiplyByDigit(b, i)) >= 0)
                return i;
        }
        return 0;
    }

    /**
     * Half-up rounding: increments the last digit of a decimal string,
     * propagating carry leftward if needed.
     * Example: "3.999" -> "4.0"
     */
    private static String applyRounding(String s) {
        char[] digits = s.toCharArray();
        int i = digits.length - 1;
        while (i >= 0) {
            if (digits[i] == '.') {
                i--;
                continue;
            }
            if (digits[i] < '9') {
                digits[i]++;
                return new String(digits);
            } else {
                digits[i] = '0';
                i--;
            }
        }
        return "1" + new String(digits);
    }
}