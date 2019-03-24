import json
from flask import Flask, render_template, request, send_from_directory

import sys
import os

sys.path.append('..')

CONTENT_DIR = 'templates/WEBPAGES_RAW/'
json_file = open(CONTENT_DIR + 'bookkeeping.json')
paths = json.load(json_file)
json_file.close()

app = Flask(__name__)
# template_folder=CONTENT_DIR


@app.route('/')
def index():
    return render_template('search.html')


@app.route("/result", methods=['GET', 'POST'])
def result():
    from searcher import search

    keyword = request.args.get('keyword').strip().encode('utf-8')
    keywords = keyword.split(' ')

    if keyword == '' or len(keywords) == 0:
        return render_template('search.html')

    # word, docId, phase, score
    results = search(keywords)
    results = map(lambda a: (a[0], paths[a[0]], a[1], a[2]), results)
    return render_template('result.html', keyword=keyword, results=results)


@app.route('/redirect/<dir_no>/<file_no>')
def redirect(dir_no, file_no):
    return render_template('WEBPAGES_RAW/{dir}/{file}'.format(dir=dir_no, file=file_no))


# @app.route('/test')
# def test():
#     # template_path = dict()
#     # template_path['template_folder'] = CONTENT_DIR
#     # app.update_template_context(template_path)
#     return render_template('WEBPAGES_RAW/0/1')


# run the application
if __name__ == "__main__":
    app.run()
