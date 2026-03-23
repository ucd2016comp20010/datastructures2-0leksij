package project20280.exercises;
import project20280.list.SinglyLinkedList;

public class Wk2 {

//Q6 Very shortly, Singly linked lists have a linear structure with a null-terminated end,
// while circularly linked lists form a continuous loop.

//Q7 linked lists are preferred over arrayswhen we need frequent insertions and deletions,
// dynamic resizing, memory efficiency, constant-time operations at the beginning or end of the collection,
// and flexibility in data structure design.
// Though, arrays on their part are way more suitable when time efficiency is a number 1 priotity


//Q8
// in a multimedia application, a circularly linked list can be employed to manage a pool of audio buffers.
// As data is processed, buffers can be reused efficiently by rotating the circular list

    //In turn-based games, such as board games or strategy games,
// a circularly linked list can efficiently manage player turns.
// Each node in the list represents a player,
// and the circular structure ensures that turns cycle through all players continuously.



    public static void main(String[] args) {
        SinglyLinkedList<Integer> l1 = new SinglyLinkedList<>();
        l1.addLast(2); l1.addLast(6); l1.addLast(20); l1.addLast(24);

        SinglyLinkedList<Integer> l2 = new SinglyLinkedList<>();
        l2.addLast(1); l2.addLast(3); l2.addLast(5); l2.addLast(8);
        l2.addLast(12); l2.addLast(19); l2.addLast(25);

        // Merge
        SinglyLinkedList<Integer> merged = SinglyLinkedList.sortedMerge(l1, l2);
        System.out.println("Merged: " + merged);

        // Reverse
        SinglyLinkedList.reverse(merged);
        System.out.println("Reversed: " + merged);

        // Clone
        SinglyLinkedList<Integer> cloned = SinglyLinkedList.cloneList(merged);
        System.out.println("Cloned: " + cloned);
    }


}
