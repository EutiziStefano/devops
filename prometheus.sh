#!/bin/bash

helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm repo update
helm install prometheus prometheus-community/prometheus

kubectl patch svc prometheus-server -p '{"spec": {"type": "LoadBalancer"}}'
