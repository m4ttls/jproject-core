package com.curcico.jproject.core.daos;

import java.util.List;

import com.curcico.jproject.core.entities.TimeRangeEntity;
import com.curcico.jproject.core.exception.BaseException;

public interface TimeRangeEntityDao<T extends TimeRangeEntity> extends Dao<T> {
	
	/** Retorna una lista de los filtros proporcionados mas los filtros de entidades activas.
	 * No modifica la lista original.
	 * @param filters
	 * @return Retorna una lista de los filtros proporcionados mas los filtros de entidades activas.
	 * @throws BaseException
	 */
	List<ConditionEntry> addFiltersActive(List<ConditionEntry> filters) throws BaseException;

}
