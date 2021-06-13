package com.zbalogh.reservation.apiserver.grpc.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbalogh.reservation.apiserver.config.ReservationAppConfig;
import com.zbalogh.reservation.apiserver.exceptions.GrpcException;
import com.zbalogh.reservation.apiserver.grpc.stub.UserGRPC.FindUserByNameRequest;
import com.zbalogh.reservation.apiserver.grpc.stub.UserGRPC.UserResponse;
import com.zbalogh.reservation.apiserver.grpc.stub.UserGRPCServiceGrpc;
import com.zbalogh.reservation.apiserver.grpc.stub.UserGRPCServiceGrpc.UserGRPCServiceBlockingStub;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

@Service
public class UserGrpcService {
	
	private static final Logger logger = LoggerFactory.getLogger(UserGrpcService.class);
	
	@Autowired
	private ReservationAppConfig config;
	
	private boolean initialized = false;
	
	private String host;
	
	private int port;
	
	private ManagedChannel channel = null;
	
	private UserGRPCServiceBlockingStub stub = null;

	public UserGrpcService() {
		super();
	}
	
	public void init() throws Exception
	{
		this.host = config.getAuthServerName();
		this.port = 5002;
		
		// create channel
		createChannel();
		
		// create stub
		createStub();
		
		// set the "initialized" flag to  TRUE
		initialized = true;
	}
	
	private void createChannel()
	{
		logger.info("Creating channel for GRPC server: " + host + ":" + port);
		
		// create communication channel with the given host and port to connect to GRPC server
		ManagedChannel channel = ManagedChannelBuilder
								.forAddress(host, port)
								.usePlaintext()
								.build();
		
		this.channel = channel; 
	}
	
	private void createStub()
	{
		logger.info("Creating stub...");
		
		// create blocking stub
		stub = UserGRPCServiceGrpc.newBlockingStub(channel);
		
		// create async stub
		//asyncStub = UserGRPCServiceGrpc.newStub(channel);
	}
	
	public UserResponse findUserByName(String username) throws GrpcException
	{
		try {
			if (!isInitialized()) {
				init();
			}
			logger.info("Requesting findUserByName() GRPC method.");
			
			FindUserByNameRequest request = FindUserByNameRequest.newBuilder()
					.setUsername(username)
					.build();
			
			UserResponse response = stub.findUserByName(request);
			
			logger.info("Response received.");
			
			return response;
		}
		catch (Exception ex) {
			//logger.error("Exception at findUserByName() method.", ex);
			throw new GrpcException(ex);
		}
	}

	public boolean isInitialized()
	{
		return initialized;
	}

}
