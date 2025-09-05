#!/bin/bash
set -e # Stops the script if any command fails

aws cloudformation delete-stack \
    --stack-name reservation-app-public

aws cloudformation deploy \
    --stack-name reservation-app-public \
    --template-file "./cdk.out/aws-public-nets/resappawspublicstack.template.json" \
    --capabilities CAPABILITY_IAM CAPABILITY_NAMED_IAM

aws elbv2 describe-load-balancers \
    --query "LoadBalancers[0].DNSName" --output text
