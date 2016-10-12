package com.curcico.jproject.core.daos;

import com.curcico.jproject.core.entities.BaseAuditedEntity;
import com.curcico.jproject.core.exception.InternalErrorException;

public interface BaseAuditedEntityDao<T extends BaseAuditedEntity> extends BaseEntityDao<T> {

	/**
	 * @param object
	 * @param user
	 * @return
	 * @throws InternalErrorException
	 */
	T update(T object, Integer user) throws InternalErrorException;
	
	/**
	 * @param object
	 * @param user
	 * @return
	 * @throws InternalErrorException
	 */
	T save(T object, Integer user) throws InternalErrorException;
	
	/**
	 * @param object
	 * @param user
	 * @return
	 * @throws InternalErrorException
	 */
	T saveOrUpdate(T object, Integer user) throws InternalErrorException;
	
	/**
	 * @param object
	 * @param user
	 * @return
	 * @throws InternalErrorException
	 */
	T delete(T object, Integer user) throws InternalErrorException;
	
	@Override
	@Deprecated
	T delete(T object) throws InternalErrorException;
	
	@Override
	@Deprecated
	T saveOrUpdate(T object) throws InternalErrorException;
	
	@Override
	@Deprecated
	T save(T object) throws InternalErrorException;
	
	@Override
	@Deprecated
	T update(T object) throws InternalErrorException;
}
