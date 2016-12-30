package com.curcico.jproject.core.services;

import org.springframework.transaction.annotation.Transactional;

import com.curcico.jproject.core.daos.BaseAuditedEntityDaoCUD;
import com.curcico.jproject.core.entities.BaseAuditedEntity;
import com.curcico.jproject.core.exception.BaseException;
import com.curcico.jproject.core.exception.BusinessException;
import com.curcico.jproject.core.exception.InternalErrorException;

public abstract class BaseAuditedEntityServiceImpl<T extends BaseAuditedEntity, U extends BaseAuditedEntityDaoCUD<T>>
		extends BaseEntityServiceImpl<T, U> 
		implements BaseAuditedEntityService<T> {

	@Override
	@Transactional(rollbackFor=Exception.class)
	public T update(T object, Integer user) throws BaseException {
		if(object!=null && object.getId()!=null && user!=null){
			return this.saveOrUpdate(object, user);
		}
		throw new InternalErrorException("invalid.parameters");
	}

	@Override
	@Transactional(rollbackFor=Exception.class)
	public T save(T object, Integer user) throws BaseException {
		if(object!=null && object.getId()==null && user!=null){
			return this.saveOrUpdate(object, user);
		}
		throw new InternalErrorException("invalid.parameters");
	}

	@Override
	@Transactional(rollbackFor=Exception.class)
	public T saveOrUpdate(T object, Integer user) throws BaseException {
		if(object != null && user != null){
			if(entityValidate(object)){
				if (object.getId()==null || object.getId().equals(0)){
						dao.save(object, user);
						return object;
				} else {
					dao.update(object, user);
					return object;
				}
			}
		}
		logger.error("Some parameters (entity or userId) are invalid.");
		throw new BusinessException("invalid.parameters");
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
