/**
 * LargeNumber class using a Doubly Linked List.
 * Digits are stored from MOST significant (head) to LEAST significant (tail).
 * Example: "123" -> head:[1] <-> [2] <-> [3]:tail
 */
public class LargeNumber {
    Node head;  // most significant digit
    Node tail;  // least significant digit
    int size;

    public LargeNumber() {
        head = null;
        tail = null;
        size = 0;
    }

    // Build a LargeNumber from a String (e.g. "12345")
    public LargeNumber(String number) {
        head = null;
        tail = null;
        size = 0;
        for (char c : number.toCharArray()) {
            if (Character.isDigit(c)) {
                appendDigit(c - '0');
            }
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

    // Convert the linked list back to a String for display
    @Override
    public String toString() {
        if (head == null) return "0";
        StringBuilder sb = new StringBuilder();
        Node current = head;
        while (current != null) {
            sb.append(current.digit);
            current = current.next;
        }
        return sb.toString();
    }

    // Compare two LargeNumbers: returns 1 if this > other, -1 if this < other, 0 if equal
    public int compareTo(LargeNumber other) {
        String a = this.toString();
        String b = other.toString();

        if (a.length() != b.length()) {
            return a.length() > b.length() ? 1 : -1;
        }
        // Same length: compare digit by digit
        for (int i = 0; i < a.length(); i++) {
            if (a.charAt(i) != b.charAt(i)) {
                return a.charAt(i) > b.charAt(i) ? 1 : -1;
            }
        }
        return 0; // equal
    }

    // Check if this number is zero
    public boolean isZero() {
        return head != null && head == tail && head.digit == 0;
    }
}
