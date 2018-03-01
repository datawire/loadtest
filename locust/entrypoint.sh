#!/usr/bin/env sh
set -o errexit
set -o nounset
set -o xtrace

LOCUST_MODE="${LOCUST_MODE:-standalone}"
LOCUST_MASTER_PORT="${LOCUST_MASTER_PORT:-5557}"
LOCUST_FILE="${LOCUST_FILE:-locustfile.py}"
LOCUST_TARGET="${LOCUST_TARGET:?Variable 'LOCUST_TARGET' not set}"
LOCUST_OPTS="-f ${LOCUST_FILE} --host=${LOCUST_TARGET} --no-reset-stats"

case $(printf ${LOCUST_MODE} | [:lower:]' '[:upper:]) in
    "MASTER")
        LOCUST_OPTS="--master --master-bind-port=${LOCUST_MASTER_PORT} $LOCUST_OPTS"
        ;;
    "SLAVE")
        LOCUST_MASTER_HOST="${LOCUST_MASTER_HOST:?Variable 'LOCUST_MASTER_HOST' not set}"
        LOCUST_OPTS="--slave --master-host=$LOCUST_MASTER_HOST --master-port=$LOCUST_MASTER_PORT $LOCUST_OPTS"
        ;;
esac

locust ${LOCUST_OPTS}
