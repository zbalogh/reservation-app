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
using Microsoft.Extensions.Configuration;

namespace ReservationAuthServer.Services
{
    public interface IUserService
    {
        AuthenticateResponse Authenticate(AuthenticateRequest model);

        IEnumerable<User> GetAll();

        User GetById(int id);

        User GetByUsername(string username);
    }

    public class UserService : IUserService
    {
        // users hardcoded for simplicity, store in a db with hashed passwords in production applications
        private List<User> _users = new List<User>();

        private readonly AppSettings _appSettings;

        private readonly IJwtAuthManager _jwtAuthManager;

        private readonly IConfiguration _configuration;

        public UserService(IOptions<AppSettings> appSettings, IJwtAuthManager jwtAuthManager, IConfiguration configuration)
        {
            // set global variables
            _appSettings = appSettings.Value;
            _jwtAuthManager = jwtAuthManager;
            _configuration = configuration;

            // initialize the internal users cache
            InitUsersList();
        }

        private void InitUsersList()
        {
            var adminUserPassword = ReadConfig("ADMIN_USER_PASSWORD");

            _users.Add(
                new User { Id = 1, FirstName = "Admin", LastName = "User", Username = "admin", Password = adminUserPassword }
            );
        }

        private string ReadConfig(string key)
        {
            return _configuration[key];
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

        public User GetByUsername(string username)
        {
            return _users.FirstOrDefault(x => x.Username == username);
        }
    }

}
