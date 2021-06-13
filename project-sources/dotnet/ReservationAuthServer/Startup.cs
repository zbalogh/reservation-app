using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.Hosting;
using Microsoft.AspNetCore.HttpsPolicy;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;
using Microsoft.Extensions.Logging;
using Microsoft.OpenApi.Models;
using ReservationAuthServer.Helpers;
using ReservationAuthServer.Services;
using Swashbuckle.AspNetCore.SwaggerUI;

namespace ReservationAuthServer
{
    public class Startup
    {
        public Startup(IConfiguration configuration)
        {
            Configuration = configuration;
        }

        public IConfiguration Configuration { get; }

        // This method gets called by the runtime. Use this method to add services to the container.
        public void ConfigureServices(IServiceCollection services)
        {
            // Enable CORS for any origins
            services.AddCors(options =>
            {
                options.AddPolicy("AllowCorsPolicy", builder =>
                {
                    builder.AllowAnyOrigin()
                           .AllowAnyMethod()
                           .AllowAnyHeader();
                });
            });
            Console.WriteLine("CORS enabled.");

            // Add routing
            services.AddRouting();
            Console.WriteLine("Routing added.");

            // add controllers
            services.AddControllers();
            Console.WriteLine("Controllers added.");

            // add "AppSettings" configuration
            services.Configure<AppSettings>(Configuration.GetSection("AppSettings"));
            Console.WriteLine("AppSettings added.");

            // add GRPC service
            services.AddGrpc();
            Console.WriteLine("gRPC added.");

            // configure DI for application services
            services.AddScoped<IUserService, UserService>();
            services.AddSingleton<IJwtAuthManager, JwtAuthManager>();
            services.AddScoped<UserGrpcService>();
            Console.WriteLine("Configured DI.");

            services.AddSwaggerGen(c =>
            {
                c.SwaggerDoc("v1", new OpenApiInfo { Title = "ReservationAuthServer", Version = "v1" });
            });
            Console.WriteLine("SwaggerGen added.");

            Console.WriteLine("ConfigureServices(): method finished.");
        }

        // This method gets called by the runtime. Use this method to configure the HTTP request pipeline.
        public void Configure(IApplicationBuilder app, IWebHostEnvironment env)
        {
            if (env.IsDevelopment())
            {
                app.UseDeveloperExceptionPage();
            }

            app.UseSwagger(c =>
                {
                    c.RouteTemplate = "api/auth/swagger/{documentname}/swagger.json";
                }
            );
            app.UseSwaggerUI(c =>
            {
                c.SwaggerEndpoint("/api/auth/swagger/v1/swagger.json", "ReservationAuthServer v1");
                c.RoutePrefix = "api/auth/swagger";
            });
            Console.WriteLine("Use Swagger.");

            app.UseRouting();
            Console.WriteLine("Use Routing.");

            app.UseCors("AllowCorsPolicy");
            Console.WriteLine("Use CORS.");

            // custom jwt authentication middleware
            app.UseMiddleware<JwtMiddleware>();
            Console.WriteLine("Use JWT Authentication Middleware.");

            app.UseEndpoints(endpoints =>
            {
                // Map REST Controllers
                endpoints.MapControllers();

                 // Map GRPC Services
                endpoints.MapGrpcService<UserGrpcService>();
            });

            Console.WriteLine("Configure(): method finished.");
        }
    }
}
