FROM mcr.microsoft.com/dotnet/aspnet:5.0 AS runtime

EXPOSE 5000

WORKDIR /app

COPY out/Release/publish /app

ENV ASPNETCORE_URLS=http://+:5000

ENV ASPNETCORE_HTTP_PORT=5000

ENTRYPOINT ["dotnet", "ReservationAuthServer.dll"]
