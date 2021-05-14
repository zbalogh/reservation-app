using Microsoft.Extensions.Options;
using Microsoft.IdentityModel.Tokens;
using System;
using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Text;
using ReservationAuthServer.Helpers;

namespace ReservationAuthServer.Services
{
    public interface IJwtAuthManager
    {
        string generateJwtToken(int userid, string username);

        JwtSecurityToken DecodeJwtToken(string token);
    }

    public class JwtAuthManager : IJwtAuthManager
    {
        private readonly AppSettings _appSettings;

        public JwtAuthManager(IOptions<AppSettings> appSettings)
        {
            _appSettings = appSettings.Value;
        }

        public string generateJwtToken(int userid, string username)
        {
            // create token handler instance
            var tokenHandler = new JwtSecurityTokenHandler();

            // get the "secret" which is stored in the AppSettings
            var key = Encoding.ASCII.GetBytes(_appSettings.Secret);

            // create Token Descriptor where we define the token information such as expire, claims, etc.
            var tokenDescriptor = new SecurityTokenDescriptor
            {
                // "subject" contains the claim with the ID and Username
                Subject = new ClaimsIdentity(new[]
                { 
                    new Claim("id", userid.ToString()),
                    new Claim("username", username)
                }),
                // set "expires" for 365 days
                Expires = DateTime.UtcNow.AddDays(365),
                // set the signature
                SigningCredentials = new SigningCredentials(new SymmetricSecurityKey(key), SecurityAlgorithms.HmacSha256Signature)
            };

            // create JWT token based on the given descriptor
            var token = tokenHandler.CreateToken(tokenDescriptor);

            // serialize the JWT token to string
            return tokenHandler.WriteToken(token);
        }

        public JwtSecurityToken DecodeJwtToken(string token)
        {
            // create token handler instance
            var tokenHandler = new JwtSecurityTokenHandler();

            // get the "secret" which is stored in the AppSettings
            var key = Encoding.ASCII.GetBytes(_appSettings.Secret);

            // validate the given token with the TokenHandler
            tokenHandler.ValidateToken(token, new TokenValidationParameters
            {
                ValidateIssuerSigningKey = true,
                IssuerSigningKey = new SymmetricSecurityKey(key),
                ValidateIssuer = false,
                ValidateAudience = false,
                // set clockskew to zero so tokens expire exactly at token expiration time (instead of 5 minutes later)
                ClockSkew = TimeSpan.Zero
            }, out SecurityToken validatedToken);

            // get the validated JWT token
            var jwtToken = (JwtSecurityToken) validatedToken;

            return jwtToken;
        }
    }

}
