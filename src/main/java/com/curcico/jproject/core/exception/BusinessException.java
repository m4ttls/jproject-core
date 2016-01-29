package com.curcico.jproject.core.exception;

public class BusinessException extends BaseException {
	
	private String paramter;

		// Constantes
	private static final long serialVersionUID = -5976605551678811351L;

		// Constructores
	public BusinessException() {
		super("general.error");
	}	

	public BusinessException(Throwable e) {
		this(e.getMessage(), e);
	}
	
	public BusinessException(String message) {
		super(message);
		this.paramter = null;
	}
	
	public BusinessException(String message, String paramter) {
		super(message);
		this.paramter = paramter;
	}
	
	public BusinessException(String message, Throwable e) {
		super(message, e);
	}

	public String getParamter() {
		return paramter;
	}

	public void setParamter(String paramter) {
		this.paramter = paramter;
	}
	
	@Override
	public String getMessage() {
		if(super.getMessage()==null) return "general.error";
		return super.getMessage();
	}
}