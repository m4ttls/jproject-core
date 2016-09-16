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
	T delete(T object, Integer userId) throws BaseException;

	/**
	 * @param id
	 * @param version
	 * @return
	 * @throws BaseException
	 */
	T delete(Integer id, Integer version) throws BaseException;
	
	/**
	 * @param id
	 * @return
	 * @throws BaseException
	 */
	T loadEntityById (Integer id) throws BaseException;

	/**
	 * @param id
	 * @param attributesInitialized
	 * @return
	 * @throws BaseException
	 */
	T loadEntityById(Integer id, String[] attributesInitialized) throws BaseException;
	
	/**
	 * @param id
	 * @param fetchs
	 * @return
	 * @throws BaseException
	 */
	T loadEntityById(Integer id, Set<ManagerFetchs> fetchs) throws BaseException;
	
	/**
	 * @param filters
	 * @return
	 * @throws BaseException
	 */
	T loadEntityByFilters(List<? extends ConditionEntry> filters) throws BaseException;
	
	/**
	 * @param filters
	 * @param fetchs
	 * @return
	 * @throws BaseException
	 */
	T loadEntityByFilters(List<? extends ConditionEntry> filters, Set<ManagerFetchs> fetchs)  throws BaseException;

	/**
	 * @return
	 * @throws BaseException
	 */
	List<Integer> getIds() throws BaseException;
	
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
	
	/** No es recomendable que al servicio se le envíe la criteria armada... en breve se quitará este método.
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
	@Deprecated
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
	
	/**
	 * Busqueda de entidades paginada con ordenamiento y condiciones, retorna el objeto GridWrapper armado.
	 * @param filters
	 * @param page
	 * @param rows
	 * @param orderBy
	 * @param orderMode
	 * @param fetchs
	 * @return retorna el objeto GridWrapper armado
	 * @throws BaseException
	 */
	GridWrapper<T> findByFiltersGridWrapper(List<ConditionEntry> filters, Integer page, Integer rows, String orderBy, String orderMode,
			Set<ManagerFetchs> fetchs) throws BaseException;
	
}
