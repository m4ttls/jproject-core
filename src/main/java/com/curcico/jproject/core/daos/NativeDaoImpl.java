package com.curcico.jproject.core.daos;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.curcico.jproject.core.exception.BaseException;
import com.curcico.jproject.core.exception.InternalErrorException;
import com.curcico.jproject.core.wrapper.GridWrapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * @author acurci
 *
 */
public abstract class NativeDaoImpl {

	protected Logger logger = Logger.getLogger(getClass());
	
    @Autowired
	protected SessionFactory sessionFactory;
       
	public NativeDaoImpl() {
		super();
    }
	
	/** Cuenta la cantidad total de registros
	 * @param queryStr    Query base se requiere que termine en una condicion (por ejemplo: WHERE 1=1 ). Puede tener parámetros
	 * @param parameters  Listado de parámetros <nombre_parametro, valor>
	 * @param conditions  Lista de conditionEntry (si es null no aplica condiciones)
	 * @return cantidad total de registros
	 * @throws BaseException
	 */
	@Transactional
	public Long countByFilters( String queryStr,
								Map<String, Object> parameters,
								List<ConditionEntry> conditions
								) throws BaseException {
		try {
			if(parameters==null) parameters = new HashMap<String, Object>();
			Map<String, Object> parametrosInternos = new HashMap<String, Object>();
			if(parameters!=null) 
				parametrosInternos.putAll(parameters);
			String queryBase = "SELECT COUNT(1) FROM (" + getQueryBase (queryStr, conditions, parametrosInternos) + ")";
			SQLQuery q = sessionFactory.getCurrentSession().createSQLQuery(queryBase);
			setParameters(q, parametrosInternos);
			return ((Number) q.uniqueResult()).longValue();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new InternalErrorException(e);
		}
	}

