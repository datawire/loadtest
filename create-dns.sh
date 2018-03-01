#!/usr/bin/env bash
set -o nounset
set -o errexit
set -o xtrace

datawire_zone=$(aws route53 list-hosted-zones --query 'HostedZones[?Name==`datawire.io.`].Id' --output text | sed 's|/hostedzone/||')
load_balancer="$(kubectl get svc -n loadtest ambassador-stable -o=jsonpath='{.status.loadBalancer.ingress[0].hostname}')"


CLUSTER_DNS_NAME="target-stable.datawire.io"
aws route53 change-resource-record-sets \
    --hosted-zone-id ${datawire_zone} \
    --change-batch "{\"Changes\": [{\"Action\": \"CREATE\", \"ResourceRecordSet\": {\"Name\": \"$CLUSTER_DNS_NAME\", \"Type\": \"A\", \"AliasTarget\": {\"HostedZoneId\": \"Z35SXDOTRQ7X7K\", \"DNSName\": \"${load_balancer}\", \"EvaluateTargetHealth\": false} } } ] }"
