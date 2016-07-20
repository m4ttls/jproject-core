package com.curcico.jproject.core.entities;

import java.sql.Date;
import java.sql.Timestamp;

public class CompositionEntityExample extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String code;
	private SimpleEntityExample composition;
	private CompositionEntityExample compositionParent;
	private Timestamp timestamp;
	private Date date;
	
	

	public CompositionEntityExample() {
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
	
	public SimpleEntityExample getComposition() {
		return composition;
	}
	
	public void setComposition(SimpleEntityExample composition) {
		this.composition = composition;
	}
	
	public CompositionEntityExample getCompositionParent() {
		return compositionParent;
	}
	
	public void setCompositionParent(CompositionEntityExample compositionParent) {
		this.compositionParent = compositionParent;
	}
	
	public Timestamp getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}
	
	public Date getDate() {
		return date;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}
	

}
