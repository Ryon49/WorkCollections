// Weifeng Dong
// weifengd

// Define all helper functions for hw1 in this file

#include "hw1.h"

bool compareValue(int n, char *s) {
    char result[100];
	sprintf(result, "%d", n);
	return strcmp(result, s) == 0;
}

bool isWhitespace(char c) {
    return c == 0x20 || c == 0x9 || c == 0xD || c == 0xB || c == 0xC;
}

bool isNumber(char c) {
    return c >= 0x30 && c <= 0x39;
}

bool isLetter(char c) {
    return (c >= 0x41 && c <= 0x5A) || (c >= 0x61 && c <= 0x7A);
}

bool isSymbol(char c) {
    // check in range [0x21, 0x7E]
    return (c >= 0x21 && c <= 0x7E) 
                && !isNumber(c)
                && !isLetter(c);
}

void print_char(char c, bool redirect) {
	// has 2> stderr.txt and -O
	if (redirect) {
    	fprintf(stderr, "%c", c);
	}
}

void print_space(int num_space) {
	int j;
	for (j = 0; j < num_space; j++) {
		print_char(' ', true);
	}
}

void print_dummy() {
    printf("1");
}

void print_error() {
	printf("Error code: 1\n");
}