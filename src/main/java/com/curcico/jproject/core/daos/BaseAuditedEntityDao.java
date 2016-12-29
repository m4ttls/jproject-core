package com.curcico.jproject.core.daos;

import com.curcico.jproject.core.entities.BaseAuditedEntity;
import com.curcico.jproject.core.exception.BaseException;

public interface BaseAuditedEntityDao<T extends BaseAuditedEntity> extends BaseEntityDao<T> {

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
	
	@Override
	@Deprecated
	T delete(T object) throws BaseException;
	
	@Override
	@Deprecated
	T saveOrUpdate(T object) throws BaseException;
	
	@Override
	@Deprecated
	T save(T object) throws BaseException;
	
	@Override
	@Deprecated
	T update(T object) throws BaseException;
}
