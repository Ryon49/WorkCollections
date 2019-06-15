#include "hw3_p1.h"

int intComparator(void* p, void* q) {
    return ((int)p - (int)q);
}

int strComparator(void* str1, void* str2) {
    char* s1 = (char*)str1, *s2 = (char*)str2;
    for (; *s1 && *s2 && *s1 == *s2; ++s1, ++s2){}
    if (*s1 < *s2){
        return -1;
    }
    else if (*s1 > *s2){
        return 1;
    }
    return 0;
}

int sumHW(Student *student) {
    return student->hw_score[0] + student->hw_score[1] + student->hw_score[2];
}

int sumMidterm(Student *student) {
    return student->m_scores->m1_score + student->m_scores->m2_score;
}

int sumAll(Student *student) {
    return sumHW(student) + sumMidterm(student) + student->final;
}

int idComparator(void *student1, void *student2){
    int diff = ((Student *)student1)->id - ((Student *)student2)->id;
    if (diff < 0) {
        return -1;
    } else if (diff == 0) {
        return 0;
    } else {
        return 1;
    }
}	

int nameComparator(void *student1, void *student2){
    int lastNameDiff = strComparator(((Student *)student1)->name.lastName, ((Student *)student2)->name.lastName);   
    if (lastNameDiff != 0) {
        return lastNameDiff < 0 ? -1 : 1;
    }
    int firstNameDiff = strComparator(((Student *)student1)->name.firstName, ((Student *)student2)->name.firstName);   
    if (firstNameDiff != 0) {
        return firstNameDiff < 0 ? -1 : 1;
    }
    int middleNameDiff = strComparator(((Student *)student1)->name.middleName, ((Student *)student2)->name.middleName);   
    if (middleNameDiff != 0) {
        return middleNameDiff < 0 ? -1 : 1;
    }
    return idComparator(student1, student2);
}

int totalHWComparator(void *student1, void *student2){
    int diff = sumHW((Student *)student1) - sumHW((Student *)student2);
    if (diff == 0) {
        return idComparator(student1, student2);
    } else {
        return diff < 0 ? -1 : 1;
    }
}

int totalMidtermComparator(void *student1, void *student2){
    int diff = sumMidterm((Student *)student1) - sumMidterm((Student *)student2);
    if (diff == 0) {
        return idComparator(student1, student2);
    } else {
        return diff < 0 ? -1 : 1;
    }
}

int totalPtsComparator(void *student1, void *student2){
    int diff = sumAll((Student *)student1) - sumAll((Student *)student2);
    if (diff == 0) {
        return idComparator(student1, student2);
    } else {
        return diff < 0 ? -1 : 1;
    }
}

void printCSVStudentList(List_t* list) {
    node_t *ref = list->head;
    Student *student;
    while (ref != NULL) {
        student = (Student *) ref->value;
        printf("%d,%s,%s,%s,%d,%d,%d,%d,%d,%d\n", 
                student->id, 
                student->name.firstName, student->name.lastName, student->name.middleName,
                student->hw_score[0], student->hw_score[1], student->hw_score[2],
                student->m_scores->m1_score, student->m_scores->m2_score, 
                student->final);
        ref = ref->next;
    }
}