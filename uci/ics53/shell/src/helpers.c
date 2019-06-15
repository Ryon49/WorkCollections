// Your helper functions need to be here.

#include "helpers.h"
#include <stdio.h>
#include <string.h>
#include <shell_util.h> 

int timeComparator(void* entry1, void* entry2) {
    int compare = ((ProcessEntry_t *)entry1)->seconds - ((ProcessEntry_t *)entry2)->seconds;
    if (compare == 0) {
        return 0;
    }
    return compare < 0 ? -1 : 1;
}
    
void print_command(char *args[], int size) {
    int i = 0;
    for (i = 0; i < size; i++) {
        printf("%s ", args[i]);
    }
    printf("\n");
}

int isNumber(char *s) {
    int len = strlen(s);
    int i = 0;
    for (i = 0; i < len; i++) {
        if (*(s + i) > '9' || *(s + i) < '0') {
            return 0;
        }
    }
    return 1;
}