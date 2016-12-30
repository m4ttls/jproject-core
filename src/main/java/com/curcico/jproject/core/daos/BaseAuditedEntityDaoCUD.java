package com.curcico.jproject.core.daos;

import com.curcico.jproject.core.entities.BaseAuditedEntity;
import com.curcico.jproject.core.exception.BaseException;

public interface BaseAuditedEntityDaoCUD<T extends BaseAuditedEntity> extends BaseEntityDao<T> {

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
