#include "hw3_p1.h"
#include "linkedList.h"

int isNumber(char *s) {
	while (*s) {
		if (!isdigit(*s)) {
			return 0;
		}
		s++;
	}
	return 1;
}

char * _strdup(char* src) {
	int n = strlen(src);
	char *dest = malloc(n + 1);
    int i;
    for (i=0; i<n; i++) {
		char c = *(src+i);
        *(dest+i) = c;
	} 
	*(dest+i) = '\0';
	return dest;
}

void toStudent(char *record, Student *student) {
	// id
	student->id = atoi(strtok(strdup(record), ","));
	// name
	char *fn = strtok(NULL, ",");
	char *ln = strtok(NULL, ",");
	student->name.firstName = fn;
	student->name.lastName = ln;
	// check middleName
	char *temp = strtok(NULL, ",");
	if (isNumber(temp) == 1) {
		// no middle name
		student->name.middleName = "";
		student->hw_score[0] = atoi(temp);
	} else {
		student->name.middleName = temp;
		student->hw_score[0] = atoi(strtok(NULL, ","));
	}
	// hw
	student->hw_score[1] = atoi(strtok(NULL, ","));
	student->hw_score[2] = atoi(strtok(NULL, ","));
	// midterm
	Midterms *m_scores = malloc(sizeof(Midterms));
	m_scores->m1_score = atoi(strtok(NULL, ","));
	m_scores->m2_score = atoi(strtok(NULL, ","));
	student->m_scores = m_scores;
	// final
	student->final = atoi(strtok(NULL, ",\n"));
}

// 53csv implemenation 
int main(int argc, char* argv[]){

	if (argc < 2) {
		printf("not enough argument\n");
		exit(1);
	}
	int sortMode = atoi(argv[1]);

	List_t *list = malloc(sizeof(List_t));
	list->length = 0;
	switch(sortMode) {
		case 1: 
			list->comparator = &idComparator;
			break;
		case 2: 
			list->comparator = &nameComparator;
			break;	
		case 3: 
			list->comparator = &totalHWComparator;
			break;
		case 4: 
			list->comparator = &totalMidtermComparator;
			break;
		case 5: 
			list->comparator = &totalPtsComparator;
			break;
		default:
			printf("invalid argument\n");
	}

	char *line = NULL;
    size_t len = 0;
	ssize_t read;

	while ((read = getline(&line, &len, stdin)) != -1) 
	{
		Student *student = malloc(sizeof(Student));
		toStudent(line, student);
		insertInOrder(list, (void *) student);
    }
	printCSVStudentList(list);
	return 0;
}