	/** Búsqueda por filtros para querys nativas
	 * @param queryStr    Query base se requiere que termine en una condicion (por ejemplo: WHERE 1=1 ). Puede tener parámetros
	 * @param parameters  Listado de parámetros <nombre_parametro, valor>
	 * @param conditions  Lista de conditionEntry (si es null no aplica condiciones)
	 * @param page	      Nro de pagina a mostrar, comenzando por 1 (si es null no aplica paginación)
	 * @param rows		  Cantidad de filas por página (si es null no aplica paginación)
	 * @param orderBy     Campo por el que se desea ordenar los resultados (si es null aplica un tamaño de página de 25)
	 * @param orderMode   Sentido por el que se desea ordenar los resultados (si es null aplica ascendente)
	 * @param responseType 	  Tipo de respuesta esperada, puede ser Collection.class, JsonArray.class o un dto personalizado.
	 * @param dateFormat  Formato de las fechas para los json
	 * @return Arreglo de objetos del tipo responseType
	 * @throws BaseException
	 */
	@Transactional
	public <T> Object findByFilters(String queryStr, 
											Map<String, Object> parameters,
											List<ConditionEntry> conditions,
											Integer page, Integer rows, 
											String orderBy, String orderMode, 
											Class<T> responseType, 
											String dateFormat) throws BaseException {
		try {
			Map<String, Object> parametrosInternos = new HashMap<String, Object>();
			if(parameters!=null) 
				parametrosInternos.putAll(parameters);
			//ADICION DE CONDICIONES
			String queryBase = getQueryBase (queryStr, conditions, parametrosInternos);
			 // ORDENAMIENTOS
			if(orderBy!=null && !orderBy.isEmpty() 
					&& (orderMode==null || orderMode.equalsIgnoreCase("asc") || orderMode.equalsIgnoreCase("desc")) )
				queryBase = "SELECT * FROM (" + queryBase + ") ORDER BY " + orderBy + (orderMode==null?"":" " + orderMode);
			//PAGINACION
			if(page!=null){
				if(rows==null) rows = 25;
				queryBase = "SELECT * FROM ( SELECT qwrp.*, ROWNUM AS rn "
											+ "FROM (" + queryBase + ") qwrp) "
							+ " WHERE rn BETWEEN :pagination_from AND :pagination_to ";
				parametrosInternos.put("pagination_from", ((page-1)*rows+1));
				parametrosInternos.put("pagination_to",   (page*rows) );				
			}
			//SETEO DE PARAMETROS
			SQLQuery q = sessionFactory.getCurrentSession().createSQLQuery(queryBase);
			setParameters(q, parametrosInternos);
			//EVALUACION DE TIPO DE RESPUESTA ESPERADA Y EJECUCION
			if(responseType==null || responseType==GridWrapper.class || responseType==JsonArray.class || Iterable.class.isAssignableFrom(responseType)) {
				q.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
			} else {
				q.addEntity(responseType);
				return q.list();
			}
			
			if(dateFormat==null || dateFormat.isEmpty()) dateFormat = "dd-MM-yyyy HH:mm:ss";
			DateFormat df = new SimpleDateFormat(dateFormat);
			
			@SuppressWarnings("unchecked")
			ArrayList<HashMap<String, Object>> filas =  (ArrayList<HashMap<String, Object>>) q.list();
			if(responseType==JsonArray.class) {
				//CONVERSION A JSON
				JsonArray array = new JsonArray();
				if (filas.isEmpty()) return array;
				for (HashMap<String, Object> columnas: filas) {
					JsonObject json = new JsonObject();
					for (String columna : columnas.keySet()) {
						if(columnas.get(columna)!=null){
						Class<?> c = (columnas.get(columna)).getClass();
							if 		(c==BigDecimal.class) 	json.addProperty(columna, (Number) columnas.get(columna) );
							else if (c==String.class) 		json.addProperty(columna, (String) columnas.get(columna) );
							else if (c==Timestamp.class) 	json.addProperty(columna, df.format((Timestamp) columnas.get(columna)));
						} else {
							json.addProperty(columna, "");
						}
					}
					array.add(json);
				}
				return array;
			}
			
			if(responseType==GridWrapper.class) {
				Long records = this.countByFilters(queryStr, parameters, conditions);
				return new GridWrapper<HashMap<String,Object>>(page, rows, records, (Collection<HashMap<String,Object>>)filas);
			}
			
			if(responseType==Collection.class) {
				return filas;
			} else {
				//CONVERSION A XML
				DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

				// root elements
				Document doc = docBuilder.newDocument();
				Element rootElement = doc.createElement("ROWSET");
				doc.appendChild(rootElement);
				for (HashMap<String, Object> columnas: filas) {
					Element row = doc.createElement("ROW");
					rootElement.appendChild(row);
					for (String columna : columnas.keySet()) {
						Element e = doc.createElement(columna);
						if(columnas.get(columna)!=null){
							Class<?> c = (columnas.get(columna)).getClass();
							if 		(c==BigDecimal.class) 	e.appendChild(doc.createTextNode(((Number) columnas.get(columna)).toString()));
							else if (c==String.class) 		e.appendChild(doc.createTextNode((String) columnas.get(columna)));
							else if (c==Timestamp.class) 	e.appendChild(doc.createTextNode(df.format((Timestamp) columnas.get(columna))));
						}
						row.appendChild(e);
					}
				}
				
				StringWriter writer = new StringWriter();
				
				// write the content into xml file
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(doc);
				transformer.transform(source, new javax.xml.transform.stream.StreamResult(writer));
				return writer.toString();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new InternalErrorException(e);
		}
	}


	/** Búsqueda por filtros para querys nativas
	 * @param queryStr    Query base se requiere que termine en una condicion (por ejemplo: WHERE 1=1 ). Puede tener parámetros
	 * @param parameters  Listado de parámetros <nombre_parametro, valor>
	 * @param conditions  Lista de conditionEntry (si es null no aplica condiciones)
	 * @param page	      Nro de pagina a mostrar, comenzando por 1 (si es null no aplica paginación)
	 * @param rows		  Cantidad de filas por página (si es null no aplica paginación)
	 * @param orderBy     Campo por el que se desea ordenar los resultados (si es null aplica un tamaño de página de 25)
	 * @param orderMode   Sentido por el que se desea ordenar los resultados (si es null aplica ascendente)
	 * @param responseType 	  Tipo de respuesta esperada, puede ser Collection.class, JsonArray.class, GridWrapper.class o un dto personalizado.
	 * @return Arreglo de objetos del tipo responseType
	 * @throws BaseException
	 */
	@Transactional
	public <T> Object findByFilters(String queryStr, 
											Map<String, Object> parameters,
											List<ConditionEntry> conditions,
											Integer page, Integer rows, 
											String orderBy, String orderMode, 
											Class<T> responseType) throws BaseException {
		return findByFilters(queryStr, parameters, conditions, page, rows, orderBy, orderMode, responseType, null);
	}
	
	/** Búsqueda por filtros para querys nativas
	 * @param queryStr    Query base se requiere que termine en una condicion (por ejemplo: WHERE 1=1 ). Puede tener parámetros
	 * @param parameters  Listado de parámetros <nombre_parametro, valor>
	 * @param conditions  Lista de conditionEntry (si es null no aplica condiciones)
	 * @param page	      Nro de pagina a mostrar, comenzando por 1 (si es null no aplica paginación)
	 * @param rows		  Cantidad de filas por página (si es null no aplica paginación)
	 * @param orderBy     Campo por el que se desea ordenar los resultados (si es null aplica un tamaño de página de 25)
	 * @param orderMode   Sentido por el que se desea ordenar los resultados (si es null aplica ascendente)
	 * @return JsonArray con los resultados
	 * @throws BaseException
	 */
	@Transactional
	public JsonArray findJsonByFilters(String queryStr, 
											Map<String, Object> parameters,
											List<ConditionEntry> conditions,
											Integer page, Integer rows, 
											String orderBy, String orderMode) throws BaseException {
		return (JsonArray) findByFilters(queryStr, parameters, conditions, page, rows, orderBy, orderMode, JsonArray.class, null);
	}
	
	/** Carga una entidad en base a los parámetros y las condiciones, si existen mas de una arroja una InternalErrorException
	 * si no encuentra ninguna, arroja null.
	 * @param queryStr    Query base se requiere que termine en una condicion (por ejemplo: WHERE 1=1 ). Puede tener parámetros
	 * @param parameters  Listado de parámetros <nombre_parametro, valor>
	 * @param conditions  Lista de conditionEntry (si es null no aplica condiciones)
	 * @throws BaseException
	 */
	@Transactional
	public Object loadByFilters(String queryStr, 
											Map<String, Object> parameters,
											List<ConditionEntry> conditions) throws BaseException {
		@SuppressWarnings("unchecked")
		Collection<Object> resultado = (Collection<Object>) findByFilters(queryStr, parameters, conditions, null, null, null, null, Collection.class, null);
		if(resultado.isEmpty()) return null;
		if(resultado.size()==1) return resultado.iterator().next();
		throw new InternalErrorException("Se encontraron múltiples resultados para la consulta.");
	}
	
	
	
	/* ************************************************************************ */
	/* METODOS DE RESOLUCION                                 					*/
	/* ************************************************************************ */
	
	private void setParameters(SQLQuery q, Map<String, Object> parameters) {
		if(parameters==null || parameters.isEmpty()) return;
		Set<String> prms = parameters.keySet();
		for (String p : prms) {
			Object value = parameters.get(p);
			if(value==null) q.setString(p, null);
			if(String.class.isInstance(value)) 		q.setString(	p, 	(String) value);
			if(Integer.class.isInstance(value)) 	q.setInteger(	p, 	(Integer) value);
			if(Long.class.isInstance(value)) 		q.setLong(		p, 	(Long) value);
			if(Date.class.isInstance(value)) 		q.setDate(		p, 	(Date) value);
			if(BigInteger.class.isInstance(value)) 	q.setBigInteger(p, 	(BigInteger) value);
			if(BigDecimal.class.isInstance(value)) 	q.setBigDecimal(p, 	(BigDecimal) value);
			if(Boolean.class.isInstance(value)) 	q.setBoolean(	p, 	(Boolean) value);
			if(Float.class.isInstance(value)) 		q.setFloat(		p, 	(Float) value);
		}
		
	}

	protected String getQueryBase ( String sqlQuery, List<ConditionEntry> conditions, Map<String, Object> parameters) throws BaseException {
		String qry = sqlQuery;
		if(conditions!=null){
			qry = "SELECT qb.* FROM (" + sqlQuery + ") qb WHERE 1=1 ";
			for (ConditionEntry conditionEntry : conditions) {
				String condicion = conditionEntry.resolveNativeQuery(parameters);
				qry += " AND " + condicion;
			}
		}
		return qry;
	}
	
}