package com.curcico.jproject.core.exception;

public class InternalErrorException extends BaseException {

		// Constantes
	private static final long serialVersionUID = -4740096607994932706L;

	public InternalErrorException() {
		super();
	}	

	public InternalErrorException(Throwable cause) {
		super(cause);
	}
	
	public InternalErrorException(String message) {
		super(message);
	}
	
	public InternalErrorException(String message, Throwable cause) {
		super(message, cause);
	}	
	
}
