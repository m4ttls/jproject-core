package com.curcico.jproject.core.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.WordUtils;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import com.curcico.jproject.core.entities.BaseEntity;
import com.curcico.jproject.core.exception.BaseException;
import com.curcico.jproject.core.exception.BusinessException;
import com.curcico.jproject.core.exception.InternalErrorException;
import com.curcico.jproject.core.exception.ReflectionException;


public class ReflectionUtils {

		// Logger
	private static Logger logger = Logger.getLogger(ReflectionUtils.class);

		// Metodos
	public static Object getInstance(String className) throws ReflectionException {
		try {
			Class<?> clazz = Class.forName(className);
			Object object = clazz.newInstance();
			return object;
		} catch (ClassNotFoundException e) {
			logger.error(e.getMessage());
			throw new ReflectionException(e);
		} catch (InstantiationException e) {
			logger.error(e.getMessage());
			throw new ReflectionException(e);
		} catch (IllegalAccessException e) {
			logger.error(e.getMessage());
			throw new ReflectionException(e);
		} catch (SecurityException e) {
			logger.error(e.getMessage());
			throw new ReflectionException(e);
		} catch (IllegalArgumentException e) {
			logger.error(e.getMessage());
			throw new ReflectionException(e);
		}
	}

	public static Object getInstance(String className, Class<?> type, Object param) throws ReflectionException {
		try {
			Class<?> clazz = Class.forName(className);
			Class<?>[] types = { type };
			Constructor<?> constructor = clazz.getConstructor(types);
			Object[] params = { param };
			return constructor.newInstance(params);
		} catch (ClassNotFoundException e) {
			logger.error(e.getMessage());
			throw new ReflectionException(e);
		} catch (InstantiationException e) {
			logger.error(e.getMessage());
			throw new ReflectionException(e);
		} catch (IllegalAccessException e) {
			logger.error(e.getMessage());
			throw new ReflectionException(e);
		} catch (NoSuchMethodException e) {
			logger.error(e.getMessage());
			throw new ReflectionException(e);
		} catch (SecurityException e) {
			logger.error(e.getMessage());
			throw new ReflectionException(e);
		} catch (IllegalArgumentException e) {
			logger.error(e.getMessage());
			throw new ReflectionException(e);
		} catch (InvocationTargetException e) {
			logger.error(e.getMessage());
			throw new ReflectionException(e);
		}
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
		logger.debug("Respuesta: " + clase.getCanonicalName());
		return clase;
	}
	
	public static List<Field> getAllFields(List<Field> fields, Class<?> type) {
	    for (Field field: type.getDeclaredFields()) {
	        fields.add(field);
	    }
	    if (type.getSuperclass() != null) {
	        fields = getAllFields(fields, type.getSuperclass());
	    }
	    return fields;
	}
	
	public static Object castField(Class<?> clase, String fieldName, String field, SimpleDateFormat formatter) throws ReflectionException{
		Class<?> fiedType = getCast(clase, fieldName);
		
		try {
			if(fiedType == String.class){
				return field;
			}else if(fiedType == Integer.class){
				return Integer.parseInt(field);
			}else if(fiedType == Long.class){
				return Long.parseLong(field);
			}else if(fiedType == Float.class){
				return Float.parseFloat(field);
			}else if(fiedType == Double.class){
				return Double.parseDouble(field);
			}else if(fiedType == BigDecimal.class){
				return new BigDecimal(field);
			}else if(fiedType == Boolean.class){
				return Boolean.parseBoolean(field);
			}else if(fiedType == java.util.Date.class || fiedType == java.sql.Date.class || fiedType == Timestamp.class ){
				if(formatter == null){
					String pattern = "([0-9]){2}-([0-9]){2}-([0-9]){4}";
					if (field.matches(pattern)) {
						formatter = new SimpleDateFormat("dd-MM-yyyy");
					}else{
						formatter = new SimpleDateFormat("dd/MM/yyyy");
					}
				}
				
				return formatter.parse(field);
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
	
	public static Object castField(Class<?> clase, String fieldName, String field) throws ReflectionException {
		return castField(clase, fieldName, field, null);
	}

	
	/**
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
		} catch (NoSuchMethodException | SecurityException e) {
			logger.error(e.getMessage(), e);
			throw new BusinessException ("internal.error", e);
		} catch (IllegalAccessException e) {
			logger.error(e.getMessage(), e);
			throw new BusinessException ("internal.error", e);
		} catch (IllegalArgumentException e) {
			logger.error(e.getMessage(), e);
			throw new BusinessException ("internal.error", e);
		} catch (InvocationTargetException e) {
			logger.error(e.getMessage(), e);
			throw new BusinessException ("internal.error", e);
		}
	}
	
	public static Object getFieldValueByReflection(Class<?> clase, Object entity, String field) throws BaseException{
		String metodName = "get" + WordUtils.capitalize(field);
		try {
			Method method = clase.getDeclaredMethod(metodName);
			Object o = method.invoke(entity);
			return o;
		} catch (IllegalAccessException e){
			logger.error(e.getMessage(), e);
			throw new InternalErrorException(e);
		} catch (NoSuchMethodException e) {
			logger.error(e.getMessage(), e);
			throw new InternalErrorException(e);
		} catch (SecurityException e) {
			logger.error(e.getMessage(), e);
			throw new InternalErrorException(e);
		} catch (IllegalArgumentException e) {
			logger.error(e.getMessage(), e);
			throw new InternalErrorException(e);
		} catch (InvocationTargetException e) {
			logger.error(e.getMessage(), e);
			throw new InternalErrorException(e);
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
