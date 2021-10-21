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
# delete previous packages in the docs/helm-charts repository folder
#
rm -f ../../docs/helm-charts/*.tgz


#
# delete the previous index.yaml file
#
rm -f ../../docs/helm-charts/index.yaml


#
# move the package files into the docs/helm-charts repository folder
#
mv *.tgz ../../docs/helm-charts


#
# re-build the repository index
#
helm repo index ../../docs/helm-charts --url https://zbalogh.github.io/reservation-app/helm-charts

