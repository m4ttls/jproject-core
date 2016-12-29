package com.curcico.jproject.core.daos;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.StaleObjectStateException;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.curcico.jproject.core.entities.BaseEntity;
import com.curcico.jproject.core.exception.BaseException;
import com.curcico.jproject.core.exception.ConcurrentAccessException;
import com.curcico.jproject.core.exception.InternalErrorException;
import com.curcico.jproject.core.utils.ConditionsUtils;
import com.curcico.jproject.core.wrapper.GridWrapper;

public abstract class BaseEntityDaoImpl<T extends BaseEntity> implements BaseEntityDao<T> {

	protected Logger logger = Logger.getLogger(getClass());



	protected final Class<T> typeParameterClass;

    	@Autowired
	protected SessionFactory sessionFactory;
	

	public BaseEntityDaoImpl() {
        	ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
        	this.typeParameterClass = (Class<T>)type.getActualTypeArguments()[0];
	}

    private static Class<?> findTypeParameterClass(Class<?> clase){
    	if(ParameterizedType.class.isAssignableFrom(clase.getGenericSuperclass().getClass())){
    		ParameterizedType type = (ParameterizedType) clase.getGenericSuperclass();
    		return (Class<?>)type.getActualTypeArguments()[0];
    	} else {
    		return findTypeParameterClass(clase.getSuperclass());
    	}
    }

   
	@Override
    @Transactional
	public Long count() throws BaseException {
		return countByFilters(null);
	}

	@Override
    @Transactional
	public Long countByFilters(List<ConditionEntry> conditions) throws BaseException {
		Criteria criteria=this.sessionFactory.getCurrentSession().createCriteria(this.typeParameterClass);
		return countByFilters(criteria, conditions);
	}

	@Override
	@Transactional
	public Long countByFilters(Criteria criteria, List<ConditionEntry> conditions) throws BaseException {
		try {
			List<ConditionEntry> filters = null;
			if(conditions!=null){
				filters = new ArrayList<>();
				filters.addAll(conditions);
			}
			setFilters(criteria, filters, null);
			criteria.setProjection(null)
					.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
					.setProjection(Projections.rowCount());
			return (Long) criteria.uniqueResult();
		} catch (StaleObjectStateException e) {
			logger.error(e.getMessage(), e);
			throw new ConcurrentAccessException(e);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new InternalErrorException(e);
		}
	}
	
	@Override
	public Collection<? extends T> findAll() throws BaseException {
		return this.findAll(null, null, null, null);
	}

	@Override
	public Collection<? extends T> findAll(Integer numeroDePagina, Integer tamanioPagina) throws BaseException{
		return this.findAll(numeroDePagina, tamanioPagina, null, null);
	}

	@Override
	public Collection<? extends T> findAll(String orderBy, String orderMode) throws BaseException{
		return this.findAll(null, null, orderBy, orderMode);
	}

	@Override
	public Collection<? extends T> findAll(Integer page, Integer rows, String orderBy, String orderMode) 
			throws BaseException{
		return findByFilters(null, page, rows, orderBy, orderMode, null);
	}

	@Override
	public Collection<? extends T> findByFilters(List<ConditionEntry> filters) throws BaseException {
		return findByFilters(filters, null);
	}
	
	@Override
	public Collection<? extends T> findByFilters(List<ConditionEntry> filters, Set<ManagerFetchs> fetchs) throws BaseException {
		return findByFilters(filters, null, null, null, null, fetchs);
	}

