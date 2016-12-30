package com.curcico.jproject.core.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.TableGenerator;

import org.hibernate.annotations.SQLDelete;

@Entity(name="TEST_VTR_ENTITY")
@SQLDelete(sql="update 	TEST_VTR_ENTITY set	deleted=1, VALID_TO=sysdate where id=? and VERSION=?" )
public class OneBaseTimeRangeEntity extends BaseTimeRangeEntity {

	@SuppressWarnings("unused")
	private static final long serialVersionUID = -6770678021712829066L;

	private String descripcion;
	private String nombre;
	

	@Override
	@Id
	@GeneratedValue( strategy = GenerationType.TABLE, generator = "idGenerator" )
	@TableGenerator( name = "idGenerator", table = "INT_ID_GEN", pkColumnName = "GEN_KEY", /*pkColumnValue = "GEN_VALUE",*/ valueColumnName = "GEN_VALUE", initialValue = 1, allocationSize = 1 )
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

	@Column(name = "NOMBRE", nullable=false, length=60)
	public String getNombre() {
		return nombre;
	}
	
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

}
