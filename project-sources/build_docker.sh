#!/bin/bash


#cd "$(dirname "$0")"

#dir=$(cd -P -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd -P)

workdir="$(dirname ${BASH_SOURCE[0]})"

cd $workdir

BASE_DIR=$(pwd)

echo "BASE_DIR: $BASE_DIR"



#
# Delete the existing docker images from your local image repo
#

docker rmi --force $(docker images -q 'zbalogh/reservation-api-server:latest' | uniq)

docker rmi --force $(docker images -q 'zbalogh/reservation-angular-ui:latest' | uniq)

docker rmi --force $(docker images -q 'zbalogh/reservation-dotnet-auth-server:latest' | uniq)



#
# Create new docker images in your local image repo
#

cd $BASE_DIR/api-server
docker build -t zbalogh/reservation-api-server:latest -f Dockerfile .

cd $BASE_DIR/frontend/angular-gui
docker build -t zbalogh/reservation-angular-ui:latest -f Dockerfile .

cd $BASE_DIR/dotnet/ReservationAuthServer
docker build -t zbalogh/reservation-dotnet-auth-server:latest -f Dockerfile .
