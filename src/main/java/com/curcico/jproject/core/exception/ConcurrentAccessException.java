package com.curcico.jproject.core.exception;

public class ConcurrentAccessException extends InternalErrorException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8456825027202994923L;

	public ConcurrentAccessException() {
		super();
	}	

	public ConcurrentAccessException(Throwable cause) {
		super(cause);
	}
	
	public ConcurrentAccessException(String message) {
		super(message);
	}
	
	public ConcurrentAccessException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
