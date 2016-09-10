package com.curcico.jproject.core.daos;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.FetchMode;

/**
 * Clase que se utiliza para obtener los fetchs. Por
 * defecto es FetchMode.JOIN y no es una coleccion.
 * 
 * @author dumar
 *
 */
public class ManagerFetchs {
	private String fetch;
	private FetchMode fetchMode;
	private boolean isColletion = false;

	public String getFetch() {
		return fetch;
	}

	public void setFetch(String fetch) {
		this.fetch = fetch;
	}

	public void setFetchMode(FetchMode fetchMode) {
		this.fetchMode = fetchMode;
	}
	
	public FetchMode getFetchMode() {
		return fetchMode;
	}

	public boolean isColletion() {
		return isColletion;
	}

	public void setColletion(boolean isColletion) {
		this.isColletion = isColletion;
	}

	public ManagerFetchs(String fetch, FetchMode fetchMode, boolean isColletion) {
		super();
		this.fetch = fetch;
		this.fetchMode = fetchMode;
		this.isColletion = isColletion;
	}

	public ManagerFetchs(String fetch) {
		super();
		this.fetch = fetch;
		this.fetchMode = FetchMode.JOIN;
		this.isColletion = false;
	}

	public ManagerFetchs() {
		super();
	}

	public static Set<ManagerFetchs>transformFetchs(String fetchsArray){
		if(fetchsArray==null || fetchsArray.equals("")) 
			return null;
		String [] fetchs = fetchsArray.replaceAll("[", "").replaceAll("]", "").split(",");
		Set<ManagerFetchs> f = new HashSet<>();
		for (String string : fetchs) {
			f.add(new ManagerFetchs(string));
		}
		return f;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fetch == null) ? 0 : fetch.hashCode());
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
		ManagerFetchs other = (ManagerFetchs) obj;
		if (fetch == null) {
			if (other.fetch != null)
				return false;
		} else if (!fetch.equals(other.fetch))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ManagerFetchs [fetch=" + fetch + ", fetchMode=" + fetchMode
				+ "]";
	}

}
