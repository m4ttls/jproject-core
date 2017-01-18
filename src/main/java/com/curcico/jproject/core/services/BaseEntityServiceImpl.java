package com.curcico.jproject.core.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.hibernate.Criteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.curcico.jproject.core.daos.BaseEntityDao;
import com.curcico.jproject.core.daos.ConditionEntry;
import com.curcico.jproject.core.daos.ConditionSimple;
import com.curcico.jproject.core.daos.ManagerFetchs;
import com.curcico.jproject.core.entities.BaseEntity;
import com.curcico.jproject.core.exception.BaseException;
import com.curcico.jproject.core.exception.BusinessException;
import com.curcico.jproject.core.exception.InternalErrorException;
import com.curcico.jproject.core.wrapper.GridWrapper;


/**
 * Clase abstracta que implementa los métodos mas comunes de los servicios.
*/
public abstract class BaseEntityServiceImpl<T extends BaseEntity, U extends BaseEntityDao<T>> implements BaseEntityService<T> {

	protected  final Logger logger = LoggerFactory.getLogger(getClass());
	
	protected  U dao;

	public U getDao() {
		return dao;
	}
	
	@Autowired
	public void setDao(U dao){
		this.dao = dao;
	}
	
	@Override
	@Transactional
	public T loadEntityById(Integer id) throws BaseException{
			return dao.loadEntityById(id);
	}
	@Override
	@Transactional
	public T loadEntityById(Integer id, String[] attributesInitialized) throws BaseException{
		return loadEntityById(id, ManagerFetchs.createdFetchs(attributesInitialized));
	}
	
	@Override
	@Transactional
	public T loadEntityById(Integer id, Set<ManagerFetchs> fetchs)
			throws BaseException {
		return dao.loadEntityById(id, fetchs);
	}

	@Override
	@Transactional
	public T loadEntityByFilters(List<? extends ConditionEntry> filters) throws BaseException {
		return dao.loadEntityByFilters(filters);
	}
		
	@Override
	@Transactional
	public T loadEntityByFilters(List<? extends ConditionEntry> filters,
			Set<ManagerFetchs> fetchs) throws BaseException {
			return dao.loadEntityByFilters(filters, fetchs);
	}
			
	@Override
	@Transactional(rollbackFor=Exception.class)
	public T saveOrUpdate(T entity) throws BaseException {
			if(entity != null){
				if(entityValidate(entity)){
					if (entity.getId()==null || entity.getId().equals(0)){
							dao.save(entity);
							return entity;
					} else {
						dao.update(entity);
						return entity;
					}
				}
			}
			logger.error("Some parameters (entity or userId) are invalid.");
			throw new BusinessException("invalid.parameters");
	}
	
	@Override
	@Deprecated
	@Transactional(rollbackFor=Exception.class)
	public T createOrUpdate(T entity, Integer userId) throws BaseException {
			return this.saveOrUpdate(entity);
	}

	@Transactional
	protected boolean entityValidate(T entity) throws BaseException {
		return true;
	}

	@Transactional
	protected void beforeSave(T entity,Integer userId) throws BaseException {
		return ;
	}
	
	
	@Override
	@Transactional(rollbackFor=Exception.class)
	public T delete(T object) throws BaseException{
		if(object==null) 
			throw new InternalErrorException("invalid.parameters");
		return dao.delete(object);
	}
	
	@Override
	@Deprecated
	@Transactional(rollbackFor=Exception.class)
	public T delete(T entity, Integer userId) throws BaseException{
		if(entity!=null && entity.getId()!=null && userId!=null){
			entity = loadEntityById(entity.getId());
			dao.delete(entity);
		} else {
			throw new BusinessException("invalid.parameters");
		}
		return entity;
	}
	
	@Override
	@Transactional
	public List<Integer> getIds() throws BaseException{
			return dao.getIds();	
	}
	
	@Deprecated
	public List<ConditionEntry> transformFilters(Map<String, Object> filters) throws BaseException{
		List<ConditionEntry> conditions = new ArrayList<ConditionEntry>();
		for (Entry<String, Object> e : filters.entrySet()) {
			conditions.add(new ConditionSimple(e.getKey(), e.getValue()));
		}
		return conditions;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	@Transactional
	public Collection<T> findByFilters(List<? extends ConditionEntry> filters, Integer page, Integer rows,
			String orderBy, String orderMode) throws BaseException {
		Collection<T> resultados = null;
			resultados = (Collection<T>) dao.findByFilters(filters, page, rows, orderBy, orderMode, null);
		return resultados;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	@Transactional
	public Collection<T> findByFilters(List<? extends ConditionEntry> filters) throws BaseException {
		Collection<T> resultados = null;
			resultados = (Collection<T>) dao.findByFilters(filters);
		return resultados;
	}
	
	@Override
	@Transactional
	public Long getCountByFilters(List<? extends ConditionEntry> filters) throws BaseException {
			return dao.countByFilters(filters);
	}
	
	@Override
	@Transactional
	@SuppressWarnings("unchecked")
	public List<T> findAll() throws BaseException {
		List<T> results = null;
			results = (List<T>) dao.findAll();
		return results;
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional
	public Collection<T> findByFilters(Criteria criteria, List<? extends ConditionEntry> filters, Integer page, Integer rows,
			String orderBy, String orderMode,Set<ManagerFetchs> fetchs) throws BaseException {
		Collection<T> resultados = null;
		resultados = (Collection<T>) dao.findByFilters(criteria, filters, page, rows, orderBy, orderMode, fetchs);
		return resultados;
	}
	@Override
	@SuppressWarnings("unchecked")
	@Transactional
	public Collection<T> findByFilters(List<? extends ConditionEntry> filters, Integer page, Integer rows,
			String orderBy, String orderMode,Set<ManagerFetchs> fetchs) throws BaseException {
		Collection<T> resultados = null;
			resultados = (Collection<T>) dao.findByFilters(filters, page, rows, orderBy, orderMode, fetchs);
		return resultados;
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional
	public Collection<T> findByFilters(List<? extends ConditionEntry> filters,
			Set<ManagerFetchs> fetchs) throws BaseException {
		Collection<T> resultados = null;
			resultados = (Collection<T>) dao.findByFilters(filters, fetchs);
		return resultados;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	@Transactional
	public GridWrapper<T> findByFiltersGridWrapper(List<ConditionEntry> filters, Integer page, Integer rows, String orderBy, String orderMode,
			Set<ManagerFetchs> fetchs) throws BaseException{
		return (GridWrapper<T>) dao.findByFiltersGridWrapper(filters, page, rows, orderBy, orderMode, fetchs);
	}
}
