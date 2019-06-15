/**
 * Do not submit your assignment with a main function in this file.
 * If you submit with a main function in this file, you will get a zero.
 * If you want to make helper functions, put them in helpers.c
 */
#include "icsmm.h"
#include "helpers.h"
#include <errno.h>
#include <stdio.h>
#include <stdlib.h>

const int PAGE_SIZE = 4096;

ics_free_header *freelist_head = NULL;
ics_free_header *freelist_next = NULL;

void *prologue = NULL;

bool initialized = false;

void ics_init();
void extendHeap();
void coalesceAfter(void *);
void coalesceBefore(void *);

void *ics_malloc(size_t size) { 

    if (!initialized) {
        ics_init();
        initialized = true;
    }

    if (size <= 0 || size > 16352) {
        errno = EINVAL;
        return NULL;
    }

    if (freelist_next == NULL) {
        freelist_next = freelist_head;
    }

    int block_size = 16 + alignSize(size);
    ics_free_header *requestHeader;
    while ((freelist_head == NULL) || (requestHeader = find_fit(block_size)) == NULL) {
        if (ics_inc_brk() == (void *)-1) {
            errno = ENOMEM;
            return NULL;
        }
        extendHeap();
    }

    int orig_block_size = requestHeader->header.block_size;

    bool split = 1;
    if (orig_block_size - block_size < 32) {
        block_size = orig_block_size;
        split = 0;
    }

    // set this block
    requestHeader->header.block_size = block_size + 1;
    requestHeader->header.unused = 0xaaaaaaaa;
    requestHeader->header.requested_size = size;

    ics_footer *requestFooter = (ics_footer *)((void *)requestHeader + block_size - 8);

    requestFooter->block_size = block_size + 1;
    requestFooter->unused = 0xffffffffffff;

    if (split) {
        // split, set next free block
        ics_free_header *nextBlockHeader = (ics_free_header *)((void *)requestFooter + 8);

        nextBlockHeader->next = requestHeader->next;
        nextBlockHeader->prev = requestHeader->prev;

        nextBlockHeader->header.block_size = orig_block_size - block_size;
        nextBlockHeader->header.unused = 0xaaaaaaaa;
        nextBlockHeader->header.requested_size = 0;

        ics_footer *nextBlockFooter = (ics_footer *)((void *)nextBlockHeader + nextBlockHeader->header.block_size - 8);
        nextBlockFooter->block_size = orig_block_size - block_size;

        nextBlockHeader->next = freelist_head;
        freelist_head->prev = nextBlockHeader;
        freelist_head = nextBlockHeader;
    } else {
        // no split

        // first block of freelist_head is now allocated, move head to next
        if (freelist_head == requestHeader) {
            freelist_head = freelist_head->next;
        }
    }

    // adjust next and prev pointers
    if (requestHeader->prev != NULL) {
        requestHeader->prev->next = freelist_next->next;
    }
    if (freelist_next->next != NULL) {
        freelist_next->next->prev = requestHeader->prev;
    }

    // move freelist_next to next, reset to freelist_head if freelist_next reaches the end
    freelist_next = freelist_next->next;
    if (freelist_next == NULL) {
        freelist_next = freelist_head;
    }

    return ((void *)requestHeader + 8); 
}

void *ics_realloc(void *ptr, size_t size) {  
    if (ptr == NULL) {
        errno = EINVAL;
        return NULL;
    }
    if (size < 0 || size > 16352) {
        errno = EINVAL;
        return NULL;
    }
    if (size == 0) {
        ics_free(ptr);
        return NULL;
    } 

    ics_free_header *curheader = (ics_free_header *)(ptr - 8);
    // check if ptr is 16 byte aligned
    if (((void *)curheader - prologue) % 16 != 0) {
        errno = EINVAL;
        return NULL;
    }

    // malloc a new pointer;
    void *newPayload = ics_malloc(size);

    if (newPayload == NULL) {
        errno = ENOMEM;
        return NULL;
    }

    int copy = size;
    if (curheader->header.requested_size < size) {
        copy = curheader->header.requested_size;
    }
    
    // copy the value of current ptr value to the next ptr
    int i = 0;
    for (i = 0; i < copy; ++i) {
        *((char *)newPayload + i) = *((char *)ptr + i);
    }
    // mark this ptr as free
    ics_free(ptr);
    return newPayload;
}

