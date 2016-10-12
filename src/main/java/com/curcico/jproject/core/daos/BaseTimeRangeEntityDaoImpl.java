package com.curcico.jproject.core.daos;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.curcico.jproject.core.daos.ConditionComplex.Operator;
import com.curcico.jproject.core.entities.BaseTimeRangeEntity;
import com.curcico.jproject.core.exception.BaseException;
import com.curcico.jproject.core.exception.InternalErrorException;

public abstract class BaseTimeRangeEntityDaoImpl<T extends BaseTimeRangeEntity> 
	extends BaseAuditedEntityDaoImpl<T>	
	implements BaseTimeRangeEntityDao<T> {

	
    public BaseTimeRangeEntityDaoImpl() {
        super();
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
		respuesta.add(new ConditionSimple("validFrom", SearchOption.NOT_NULL, null));
		respuesta.add(new ConditionSimple("validFrom", SearchOption.LESS_EQUAL, date));		
		ConditionComplex cc = new ConditionComplex(Operator.OR);
		cc.addCondition(new ConditionSimple("validTo", SearchOption.NULL, null));
		cc.addCondition(new ConditionSimple("validTo", SearchOption.GREATER_EQUAL, date));
		respuesta.add(cc);
		return respuesta;
	}
	

	@Override
	public T save(T object, Integer user) throws InternalErrorException {
		object.setValidFrom(new Timestamp(Calendar.getInstance().getTimeInMillis()));
		return super.save(object, user);
	}
	
	@Override
	public T delete(T object, Integer user) throws InternalErrorException {
		/* Hago las validaciones por si me sobreescriben el delete con la anotación de hibernate */
		if(object.getVersion()==null) throw new InternalErrorException("falta.parametro.version");
		if(user==null) throw new InternalErrorException("falta.parametro.usuario");
		T entity = loadEntityById(object.getId());
		if(!entity.getVersion().equals(object.getVersion()))
				throw new InternalErrorException("concurrent.access.exception");
		object.setValidTo(new Timestamp(Calendar.getInstance().getTimeInMillis()));
		return this.update(object, user);
	}

}
