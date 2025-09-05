#!/bin/bash
set -e # Stops the script if any command fails

aws --endpoint-url=http://localhost:4566 cloudformation delete-stack \
    --stack-name reservation-app

aws --endpoint-url=http://localhost:4566 cloudformation deploy \
    --stack-name reservation-app \
    --template-file "./cdk.out/local/resapplocalstack.template.json"

aws --endpoint-url=http://localhost:4566 elbv2 describe-load-balancers \
    --query "LoadBalancers[0].DNSName" --output text
