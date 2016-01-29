package com.curcico.jproject.core.exception;

public class BaseException extends Exception {

	private static final long serialVersionUID = -9125548924867112239L;
	
	public BaseException() {
		super();
	}
	
	public BaseException(String message) {
		super(message);
	}
	
	public BaseException(Throwable cause) {
		super(cause);
	}

	public BaseException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
