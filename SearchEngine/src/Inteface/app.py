from flask import Flask, url_for, request
from flask.templating import render_template
import json
app = Flask(__name__)

sites = None


@app.route("/")
def home_page():
    return render_template("home.html")


@app.route("/about")
def about_page():
    return render_template("about.html")


@app.route("/chart")
def chart_page():
    return render_template("chart.html")


@app.route("/search")
def search_page():
    key = request.args.get('key', type=str)
    sites, count = getResults(key)
    return render_template("results.html", count=count, sites=sites, key=key)


def getResults(key):
    # 1- word stemming
    # 2- look in the crawl.json
    # 3- add to sites list\
    with open("static/assets/crawl.json") as f:
        results = json.load(f)
        sites = None
        for word in results['results']:
            if word['word'] == key:
                sites = word['sites']
                return (sites, len(sites))


if __name__ == "__main__":
    app.run(host='127.0.0.1', port=5000, debug=True)
