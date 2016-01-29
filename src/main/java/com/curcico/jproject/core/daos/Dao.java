package com.curcico.jproject.core.daos;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;

import com.curcico.jproject.core.entities.BaseEntity;
import com.curcico.jproject.core.exception.InternalErrorException;

public interface Dao<T extends BaseEntity> {

	/** Busca una entidad por id
	 * @param id
	 * @return
	 * @throws InternalErrorException
	 */
	T loadEntityById(Integer id)  throws InternalErrorException;
	
	/**
	 * Busca una entidad por id
	 * @param id
	 * @param fetchs
	 * @return
	 * @throws InternalErrorException
	 */
	T loadEntityById(Integer id, Set<ManagerFetchs> fetchs) throws InternalErrorException;
	
	/** 
	 * Busca una unica entidad en base a filtros, 
	 * arroja un DaoException si se obtiene mas de un registro que satisfaga los filtros.
	 * @param filters
	 * @return
	 * @throws InternalErrorException
	 */
	T loadEntityByFilters(List<? extends ConditionEntry> filters)  throws InternalErrorException;
	
	/** 
	 * Busca una unica entidad en base a filtros, 
	 * arroja un DaoException si se obtiene mas de un registro que satisfaga los filtros
	 * 
	 * @param filters
	 * @param fetchs
	 * @return
	 * @throws InternalErrorException
	 */
	T loadEntityByFilters(List<? extends ConditionEntry> filters, Set<ManagerFetchs> fetchs)  throws InternalErrorException;
	
	/** 
	 * Guarda un objeto en la base de datos
	 * @param object
	 * @throws InternalErrorException
	 */
	void save(T object) throws InternalErrorException;

	/** Elimina un objeto en la base de datos (utiliza la sentencia de eliminación definia para la entidad)
	 * @param object
	 * @throws InternalErrorException
	 */
	void delete(T object) throws InternalErrorException;

	/** 
	 * Actualiza un objeto en la base de datos
	 * @param object
	 * @throws InternalErrorException
	 */
	void update(T object) throws InternalErrorException;

	/** 
	 * Guarda o actualiza un objeto en la base de datos
	 * @param object
	 * @throws InternalErrorException
	 */
	void saveOrUpdate(T object) throws InternalErrorException;

	/** 
	 * Total de registros de la tabla (incluye las condiciones definidas en el annotation where de hibernate)
	 * @return
	 * @throws InternalErrorException
	 */
	Integer count()  throws InternalErrorException;

	/** 
	 * Devuelve el total de registros de la tabla (incluye las condiciones definidas en el annotation where de hibernate)
	 * @return
	 * @throws InternalErrorException
	 */
	Collection<? extends T> findAll() throws InternalErrorException;
	
	/** 
	 * Devuelve la pagina seleccionada del total de registros de la tabla (incluye las condiciones definidas en el annotation where de hibernate)
	 * @param numeroDePagina (primera pagina = 1)
	 * @param tamanioPagina
	 * @return
	 * @throws InternalErrorException
	 */
	Collection<? extends T> findAll(Integer numeroDePagina, Integer tamanioPagina) throws InternalErrorException;

	/** 
	 * Devuelve el total de registros de la tabla ordenado (incluye las condiciones definidas en el annotation where de hibernate)
	 * @param orderBy
	 * @param orderMode
	 * @return
	 * @throws InternalErrorException
	 */
	Collection<? extends T> findAll(String orderBy, String orderMode) throws InternalErrorException;
	
	/** 
	 * Devuelve el total de registros de la tabla ordenado y paginado (incluye las condiciones definidas en el annotation where de hibernate)
	 * @param numeroDePagina La primera página es la numero 1
	 * @param tamanioPagina
	 * @param orderBy
	 * @param orderMode
	 * @return
	 * @throws InternalErrorException
	 */
	Collection<? extends T> findAll(Integer numeroDePagina, Integer tamanioPagina, String orderBy, String orderMode) throws InternalErrorException;
	
	/** 
	 * Lista de Ids
	 * @return
	 * @throws InternalErrorException
	 */
	List<Integer> getIds() throws InternalErrorException;
	
	/** Cantidad de registros que cumplen las condiciones
	 * @param conditions
	 * @return
	 * @throws InternalErrorException
	 */
	Long countByFilters(List<? extends ConditionEntry> conditions) throws InternalErrorException;
	
	/** 
	 * Busqueda de entidades con condiciones 
	 * @param conditions
	 * @return
	 * @throws InternalErrorException
	 */
	Collection<? extends T> findByFilters(List<? extends ConditionEntry> conditions) throws InternalErrorException;
	
	/**
	 * Busqueda de entidades con condiciones
	 * @param filters
	 * @param fetchs
	 * @return
	 * @throws InternalErrorException
	 */
	Collection<? extends T> findByFilters(List<? extends ConditionEntry> filters, Set<ManagerFetchs> fetchs) throws InternalErrorException;
	
	/** 
	 * Busqueda de entidades paginada con ordenamiento y condiciones 
	 * @param conditions
	 * @param numeroDePagina La primera página es la numero 1
	 * @param tamanioPagina
	 * @param orderBy
	 * @param orderMode
	 * @return
	 * @throws InternalErrorException
	 */
	Collection<? extends T> findByFilters(List<? extends ConditionEntry> conditions, 
			Integer numeroDePagina, Integer tamanioPagina, String orderBy, String orderMode, Set<ManagerFetchs> fetchs) throws InternalErrorException;

	/**
	 * Busqueda de entidades paginada con ordenamiento y condiciones 
	 * @param criteria
	 * @param filters
	 * @param page
	 * @param rows
	 * @param orderBy
	 * @param orderMode
	 * @param fetchs
	 * @return
	 * @throws InternalErrorException
	 */
	Collection<? extends T> findByFilters(Criteria criteria, List<? extends ConditionEntry> filters, 
			Integer page, Integer rows, String orderBy, String orderMode, Set<ManagerFetchs> fetchs) throws InternalErrorException;
	
	/**
	 * Obtiene dinamicamente los alias por dao dependiendo de las condiciones del query. 
	 * Si el dao sobreescribe el metodo getAlias (en caso de que prefiera utilizar
	 * otra configuración distinta a la por defecto), esos alias serán los utilizados
	 * a la hora de realizar el query sumado a los que se green automaticamente
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
	List<List<String>> getContentReportGeneric(String sql, Map<String,Object> param, List<String> fieldSelect);

	/** Devuelve un criteria sin restricciones para la entidad T
	 * @return
	 * @throws InternalErrorException
	 */
	Criteria getCriteria() throws InternalErrorException;

	/**
	 * Devuelve un criteria sin restricciones para la entidad T con el alias dado
	 * @param alias
	 * @return
	 * @throws InternalErrorException
	 */
	Criteria getCriteria(String alias) throws InternalErrorException;
	
}