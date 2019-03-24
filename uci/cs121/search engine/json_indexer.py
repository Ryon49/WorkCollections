import json
from collections import Counter

from bs4 import BeautifulSoup, Comment
import nltk

import os
import errno
import math

CONTENT_DIR = 'templates/WEBPAGES_RAW/'
TARGET_DIR = "clean/"

FILTERED_TAGS = ['style', 'script', 'meta', 'link']
SKIPPED_TAGS = ['html', 'body']
IMPORTANT_TAGS = ['title', 'h1', 'h2', 'h3', 'b', 'strong']

with open(CONTENT_DIR + 'bookkeeping.json') as _f:
    paths = json.load(_f)
    _f.close()

lemma = nltk.wordnet.WordNetLemmatizer()


def clean_text(_text):
    words = ''.join([c if c.isalnum() else " " for c in _text.lower()]).split(' ')
    return map(lambda a: lemma.lemmatize(a), filter(lambda a: a != '' and a != ' ' and len(a) <= 50, words))



def create_dir(filename):
    if not os.path.exists(os.path.dirname(filename)):
        try:
            os.makedirs(os.path.dirname(filename))
        except OSError as exc:  # Guard against race condition
            if exc.errno != errno.EEXIST:
                raise


if __name__ == '__main__':
    keys = filter(lambda x: not os.path.exists(TARGET_DIR + x), paths.keys())
    print(len(keys))
    for path in keys:
        # for path in keys:
        with open(CONTENT_DIR + path) as f:
            soup = BeautifulSoup(f, "lxml")
        # remove unwanted tags
        for tag in FILTERED_TAGS:
            for t in soup.find_all(tag):
                t.decompose()

        # remove html comment
        comments = soup.findAll(text=lambda text: isinstance(text, Comment))
        for comment in comments:
            comment.extract()

        soup_text = soup.text
        word_counter = Counter(clean_text(soup_text))

        output = dict()

        create_dir(TARGET_DIR + path)
        output['denominator'] = math.sqrt(sum(map(lambda x: x ** 2, word_counter.values())))
        output['frequency'] = word_counter

        # clean sentence by removing characters like '\n', '\r' etc.
        # filter out empty string ''
        output['phases'] = filter(lambda s: s != '', map(lambda s: ' '.join(clean_text(s)), soup_text.split("\n")))

        with open(TARGET_DIR + path, 'w+') as o:
            json.dump(output, o)
