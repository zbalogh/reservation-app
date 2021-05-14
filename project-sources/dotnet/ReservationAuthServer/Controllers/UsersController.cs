using Microsoft.AspNetCore.Cors;
using Microsoft.AspNetCore.Mvc;
using ReservationAuthServer.Models;
using ReservationAuthServer.Services;

namespace ReservationAuthServer.Controllers
{
    [ApiController]
    [Route("api/auth/users")]
    [EnableCors("AllowCorsPolicy")]
    [ResponseCache(Duration = 0, Location = ResponseCacheLocation.None, NoStore = true)]
    public class UsersController : ControllerBase
    {
        private IUserService _userService;

        public UsersController(IUserService userService)
        {
            _userService = userService;
        }

        [Authorize]
        [HttpGet]
        public IActionResult GetAll()
        {
            var users = _userService.GetAll();
            return Ok(users);
        }
    }
}
