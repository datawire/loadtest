#!/usr/bin/env bash
set -o nounset
set -o errexit

LOCUST_MASTER="$(kubectl -n loadtest get pods --selector=app=ambassador-stable --output=jsonpath='{.items[0].metadata.name}')"
kubectl -n loadtest port-forward "$LOCUST_MASTER" 8877