int ics_free(void *ptr) {
    if (ptr == NULL) {
        errno = EINVAL;
        return -1;
    }

    // 1. check if ptr is within the heap
    if (ptr < prologue || ptr > ((void*)ics_get_brk() - 8)) {
        errno = EINVAL;
        return -1;
    }
    ics_free_header *freeHeader = (ics_free_header *)(ptr - 8);

    // 1. check if header is 16 byte aligned
    if (((void *)freeHeader - prologue) % 16 != 0) {
        errno = EINVAL;
        return -1;
    }

    // 2. check if header's unused field is set to 0xaaaaaaaa
    if (freeHeader->header.unused != 0xaaaaaaaa) {
        errno = EINVAL;
        return -1;
    }

    ics_footer *freeFooter = (ics_footer *)((void *)freeHeader + freeHeader->header.block_size - 9);

    // 3. check if footer's unused field is set to  0xffffffffffff
    if (freeFooter->unused != 0xffffffffffff) {
        errno = EINVAL;
        return -1;
    }

    // 4. check block_size are equal for header and footer
    if (freeHeader->header.block_size != freeFooter->block_size) {
        errno = EINVAL;
        return -1;
    }

    // 5. ptr's request size is less than the block_size;
    if (freeHeader->header.requested_size >= freeHeader->header.block_size) {
        errno = EINVAL;
        return -1;
    }

    // 6. allocated bit is set in both header and footer's block_size
    if ((freeHeader->header.block_size & 1) == 0 || (freeFooter->block_size & 1) == 0) {
        errno = EINVAL;
        return -1;
    }

    // set allocated bit to 0
    freeHeader->header.block_size -= 1;
    freeFooter->block_size -= 1;
    
    // add this block to header
    if (freelist_head != NULL) {
        freelist_head->prev = freeHeader;
    }
    freeHeader->next = freelist_head;
    freeHeader->prev = NULL;
    freelist_head = freeHeader;

    if (freelist_next == NULL) {
        freelist_next = freelist_head;
    }

    coalesceAfter(freeHeader);
    coalesceBefore(freeHeader);

    return 0;
}

void coalesceAfter(void *ptr) {
    ics_free_header *curHeader = (ics_free_header *)ptr;

    if (ptr + curHeader->header.block_size > ics_get_brk() - 16) {
        return;
    }
    ics_free_header *nextHeader = ((void *)curHeader + curHeader->header.block_size);

    if ((nextHeader->header.block_size & 1) == 0 && nextHeader->header.unused == 0xaaaaaaaa) {
        ics_footer *nextFooter = (ics_footer *)((void *)nextHeader + nextHeader->header.block_size - 8);

        if ((nextFooter->block_size & 1) == 0 && nextFooter->unused == 0xffffffffffff) {

            // both checked, found a block that can coalesce
            if (nextHeader->prev != NULL) {
                nextHeader->prev->next = nextHeader->next;
            }
            if (nextHeader->next != NULL) {
                nextHeader->next->prev = nextHeader->prev;
            }

            curHeader->header.block_size += nextHeader->header.block_size;

            ics_footer *curFooter = (ics_footer *)((void *)curHeader + curHeader->header.block_size - 8);
            curFooter->block_size = curHeader->header.block_size;

            if (freelist_next == nextHeader) {
                freelist_next = freelist_head;
            }
        }
    }
}

void coalesceBefore(void *ptr) {
    if (ptr - 8 < prologue) {
        return;
    }

    ics_free_header *curHeader = (ics_free_header *)ptr;
    ics_footer *prevFooter = ((void *)curHeader - 8);

    if ((prevFooter->block_size & 1) == 0  && prevFooter->unused == 0xffffffffffff) { 
        ics_free_header *prevHeader = ((void *)prevFooter - prevFooter->block_size + 8);
        if ((prevHeader->header.block_size & 1) == 0 && prevHeader->header.unused == 0xaaaaaaaa) {
            // both checked, found a block that can coalesce
            if (prevHeader->prev != NULL) {
                prevHeader->prev->next = prevHeader->next;
            }
            if (prevHeader->next != NULL) {
                prevHeader->next->prev = prevHeader->prev;
            }

            prevHeader->header.block_size += curHeader->header.block_size;
            prevFooter = (ics_footer *)((void *)prevHeader + prevHeader->header.block_size - 8);
            prevFooter->block_size = prevHeader->header.block_size;
            
            prevHeader->prev = NULL;
            prevHeader->next = curHeader->next;
            freelist_head = prevHeader;

            if (freelist_next == prevHeader) {
                freelist_next = freelist_head;
            }
        }        
    }
}

void ics_init() {
    prologue = ics_get_brk() + 8;

    freelist_head = (ics_free_header *)(ics_get_brk() + 8);
    freelist_next = freelist_head; 

    // expand access range
    ics_inc_brk();

    // initilize the list
    freelist_head->next = NULL;
    freelist_head->prev = NULL;

    // initialize the header
    freelist_head->header.block_size = 4080;
    freelist_head->header.unused = 0xaaaaaaaa;
    freelist_head->header.requested_size = 0;

    // initialize the footer
    ics_footer *footer = (ics_footer *)(ics_get_brk() - 16);
    footer->block_size = 4080;
    footer->unused = 0xffffffffffff;

    prologue = freelist_head;
}

void extendHeap() {
    // adjust last header
    ics_free_header *newHeader = (ics_free_header *)(ics_get_brk() - 8 - PAGE_SIZE);
    newHeader->header.block_size = PAGE_SIZE;
    newHeader->header.unused = 0xaaaaaaaa;
    newHeader->header.requested_size = 0;

    ics_footer *footer = (ics_footer *)(ics_get_brk() - 16);
    footer->block_size = PAGE_SIZE;
    footer->unused = 0xffffffffffff;

    // add this block to header
    if (freelist_head != NULL) {
        freelist_head->prev = newHeader;
    }
    newHeader->next = freelist_head;
    newHeader->prev = NULL;
    freelist_head = newHeader;

    coalesceBefore(newHeader);

    // set next pointer to head of free list
    freelist_next = freelist_head;
}