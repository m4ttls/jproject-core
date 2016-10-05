package com.curcico.jproject.core.services;

import com.curcico.jproject.core.entities.BaseAuditedEntity;
import com.curcico.jproject.core.exception.BaseException;

public interface BaseAuditedEntityService<T extends BaseAuditedEntity> 
			extends Service<T> {
	
	/**
	 * @param object
	 * @param user
	 * @return
	 * @throws BaseException
	 */
	T update(T object, Integer user) throws BaseException;
	
	/**
	 * @param object
	 * @param user
	 * @return
	 * @throws BaseException
	 */
	T save(T object, Integer user) throws BaseException;
	
	/**
	 * @param object
	 * @param user
	 * @return
	 * @throws BaseException
	 */
	T saveOrUpdate(T object, Integer user) throws BaseException;
	
	/**
	 * @param object
	 * @param user
	 * @return
	 * @throws BaseException
	 */
	T delete(T object, Integer user) throws BaseException;

	
}
