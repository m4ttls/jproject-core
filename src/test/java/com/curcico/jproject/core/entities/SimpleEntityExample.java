package com.curcico.jproject.core.entities;

public class SimpleEntityExample extends BaseEntity {

	/**
	 * 
	 */
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;
	
	private String code;

	
	public SimpleEntityExample() {
		super();
	}

	@Override
	public Integer getId() {
		return super.id;
	}

	public String getCode() {
		return code;
	}
	
	public void setCode(String code) {
		this.code = code;
	}
}
