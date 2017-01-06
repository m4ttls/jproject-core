package com.curcico.jproject.core.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;

import org.apache.commons.lang.WordUtils;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import com.curcico.jproject.core.entities.BaseEntity;
import com.curcico.jproject.core.exception.BaseException;
import com.curcico.jproject.core.exception.InternalErrorException;
import com.curcico.jproject.core.exception.ReflectionException;

/**
 * @author acurci
 *
 */
public class ReflectionUtils {

	private static Logger logger = Logger.getLogger(ReflectionUtils.class);
	
	// Allowed formats
	private static String allowedDateFormats[] = new String[]{"yyyy-MM-dd'T'HH:mm:ss.S'Z'", "dd/MM/yyyy", "dd-MM-yyyy"};
	
	/** Devuelve una instancia de la clase por reflexión
	 * @param className
	 * @return
	 * @throws ReflectionException
	 */
	public static Object getInstance(String className) throws ReflectionException {
		try {
			Class<?> clazz = Class.forName(className);
			Object object = clazz.newInstance();
			return object;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new ReflectionException(e);
		}
	}
	
	/** Devuelve una instancia de la clase por reflexión
	 * @param className
	 * @param type
	 * @param param
	 * @return
	 * @throws ReflectionException
	 */
	public static Object getInstance(String className, Class<?> type, Object param) throws ReflectionException {
		try {
			Class<?> clazz = Class.forName(className);
			Class<?>[] types = { type };
			Constructor<?> constructor = clazz.getConstructor(types);
			Object[] params = { param };
			return constructor.newInstance(params);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new ReflectionException(e);
		}
	}
	
	/**
	 * Obtiene la anotacion column de un campo
	 * @param clase
	 * @param field
	 * @return
	 */
	public static String getAnnotationColumn(Class<?> clase, String field){
		
		logger.debug("Llamado: " + clase.getCanonicalName() + " - " + field);
		String[] ruta = (field.split("\\."));
		String   atributo = ruta.length>0?ruta[0]:field;
		String   rutaRestante = null;
		
		if(ruta.length>1){
			rutaRestante = field.substring(atributo.length()+1);
		}
		List<Field> fields = new ArrayList<Field>();
		for (Field f : getAllFields(fields, clase)){
			logger.debug("Analizo campo: " + f.getName());
			if(f.getName().equals(atributo)){
				Class<?> nuevaClase = f.getType();
				if(rutaRestante != null) {
				//if(AbstractEntity.class.isAssignableFrom(nuevaClase)){
					return getAnnotationColumn(nuevaClase, rutaRestante);
				} else {
					return nuevaClase.getAnnotation(Column.class).name();
				}
			}
		}
		return "";
	}	
	
	/** Retorna el tipo de dato correspondiente al campo de la clase pasados como parámetros
	 * @param clase
	 * @param field
	 * @return
	 */
	public static Class<?> getCast(Class<?> clase, String field){
		
		logger.debug("Llamado: " + clase.getCanonicalName() + " - " + field);
		String[] ruta = (field.split("\\."));
		String   atributo = ruta.length>0?ruta[0]:field;
		String   rutaRestante = null;
		
		if(ruta.length>1){
			rutaRestante = field.substring(atributo.length()+1);
		}
		List<Field> fields = new ArrayList<Field>();
		for (Field f : getAllFields(fields, clase)){
			if(f.getName().equals(atributo)){
				Class<?> nuevaClase = f.getType();
				logger.debug("Analizo campo: " + f.getName() + " - " + nuevaClase.getName());
				
				if(rutaRestante != null) {
					if(ReflectionUtils.isCollectionField(atributo, clase)) {
						return getCast(ReflectionUtils.getParameterizedTypeCollection(f), rutaRestante);
					} else {
						if(BaseEntity.class.isAssignableFrom(nuevaClase))
							return getCast(nuevaClase, rutaRestante);
						else 
							return nuevaClase;
					}
				} else {
					if(ReflectionUtils.isCollectionField(atributo, clase))
						nuevaClase = ReflectionUtils.getParameterizedTypeCollection(f);
					logger.debug("Respuesta: " + nuevaClase.getCanonicalName());
					return nuevaClase;
				}
			}
		}
		logger.debug("NO ENCONTRADO: " + clase.getCanonicalName());
		return null;
	}
	
