from genericpath import exists
from settings import DB_DRIVER
from nltk import tokenize
from nltk.stem import PorterStemmer, LancasterStemmer, snowball
from nltk.tokenize import word_tokenize
from settings import db_cursor
import re
import json


class WordProcess:
    def __init__(self, sentence="comp", stemmer="porter"):
        self.sent = sentence
        self.tokenizer = None
        self.stemmer = snowball.EnglishStemmer()

    def get_stem(self):
        tokens = []
        for word in word_tokenize(self.sent):
            tokens.append(self.stemmer.stem(word))
        return tokens

    def get_words(self):
        return self.sent.split(" ")


class Pagination:
    def __init__(self, list, limit=10) -> None:
        self.limit = limit
        self.list = list
        self.length = len(list)
        self.page = 1

    def getwebs(self, page=1):
        self.page = page
        self.start_idx = self.limit * (page - 1)
        self.end_idx = self.limit+self.start_idx if self.length >= (
            self.limit+self.start_idx) else self.length

        page_info = {
            "websites": self.list[self.start_idx:self.end_idx],
            "nextPage": self.getNextPage(),
            "prevPage": self.getPrevPage(),
            "hasNextPage": self.hasNextPage(),
            "hasPrevPage": self.hasPrevPage()
        }
        return page_info

    def getNextPage(self):
        return self.page+1 if self.end_idx <= self.length else 1

    def hasNextPage(self):
        return True if self.end_idx < self.length else False

    def getPrevPage(self):
        return self.page-1 if self.page > 1 else 1

    def hasPrevPage(self):
        return True if self.page > 1 else False

    def getprevUrl(self):
        return f"&page={self.page+1}"


def read_suggestions():
    with open("static/assets/file.txt", 'r') as f:
        data = f.read()
        data = data.replace("\n", " ")
        return data


def write_suggestion(key):
    with open("static/assets/file.txt", 'r+') as f:
        content = f.read()
        if(key not in content):
            f.write('\n' + key)


# db_cursor.execute(
#     "select * from INFORMATION_SCHEMA.COLUMNS where TABLE_NAME='DocumentsTable'")
# row = db_cursor.fetchall()  # key, url, freq, score
def process_query(key, stem, match_search=False):
    # 1- get websites of the key word.
    db_cursor.execute(
        f"Select doc_url from KeywordsInDocTable where keyword = '{stem}' ORDER BY score DESC, term_freq DESC")
    web_urls = db_cursor.fetchall()  # "URLS"

    # 2- store website, desc, title
    urls = []
    pattern = re.compile(f"(.*)({key})(\W.*)", re.DOTALL)
    for web in web_urls:
        if web[0] not in urls:
            url = web[0]
            db_cursor.execute(
                f"Select doc_description, title  from DocumentsTable where doc_url = '{url}'")
            desc, title = db_cursor.fetchone()

            if(desc):
                if match_search:
                    print("Enter Match Search")
                    before, after, key, exists = refactor_test(
                        desc, key, pattern)
                else:
                    before, after, key, exists = refactor_desc(desc, stem)

                if(exists):
                    urls.append({"url": url, "before": before,
                                 "after": after, "key": key, "title": title})
    return urls


def refactor_test(desc, key, pattern):
    res = re.search(pattern, desc)
    before = ""
    after = ""
    exists = False
    if(res):
        before, key, after = res.groups()
        exists = True
        before = before[len(before)-50:len(before)
                        ] if len(before) > 50 else before
        after = after[0:50] if len(after) > 50 else after
    return before, after, key, exists


def refactor_desc(desc, key):
    idx = desc.lower().find(key)
    start = 0
    end = len(desc)
    key_len = len(key)

    if(idx == -1):
        key = ""
        key_len = 0
        idx = 0
    lenth = 100

    if(len(desc) > idx + lenth):
        end = idx + lenth
    if(idx - lenth >= 0):
        start = idx - lenth
    before = desc[start:idx]
    after = desc[idx+key_len:end]
    return before, after, key, True


def getRecordsCount():
    db_cursor.execute("SELECT * FROM KeywordsInDocTable")
    row = db_cursor.fetchone()
    print(row)
    row = db_cursor.fetchone()
    print(row)


def getResults(key):
    # 1- word stemming
    # 2- look in the crawl.json
    # 3- add to sites list\
    with open("static/assets/crawl.json") as f:
        results = json.load(f)
        sites = []
        for word in results['results']:
            if word['word'] == key:
                sites = word['sites']
                break
        return (sites, len(sites))
