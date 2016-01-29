package com.curcico.jproject.core.daos;

import org.hibernate.sql.JoinType;
/**
 * Clase que se utiliza para obtener los Alias. Por 
 * defecto JoinType.LEFT_OUTER_JOIN y inserted = false 
 * debido a que se utiliza como bandera para no insertar
 * mas de una vez el alias cuando se va a realizar el 
 * query
 * 
 * @author dumar
 *
 */
public class ManagerAlias {
	private String alias;
	private String path;
	private JoinType joinType;
	private boolean inserted = false;

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		if(alias.contains("\\.")){
			alias.replace('.', '_');
		}
		this.alias = alias;
	}

	public String getPath() {
		return path;
	}

	public boolean isInserted() {
		return inserted;
	}

	public void setInserted(boolean inserted) {
		this.inserted = inserted;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public JoinType getJoinType() {
		return joinType;
	}

	public void setJoinType(JoinType joinType) {
		this.joinType = joinType;
	}

	public ManagerAlias(String alias, String path, JoinType joinType) {
		super();
		setAlias(alias);
		this.path = path;
		this.joinType = joinType;
	}
	
	public ManagerAlias(String alias, String path) {
		this(alias, path, JoinType.LEFT_OUTER_JOIN);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((alias == null) ? 0 : alias.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ManagerAlias other = (ManagerAlias) obj;
		if (alias == null) {
			if (other.alias != null)
				return false;
		} else if (!alias.equals(other.alias))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ManagerAlias [alias=" + alias + ", path=" + path + "]";
	}

}
