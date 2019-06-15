// Weifeng Dong
// weifengd

#include "morsecode.h"
#include "hw2.h"

char* MorseCode[] = {MorseExclamation, MorseDblQoute, MorseHashtag, Morse$, MorsePercent, MorseAmp, MorseSglQoute, MorseOParen, MorseCParen, MorseStar, MorsePlus, MorseComma, MorseDash, MorsePeriod, MorseFSlash, Morse0, Morse1, Morse2, Morse3, Morse4, Morse5, Morse6, Morse7, Morse8, Morse9, MorseColon, MorseSemiColon, MorseLT, MorseEQ, MorseGT, MorseQuestion, MorseAt, MorseA, MorseB, MorseC, MorseD, MorseE, MorseF, MorseG, MorseH, MorseI, MorseJ, MorseK, MorseL, MorseM, MorseN, MorseO, MorseP, MorseQ, MorseR, MorseS, MorseT, MorseU, MorseV, MorseW, MorseX, MorseY, MorseZ};

/* Part 1 Functions */
int toMorse(FILE *instream, char **mcmsg_ptr){
    int size = 0;
    
    int asciiError = 0;
    // init, +1 for '\0' at the end
    char *ptr = malloc(sizeof(char) * size);
    if (ptr == NULL) {
        *mcmsg_ptr = NULL;
        return -1;
    }

    char c;
    int startSpace = 1;
    int space = 0;

    while ((c = getc(instream)) != EOF) {
        if (isAscii(c) == 0) {
            asciiError = 1;
            // printf("%d\n", c);
            break;
        }

        c = toUpper(c);
		if (!isValid(c)) {
            // printf("space: %d, ascii: %d\n", space, c);
            space = 0;
            continue;
		}
        if (c == ' ') {
            if (space == 0) {
                space = 1;
            }
        } else {
            if (space == 1) {
                if (startSpace == 1) {
                    ptr = realloc(ptr, sizeof(char) * (size + 2));
                    if (ptr == NULL) {
                        *mcmsg_ptr = NULL;
                        return -1;
                    }
                    _strcat(ptr, "xx", &size, 2);
                    startSpace = 0;
                } else {
                    // append 'x' ptr
                    ptr = realloc(ptr, sizeof(char) * (size + 1));
                    if (ptr == NULL) {
                        return -1;
                    }
                    _strcat(ptr, "x", &size, 1);
                }
                space = 0;
            }
            startSpace = 0;
            // // append *(MorseCode + toIndex(c)] to ptr
            char *seq = *(MorseCode + toIndex(c));
            // printf("%c\t%s\n", c, *(MorseCode + toIndex(c)));
            int seqLen = _strlen(seq);
            if (seqLen > 0) {
                ptr = realloc(ptr, sizeof(char) * (size + seqLen + 1));
                if (ptr == NULL) {
                    *mcmsg_ptr = NULL;
                    return -1;
                }
                _strcat(ptr, seq, &size, 0);
                _strcat(ptr, "x", &size, 1);
            }

            // printf("%d\t%s\n", toIndex(c), seq);
        }
    }
        // append 'x' to ptr
        ptr = realloc(ptr, sizeof(char) * (size + 1));
        if (ptr == NULL) {
            *mcmsg_ptr = NULL;
            return -1;
        }
        _strcat(ptr, "x", &size, 1);
    // *(ptr + size++) = '\0';
    *mcmsg_ptr = ptr;
    if (asciiError == 1) {
        return 0;
    } else {
        return 1;
    }
}

void createKey(char* keyphrase, char* key){
    int *nums = malloc(sizeof(int) * 26);
    if (nums == NULL) {
        exit(4);
    }
    int i;
    for (i = 0; i < 26; i++){
        *(nums + i) = 0;
    }

    int at = 0;    
    for (i = 0; i < _strlen(keyphrase); i++) {
        if (isLetter(*(keyphrase + i))) {
            char c = toUpper(*(keyphrase + i));
            int index = c - 'A';
            if (*(nums + index) == 0) {
                *(nums + index) = 1;
                *(key + at++) = c;
            }
        }
    }

    for (i = 0; i < 26; i++) {
        if (*(nums + i) == 0) {
            *(key + at++) = i + 'A';
        }
    }
    *(key + 26) = '\0';
}