	/** Devuelve todos los campos del objeto (incluso los de las superclases)
	 * @param fields
	 * @param type
	 * @return
	 */
	public static List<Field> getAllFields(List<Field> fields, Class<?> type) {
	    for (Field field: type.getDeclaredFields()) {
	        fields.add(field);
	    }
	    if (type.getSuperclass() != null) {
	        fields = getAllFields(fields, type.getSuperclass());
	    }
	    return fields;
	}
	
	/** Dada la clase, el atributo y un valor como string, castea este ultimo al tipo de dato
	 * correspondiente al atributo informado
	 * @param clase
	 * @param fieldName
	 * @param field
	 * @param formatter
	 * @return
	 * @throws ReflectionException
	 */
	public static Object castField(Class<?> clase, String fieldName, String field, DateFormat formatter) throws ReflectionException{
		Class<?> fieldType = getCast(clase, fieldName);
		
	try {
		if(fieldType == String.class){
			return field;
		}else if(fieldType == Integer.class){
			return Integer.parseInt(field);
		}else if(fieldType == Long.class){
			return Long.parseLong(field);
		}else if(fieldType == Float.class){
			return Float.parseFloat(field);
		}else if(fieldType == Double.class){
			return Double.parseDouble(field);
		}else if(fieldType == BigDecimal.class){
			return new BigDecimal(field);
		}else if(fieldType == Boolean.class){
			return Boolean.parseBoolean(field);
		}else if(fieldType == java.util.Date.class || fieldType == java.sql.Date.class || fieldType == Timestamp.class ){
				
				Object possibleDate =  parseDateWithMultiFormat(field, allowedDateFormats);
				
				// Si no tiene un formato de los perviamente establecidos, 
				// y paso como parametro un formatter, pruebo con ese
				if (possibleDate == null && formatter != null){
					return formatter.parse(field);
				}
				
				// La parseo correctamente
				if (possibleDate != null){
					return possibleDate;
				}
				
				// Como es nula, no la pudo parsear => Error de Parseo
				throw new java.text.ParseException("Formato de fecha incorrecto.", 0);
			
		}else if(java.lang.Enum.class.isAssignableFrom(fieldType)){
			Object[] enumValues = fieldType.getEnumConstants();
			for (int i = 0; i < enumValues.length; i++) {
				if(enumValues[i].toString().equals(field))
					return enumValues[i];
			}
		}	
	} catch (SecurityException e) {
		logger.error(e.getMessage());
		throw new ReflectionException(e);
	} catch (ParseException e) {
		logger.error(e.getMessage());
		throw new ReflectionException(e);
	}
	return null;
	}

	/** Dada la clase, el atributo y un valor como string, castea este ultimo al tipo de dato
	 * correspondiente al atributo informado
	 * @param clase
	 * @param fieldName
	 * @param field
	 * @return Objeto casteado al tipo correspondiente
	 * @throws ReflectionException
	 */
	public static Object castField(Class<?> clase, String fieldName, String field) 
			throws ReflectionException{
		//SimpleDateFormat formatter = new SimpleDateFormat("dd-mm-yyyy");
		DateFormat m_ISO8601Local = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		// para forzar que valide parametros de fecha en rango (mes no puede ser 13)
		m_ISO8601Local.setLenient(false);
		return castField(clase, fieldName, field, m_ISO8601Local);
	}

	
//	public static Object castField(Class<?> clase, String fieldName, String field) throws ReflectionException {
//		return castField(clase, fieldName, field, null);
//	}

