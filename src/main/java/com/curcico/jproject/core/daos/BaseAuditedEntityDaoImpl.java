package com.curcico.jproject.core.daos;

import java.sql.Timestamp;
import java.util.Calendar;

import com.curcico.jproject.core.entities.BaseAuditedEntity;
import com.curcico.jproject.core.exception.InternalErrorException;

public abstract class BaseAuditedEntityDaoImpl<T extends BaseAuditedEntity> 
	extends BaseEntityDaoImpl<T>	
	implements BaseAuditedEntityDao<T> {

	
    public BaseAuditedEntityDaoImpl() {
        super();
    }

	@Override
	public T update(T object, Integer user) throws InternalErrorException {
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
	public T save(T object, Integer user) throws InternalErrorException {
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
	public T saveOrUpdate(T object, Integer user) throws InternalErrorException {
		if(object.getId()!=null){
			return this.update(object, user);
		} else {
			return this.save(object, user);
		}
	}

	@Override
	public T delete(T object, Integer user) throws InternalErrorException {
		/* Hago las validaciones por si me sobreescriben el delete con la anotación de hibernate */
		if(object.getVersion()==null) throw new InternalErrorException("falta.parametro.version");
		if(user==null) throw new InternalErrorException("falta.parametro.usuario");
		T entity = loadEntityById(object.getId());
		if(!entity.getVersion().equals(object.getVersion()))
				throw new InternalErrorException("concurrent.access.exception");
		entity = this.update(entity, user);
		return super.delete(entity);
	}
	
	@Override
	@Deprecated
	public T delete(T object) throws InternalErrorException {
		throw new UnsupportedOperationException("not supported");
	}
	
	@Override
	@Deprecated
	public T update(T object) throws InternalErrorException {
		throw new UnsupportedOperationException("not supported");
	}
	
	@Override
	@Deprecated
	public T save(T object) throws InternalErrorException {
		throw new UnsupportedOperationException("not supported");
	}
	
	@Override
	@Deprecated
	public T saveOrUpdate(T object) throws InternalErrorException {
		throw new UnsupportedOperationException("not supported");
	}

}
