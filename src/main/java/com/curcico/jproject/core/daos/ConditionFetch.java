package com.curcico.jproject.core.daos;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;

import com.curcico.jproject.core.exception.InternalErrorException;

public class ConditionFetch extends Condition {
	
	Logger logger = Logger.getLogger(getClass());
	
	private Set<ManagerFetchs> fetchs;

	public ConditionFetch(Set<ManagerFetchs> fetchs, Set<ManagerFetchs> fetchsAllowed) {
		super();
		if(fetchsAllowed != null && fetchs != null){
			this.fetchs = new LinkedHashSet<ManagerFetchs>(fetchsAllowed);
			this.fetchs.retainAll(fetchs);
			this.fetchs.addAll(fetchs);
		}else if(fetchsAllowed == null){
			this.fetchs = new LinkedHashSet<ManagerFetchs>(fetchs);
		}else if(fetchs == null){
			this.fetchs = new LinkedHashSet<ManagerFetchs>();
		}
		
	}
	
	public ConditionFetch(Set<ManagerFetchs> fetchs) {
		super();
		this.fetchs = fetchs;
	}

	public ConditionFetch() {
		super();
		this.fetchs = null;
	}
	
	public Set<ManagerFetchs> getFetchs() {
		return fetchs;
	}

	public void resolve(Criteria criteria, Set<ManagerAlias> alias,
			Map<String, String> translations, Set<ManagerFetchs> fetchsAvailables) throws InternalErrorException {
		for (ManagerFetchs fetch : fetchs) {
			if(fetchsAvailables.contains(fetch)){
				Set<ManagerAlias> aliasByColumn = getAlias(fetch.getFetch());
				addAlias(criteria, alias, aliasByColumn, translations);
			} else {
				logger.error("FETCH NO PERMITIDO: " + fetch.getFetch());
				throw new InternalErrorException("fetch.not.available");
			}
		}
	}
	

}
