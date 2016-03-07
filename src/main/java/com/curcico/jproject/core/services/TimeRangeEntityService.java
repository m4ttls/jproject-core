package com.curcico.jproject.core.services;

import java.util.List;

import com.curcico.jproject.core.daos.ConditionEntry;
import com.curcico.jproject.core.entities.TimeRangeEntity;
import com.curcico.jproject.core.exception.BaseException;

public interface TimeRangeEntityService<T extends TimeRangeEntity> 
			extends Service<T> {
	
	/** Retorna una lista de los filtros proporcionados mas los filtros de entidades activas.
	 * No modifica la lista original.
	 * @param filters
	 * @return Retorna una lista de los filtros proporcionados mas los filtros de entidades activas.
	 * @throws BaseException
	 */
	List<ConditionEntry> addFiltersActive(List<ConditionEntry> filters) throws BaseException;

}
