#ifndef HW3P1_H
#define HW3P1_H
#include "linkedList.h"
#include<string.h>
#include<ctype.h>

struct person {
	char* firstName;
	char* lastName;
	char* middleName;
};

typedef struct {
	int m1_score;
	int m2_score;
} Midterms;

typedef struct {
	int id;
	struct person name;
	int hw_score[3];
	Midterms* m_scores;
	int final;
} Student;

/*
 * Compares the integers pointed to by p to q.
 *
 * @return -1 if p is less than q
 *          1 if p is greater than q
 *          0 if p is equal to q
 */
int intComparator(void* p, void* q);

/* Compares the string pointed to by str1 to the string pointed to by str2.
 * Strings are compared using lexicographical order.
 * Note, the uppercase letters come before all the lowercase letters.
 *
 * @return -1 if str1 is less than str2
 *          1 if str1 is greater than str2
 *          0 if str1 is equal to str2.
 */
int strComparator(void* str1, void* str2);

/*
 * Function descriptions are in the assignment document
 */
int idComparator(void *student1, void *student2);
int nameComparator(void *student1, void *student2);
int totalHWComparator(void *student1, void *student2);
int totalMidtermComparator(void *student1, void *student2);
int totalPtsComparator(void *student1, void *student2);
void printCSVStudentList(List_t* list);

#endif
