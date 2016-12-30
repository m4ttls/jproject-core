package com.curcico.jproject.core.daos;

import java.sql.Timestamp;
import java.util.Calendar;

import com.curcico.jproject.core.entities.BaseAuditedEntity;
import com.curcico.jproject.core.exception.BaseException;
import com.curcico.jproject.core.exception.InternalErrorException;

public abstract class BaseAuditedEntityDaoImpl<T extends BaseAuditedEntity> 
	extends BaseEntityDaoImpl<T>	
	implements BaseEntityDao<T>, BaseAuditedEntityDaoCUD<T> {

	
    public BaseAuditedEntityDaoImpl() {
        super();
    }

	@Override
	public T update(T object, Integer user) throws BaseException {
		if(user==null) 
			throw new InternalErrorException("falta.parametro.usuario");
		if(object.getVersion()==null) 
			throw new InternalErrorException("falta.parametro.version");
		Timestamp now = new Timestamp(Calendar.getInstance().getTimeInMillis());
		object.setUpdatedByUser(user);
		object.setUpdatedTime(now);
		return super.update(object);
	}

	@Override
	public T save(T object, Integer user) throws BaseException {
		if(user==null) 
			throw new InternalErrorException("falta.parametro.usuario");
		Timestamp now = new Timestamp(Calendar.getInstance().getTimeInMillis());
		object.setUpdatedByUser(user);
		object.setUpdatedTime(now);
		object.setCreatedByUser(user);
		object.setCreatedTime(now);	
		return super.save(object);
	}

	@Override
	public T saveOrUpdate(T object, Integer user) throws BaseException {
		if(object.getId()!=null){
			return this.update(object, user);
		} else {
			return this.save(object, user);
		}
	}

	@Override
	public T delete(T object, Integer user) throws BaseException {
		/* Hago las validaciones por si me sobreescriben el delete con la anotación de hibernate */
		if(object.getVersion()==null) throw new InternalErrorException("falta.parametro.version");
		if(user==null) throw new InternalErrorException("falta.parametro.usuario");
		T entity = loadEntityById(object.getId());
		if(!entity.getVersion().equals(object.getVersion()))
				throw new InternalErrorException("concurrent.access.exception");
		entity = this.update(entity, user);
		return super.delete(entity);
	}
}