char morseToKey(char* mcmsg, char* key){
    char* FMCarray = ".....-..x.-..--.-x.x..x-.xx-..-.--.x--.-----x-x.-x--xxx..x.-x.xx-.x--x-xxx.xx-";

    if (_strlen(mcmsg) < 3) {
        return -1;
    }
    // validate the first 3 characters of mcmsg
    int i, j;
    for (i = 0; i < 3; i++) {
        char c = *(mcmsg + i);
        if (!(c == '-' || c == '.' || c == 'x')) {
            return -1;
        }
    }

    int len = _strlen(FMCarray);
    for (i = 0, j = 0; i < len; i+=3, ++j) {
        if (_strcmp(FMCarray + i, mcmsg, 3) == 1) {
            return *(key + j);
        }
    }
    return -1;
}

int FMCEncrypt(FILE *instream, char* key, FILE *outstream){
    // Insert code here
    char *morse = NULL;
    int resultCode = toMorse(instream, &morse);
    if (resultCode == -1) {
        return -1;
    }

    // printf("key:\t%s\n", key);
    // printf("morse:\t%s\n\n", morse);
    // printf("output:\t");
    int i;
    for (i = 0; ; i+=3) {
        if (_strlen(morse + i) < 3) {
            break;
        }
        int k = morseToKey(morse + i, key);
        if (k != -1) {
            int writeResult = fprintf(outstream, "%c", k);
            if (writeResult < 0) {
                exit(3);
            }
        }
    }
    if (resultCode == 0) {
        return 0;
    }
    // fprintf(outstream, "\n");
    return 1;
}

/* Part 2 Functions */
int fromMorse(char *mcmsg, FILE* outstream){
    // printf("morse:\t%s\n", mcmsg);
    int outputSpace = 0;
    while (*mcmsg) {
        int offset = _strtok(mcmsg, 'x');
        if (offset == 0) {
            outputSpace = 1;
        } else {
            if (outputSpace == 1) {
                int writeResult = fprintf(outstream, " ");
                if (writeResult < 0) {
                    exit(3);
                }
                outputSpace = 0;
            }
            int i;
            for (i = 0; i < 58; i++) {
                char *code = *(MorseCode + i);
                if (_strcmp(code, mcmsg, max(_strlen(code), offset)) == 1) {
                    // printf("%d\n", toChar(i));
                    int writeResult = fprintf(outstream, "%c", toChar(i));
                    if (writeResult < 0) {
                        exit(3);
                    }
                    break;
                }
            }
        }
        mcmsg += offset + 1;
    }
    return 1;
}

int FMCDecrypt(FILE *instream, char* key, FILE *outstream){
    char* FMCarray = ".....-..x.-..--.-x.x..x-.xx-..-.--.x--.-----x-x.-x--xxx..x.-x.xx-.x--x-xxx.xx-";
    
    char c;
    int size = 0;
    char *morse = malloc(sizeof(char) * size);
    int asciiError = 0;

    while ((c = getc(instream)) != EOF) {
        // find the index of c in *key
        if (isAscii(c) == 0) {
            asciiError = 1;
            break;
        }
        if (!isUpper(c)) {
            continue;
        }
        int index = _strtok(key, c);

        if (index >= 0) {
            morse = realloc(morse, sizeof(char) * size + 3);
            if (morse == NULL) {
                return -1;
            }
            _strcat(morse, FMCarray + index * 3, &size, 3);
        }
    }

    if (size != 0 && *(morse + size - 1) != 'x' && *(morse + size - 2) != 'x') {
        morse = realloc(morse, sizeof(char) * size + 1);
        if (morse == NULL) {
            return -1;
        }
        _strcat(morse, "x", &size, 1);
    }

    *(morse + size++) = '\0';
    // printf("%s\n", morse);
    int resultCode = fromMorse(morse, outstream);
    if (asciiError == 1) {
        return 0;
    }
    return resultCode;
}