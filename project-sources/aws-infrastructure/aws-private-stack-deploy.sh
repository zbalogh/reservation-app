#!/bin/bash
set -e # Stops the script if any command fails

aws cloudformation delete-stack \
    --stack-name reservation-app-private

aws cloudformation deploy \
    --stack-name reservation-app-private \
    --template-file "./cdk.out/aws-private-nets/resappawsprivatestack.template.json" \
    --capabilities CAPABILITY_IAM CAPABILITY_NAMED_IAM

aws elbv2 describe-load-balancers \
    --query "LoadBalancers[0].DNSName" --output text
