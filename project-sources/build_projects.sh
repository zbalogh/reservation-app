#!/bin/bash


#cd "$(dirname "$0")"

#dir=$(cd -P -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd -P)

workdir="$(dirname ${BASH_SOURCE[0]})"

cd $workdir

BASE_DIR=$(pwd)

echo "BASE_DIR: $BASE_DIR"



cd $BASE_DIR/api-server
mvn clean install


cd $BASE_DIR/frontend/angular-gui
rm -rf dist
npm run bp-dist


cd $BASE_DIR/dotnet/ReservationAuthServer
#rm -rf bin/Release
rm -rf out/Release
#rm -rf obj/Release
dotnet clean
dotnet build
dotnet publish -c Release -o out/Release/publish
