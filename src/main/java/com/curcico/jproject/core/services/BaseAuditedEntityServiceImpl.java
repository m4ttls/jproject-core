package com.curcico.jproject.core.services;

import org.springframework.transaction.annotation.Transactional;

import com.curcico.jproject.core.daos.BaseAuditedEntityDao;
import com.curcico.jproject.core.entities.BaseAuditedEntity;
import com.curcico.jproject.core.exception.BaseException;
import com.curcico.jproject.core.exception.BusinessException;
import com.curcico.jproject.core.exception.InternalErrorException;

public abstract class BaseAuditedEntityServiceImpl<T extends BaseAuditedEntity, U extends BaseAuditedEntityDao<T>>
		extends CommonsService<T, U> 
		implements BaseAuditedEntityService<T> {

	@Override
	@Transactional(rollbackFor=Exception.class)
	public T update(T object, Integer user) throws BaseException {
		if(object!=null && object.getId()!=null && user!=null){
			return dao.update(object, user);
		}
		throw new InternalErrorException("invalid.parameters");
	}

	@Override
	@Transactional(rollbackFor=Exception.class)
	public T save(T object, Integer user) throws BaseException {
		if(object!=null && object.getId()!=null && user!=null){
			return dao.save(object, user);
		}
		throw new InternalErrorException("invalid.parameters");
	}

	@Override
	@Transactional(rollbackFor=Exception.class)
	public T saveOrUpdate(T object, Integer user) throws BaseException {
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
	
	@Override
	@Transactional(rollbackFor=Exception.class)
	public T createOrUpdate(T entity, Integer userId) throws BaseException {
			if(entity != null && userId != null){
				if(entityValidate(entity)){
					if (entity.getId()==null || entity.getId().equals(0)){
							dao.save(entity, userId);
							return entity;
					} else {
						dao.update(entity, userId);
						return entity;
					}
				}
			}
			logger.error("Some parameters (entity or userId) are invalid.");
			throw new BusinessException("invalid.parameters");
	}

}
