using System;
using System.Threading.Tasks;
using Grpc.Core;
using Microsoft.Extensions.Logging;
using ReservationAuthServer.GRPC;

namespace ReservationAuthServer.Services
{
    /**
     * GRPC User Service based on the proto definitation.
     * This service is mapped in the Startup.cs file as GRPC service.
     * Also this service class is registered in the Dependency Injection Repository.
     */
    public class UserGrpcService : UserGRPCService.UserGRPCServiceBase
    {
        private readonly ILogger _logger;
        
        private readonly IUserService _userService;

        public UserGrpcService(ILogger<UserGrpcService> logger, IUserService userService)
        {
            _logger = logger;
            _userService = userService;
        }

        public override Task<UserResponse> FindUserByName(FindUserByNameRequest request, ServerCallContext context)
        {
            // get user by the given name from the user service
            var user = _userService.GetByUsername(request.Username);

            if (user != null) {
                // create response
                UserResponse response = new UserResponse();

                // map user fields to the response
                response.Id = user.Id;
                response.Firstname = user.FirstName;
                response.Lastname = user.LastName;
                response.Username = user.Username;

                // send response
                return Task.FromResult(response);
            }
            else {
                // if user is null (not found) then we throw an exception with status code and message
                var status = new Status(StatusCode.NotFound, "User not found.");
                throw new RpcException(status);
            }
        }
    }

}
