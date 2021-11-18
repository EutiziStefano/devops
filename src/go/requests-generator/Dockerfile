FROM golang
RUN mkdir /app
ADD . /app
WORKDIR /app
RUN go mod init example.com/requests-generator
RUN go build -o main .
CMD ["/app/main"]
