// string helper function

int _strlen(char *s) {
    int size = 0;
    while (*(s + size)) {
        size++;
    }
    return size;
}

void _strcat(char *to, char *s, int *size, int len) {
    if (len == 0) {
        while (*s) {
            *(to + *size) = *s;
            ++(*size);
            ++s;
        }
    } else {
        while (len > 0) {
            *(to + *size) = *s;
            ++(*size);
            ++s;
            len--;
        }
    }
}

void _strcpy(char *dst, char *src) {
    while((*(dst++) = *(src++)));
}

// find first n, where *(s + n) == c
// return -1 if not found
int _strtok(char *s, char c) {
    int n = 0;
    while (*s) {
        if (*(s + n) == c) {
            return n;
        }
        ++n;
    }
    return -1;
}

// compare up to len characters, and check if they are the same
int _strcmp(char* s1, char *s2, int len) {
    int i;
    for (i = 0; i < len; i++) {
        if (!(*s1 && *s2)) {
            return 0;
        } else if (*(s1 + i) != *(s2 + i)) {
            return 0;
        }
    }
    return 1;
}

// Define all helper functions for hw1 in this file

int isValid(char c) {
    return c >= ' ' && c <= 'Z';
}

int isLower(char c) {
    return c >= 'a' && c <= 'z'; 
}

int isUpper(char c) {
    return c >= 'A' && c <= 'Z';
}

int isLetter(char c) {
    return isLower(c) || isUpper(c);
}

int isAscii(char c) {
    if (c >= 0 && c <= 127) {
        return 1;
    }
    return 0;
}

char toUpper(char c) {
    if (isLower(c)) {
        return c - ('a' - 'A');
    }
    return c;
}

int toIndex(char c) {
    return c - '!';
}

char toChar(int i) {
    return i + '!';
}

int max(int a, int b) {
    return a > b ? a : b;
}