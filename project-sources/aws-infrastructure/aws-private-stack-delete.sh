#!/bin/bash
set -e # Stops the script if any command fails

aws cloudformation delete-stack \
    --stack-name reservation-app-private
