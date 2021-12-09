#!/bin/bash


#cd "$(dirname "$0")"

#dir=$(cd -P -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd -P)

workdir="$(dirname ${BASH_SOURCE[0]})"

cd $workdir

BASE_DIR=$(pwd)

echo "BASE_DIR: $BASE_DIR"


#
# create Helm packages
#
helm package reservation-app


#
# delete the temporary folder
#
rm -f ../tmp/reservation-demo/helm-charts


#
# create an empty temporary folder
#
mkdir -p ../tmp/reservation-demo/helm-charts


#
# move the package files into the docs/helm-charts repository folder
#
mv *.tgz ../tmp/reservation-demo/helm-charts


#
# re-build the repository index
#
helm repo index ../tmp/reservation-demo/helm-charts --url https://zbalogh.github.io/reservation-app

