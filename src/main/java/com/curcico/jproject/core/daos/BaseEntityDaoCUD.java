package com.curcico.jproject.core.daos;

import com.curcico.jproject.core.entities.BaseEntity;
import com.curcico.jproject.core.exception.BaseException;

public interface BaseEntityDaoCUD<T extends BaseEntity> {

	/**
	 * Guarda un objeto en la base de datos
	 * 
	 * @param object
	 * @throws BaseException
	 */
	T save(T object) throws BaseException;

	/**
	 * Elimina un objeto en la base de datos (utiliza la sentencia de
	 * eliminación definia para la entidad)
	 * 
	 * @param object
	 * @throws BaseException
	 */
	T delete(T object) throws BaseException;
	
	/**
	 * Actualiza un objeto en la base de datos
	 * 
	 * @param object
	 * @throws BaseException
	 */
	T update(T object) throws BaseException;

	/**
	 * Guarda o actualiza un objeto en la base de datos
	 * 
	 * @param object
	 * @throws BaseException
	 */
	T saveOrUpdate(T object) throws BaseException;


}
