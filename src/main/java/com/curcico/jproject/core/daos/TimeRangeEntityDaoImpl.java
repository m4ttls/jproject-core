package com.curcico.jproject.core.daos;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.curcico.jproject.core.daos.ConditionComplex.Operator;
import com.curcico.jproject.core.entities.TimeRangeEntity;
import com.curcico.jproject.core.exception.BaseException;

public abstract class TimeRangeEntityDaoImpl<T extends TimeRangeEntity> 
	extends CommonDao<T>	
	implements TimeRangeEntityDao<T> {

	
    public TimeRangeEntityDaoImpl(Class<T> typeParameterClass) {
        super(typeParameterClass);
    }

	/* (non-Javadoc)
	 * @see com.curcico.jproject.core.daos.TimeRangeEntityDao#addFiltersActive(java.util.List)
	 */
	@Override
	public List<ConditionEntry> addFiltersActive(List<ConditionEntry> filters)
			throws BaseException {
		Date now = new Date();
		return addFiltersActiveAtDate(now, filters);
	}
	
	@Override
	public List<ConditionEntry> addFiltersActiveAtDate(Date date, List<ConditionEntry> filters)
			throws BaseException {
		List<ConditionEntry> respuesta = new ArrayList<ConditionEntry>();
		if(filters!=null) respuesta.addAll(filters);
		respuesta.add(new ConditionSimple("vigenciaDesde", SearchOption.NOT_NULL, null));
		respuesta.add(new ConditionSimple("vigenciaDesde", SearchOption.LESS_EQUAL, date));		
		ConditionComplex cc = new ConditionComplex(Operator.OR);
		cc.addCondition(new ConditionSimple("vigenciaHasta", SearchOption.NULL, null));
		cc.addCondition(new ConditionSimple("vigenciaHasta", SearchOption.GREATER_EQUAL, date));
		respuesta.add(cc);
		return respuesta;
	}
	

}
