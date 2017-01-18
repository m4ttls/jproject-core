package com.curcico.jproject.core.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import io.swagger.annotations.ApiModelProperty;

@MappedSuperclass
public abstract class BaseTimeRangeEntity extends BaseAuditedEntity {

	private Date validFrom = new Date();
	private Date validTo;
	
	@Column(name="VALID_FROM", nullable=false)
	@ApiModelProperty(value="Fecha de vigencia desde", required=false, position=990)
	public Date getValidFrom() {
		return validFrom;
	}
	
	public void setValidFrom(Date validFrom) {
		this.validFrom = validFrom;
	}

	@Column(name="VALID_TO", nullable=true)
	@ApiModelProperty(value="Fecha de vigencia hasta", required=false, position=990)
	public Date getValidTo() {
		return validTo;
	}
	
	public void setValidTo(Date validTo) {
		this.validTo = validTo;
	}
	
	@Transient
	@ApiModelProperty(value="Marca de entidad activa", required=false, position=990)
	public boolean isActive(){
		Date now = new Date();
		return validFrom!=null && validFrom.before(now) && (validTo==null || validTo.after(now));
	}
	
	@Override
	public void extractMutableValues(Object newObject) {
		super.extractMutableValues(newObject);
		BaseTimeRangeEntity other = (this.getClass().cast(newObject));
		this.setValidTo(other.getValidTo());
	}
	
}
