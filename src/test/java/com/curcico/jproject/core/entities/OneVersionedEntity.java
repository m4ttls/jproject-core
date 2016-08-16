package com.curcico.jproject.core.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.TableGenerator;

@Entity(name="TEST_VERSIONED_ENTITY")
public class OneVersionedEntity extends BaseEntity {

	private String descripcion;
	
	private OneVersionedTimeRangeEntity vtr;
	
	@Override
	@Id
	@GeneratedValue( strategy = GenerationType.TABLE, generator = "idGenerator" )
	@TableGenerator( name = "idGenerator", table = "INT_ID_GEN", pkColumnName = "GEN_KEY", valueColumnName = "GEN_VALUE", initialValue = 1, allocationSize = 1 )
	public Integer getId() {
		return this.id;
	}
	
	@Column(name = "DESCRIPCION", nullable=false, length=60)
	public String getDescripcion() {
		return descripcion;
	}
	
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="ONE_VTR")
	public OneVersionedTimeRangeEntity getVtr() {
		return vtr;
	}
	
	public void setVtr(OneVersionedTimeRangeEntity vtr) {
		this.vtr = vtr;
	}
	
}
