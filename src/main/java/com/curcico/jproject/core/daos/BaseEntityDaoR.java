package com.curcico.jproject.core.daos;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;

import com.curcico.jproject.core.entities.BaseEntity;
import com.curcico.jproject.core.exception.BaseException;
import com.curcico.jproject.core.wrapper.GridWrapper;

/**
 * @author Ing. Alejandro Daniel Curci (acurci@gmail.com)
 *
 * @param <T>
 */
public interface BaseEntityDaoR<T extends BaseEntity> {

	/**
	 * Busca una entidad por id
	 * 
	 * @param id
	 * @return
	 * @throws BaseException
	 */
	T loadEntityById(Integer id) throws BaseException;

	/**
	 * Busca una entidad por id
	 * 
	 * @param id
	 * @param fetchs
	 * @return
	 * @throws BaseException
	 */
	T loadEntityById(Integer id, Set<ManagerFetchs> fetchs)
			throws BaseException;

	/**
	 * Busca una unica entidad en base a filtros, arroja un DaoException si se
	 * obtiene mas de un registro que satisfaga los filtros.
	 * 
	 * @param filters
	 * @return
	 * @throws BaseException
	 */
	T loadEntityByFilters(List<ConditionEntry> filters)
			throws BaseException;

	/**
	 * Busca una unica entidad en base a filtros, arroja un DaoException si se
	 * obtiene mas de un registro que satisfaga los filtros
	 * 
	 * @param filters
	 * @param fetchs
	 * @return
	 * @throws BaseException
	 */
	T loadEntityByFilters(List<ConditionEntry> filters,
			Set<ManagerFetchs> fetchs) throws BaseException;

	/**
	 * Total de registros de la tabla (incluye las condiciones definidas en el
	 * annotation where de hibernate)
	 * 
	 * @return
	 * @throws BaseException
	 */
	Long count() throws BaseException;

	/**
	 * Devuelve el total de registros de la tabla (incluye las condiciones
	 * definidas en el annotation where de hibernate)
	 * 
	 * @return
	 * @throws BaseException
	 */
	Collection<? extends T> findAll() throws BaseException;

	/**
	 * Devuelve la pagina seleccionada del total de registros de la tabla
	 * (incluye las condiciones definidas en el annotation where de hibernate)
	 * 
	 * @param numeroDePagina
	 *            (primera pagina = 1)
	 * @param tamanioPagina
	 * @return
	 * @throws BaseException
	 */
	Collection<? extends T> findAll(Integer numeroDePagina,
			Integer tamanioPagina) throws BaseException;

	/**
	 * Devuelve el total de registros de la tabla ordenado (incluye las
	 * condiciones definidas en el annotation where de hibernate)
	 * 
	 * @param orderBy
	 * @param orderMode
	 * @return
	 * @throws BaseException
	 */
	Collection<? extends T> findAll(String orderBy, String orderMode)
			throws BaseException;

	/**
	 * Devuelve el total de registros de la tabla ordenado y paginado (incluye
	 * las condiciones definidas en el annotation where de hibernate)
	 * 
	 * @param numeroDePagina
	 *            La primera página es la numero 1
	 * @param tamanioPagina
	 * @param orderBy
	 * @param orderMode
	 * @return
	 * @throws BaseException
	 */
	Collection<? extends T> findAll(Integer numeroDePagina,
			Integer tamanioPagina, String orderBy, String orderMode)
			throws BaseException;

	/**
	 * Lista de Ids
	 * 
	 * @return
	 * @throws BaseException
	 */
	List<Integer> getIds() throws BaseException;

	/**
	 * Cantidad de registros que cumplen las condiciones
	 * 
	 * @param conditions
	 * @return
	 * @throws BaseException
	 */
	Long countByFilters(List<ConditionEntry> conditions)
			throws BaseException;

