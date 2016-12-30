package com.curcico.jproject.core.services;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;

import com.curcico.jproject.core.daos.ConditionEntry;
import com.curcico.jproject.core.daos.ManagerFetchs;
import com.curcico.jproject.core.entities.BaseEntity;
import com.curcico.jproject.core.exception.BaseException;
import com.curcico.jproject.core.wrapper.GridWrapper;




/** 
 * @author Ing. Alejandro Daniel Curci (acurci@gmail.com)
 * Interface que indica los metodos base para cada entidad, esta interfaz esta implementada en la clase abstracta CommonsService<T>
 *  por lo tanto, todo método declarado aquí debe ser implementado en dicha clase (o por lo menos declarado para que las clases hijas
 *  lo implementen)
 * */
public interface BaseEntityServiceCUD<T extends BaseEntity> {

	/**
	 * @param entity
	 * @return
	 * @throws BaseException
	 */
	T saveOrUpdate(T entity) throws BaseException;

	/**
	 * @param object
	 * @throws BaseException
	 */
	T delete(T object) throws BaseException;
	
}
