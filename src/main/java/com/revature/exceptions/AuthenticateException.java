package com.revature.exceptions;

public class AuthenticateException extends Exception {

	public AuthenticateException() {
	}

	public AuthenticateException(String message) {
		super(message);
	}

	public AuthenticateException(Throwable cause) {
		super(cause);
	}

	public AuthenticateException(String message, Throwable cause) {
		super(message, cause);
	}

	public AuthenticateException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
