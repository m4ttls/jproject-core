package com.curcico.jproject.core.daos;

import java.util.Date;
import java.util.List;

import com.curcico.jproject.core.entities.BaseTimeRangeEntity;
import com.curcico.jproject.core.exception.BaseException;

public interface BaseTimeRangeEntityDao<T extends BaseTimeRangeEntity> extends BaseAuditedEntityDao<T> {
	
	/** Retorna una lista de los filtros proporcionados mas los filtros de entidades activas.
	 * No modifica la lista original.
	 * @param filters
	 * @return Retorna una lista de los filtros proporcionados mas los filtros de entidades activas.
	 * @throws BaseException
	 */
	List<ConditionEntry> addFiltersActive(List<ConditionEntry> filters) throws BaseException;

	/** Retorna una lista de los filtros proporcionados mas los filtros de entidades activas a una fecha especificada como parametro.
	 * No modifica la lista original.
	 * @param date Fecha a la cual se buscaran las entidades activas
	 * @param filters
	 * @return Retorna una lista de los filtros proporcionados mas los filtros de entidades activas a la fecha proporcionada como parámetro.
	 * @throws BaseException
	 */
	List<ConditionEntry> addFiltersActiveAtDate(Date date, List<ConditionEntry> filters)
			throws BaseException;
	
}
