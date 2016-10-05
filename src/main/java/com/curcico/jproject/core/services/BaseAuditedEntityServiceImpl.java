package com.curcico.jproject.core.services;

import org.springframework.transaction.annotation.Transactional;

import com.curcico.jproject.core.daos.BaseAuditedEntityDao;
import com.curcico.jproject.core.entities.BaseAuditedEntity;
import com.curcico.jproject.core.exception.BaseException;
import com.curcico.jproject.core.exception.InternalErrorException;

public abstract class BaseAuditedEntityServiceImpl<T extends BaseAuditedEntity, U extends BaseAuditedEntityDao<T>>
		extends CommonsService<T, U> 
		implements BaseAuditedEntityService<T> {

	@Override
	public T update(T object, Integer user) throws InternalErrorException {
		if(object!=null && object.getId()!=null && user!=null){
			return dao.update(object, user);
		}
		throw new InternalErrorException("invalid.parameters");
	}

	@Override
	public T save(T object, Integer user) throws InternalErrorException {
		if(object!=null && object.getId()!=null && user!=null){
			return dao.save(object, user);
		}
		throw new InternalErrorException("invalid.parameters");
	}

	@Override
	public T saveOrUpdate(T object, Integer user) throws InternalErrorException {
		if(object!=null && object.getId()!=null && user!=null){
			return dao.saveOrUpdate(object, user);
		}
		throw new InternalErrorException("invalid.parameters");
	}

	@Override
	@Transactional(rollbackFor=Exception.class)
	public T delete(T object, Integer user) throws BaseException{
		if(object!=null && object.getId()!=null && user!=null){
				return dao.delete(object, user);
		}
		throw new InternalErrorException("invalid.parameters");
	}

}
