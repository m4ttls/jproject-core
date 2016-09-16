package com.curcico.jproject.core.entities;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import javax.persistence.Version;

import io.swagger.annotations.ApiModelProperty;

@MappedSuperclass
@Access(AccessType.PROPERTY)
public abstract class BaseEntity {
	
	protected Integer 	id;
	protected String 	deleted;
	protected Integer	version = 0;

	@Transient
	public abstract Integer getId();
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name="DELETED", nullable=true, length=1)
	@ApiModelProperty(value="Marca de borrado", required=false, position=999)
	public String getDeleted() {
		return deleted;
	}

	public void setDeleted(String deleted) {
		this.deleted = deleted;
	}

	/**
	 * @return the version
	 */
	@Version
	@Column(name="VERSION")
	@ApiModelProperty(value="Version de la entidad para control de concurrencia", required=false, position=999)
	public Integer getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(Integer version) {
		this.version = version;
	}
	
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!this.getClass().isInstance(obj))
			return false;
		BaseEntity other = (BaseEntity) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if(!version.equals(other.version))
			return false;
		return true;
	}
	
	public void extractMutableValues(Object newObject) {
		BaseEntity other = (this.getClass().cast(newObject));
		this.setVersion(other.getVersion());
	}
}
