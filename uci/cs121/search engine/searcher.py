import json
import math

import mysql.connector

from collections import Counter
import nltk

simple_search = "select docId, phase, cos_score + tf_idf as score from unigrams where word = %s " \
                "order by score desc, h1_tag desc, title_tag desc " \
                "limit 20;"

complex_search = "with temp(docId, appear_count, score) as (" \
                 "select distinct docId, count(*), sum(tf_idf) + sum(cos_score) from unigrams " \
                 "where {query} group by docId) " \
                 "select * from temp where appear_count > {appear_count} " \
                 "order by appear_count desc, score limit 100;"

TARGET_DIR = "clean/"

lemma = nltk.wordnet.WordNetLemmatizer()

json_data = dict()


def search(words):
    conn = mysql.connector.connect(user='root', password='admin',
                                   host='localhost', database="cs121", use_pure=True,
                                   auth_plugin='mysql_native_password', charset="utf8")
    cursor = conn.cursor(prepared=True)

    words = map(lambda a: lemma.lemmatize(a), words)

    if len(words) == 1:
        cursor.execute(simple_search, (words[0],))
        result_set = cursor.fetchall()
        cursor.close()
        return result_set
    else:
        query_freq = Counter(words)
        query_denominator = math.sqrt(sum(map(lambda x: x ** 2, query_freq.values())))

        query = ' or '.join(map(lambda w: "word = '{}'".format(w), words))

        appear_count = math.floor(len(words) / 2)

        cursor.execute(complex_search.format(cmd='with', query=query, appear_count=appear_count))
        result_set = cursor.fetchall()

        ret = []
        for result in result_set:
            if result[0] in json_data:
                data = json_data[result[0]]
            else:
                with open(TARGET_DIR + result[0], 'r') as f:
                    data = json.load(f)
            doc_freq = data['frequency']
            numerator = 0

            # calculate cosine
            for word in words:
                if word in doc_freq:
                    numerator += (query_freq[word] * doc_freq[word])
            denominator = query_denominator * data['denominator']

            cosine = float(numerator) / denominator
            ret.append((result[0], find_closest_match(data['phases'], words), cosine))

        cursor.close()
        return sorted(ret, key=lambda t: t[2], reverse=True)[:20]


def find_closest_match(phases, words):
    ret = ''
    score = 0
    for phase in phases:
        temp_score = 0
        for word in words:
            ws = phase.split(' ')
            temp_score += ws.count(word)
        if temp_score > score:
            score = temp_score
            ret = phase
    return ret
