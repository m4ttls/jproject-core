package com.curcico.jproject.core.services;

import java.util.Date;
import java.util.List;

import com.curcico.jproject.core.daos.ConditionEntry;
import com.curcico.jproject.core.daos.TimeRangeEntityDao;
import com.curcico.jproject.core.entities.TimeRangeEntity;
import com.curcico.jproject.core.exception.BaseException;

public abstract class TimeRangeEntityServiceImpl<T extends TimeRangeEntity, U extends TimeRangeEntityDao<T>>
		extends CommonsService<T, U> 
		implements TimeRangeEntityService<T> {
	
	@Override
	public List<ConditionEntry> addFiltersActive(List<ConditionEntry> filters) throws BaseException{
		return this.getDao().addFiltersActive(filters);
	}

	@Override
	public List<ConditionEntry> addFiltersActiveAtDate(Date date,
			List<ConditionEntry> filters) throws BaseException {
		return this.getDao().addFiltersActiveAtDate(date, filters);
	}
}