	/**
	 * Cantidad de registros que cumplen las condiciones (CUIDADO! el método
	 * modifica la criteria, no se puede utilizar en los métodos que traen los
	 * resultados.
	 * 
	 * @param criteria
	 * @param filters
	 * @return
	 * @throws BaseException
	 */
	Long countByFilters(Criteria criteria,
			List<ConditionEntry> filters)
			throws BaseException;

	/**
	 * Busqueda de entidades con condiciones
	 * 
	 * @param conditions
	 * @return
	 * @throws BaseException
	 */
	Collection<? extends T> findByFilters(
			List<ConditionEntry> conditions)
			throws BaseException;

	/**
	 * Busqueda de entidades con condiciones
	 * 
	 * @param filters
	 * @param fetchs
	 * @return
	 * @throws BaseException
	 */
	Collection<? extends T> findByFilters(
			List<ConditionEntry> filters, Set<ManagerFetchs> fetchs)
			throws BaseException;

	/**
	 * Busqueda de entidades paginada con ordenamiento y condiciones
	 * 
	 * @param conditions
	 * @param numeroDePagina
	 *            La primera página es la numero 1
	 * @param tamanioPagina
	 * @param orderBy
	 * @param orderMode
	 * @return
	 * @throws BaseException
	 */
	Collection<? extends T> findByFilters(
			List<ConditionEntry> conditions, Integer numeroDePagina,
			Integer tamanioPagina, String orderBy, String orderMode,
			Set<ManagerFetchs> fetchs) throws BaseException;

	/**
	 * Busqueda de entidades paginada con ordenamiento y condiciones
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
	Collection<? extends T> findByFilters(Criteria criteria,
			List<ConditionEntry> filters, Integer page, Integer rows,
			String orderBy, String orderMode, Set<ManagerFetchs> fetchs)
			throws BaseException;

	/**
	 * Busqueda de entidades paginada con ordenamiento y condiciones, retorna el
	 * objeto GridWrapper armado.
	 * 
	 * @param filters
	 * @param page
	 * @param rows
	 * @param orderBy
	 * @param orderMode
	 * @param fetchs
	 * @return retorna el objeto GridWrapper armado
	 * @throws BaseException
	 */
	GridWrapper<? extends T> findByFiltersGridWrapper(
			List<ConditionEntry> filters, Integer page, Integer rows,
			String orderBy, String orderMode, Set<ManagerFetchs> fetchs)
			throws BaseException;

	/**
	 * Obtiene dinamicamente los alias por dao dependiendo de las condiciones
	 * del query. Si el dao sobreescribe el metodo getAlias (en caso de que
	 * prefiera utilizar otra configuración distinta a la por defecto), esos
	 * alias serán los utilizados a la hora de realizar el query sumado a los
	 * que se green automaticamente
	 * 
	 * @return
	 */
	Set<ManagerAlias> getAlias();

	/**
	 * Obtiene los fetchs permitidos por el dao. Para poder realizar el fetch,
	 * el dao debe sobreescribir este método agregandole los fetchs permitidos.
	 * De lo contrario a la hora de realizar un fetch y no está en la colección
	 * arroja una excepcion.
	 * 
	 * @return
	 */
	Set<ManagerFetchs> getFetchs();

	/**
	 * @return
	 */
	SessionFactory getSessionFactory();

	/**
	 * @param sql
	 * @param param
	 * @param fieldSelect
	 * @return
	 */
	List<List<String>> getContentReportGeneric(String sql,
			Map<String, Object> param, List<String> fieldSelect);

	/**
	 * Devuelve un criteria sin restricciones para la entidad T
	 * 
	 * @return
	 * @throws BaseException
	 */
	Criteria getCriteria() throws BaseException;

	/**
	 * Devuelve un criteria sin restricciones para la entidad T con el alias
	 * dado
	 * 
	 * @param alias
	 * @return
	 * @throws BaseException
	 */
	Criteria getCriteria(String alias) throws BaseException;

}