import time
import json
import math
import random
from collections import defaultdict, Counter

from bs4 import BeautifulSoup, Comment

from pathos.multiprocessing import ProcessPool, ThreadPool

from db_persister import db_save_indexes

import nltk

CONTENT_DIR = 'templates/WEBPAGES_RAW/'
FILTERED_TAGS = ['style', 'script', 'meta', 'link']
SKIPPED_TAGS = ['html', 'body']
IMPORTANT_TAGS = ['title', 'h1', 'h2', 'h3', 'b', 'strong']

lemma = nltk.wordnet.WordNetLemmatizer()

with open(CONTENT_DIR + 'bookkeeping.json') as _f:
    paths = json.load(_f)
    _f.close()


def clean_text(_text):
    words = ''.join([c if c.isalnum() else " " for c in _text.lower()]).split(' ')
    # return filter(lambda a: a != '' and a != ' ' and len(a) <= 50, words)
    return map(lambda a: lemma.lemmatize(a), filter(lambda a: a != '' and a != ' ' and len(a) <= 50, words))


def find_grams(input_list, n):
    """
    an easy way to generate trigram, source code from
    zip(*[input_list[i:] for i in range(n)])
    'http://locallyoptimal.com/blog/2013/01/20/elegant-n-gram-generation-in-python/'
    """
    if n == 1:
        return input_list
    elif n == 2 or n == 3:
        return map(lambda a: ' '.join(a), zip(*[input_list[i:] for i in range(n)]))
    else:
        raise ValueError('n should be >=2 and <= 3')


def find_phase(input_list, center_index):
    if center_index < 5:
        return ' '.join(input_list[:11])
    elif len(input_list) <= (center_index - 5):
        return ' '.join(input_list[len(input_list) - 10:])
    else:
        return ' '.join(input_list[center_index - 5: center_index + 6])


def create_parser(_gram_type):
    def parser(doc_id):
        print(doc_id)
        frequency = defaultdict(Counter)
        phases = dict()
        local_phases = defaultdict(list)

        with open(CONTENT_DIR + doc_id) as f:
            soup = BeautifulSoup(f, "lxml")
            # remove unwanted tags
            for tag in FILTERED_TAGS:
                for t in soup.find_all(tag):
                    t.decompose()

            # remove html comment
            comments = soup.findAll(text=lambda text: isinstance(text, Comment))
            for comment in comments:
                comment.extract()

        # parse filtered html
        for tag in soup.find_all():
            tag_name = tag.name
            if tag_name in SKIPPED_TAGS:
                continue

            words = clean_text(tag.text.encode('utf-8'))
            grams = find_grams(words, _gram_type)

            for i in range(len(grams)):
                word = grams[i]
                if tag_name in IMPORTANT_TAGS:
                    frequency[word][tag_name] += 1
                else:
                    frequency[word]['else_tag'] += 1
                local_phases[word].append(find_phase(words, i))
        for word, _local_phases in local_phases.items():
            phases[word] = random.choice(_local_phases)

        return frequency, phases

    return parser


def build_gram(_type):
    if _type == 1:
        _name = 'unigrams'
    elif _type == 2:
        _name = 'bigrams'
    elif _type == 3:
        _name = 'trigrams'
    else:
        raise ValueError("Unexpected Index Type")
    return _type, _name


# def save(result_list, _gram_name):
#     word_cnt = defaultdict(tuple)  # should be immutable once is set
#     index = defaultdict(list)  # store inverted list
#
#     for result in result_list:
#         doc_id = result[0]
#         doc_count = 0
#         score = 0
#         for _k, _v in result[1].items():
#             phase = result[2][_k]
#             count = sum(_v.values())
#             index[_k].append((doc_id, count, _v, phase))
#             doc_count += count
#             score = count ** 2
#         word_cnt[doc_id] = (doc_count, math.sqrt(score))
#
#     db_save_indexes(word_cnt, index, _gram_name)


def runner(ngram):
    first_time = time.time()
    gram_type, gram_name = build_gram(ngram)
    # ----------------------------------------------------------------

    word_cnt = defaultdict(tuple)  # should be immutable once is set
    index = defaultdict(list)  # store inverted list

    parser = create_parser(gram_type)

    # Multiprocess
    # pool = ProcessPool(50)
    # result_list = pool.map(parser, paths.keys())
    # pool.close()

    for _id in paths.keys():
        frequency, phases = parser(_id)

        doc_word_count = 0
        score = 0
        for _k, _v in frequency.items():
            phase = phases[_k]
            count = sum(_v.values())
            index[_k].append((_id, count, _v, phase))
            doc_word_count += count
            score = count ** 2
        word_cnt[_id] = (doc_word_count, math.sqrt(score))

    record_file = open('record.txt', 'a+')
    second_time = time.time()
    print('parsing htmls complete: {} minutes\n'.format(math.ceil((second_time - first_time) / 60)))
    record_file.write('parsing htmls complete: {} minutes\n'.format(math.ceil((second_time - first_time) / 60)))
    # save(result_list, gram_name)
    db_save_indexes(word_cnt, index, gram_name)

    last_time = time.time()
    print('building index complete: {} minutes\n\n'.format(math.ceil((last_time - second_time) / 60)))
    record_file.write('building index complete: {} minutes\n\n'.format(math.ceil((last_time - second_time) / 60)))
    record_file.close()


# ----------------------------------------------------------------


if __name__ == '__main__':
    runner(1)
