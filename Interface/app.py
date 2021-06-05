from flask import Flask, url_for, request
from flask.templating import render_template
from settings import db_cursor
from utils import WordProcess, Pagination, read_suggestions, write_suggestion, process_query

app = Flask(__name__)

paginate = Pagination
data = None
isProcessing = False


@app.route("/")
def home_page():
    global isProcessing
    isProcessing = False
    s_list = read_suggestions()
    print(s_list)
    return render_template("home.html", s_list=s_list)


@app.route("/about")
def about_page():
    global isProcessing
    isProcessing = False
    return render_template("about.html")


@app.route("/chart")
def chart_page():
    global isProcessing
    isProcessing = False
    return render_template("chart.html")


@app.route("/search")
def search_page():
    key = request.args.get('key', type=str)
    page = request.args.get('page', type=int, default=1)
    print(page)
    # add to suggestion list
    write_suggestion(key)

    # Get word Stem
    wp = WordProcess(key)
    stem = wp.get_stem()[0]

    # Process the query Get the records
    global data
    global isProcessing
    if(not isProcessing):
        data = process_query(stem)
    isProcessing = True

    # Paginate
    paginate = Pagination(data).getwebs(page)
    # {"websites", "nextPage", "prevPage", "hasNext" }
    pages = paginate['websites']
    return render_template("results.html", paginate=paginate, key=key, count=len(data), sites=pages, title=key)


if __name__ == "__main__":
    app.run(host='127.0.0.1', port=5000, debug=True)
