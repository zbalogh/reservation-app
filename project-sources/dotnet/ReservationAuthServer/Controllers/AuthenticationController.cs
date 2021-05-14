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
                //return BadRequest(new { message = "Username or password is incorrect" });
                return Unauthorized(new { message = "Username or password is incorrect" });
            }

            return Ok(response);
        }
    }

}
