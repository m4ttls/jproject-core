package com.curcico.jproject.core.services;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;

import com.curcico.jproject.core.daos.ConditionEntry;
import com.curcico.jproject.core.daos.ManagerFetchs;
import com.curcico.jproject.core.entities.BaseEntity;
import com.curcico.jproject.core.exception.BaseException;




/** Interface que indica los metodos base para cada entidad, esta interfaz esta implementada en la clase abstracta CommonsService<T>
 *  por lo tanto, todo método declarado aquí debe ser implementado en dicha clase (o por lo menos declarado para que las clases hijas
 *  lo implementen)
 * */
public interface Service<T extends BaseEntity> {

	/**
	 * @param object
	 * @param userId
	 * @return
	 * @throws BaseException
	 */
	T createOrUpdate(T object, Integer userId) throws BaseException;

	/**
	 * @param object
	 * @param userId
	 * @throws BaseException
	 */
	void delete(T object, Integer userId) throws BaseException;

	/**
	 * @param id
	 * @return
	 * @throws BaseException
	 */
	T findById (Integer id) throws BaseException;
	
	/**
	 * @param id
	 * @param fetchs
	 * @return
	 * @throws BaseException
	 */
	T findEntityById(Integer id, Set<ManagerFetchs> fetchs) throws BaseException;
	
	/**
	 * @param filters
	 * @return
	 * @throws BaseException
	 */
	T findEntityByFilters(List<? extends ConditionEntry> filters) throws BaseException;
	
	/**
	 * @param filters
	 * @param fetchs
	 * @return
	 * @throws BaseException
	 */
	T findEntityByFilters(List<? extends ConditionEntry> filters, Set<ManagerFetchs> fetchs)  throws BaseException;

	/**
	 * @return
	 * @throws BaseException
	 */
	List<Integer> getIds() throws BaseException;

	/**
	 * @param id
	 * @param attributesInitialized
	 * @return
	 * @throws BaseException
	 */
	T findById(Integer id, String[] attributesInitialized) throws BaseException;
	
	/**
	 * @param filters
	 * @return
	 * @throws BaseException
	 */
	Long getCountByFilters(List<? extends ConditionEntry> filters) throws BaseException;
	
	/**
	 * @param filters
	 * @param page
	 * @param rows
	 * @param orderBy
	 * @param orderMode
	 * @return
	 * @throws BaseException
	 */
	Collection<T> findByFilters(List<? extends ConditionEntry> filters, Integer page, Integer rows,
			String orderBy, String orderMode) throws BaseException;
	
	/**
	 * 
	 * @param criteria
	 * @param filters
	 * @param page
	 * @param rows
	 * @param orderBy
	 * @param orderMode
	 * @param fetchs
	 * @return
	 * @throws BaseException
	 */
	Collection<T> findByFilters(Criteria criteria, List<? extends ConditionEntry> filters, Integer page, Integer rows,
			String orderBy, String orderMode, Set<ManagerFetchs> fetchs) throws BaseException;
	
	/**
	 * @param filters
	 * @param page
	 * @param rows
	 * @param orderBy
	 * @param orderMode
	 * @param fetchs
	 * @return
	 * @throws BaseException
	 */
	Collection<T> findByFilters(List<? extends ConditionEntry> filters, Integer page, Integer rows,
			String orderBy, String orderMode, Set<ManagerFetchs> fetchs) throws BaseException;

	/**
	 * @param filters
	 * @return
	 * @throws BaseException
	 */
	Collection<T> findByFilters(List<? extends ConditionEntry> filters) throws BaseException;
	
	/**
	 * @param filters
	 * @param fetchs
	 * @return
	 * @throws BaseException
	 */
	Collection<T> findByFilters(List<? extends ConditionEntry> filters, Set<ManagerFetchs> fetchs) throws BaseException;

	/**
	 * @return
	 * @throws BaseException
	 */
	List<T> findAll() throws BaseException;
	
}
