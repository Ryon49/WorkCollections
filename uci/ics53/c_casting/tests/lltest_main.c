#include "linkedList.h"
#include "hw3_p1.h"
// No errors in this file

int main(int argc, char* argv[]) {
    int start = 0;
    int end = 20;

    // This condition cannot be violated or the test will have undefined
    // behavior
    assert(end - start > 15);

    int i;

    // Create and initialize integer linked list
    List_t* list = malloc(sizeof(List_t));
    list->length = 0;
    list->comparator = intComparator;

    // Output: List is empty
    printList(list, INT_MODE);
    printf("\n");

    // Insert first node. Why does start need to be casted?
    insertFront(list, (void*)start);

    // Output: 0
    printList(list, INT_MODE);
    printf("\n");

    // Inserting the remaining nodes at the end
    for (i = start + 1; i <= end; i++) {
        insertRear(list, (void*)i);
    }

    // Output: 0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20
    printList(list, INT_MODE);
    printf("\n");

    /*remove 5 elements from front*/
    for (i = 0; i < 5; i++) {
        removeFront(list);
    }

    // Output: 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20
    printList(list, INT_MODE);
    printf("\n");

    /*insert back the 5 elements at the front*/
    for (i = start + 4; i >= start; i--) {
        insertFront(list, (void*)i);
    }

    // Output: 0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20
    printList(list, INT_MODE);
    printf("\n");

    /*remove 5 elements from rear*/
    for (i = 0; i < 5; i++) {
        removeRear(list);
    }

    // Output: 0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15
    printList(list, INT_MODE);
    printf("\n");

    /*remove 5 elements by index*/
    for (i = 8; i <= 11; i++) {
        removeByIndex(list, i);
    }

    // Output: 0 1 2 3 4 5 6 7 9 11 13 15
    printList(list, INT_MODE);
    printf("\n");

    /*inserting the nodes inorder*/
    for (i = start; i <= (end); i++) {
        insertInOrder(list, (void*)i);
    }

    // Output: 0 0 1 1 2 2 3 3 4 4 5 5 6 6 7 7 8 9 9 10 11 11 12 13 13 14 15 15 16 17 18 19 20
    printList(list, INT_MODE);
    printf("\n");

    /*delete the list*/
    deleteList(list);

    // Ouput: List is empty
    printList(list, INT_MODE);
    printf("\n");

    free(list);

    return 0;
}
