import os
import pyodbc

# Mongo DB
# from pymongo import MongoClient
# MONGO_URI = 'mongodb+srv://mohamed:abcdefg1097@cluster0.avx6r.mongodb.net/apt_test?retryWrites=true&w=majority'
# websites = MongoClient(MONGO_URI).apt_test.websites

DB_SERVER_URL = 'search-engine-server.database.windows.net'
DB_NAME = 'SearchEngine'
DB_USERNAME = "heba"
DB_PASSWORD = "Aya12345"
DB_DRIVER = '{SQL Server}'

db_cursor = None
with pyodbc.connect('DRIVER='+DB_DRIVER+';SERVER='+DB_SERVER_URL+';PORT=1433;DATABASE='+DB_NAME+';UID='+DB_USERNAME+';PWD=' + DB_PASSWORD) as conn:
    db_cursor = conn.cursor()
