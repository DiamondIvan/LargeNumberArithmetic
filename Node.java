/**
 * Node class for the Doubly Linked List.
 * Each node stores a single digit (0-9).
 */
public class Node {
    int digit;      // stores one digit
    Node prev;      // pointer to previous node
    Node next;      // pointer to next node

    public Node(int digit) {
        this.digit = digit;
        this.prev = null;
        this.next = null;
    }
}
