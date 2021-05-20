using Microsoft.Extensions.Options;
using Microsoft.IdentityModel.Tokens;
using System;
using System.Collections.Generic;
using System.IdentityModel.Tokens.Jwt;
using System.Linq;
using System.Security.Claims;
using System.Text;
using ReservationAuthServer.Helpers;
using ReservationAuthServer.Models;

namespace ReservationAuthServer.Services
{
    public interface IUserService
    {
        AuthenticateResponse Authenticate(AuthenticateRequest model);

        IEnumerable<User> GetAll();

        User GetById(int id);
    }

    public class UserService : IUserService
    {
        // users hardcoded for simplicity, store in a db with hashed passwords in production applications
        private List<User> _users = new List<User>
        {
            new User { Id = 1, FirstName = "Admin", LastName = "User", Username = "admin", Password = "adm1w4K6B8" }
        };

        private readonly AppSettings _appSettings;

        private readonly IJwtAuthManager _jwtAuthManager;

        public UserService(IOptions<AppSettings> appSettings, IJwtAuthManager jwtAuthManager)
        {
            _appSettings = appSettings.Value;
            _jwtAuthManager = jwtAuthManager;
        }

        public AuthenticateResponse Authenticate(AuthenticateRequest model)
        {
            var user = _users.SingleOrDefault(x => x.Username == model.Username && x.Password == model.Password);

            // return null if user not found
            if (user == null) return null;

            // authentication successful so generate JWT token
            //var token = generateJwtToken(user);
            var token = _jwtAuthManager.generateJwtToken(user.Id, user.Username);

            return new AuthenticateResponse(user, token);
        }

        public IEnumerable<User> GetAll()
        {
            return _users;
        }

        public User GetById(int id)
        {
            return _users.FirstOrDefault(x => x.Id == id);
        }
    }

}
