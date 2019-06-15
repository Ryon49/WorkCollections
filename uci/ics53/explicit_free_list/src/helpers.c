#include "helpers.h"

/* Helper function definitions go here */
size_t alignSize(size_t size) {
    if (size % 16 == 0) {
        return size;
    }

    return size / 16 * 16 + 16;
}

bool fit(ics_header header, size_t size) {
    // printf("size: %lu\tblock_size: %d\n", size, header.block_size);
    // printf("Result 1: %d\n", (header.block_size & 1) == 0);
    return (header.block_size & 1) == 0 && header.block_size >= size;
}

ics_free_header *find_fit(size_t size) {
    ics_free_header *start = freelist_next;
    bool cycle = 0;

    while (cycle == 0 || freelist_next != start) {
        int canFit = fit(freelist_next->header, size);
        if (canFit == 1) {
            return freelist_next;
        }
        freelist_next = freelist_next->next;
        if (freelist_next == NULL) {
            cycle = 1;
            freelist_next = freelist_head;
        }
    }

    return NULL;
}

ics_free_header *find_end() {
    ics_free_header *ptr = freelist_head;
    while (ptr->next != NULL) {
        ptr = ptr->next;
    }
    return ptr;
}