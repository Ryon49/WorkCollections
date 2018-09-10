
#include "hashtable.h"
#include "philphix.h"
#include <stdio.h>
#include <stdlib.h>
#include <ctype.h>
#include <string.h>

#define WORD_LENGTH 1024

void check(char key[]);

HashTable *dictionary;

int main(int argc, char **argv) {
  if (argc != 2) {
    /* Note, you can print everything you want to standard error, it will be
       ignored by the autograder */
    fprintf(stderr, "Specify a dictionary\n");
    return 0;
  }
  fprintf(stderr, "Creating hashtable\n");
  dictionary = createHashTable(2255, &stringHash, &stringEquals);
  fprintf(stderr, "Loading dictionary %s\n", argv[1]);
  readDictionary(argv[1]);
  fprintf(stderr, "Dictionary loaded\n");
  fprintf(stderr, "Processing stdin\n");
  processInput();
  return 0;
}

/*
 * void *s can be safely casted to a char * (null terminated string) which is
 * done for you here for convenience.
 */
unsigned int stringHash(void *s) {
  char *string = (void *)s;

  /* An implementation of djb2 (found in http://www.cse.yorku.ca/~oz/hash.html) */
  unsigned int hash = 5381;
  int c;
  while ((c = *string++)) {
    hash = ((hash << 5) + hash) + c;
  }
  return hash;
}

/*
 * It should return a nonzero value if the two strings are identical (case
 * sensitive comparison) and 0 otherwise.
 */
int stringEquals(void *s1, void *s2) { 
  return strcmp(s1, s2) == 0;
}

/*
 * This function should read in every word in the dictionary and store it in the
 * dictionary. You should first open the file specified, then read the content
 * and insert them into the dictionary (use `insertData` function defined in
 * `hashtable.h`). As described in the specs, you can initially assume that no
 * word is longer than 60 characters.  However, for the final 30% of your grade,
 * you cannot assumed that words have a bounded length.
 */
void readDictionary(char *name) {
  FILE *inputFile = fopen(name, "r");

  if (inputFile == NULL) {
    fprintf(stderr, "Unable to read dictionary file\n");
    return;
  }
  char *key;
  char *data;

  char keyHolder[WORD_LENGTH];
  char dataHolder[WORD_LENGTH];

  while (fscanf(inputFile, "%s", keyHolder) != EOF && fscanf(inputFile, "%s", dataHolder) != EOF) {
    key = malloc((strlen(keyHolder) + 1) * sizeof(char));
    data = malloc((strlen(keyHolder) + 1) * sizeof(char));
    strcpy(key, keyHolder);
    strcpy(data, dataHolder);

    insertData(dictionary, key, data);
  }

  fclose(inputFile);
}

/*
 * The input will be provided as standard input (stdin), and you can print the
 * processed results as standard output (stdout). You can print everything you
 * want to standard error, it will be ignored by the autograder. You can use
 * `findData` function defined in `hashtable.h`.
 */
void processInput() {
  char c;
  char word[WORD_LENGTH];
  int i = 0;
  while (1) { 
    c = getchar();

    if (isalpha(c)) {
      word[i] = c;
      i++;
    } else {
      word[i] = '\0';
      if (word[0] != '\0') {
        char key[WORD_LENGTH];
        strcpy(key, word);
        check(key);
      }

      i = 0;

      if (c != EOF) {
        putchar(c);
      } else {
        break;
      }
    }
  }
}

void check(char *key){

  char *data;
  char tempKey[WORD_LENGTH];
  strcpy(tempKey, key);
  /* condition 1 */
  data = findData(dictionary, tempKey);
  if (data != NULL) {
    fprintf(stdout, "%s", data);
    return;
  }

  /* condition 2 */
  int j = 0;
  for (j = 1; tempKey[j] != '\0'; j++) {
    tempKey[j] = tolower(tempKey[j]);
  }
  data = findData(dictionary, tempKey);
  if (data != NULL) {
    fprintf(stdout, "%s", data);
    return;
  }

  /* condition 3 */  
  tempKey[0] = tolower(key[0]);
  data = findData(dictionary, tempKey);
  if (data != NULL) {
    fprintf(stdout, "%s", data);
    return;
  }
  fprintf(stdout, "%s", key);
}