	@Override
	public Collection<? extends T> findByFilters(List<ConditionEntry> filters, 
						Integer page, Integer rows, String orderBy, String orderMode, Set<ManagerFetchs> fetchs) throws BaseException {
		return findByFilters(getCriteria(), filters, page, rows, orderBy, orderMode, fetchs);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Collection<? extends T> findByFilters(Criteria criteria, List<ConditionEntry> filters, 
						Integer page, Integer rows, String orderBy, String orderMode, Set<ManagerFetchs> fetchs) throws BaseException {
		try {
			
			List<ConditionEntry> conditions = new ArrayList<>();
			if(filters!=null)
				conditions.addAll(filters);
			setFilters(criteria, filters, fetchs);
			if(page!=null && page > 0 && rows!=null && rows > 0){
				criteria.setMaxResults(rows);
				criteria.setFirstResult((page - 1) * rows);
			}
			if(orderBy!=null && !orderBy.isEmpty()){
				if(orderMode!=null && !orderMode.isEmpty()) orderBy += " " + orderMode;
				Map<String, String> orderByMap = ConditionsUtils.transformOrderBy(orderBy);
				for (Entry<String, String> e : orderByMap.entrySet()) {
					if(e.getValue() == null || e.getValue().isEmpty() || e.getValue().equalsIgnoreCase("asc")){
						criteria.addOrder(Order.asc(e.getKey()));
					} else {
						criteria.addOrder(Order.desc(e.getKey()));					
					}
				}
			}
			criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
			
			return criteria.list();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new InternalErrorException(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public GridWrapper<? extends T> findByFiltersGridWrapper(List<ConditionEntry> filters, 
			Integer page, Integer rows, 
			String orderBy, String orderMode,
			Set<ManagerFetchs> fetchs) throws BaseException {
		Long total = countByFilters(filters);
		Collection<T> resultado = (Collection<T>) this.findByFilters(filters, page, rows, orderBy, orderMode, fetchs);
		return new GridWrapper<T>(page, rows, total, resultado);
	}
	
	@Override
	public Criteria getCriteria() throws BaseException {
		 return this.sessionFactory.getCurrentSession().createCriteria(this.typeParameterClass);
	}
	
	@Override
	public Criteria getCriteria(String alias) throws BaseException {
		return this.sessionFactory.getCurrentSession().createCriteria(this.typeParameterClass, alias);
	}
	
	
	/**
	 * @param criteria
	 * @param filters
	 * @throws BaseException 
	 */
	protected Set<ManagerFetchs> setFilters(Criteria criteria, List<ConditionEntry> filters, Set<ManagerFetchs> fetchs) 
			throws BaseException {
		Map<String, String> translations = new HashMap<String, String>();
		Set<ManagerFetchs> result = null;
		Set<ManagerAlias> alias = getAlias();
		
		if(filters!=null){				
			for (ConditionEntry conditionEntry : filters) {
				criteria.add(conditionEntry.resolve(typeParameterClass, criteria, alias, translations));
			}
		}
		
		if(fetchs != null){
			ConditionFetch conditionFetch = new ConditionFetch(fetchs, getFetchs());
			conditionFetch.resolve(criteria, alias, translations, getFetchs());
			result = new LinkedHashSet<ManagerFetchs>(conditionFetch.getFetchs());
		
			if(conditionFetch.getFetchs() != null) {
				for (ManagerFetchs fetch : conditionFetch.getFetchs()) {
					if(translations.containsKey(ConditionSimple.transform(fetch.getFetch()))){
						String field = translations.get(ConditionSimple.transform(fetch.getFetch()));
						if(!fetch.isColletion()){
							criteria.setFetchMode(field, fetch.getFetchMode());
							result.remove(fetch);
						}
					}else{
						throw new InternalErrorException("fetch.not.available");
					}
				}
			}
		}
		return result;
	}
	
	@Override
	public T save(T object) throws BaseException{
		this.sessionFactory.getCurrentSession().save(object);
		object.setVersion(object.getVersion()+1);
		return object;
	}

	@Override
	public T delete(T object)  throws BaseException{
		/* Hago las validaciones por si me sobreescriben el delete con la anotación de hibernate */
		if(object.getVersion()==null) throw new InternalErrorException("falta.parametro.version");
		T entity = loadEntityById(object.getId());
		if(!entity.getVersion().equals(object.getVersion()))
				throw new InternalErrorException("concurrent.access.exception");
		this.sessionFactory.getCurrentSession().delete(entity);
		entity.setVersion(object.getVersion()+1);
		return entity;
	}

	@Override
	public T update(T object)  throws BaseException{
		this.sessionFactory.getCurrentSession().merge(object);
		object.setVersion(object.getVersion()+1);
		return object;
	}

	@Override
	public T saveOrUpdate(T object) throws BaseException {
		try {
			this.sessionFactory.getCurrentSession().saveOrUpdate(object);
			object.setVersion(object.getVersion()+1);
		} catch (Exception e) {
			throw new InternalErrorException(e);
		}
		return object;
	}

	@Override
	public T loadEntityById(Integer id)  throws BaseException {
		return loadEntityById(id, null);
	}
	
	@Override
	public T loadEntityById(Integer id, Set<ManagerFetchs> fetchs)  throws BaseException{
		List<ConditionEntry> filters = new ArrayList<ConditionEntry>();
		filters.add(new ConditionSimple("id", SearchOption.EQUAL, id));
		return loadEntityByFilters(filters, fetchs);
	}
	
	@Override
	@Transactional(readOnly = true)
	public T loadEntityByFilters(List<ConditionEntry> filters)  throws BaseException{
		return loadEntityByFilters(filters, null);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public T loadEntityByFilters(List<ConditionEntry> filters, Set<ManagerFetchs> fetchs)  throws BaseException{			
			Criteria criteria=this.sessionFactory.getCurrentSession().createCriteria(this.typeParameterClass);
			Set<ManagerFetchs> fetchUnloaded = setFilters(criteria, filters, fetchs);
			criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
			List<T> result = criteria.list();
			if(result.size()>1){
				throw new InternalErrorException("found.multiple.entities.when.expected.zero.or.one");
			}
			T object =((result.size()==1)?result.get(0):null);
			if(object!=null && fetchUnloaded != null && !fetchUnloaded.isEmpty()){
				initializeCollectionsFetch(object, fetchUnloaded);
			}
			return object;
	}
	
	@SuppressWarnings("unchecked")
	public List<Integer> getIds() throws BaseException{
        Criteria criteria = this.sessionFactory.getCurrentSession().createCriteria(this.typeParameterClass);
        criteria.setProjection(Projections.property("id"));
        return (List<Integer>) criteria.list();
	}

	
		// Gets y Sets
	/**
	 * @param sessionFactory
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	/**
	 * @return
	 */
	public SessionFactory getSessionFactory() {
		return this.sessionFactory;
	}
	
	
    @SuppressWarnings("unchecked")
	public static <C> C deproxy (C obj) {
        if (obj == null)
            return obj;
        if (obj instanceof HibernateProxy) {
            HibernateProxy proxy = (HibernateProxy) obj;
            LazyInitializer li = proxy.getHibernateLazyInitializer();
            return (C) li.getImplementation();
        } 
        return obj;
    }

    @Override
    public Set<ManagerAlias> getAlias(){
    	Set<ManagerAlias> alias = new HashSet<ManagerAlias>();
    	return alias;
    }
    
    @Override
    public Set<ManagerFetchs> getFetchs(){
    	Set<ManagerFetchs> fetchs = new HashSet<ManagerFetchs>();
    	return fetchs;
    }
    
    
    protected Map<String, String> resolveFetchsByPair(String fetch) {
		Map<String, String> result = new HashMap<String, String>();
		String[] split = fetch.split("\\.");
		if(split.length == 1) {
			result.put(fetch, fetch);			
		} else {
			String accumulative = split[0];
			
			for (int i = 0; i < split.length - 1; i++) {
				result.put(accumulative + "." + split[i + 1], accumulative + "_" + split[i + 1]);
				accumulative += "_" + split[i + 1]; 
			}		
		}
		return result;
	}

	/** Inicializa las colecciones solicitadas en el listado de fetchs
	 * @param object
	 * @param fetchs
	 * @throws BaseException
	 */
	protected void initializeCollectionsFetch(T object, Set<ManagerFetchs> fetchUnloaded) throws BaseException {
		if(fetchUnloaded !=null ){
			for (ManagerFetchs fetch : fetchUnloaded) {
				PropertyUtilsBean beanUtil = new PropertyUtilsBean();
				try{
					if(!Hibernate.isInitialized(beanUtil.getProperty(object, fetch.getFetch()))){
						Hibernate.initialize(beanUtil.getProperty(object, fetch.getFetch()));
					}
				} catch (Exception e){
					throw new InternalErrorException(e);
				}
			}
		}
	}
	
	@SuppressWarnings("rawtypes")
	public List<List<String>> getContentReportGeneric(String sql, Map<String,Object> param, List<String> fieldSelect){
		Object objTemp;
		List<List<String>> rows = new ArrayList<List<String>>();
		SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(sql);
		Iterator iteratorParam = param.entrySet().iterator();
		while (iteratorParam.hasNext()) {
			Entry entry = (Entry) iteratorParam.next(); 
			query.setParameter(entry.getKey().toString(), entry.getValue());
		}	
		query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
		List results = query.list();
		Iterator iter = results.iterator();
		
		while ( iter.hasNext() ) {
			List<String> row = new ArrayList<String>();
			Map map = (Map) iter.next();
			Iterator iteratorField = fieldSelect.iterator();
			while (iteratorField.hasNext()){
				Object campo =iteratorField.next();
				objTemp = (Object)map.get(campo); 
				if(objTemp != null){
					row.add((objTemp).toString());
				}else{					
					row.add("");					
				}
			}
			rows.add(row);
		}
		return rows;
	}
	
}
