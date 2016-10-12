package com.curcico.jproject.core.entities;

import java.sql.Timestamp;
import java.util.Calendar;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import io.swagger.annotations.ApiModelProperty;

@MappedSuperclass
@Access(AccessType.PROPERTY)
public abstract class BaseAuditedEntity extends BaseEntity {

	protected Integer 	createdByUser;
	protected Timestamp createdTime = new Timestamp(Calendar.getInstance().getTimeInMillis());
	protected Integer 	updatedByUser;
	protected Timestamp updatedTime = new Timestamp(Calendar.getInstance().getTimeInMillis());
	
	@Column(name="created_user", nullable=false)
	@ApiModelProperty(value="Usuario de creación de la entidad", required=false, position=980)
	public Integer getCreatedByUser() {
		return createdByUser;
	}

	public void setCreatedByUser(Integer createdByUser) {
		this.createdByUser = createdByUser;
	}

	@Column(name="created_date", nullable=false)
	@ApiModelProperty(value="Fecha de creación de la entidad", required=false, position=980)
	public Timestamp getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Timestamp createdTime) {
		this.createdTime = createdTime;
	}

	@Column(name="updated_user")
	@ApiModelProperty(value="Usuario de modificació de la entidad", required=false, position=980)
	public Integer getUpdatedByUser() {
		return updatedByUser;
	}

	public void setUpdatedByUser(Integer updatedByUser) {
		this.updatedByUser = updatedByUser;
	}

	@Column(name="updated_date")
	@ApiModelProperty(value="Fecha de modificación de la entidad", required=false, position=980)
	public Timestamp getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(Timestamp updatedTime) {
		this.updatedTime = updatedTime;
	}
	
	@Override
	public void extractMutableValues(Object newObject) {
		super.extractMutableValues(newObject);
	}
}
