"""
Author: Brian Caulfield
4/20/2019
"""
#!/usr/bin/python3
import csv
import random
import sys

"""
DO NOT CHANGE
"""
FILED_NAMES = ['id', 'first_name', 'last_name', 'middle_name', 'hw1', 'hw2', 'hw3', 'midterm1', 'midterm2', 'final']


"""
Change the below to whatever makes you feel happy inside
"""
MIDDLE_PERCENT = .80  # percentage of records that have middle names

HW_MEAN = 75
HW_STD = 20

MIDTERM_MEAN = 45
MIDTERM_STD = 20

FINAL_MEAN = 60
FINAL_STD = 20


def get_names():
    """
    Gets name tuples from the original MOCK_DATA.csv. This data originally came from mockaroo, 
    but they have several limitations which this script addresses. 

    Returns:
        list: a list of name tuples in the order [(first, middle, last), ... ]
    """
    with open('MOCK_NAMES.csv', 'r') as csv_file:
        names = []
        csv_reader = csv.DictReader(csv_file)
        for row in csv_reader:
            names.append((row['first_name'], row['first_name'], row['last_name']))

        name_list = []
        for name in names:
            name_list.append((name[0], name[2], random.choice(names)[1]))

    return name_list

def get_score(mean, std):
    """
    Gets a number which conforms to the normal distribution
    
    Returns:
        int: a score for an assignment/exam
    """
    num = int(random.normalvariate(mean, std))
    if num < 0:
        return 0
    elif num > 100:
        return 100
    return num

def write_csv(names, filename, num_records):
    """
    Creates a CSV with unique student records. 
    """
    with open(filename, 'w', newline='') as csvfile:

        writer = csv.writer(csvfile, lineterminator='\n')
        lazy_id_tracker = {}
        
        for i in range(num_records):
            student_id = random.randint(11111111, 99999999) # note that IDs cannot be equal

            while student_id in lazy_id_tracker:
                student_id = random.randint(11111111, 99999999) 
            lazy_id_tracker[student_id] = 0

            hw1, hw2, hw3   = (get_score(HW_MEAN, HW_STD) for x in range(3))
            m1, m2          = (get_score(MIDTERM_MEAN, MIDTERM_STD) for x in range(2))
            fin             = get_score(FINAL_MEAN, FINAL_STD)

            middle_chance = random.randint(0, 100)

            if middle_chance <= MIDDLE_PERCENT * 100:
                middle = names[i][1]
            else:
                middle = ''

            writer.writerow([student_id, names[i][0], names[i][2], middle, hw1, hw2, hw3, m1, m2, fin])

def main():
    """
    Generates csv files full of random student information
    Usage:
        python csvgen.py filename.csv num_records
    """
    filename = sys.argv[1]
    num_records = int(sys.argv[2])
    if num_records > 1000:
        print('Max 1000 records allowed')
        return

    names = get_names()
    write_csv(names, filename, num_records)

main()
