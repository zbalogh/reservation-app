package com.zbalogh.reservation.apiserver.exceptions;

/**
 * This custom runtime exception is thrown when a new desk reservation requested
 * but there is already an existing reservation for the selected desk (with same desk number).
 * 
 * @author ZBalogh
 *
 */
public class DeskReservationExistsException extends RuntimeException
{
	private static final long serialVersionUID = -940286917167929796L;

	public DeskReservationExistsException() {
		super();
	}

	public DeskReservationExistsException(String message) {
		super(message);
	}

	public DeskReservationExistsException(Throwable cause) {
		super(cause);
	}

	public DeskReservationExistsException(String message, Throwable cause) {
		super(message, cause);
	}

	public DeskReservationExistsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
