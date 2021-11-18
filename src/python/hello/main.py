from os import getenv
from flask import Flask, Response
from prometheus_client import Counter, generate_latest

app = Flask(__name__)
NUM_REQUESTS = Counter("num_requests", "Number of requests", ["endpoint"])

@app.route("/")
def hello_world():
    NUM_REQUESTS.labels("/").inc()
    return 'Hello, World!'

@app.route("/metrics")
def requests_count():
    res = []
    res.append(generate_latest(NUM_REQUESTS))
    return Response(res, mimetype="text/plain")

@app.route("/alive")
def alive():
    return 'Alive!'