	/**
	 * Intenta parsear la fecha con los formatos indicados
	 * @param posibleDate
	 * @param formats
	 * @return Un objeto Date en caso de poder pasearlo, null en otro caso
	 */
	public static Object parseDateWithMultiFormat(String posibleDate, String[] formats) {
		
		Object retVal = null;
		for (String format : formats) {
			try {
				retVal = parseDateWithFormat(posibleDate, format);
				break;
			} catch (ParseException e) {
				// No pudo parsearlo, prueba con otro formato
				continue;
			}
		}
		return retVal;
	}
	
	/**
	 * Parsea una fecha con un formato dado
	 * @param date
	 * @param format
	 * @return
	 * @throws ParseException
	 */
	private static Object parseDateWithFormat(String date, String format) throws ParseException  {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		dateFormat.setLenient(false); // Formato exacto
		return dateFormat.parse(date);
	}
	
	/** Invoca al método set del campo por reflextion con el objeto value como parametro
	 * @param Auxdocument
	 * @param field
	 * @param value
	 * @throws BaseException 
	 */
	public static void setFieldValueByReflection(Class<?> clase, Object entity, String field, Object value) throws BaseException {
		try {
			String metodSetName = "set" + WordUtils.capitalize(field);
			@SuppressWarnings("rawtypes")
			Class[] param = new Class[1];
			param[0] = value.getClass();
			Method method = clase.getDeclaredMethod(metodSetName, param);
			method.invoke(entity, value);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new InternalErrorException ("internal.error", e);
		}
	}
	
	/** Invoca al método get del campo por reflextion y retorna su valor
	 * @param clase
	 * @param entity
	 * @param field
	 * @return
	 * @throws BaseException
	 */
	public static Object getFieldValueByReflection(Class<?> clase, Object entity, String field) 
			throws BaseException{
		String metodName = "get" + WordUtils.capitalize(field);
		try {
			Method method = clase.getDeclaredMethod(metodName);
			Object o = method.invoke(entity);
			return o;
		} catch (Exception e){
			throw new InternalErrorException("internal.error", e);
		}
	}
	
	public static Set<String> getClassesFromPackage(String packageName) throws ReflectionException {
		try {
			ReflectionClassUtils reflections = new ReflectionClassUtils(packageName);
			return reflections.getClassesFromPackage();
		
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new ReflectionException(e);
		}
	}

	/** Verifica si el campo corresponde a una colección
	 * @param aliasName
	 * @param typeParameterClass
	 * @return
	 */
	public static boolean isCollectionField(String aliasName, Class<?> typeParameterClass) {
		
		String methodName = "get" + StringUtils.capitalize(aliasName);
		Method[] methods  = typeParameterClass.getMethods();
		for (int i = 0; i < methods.length; i++) {
			if(methods[i].getName().equals(methodName)){
				return Collection.class.isAssignableFrom(methods[i].getReturnType());
			}
		}
		return false;
	}	

	/**
	 * Obtiene el tipo con el que se instancia la coleccion 
	 * @param f
	 * @return
	 */
	public static Class<?> getParameterizedTypeCollection(Field f) {
		ParameterizedType stringListType = (ParameterizedType) f.getGenericType();
	    Class<?> stringListClass = (Class<?>) stringListType.getActualTypeArguments()[0];
	    return stringListClass;
	}
	
	/** Retorna un mapa con la definición de la clase de la forma (atributo, tipo de dato) 
	 * @param clase
	 * @return
	 */
	public static Map<String, String> getDefinition(
			Class<?> clase) {
		Map<String, String> definicion = new HashMap<String, String>();
		if(clase!=null){
			List<Field> fields = new ArrayList<Field>();
			for (Field f : getAllFields(fields, clase)){
				logger.info(f.getName() + " " + f.getType().getCanonicalName());
				definicion.put(f.getName(), f.getType().getCanonicalName());
			}
		}
		return definicion;
	}	
}