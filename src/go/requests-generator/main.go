package main

import (
	"io/ioutil"
	"log"
	"math/rand"
	"net/http"
	"time"
	"os"
)

func main() {
	rand.Seed(time.Now().Unix())
	route := "" // Add route endpoint of Python app
	for {
		sleepInterval := rand.Intn(2000)
		host := os.Getenv("HW_URL")
		resp, err := http.Get(host + route)
		if err != nil {
			log.Fatalln(err)
		}
		body, err := ioutil.ReadAll(resp.Body)
		if err != nil {
			log.Fatalln(err)
		}
		sb := string(body)
		log.Printf(sb)

		log.Printf("Sleeping %d milliseconds...", sleepInterval)
		time.Sleep(time.Duration(sleepInterval) * time.Millisecond)
	}
}
