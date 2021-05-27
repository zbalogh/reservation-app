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

        [Authorize]
        [HttpGet("id/{id}")]
        public IActionResult GetUserById(int id)
        {
            User user = _userService.GetById(id);

            if (user != null) {
                return Ok(user);
            }
            else {
                return NotFound();
            }
        }

        [Authorize]
        [HttpGet("username/{username}")]
        public IActionResult GetUserByUsername(string username)
        {
            User user = _userService.GetByUsername(username);

            if (user != null) {
                return Ok(user);
            }
            else {
                return NotFound();
            }
        }
    }
}
