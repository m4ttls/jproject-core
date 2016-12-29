package com.curcico.jproject.core.utils;

import java.text.ParseException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

/**
 * Clase para evaluar expresiones JavaScript
 * @author nzdanovicz
 *
 */
public class Expression2 {

	private static Logger logger = Logger.getLogger(Expression2.class);
	private static ISO8601DateFormat dateParser = new ISO8601DateFormat();
	
	/**
	 * Evalua la expresión javascript reemplazando tokens por los valores provistos
	 * @param rule Expresión a evaluar. La expresión debe ser del tipo "${areaCode}=='A0F0D00000'"
	 * @param values Mapa con los valores a reemplazar, como clave el token buscado y como valor el objeto a reemplazar en la expresión.
	 * @param systemValues Mapa con los valores de reemplazo por default.
	 * @return resultado de la evaluación de la expresión
	 * @throws Exception ocurrió un error en la evaluación de la expresión.
	 */
	public static boolean eval(String rule, Map<String, Object> values,
			Map<String, Object> systemValues) throws Exception {

		rule = replaceValues(rule, values, systemValues);
		return _eval(rule);

	}

	/**
	 * Evalua la expresión
	 * @param expression expresión a evaluar
	 * @return resultado de la evaluación de la expresión
	 * @throws ScriptException Error en la expresión evaluada
	 */
	private static boolean _eval(String expression) throws ScriptException {
		ScriptEngineManager sem = new ScriptEngineManager();
		ScriptEngine se = sem.getEngineByName("JavaScript");
		logger.debug("_eval expression: "+expression);
		return ((Boolean) se.eval(expression));
	}

	/**
	 * Reemplaza los tokens por valores en la expresión
	 * @param rule expresión con los tokens a reemplazar
	 * @param values valores de reemplazo
	 * @param systemValues valores de reemplazo por default
	 * @return expresión con los valores definitivos.
	 */
	private static String replaceValues(String rule,
			Map<String, Object> values, Map<String, Object> systemValues) {
		String _rule = rule;

		for (String key : values.keySet()) {
			Object x = values.get(key) != null ? values.get(key) : "";

			String replaceKey = "${" + key + "}";
			logger.debug(replaceKey);
			logger.debug(x.getClass().toString());

			if (x instanceof Integer) {
				_rule = _rule.replace(replaceKey, x.toString());
				logger.debug(_rule);
			}

			if (x instanceof Long) {
				_rule = _rule.replace(replaceKey, x.toString());
				logger.debug(_rule);
			}

			if (x instanceof String) {
				_rule = _rule.replace(replaceKey, "'" + x.toString() + "'");
				logger.debug(_rule);
			}

		}

		for (String key : systemValues.keySet()) {
			Object x = systemValues.get(key);

			logger.debug(key);

			if (x instanceof Integer) {
				_rule = _rule.replace(key, x.toString());
				logger.debug(_rule);
			}

			if (x instanceof Long) {
				_rule = _rule.replace(key, x.toString());
				logger.debug(_rule);
			}

			if (x instanceof String) {
				_rule = _rule.replace(key, "'" + x.toString() + "'");
				logger.debug(_rule);
			}
			
			if (x instanceof List) {
				List list = (List) x;
				String allItems = "[" + StringUtils.join(list, ", ") + "]";

				_rule = _rule.replace(key, allItems);
				logger.debug(_rule);
			}
			
		}

		return _rule;
	}

	
	public static String getVariables(LinkedHashMap<String, Object> e, Map<String, Object> valorizaciones) {
		return getVariables(null, e, valorizaciones);
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static String getVariables(String padre, LinkedHashMap<String, Object> e, Map<String, Object> valorizaciones) {
		
    	if(padre==null){
    		padre = "";
    	} else {
    		padre = padre + ".";
    	}
		
		StringBuffer variables = new StringBuffer();
        for(String variable : e.keySet()){
        	if(padre.isEmpty()){
        		variables.append("var ");
        	}
        	variables.append(padre + variable);        		
    		if(e.get(variable)!=null) {
    			
	        	if(e.get(variable) instanceof Number) {
	        		variables.append("=" + e.get(variable) + ";");
	        		valorizaciones.put(padre + variable, e.get(variable));
	        		
	        	} else if(e.get(variable) instanceof Map){
	        		variables.append(" = new Object();");
	        		variables.append(getVariables(padre + variable, (LinkedHashMap) e.get(variable), valorizaciones));
	        	} else if(e.get(variable) instanceof List<?>){
	        		variables.append(" = new Array();");
	        		List l = (List) e.get(variable);
	        		for (int i = 0; i < l.size(); i++) {
						Object elemento = (Object) l.get(i);
		        		variables.append(padre + variable + "[" + i + "] = new Object();");
						variables.append(getVariables(padre + variable + "[" + i + "]", (LinkedHashMap) elemento, valorizaciones));
					}
	        	
	        	} else if(e.get(variable) instanceof Date){
	        		logger.error(e.get(variable));       	
	        	} else if(e.get(variable) instanceof String){
	    			try {
	    				Date d = dateParser.parse((String) e.get(variable)); 
	    				variables.append(" = new Date(" + d.getTime() + ");" );
	    				valorizaciones.put(padre + variable, d);
	    			} catch (ParseException pex){
	    				variables.append("='" + ((String) e.get(variable))
	    						.replaceAll("'", "\\\\u0027")
	    						.replaceAll("\\\\", "\\\\u005C")
	    						.replaceAll("\\\"", "\\\\u0022")
	    						+ "';");
	    				valorizaciones.put(padre + variable, e.get(variable));
	    			}
	        	} else if(e.get(variable) instanceof Boolean){
	        		variables.append("=" + e.get(variable) + ";");
	        		valorizaciones.put(padre + variable, e.get(variable));

	        	} else {
	        		variables.append(" = '';");
	        	}
    		} else {
    			variables.append(" = '';");
    		}
        }
        return variables.toString();
	}
	

}
