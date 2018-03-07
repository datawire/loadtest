#!/usr/bin/env bash
set -o nounset
set -o errexit

AMBASSADOR="$(kubectl -n loadtest get pods --selector=app=ambassador-stable --output=jsonpath='{.items[0].metadata.name}')"
kubectl -n loadtest port-forward "$AMBASSADOR" 8877
