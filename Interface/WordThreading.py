import pyodbc
import re
from concurrent.futures import ThreadPoolExecutor, thread
from threading import RLock
from settings import db_cursor
import random
import time


class ProcssThreading:

    def __init__(self, stem, key, is_match=False, is_thread=False):
        self.is_thread = is_thread
        self.is_match = is_match
        self.key = key
        self.stem = stem
        self.lock = RLock()
        self.key_pattern = re.compile(f"\\b{self.key}\\W")
        self.stem_pattern = re.compile(f"\\b({self.stem}\\w*)")
        self.results = []
        self.descs = []
        self.titles = []
        self.urls = None
        start_time = time.time()
        self.init()
        fetch_time = time.time()
        self.start_search()
        end_time = time.time()
        print(
            f"Search took: {round((end_time - start_time)*1000, 2)} ms in total")
        print(f"Fetching from DB: {round((fetch_time-start_time), 2)} s")
        print(
            f"Data Processing: {round((end_time-fetch_time)*1000, 3)} ms")

    def init(self):
        db_cursor.execute(
            f"Select doc_url from KeywordsInDocTable where keyword = '{self.stem}' ORDER BY score DESC, term_freq DESC")
        self.urls = db_cursor.fetchall()  # "URLS"
        print("URLS Ready...")
        print("Fetching Content and Title...")
        for url in self.urls:
            db_cursor.execute(
                f"Select doc_description, title  from DocumentsTable where doc_url = '{url[0]}'")
            desc, title = db_cursor.fetchone()
            self.descs.append(desc)
            self.titles.append(title)
        print("Done! Fetching Contents and Titles...")

    def start_search(self):
        if(not self.is_thread):
            if(self.is_match):
                print("starting Single match Search")
                self.signle_search_match()
            else:
                print("starting Signle Normal Search")
                self.single_search()
        else:
            if(self.is_match):
                print("starting Threaded match Search")
                with ThreadPoolExecutor() as exec:
                    exec.map(self.thread_search_match, zip(
                        self.descs, range(len(self.descs))))
            else:
                print("starting Threaded Normal Search")
                with ThreadPoolExecutor() as exec:
                    exec.map(self.thread_search, zip(
                        self.descs, range(len(self.descs))))

    def single_search(self):
        for i, desc in enumerate(self.descs):
            res = re.search(self.stem_pattern, desc)
            b_len = random.randint(50, 200)
            a_len = random.randint(50, 200)
            if(res):
                start, end = res.span()
                before = desc[start -
                              b_len:start] if start > b_len else desc[0:start]
                after = desc[end:end+a_len] if end + \
                    a_len < len(desc) else desc[end:-1]
                self.results.append({"url": self.urls[i][0], "before": before,
                                     "after": after, "key": desc[start:end], "title": self.titles[i]})

    def signle_search_match(self):
        for i, desc in enumerate(self.descs):
            res = re.search(self.key_pattern, desc)
            b_len = random.randint(50, 200)
            a_len = random.randint(50, 200)
            if(res):
                start, end = res.span()
                before = desc[start -
                              b_len:start] if start > b_len else desc[0:start]
                after = desc[end:end +
                             a_len] if len(desc) > end + a_len else desc[end:-1]
                self.results.append({"url": self.urls[i][0], "before": before,
                                     "after": after, "key": self.key, "title": self.titles[i]})

    def thread_search(self, arr):
        desc, i = arr
        res = re.search(self.stem_pattern, desc)
        b_len = random.randint(50, 200)
        a_len = random.randint(50, 200)
        if(res):
            start, end = res.span()
            before = desc[start -
                          b_len:start] if start > b_len else desc[0:start]
            after = desc[end:end+a_len] if end + \
                a_len < len(desc) else desc[end:-1]
            self.lock.acquire()
            self.results.append({"url": self.urls[i][0], "before": before,
                                 "after": after, "key": self.stem, "title": self.titles[i]})
            self.lock.release()

    def thread_search_match(self, arr):
        desc, i = arr
        res = re.search(self.key_pattern, desc)
        b_len = random.randint(50, 200)
        a_len = random.randint(50, 200)
        if(res):
            start, end = res.span()
            before = desc[start -
                          b_len:start] if start > b_len else desc[0:start]
            after = desc[end:end +
                         a_len] if len(desc) > end + a_len else desc[end:-1]
            self.results.append({"url": self.urls[i][0], "before": before,
                                 "after": after, "key": self.key, "title": self.titles[i]})

    def get_results(self):
        return self.results
