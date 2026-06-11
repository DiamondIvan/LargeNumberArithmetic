/**
 * LargeNumber class using a Doubly Linked List.
 * Digits are stored from MOST significant (head) to LEAST significant (tail).
 * Example: "123" -> head:[1] <-> [2] <-> [3]:tail
 *
 * Supports an optional negative flag for signed arithmetic.
 */
public class LargeNumber {
    Node head; // most significant digit
    Node tail; // least significant digit
    int size;
    boolean negative; // true if this number is negative

    public LargeNumber() {
        head = null;
        tail = null;
        size = 0;
        negative = false;
    }

    /**
     * Build a LargeNumber from a String.
     * Accepts an optional leading '-' for negative numbers.
     * Throws IllegalArgumentException for null, blank, or non-numeric input.
     */
    public LargeNumber(String number) {
        head = null;
        tail = null;
        size = 0;
        negative = false;

        // --- Input validation ---
        if (number == null) {
            throw new IllegalArgumentException("Input cannot be null.");
        }
        String trimmed = number.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Input cannot be empty.");
        }

        // Strip optional leading '-'
        int startIndex = 0;
        if (trimmed.charAt(0) == '-') {
            negative = true;
            startIndex = 1;
            if (trimmed.length() == 1) {
                throw new IllegalArgumentException("Input '" + number + "' is not a valid number.");
            }
        }

        // Validate that every remaining character is a digit
        for (int i = startIndex; i < trimmed.length(); i++) {
            if (!Character.isDigit(trimmed.charAt(i))) {
                throw new IllegalArgumentException(
                        "Input '" + number + "' contains non-numeric character: '" + trimmed.charAt(i) + "'");
            }
        }

        // Strip leading zeros before building nodes ──────────
        // Advance startIndex past any leading '0' characters,
        // but always keep at least the last digit (e.g. "0" stays as "0")
        while (startIndex < trimmed.length() - 1 && trimmed.charAt(startIndex) == '0') {
            startIndex++;
        }

        // Build the DLL from validated digits
        for (int i = startIndex; i < trimmed.length(); i++) {
            appendDigit(trimmed.charAt(i) - '0');
        }

        // "-0" should not be considered negative
        if (isZero())
            negative = false;
    }

    // Returns true if this number is negative
    public boolean isNegative() {
        return negative;
    }

    public LargeNumber(LargeNumber other) {
        head     = null;
        tail     = null;
        size     = 0;
        negative = other.negative;
        Node cur = other.head;
        while (cur != null) {
            appendDigit(cur.digit);
            cur = cur.next;
        }
    }

    // Add a digit to the END (least significant side)
    public void appendDigit(int digit) {
        Node newNode = new Node(digit);
        if (tail == null) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }
        size++;
    }

    // Add a digit to the FRONT (most significant side)
    public void prependDigit(int digit) {
        Node newNode = new Node(digit);
        if (head == null) {
            head = newNode;
            tail = newNode;
        } else {
            newNode.next = head;
            head.prev = newNode;
            head = newNode;
        }
        size++;
    }

    // Remove leading zeros (e.g. "007" -> "7"), but keep at least one digit
    public void removeLeadingZeros() {
        while (head != null && head.digit == 0 && head != tail) {
            head = head.next;
            head.prev = null;
            size--;
        }
    }

    public int getSize() {
        return size;
    }

    // Convert the linked list back to a String for display
    @Override
    public String toString() {
        if (head == null)
            return "0";
        StringBuilder sb = new StringBuilder();
        if (negative) sb.append('-');
        Node current = head;
        while (current != null) {
            sb.append(current.digit);
            current = current.next;
        }
        return sb.toString();
    }

    // Compare two LargeNumbers: returns 1 if |this| > |other|, -1 if |this| < |other|, 0 if equal
    // Evaluates Absolute Value (ignores negative flag).
    public int compareTo(LargeNumber other) {
        if (this.size != other.size) {
            return this.size > other.size ? 1 : -1;
        }
        
        Node pA = this.head;
        Node pB = other.head;
        
        while (pA != null && pB != null) {
            if (pA.digit != pB.digit) {
                return pA.digit > pB.digit ? 1 : -1;
            }
            pA = pA.next;
            pB = pB.next;
        }
        return 0; // equal
    }

    // Returns digits without sign — used internally by compareTo() and isZero()
    public String toAbsString() {
        if (head == null) return "0";
        StringBuilder sb      = new StringBuilder();
        Node          current = head;
        while (current != null) {
            sb.append(current.digit);
            current = current.next;
        }
        return sb.toString();
    }

    // Check if this number is zero (handles both empty list and single zero digit)
    public boolean isZero() {
        return toAbsString().equals("0");
    }

    // Convenience comparison helpers (unsigned — ignores negative flag)
    public boolean isGreaterThan(LargeNumber other) {
        return this.compareTo(other) > 0;
    }

    public boolean isLessThan(LargeNumber other) {
        return this.compareTo(other) < 0;
    }

    public boolean isEqualTo(LargeNumber other) {
        return this.compareTo(other) == 0;
    }
}
