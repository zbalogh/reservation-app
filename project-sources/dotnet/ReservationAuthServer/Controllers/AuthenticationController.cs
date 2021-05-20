using Microsoft.AspNetCore.Cors;
using Microsoft.AspNetCore.Mvc;
using ReservationAuthServer.Models;
using ReservationAuthServer.Services;

namespace ReservationAuthServer.Controllers
{
    [ApiController]
    [Route("api/auth/account")]
    [EnableCors("AllowCorsPolicy")]
    [ResponseCache(Duration = 0, Location = ResponseCacheLocation.None, NoStore = true)]
    public class AuthenticationController : ControllerBase
    {
        private IUserService _userService;

        public AuthenticationController(IUserService userService)
        {
            _userService = userService;
        }

        [HttpPost("login")]
        public IActionResult Authenticate(AuthenticateRequest model)
        {
            var response = _userService.Authenticate(model);

            if (response == null) {
                // We send Bad Request (400) response message if the authentication failed.
                // With that way, we can distinguish between the cases when access to Unauthorized (401, 403) page OR Authentication Failure (400)
                return BadRequest(new { message = "Username or password is incorrect" });
                //return Unauthorized(new { message = "Username or password is incorrect" });
            }
            
            return Ok(response);
        }

        [HttpGet("health")]
        public IActionResult HealthCheck()
        {
            // just return 200 OK without body
            return Ok();
        }
    }

}
