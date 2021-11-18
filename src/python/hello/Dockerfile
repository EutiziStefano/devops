FROM python

ADD main.py /

RUN pip install gunicorn flask prometheus_client

CMD [ "gunicorn", "-w 1", "-b 0.0.0.0:8080", "main:app" ]
