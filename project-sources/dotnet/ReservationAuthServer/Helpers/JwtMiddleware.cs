using Microsoft.AspNetCore.Http;
using Microsoft.Extensions.Options;
using System;
using System.Linq;
using System.Threading.Tasks;
using ReservationAuthServer.Services;

namespace ReservationAuthServer.Helpers
{
    public class JwtMiddleware
    {
        private readonly RequestDelegate _next;

        private readonly AppSettings _appSettings;

        private readonly IJwtAuthManager _jwtAuthManager;

        public JwtMiddleware(RequestDelegate next, IOptions<AppSettings> appSettings, IJwtAuthManager jwtAuthManager)
        {
            _next = next;
            _appSettings = appSettings.Value;
            _jwtAuthManager = jwtAuthManager;
        }

        public async Task Invoke(HttpContext context, IUserService userService)
        {
            var token = context.Request.Headers["Authorization"].FirstOrDefault()?.Split(" ").Last();

            if (token != null)
                attachUserToContext(context, userService, token);

            await _next(context);
        }

        private void attachUserToContext(HttpContext context, IUserService userService, string token)
        {
            try
            {
                // decode the JWT token to retrive the JwtSecurityToken object
                var jwtToken = _jwtAuthManager.DecodeJwtToken(token);

                // convert the userID string to integer
                // we use this ID to lookup for user from the user service
                var userId = int.Parse(jwtToken.Claims.First(x => x.Type == "id").Value);

                // get user by ID, and attach user to context on successful jwt validation
                context.Items["User"] = userService.GetById(userId);
            }
            catch
            {
                // do nothing if jwt validation fails
                // user is not attached to context so request won't have access to secure routes
            }
        }
    }

}
