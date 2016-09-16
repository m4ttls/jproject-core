package com.curcico.jproject.core.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import io.swagger.annotations.ApiModelProperty;

@MappedSuperclass
public abstract class TimeRangeEntity extends BaseEntity {

	private Date vigenciaDesde = new Date();
	private Date vigenciaHasta;
	
	@Column(name="VIGENCIA_DESDE", nullable=false)
	@ApiModelProperty(value="Fecha de vigencia desde", required=false, position=990)
	public Date getVigenciaDesde() {
		return vigenciaDesde;
	}
	public void setVigenciaDesde(Date vigenciaDesde) {
		this.vigenciaDesde = vigenciaDesde;
	}

	@Column(name="VIGENCIA_HASTA", nullable=true)
	@ApiModelProperty(value="Fecha de vigencia hasta", required=false, position=990)
	public Date getVigenciaHasta() {
		return vigenciaHasta;
	}
	public void setVigenciaHasta(Date vigenciaHasta) {
		this.vigenciaHasta = vigenciaHasta;
	}
	
	@Transient
	@ApiModelProperty(value="Marca de entidad activa", readOnly=true, required=false, position=990)
	public boolean isActive(){
		Date now = new Date();
		return vigenciaDesde!=null && vigenciaDesde.before(now) && (vigenciaHasta==null || vigenciaHasta.after(now));
	}
	
	@Override
	public void extractMutableValues(Object newObject) {
		super.extractMutableValues(newObject);
		TimeRangeEntity other = (this.getClass().cast(newObject));
		this.setVigenciaHasta(other.getVigenciaHasta());
	}
	
}
