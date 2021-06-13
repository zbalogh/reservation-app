package com.zbalogh.reservation.apiserver.exceptions;

public class GrpcException extends Exception {

	private static final long serialVersionUID = 6204450585964337757L;

	public GrpcException() {
		super();
	}

	public GrpcException(String message) {
		super(message);
	}

	public GrpcException(Throwable cause) {
		super(cause);
	}

	public GrpcException(String message, Throwable cause) {
		super(message, cause);
	}

	public GrpcException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
