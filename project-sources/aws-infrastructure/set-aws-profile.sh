#!/bin/bash
# Usage: source set-aws-profile.sh profile_name

if [ -z "$1" ]; then
  echo "Usage: source set-aws-profile.sh profile_name"
  return 1 2>/dev/null || exit 1
fi

export AWS_PROFILE="$1"
echo "AWS_PROFILE set to $AWS_PROFILE"
