using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Hosting;
using Microsoft.AspNetCore.Server.Kestrel.Core;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.Hosting;
using Microsoft.Extensions.Logging;

namespace ReservationAuthServer
{
    public class Program
    {
        public static void Main(string[] args)
        {
            CreateHostBuilder(args).Build().Run();
        }

        public static IHostBuilder CreateHostBuilder(string[] args) =>
            Host.CreateDefaultBuilder(args)
                .ConfigureWebHostDefaults(webBuilder =>
                {
                    // use Kestrel built-in web server which supports both HTTP1 and HTTP2 protocols
                    webBuilder.ConfigureKestrel(options =>
                    {
                        // listen HTTP/1.1 on port 5000 for REST/MVC services
                        options.ListenAnyIP(5000, listenOptions =>
                        {
                            listenOptions.Protocols = HttpProtocols.Http1;
                        });

                        // listen HTTP/2 on port 5002 for gRPC services
                        options.ListenAnyIP(5002, listenOptions =>
                        {
                            listenOptions.Protocols = HttpProtocols.Http2;
                        });
                    });

                    // now, let's start up the application
                    webBuilder.UseStartup<Startup>();
                });
    }

}
