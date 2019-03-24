# import mysql.connector
import math
import os
import errno
# from pathos.pools import ParallelPool, ProcessPool, ThreadPool

insert_pages_sql = '{insert} into pages(docId, unigram_count, bigram_count, trigram_count) values {params};\n'
insert_pages_param = "('{docId}', {count})"

insert_index_sql = \
    "{insert} into {table_name}(word, docId, tf_idf, cos_score, freq, phase, " \
    "title_tag, h1_tag, h2_tag, h3_tag, b_tag, strong_tag, else_tag) values {params};\n"
insert_index_params = "('{word}','{docId}',{tf_idf},{cos_score},{freq},'{phase}',{title_tag}, " \
                      "{h1_tag},{h2_tag},{h3_tag},{b_tag},{strong_tag},{else_tag})"

TARGET_DIR = 'sql/'

def create_dir(filename):
    if not os.path.exists(os.path.dirname(filename)):
        try:
            os.makedirs(os.path.dirname(filename))
        except OSError as exc:  # Guard against race condition
            if exc.errno != errno.EEXIST:
                raise


def db_save_indexes(word_cnt, index, name):
    total_page_cnt = len(word_cnt)
    index_params = []

    for word, occurrences in index.items():
        occurrence_cnt = len(occurrences)
        if total_page_cnt == occurrence_cnt:
            idf = 0
        else:
            idf = math.log(total_page_cnt / float(1 + occurrence_cnt))

        for occurrence in occurrences:
            tf = math.log(1 + occurrence[1] / float(word_cnt[occurrence[0]][0]))
            cos_score = float(occurrence[1]) / word_cnt[occurrence[0]][1]
            counter = occurrence[2]
            index_params.append(insert_index_params.format(insert='insert', table_name=name,
                                                           word=word, docId=occurrence[0],
                                                           tf_idf=tf * idf, cos_score=cos_score,
                                                           freq=occurrence[1], phase=occurrence[3],
                                                           title_tag=counter['title'], h1_tag=counter['h1'],
                                                           h2_tag=counter['h2'],
                                                           h3_tag=counter['h3'], b_tag=counter['b'],
                                                           strong_tag=counter['strong'],
                                                           else_tag=counter['else_tag']))
    print("Saving the index into .sql files")
    x = range(0, len(index_params) + 499999, 500000)
    pos = zip(x, x[1:])
    for i in range(len(pos)):
        start, end = pos[i]
        filename = TARGET_DIR + '{table_name}-data{index}.sql'.format(table_name=name, index=i + 1)
        create_dir(filename)
        with open(filename, 'w+') as fm:
            fm.write(
                insert_index_sql.format(insert='insert', table_name=name, params=','.join(index_params[start:end])))

    with open('record.txt', 'a+') as f:
        f.write("{table_name} has {cnt} records\n".format(table_name=name, cnt=len(index_params)))
