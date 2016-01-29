package com.curcico.jproject.core.exception;

public class ReflectionException extends InternalErrorException {

		// Constantes
	private static final long serialVersionUID = -7360252711389453086L;

		// Constructores
	public ReflectionException() {
		super("internal.error");
	}	
	
	public ReflectionException(Throwable e) {
		super("internal.error", e);
	}
	
	public ReflectionException(String message) {
		super(message);
	}
	
	public ReflectionException(String message, Throwable e) {
		super(message, e);
	}

}
