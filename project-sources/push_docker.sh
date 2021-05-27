#!/bin/bash


#
# Push docker images to the Docker Hub
#

docker push zbalogh/reservation-api-server:latest

docker push zbalogh/reservation-angular-ui:latest

docker push zbalogh/reservation-dotnet-auth-server:latest
