

#
# Websites: How to configure/specify HTTP and HTTPS port for the .NET Core application
#
https://stackoverflow.com/questions/37365277/how-to-specify-the-port-an-asp-net-core-application-is-hosted-on

https://stackoverflow.com/questions/48669548/why-does-aspnet-core-start-on-port-80-from-within-docker/48669703

https://andrewlock.net/5-ways-to-set-the-urls-for-an-aspnetcore-app/



#
# Websites: How to create/build docker image for .NET Core application
#
https://docs.docker.com/engine/examples/dotnetcore/

https://docs.microsoft.com/en-us/aspnet/core/host-and-deploy/docker/building-net-docker-images?view=aspnetcore-5.0

https://github.com/dotnet/dotnet-docker/blob/main/samples/run-aspnetcore-https-development.md




# Build
dotnet build

# Publish for release
dotnet publish -c Release -o out/Release/publish


# Build Docker image:
docker build -t zbalogh/reservation-dotnet-auth-server:latest -f Dockerfile .


# Push the image to the docker hub:
docker push zbalogh/reservation-dotnet-auth-server:latest


# run interactive mode:
docker run --rm -it -p 5000:5000 --name reservation-dotnet-auth-server zbalogh/reservation-dotnet-auth-server:latest


# run as daemon:
docker run --rm -d -p 5000:5000 --name reservation-dotnet-auth-server zbalogh/reservation-dotnet-auth-server:latest



#
# -p parameter exposes/publishes the container port to the host where the docker runtime is running
#
# For example: -p 8080:80   container port 80 is being exposed to the port 8080 on the host machine
#



# display the docker logs (STD Input):
docker logs <CONTAINER_NAME>
