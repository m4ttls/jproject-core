package com.curcico.jproject.core.daos;

import java.net.URLDecoder;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;

import com.curcico.jproject.core.daos.ConditionComplex.Operator;
import com.curcico.jproject.core.utils.ReflectionUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public abstract class ConditionEntry extends Condition {

	enum FilterType
	{
	  SIMPLE, ARRAY, GROUP, EMPTY, INVALID 
	}
	
	public abstract Criterion resolve(Criteria criteria, Set<ManagerAlias> alias,
			Map<String, String> translations);
	
	public abstract String resolveNativeQuery(Map<String, Object> parameters);

	public static String getFieldWithAlias(String path) {
		if(path.contains(".")){
			String fields[] = path.split("\\.");
			return fields[fields.length-2]+"."+fields[fields.length-1];
		}else{
			return path;
		}
	}
	/** Interpreta y transforma el string de los filtros a un listado de ConditionEntry
	 * @param clase
	 * @param filters
	 * @return
	 * @throws Exception
	 */
	public static List<ConditionEntry> transformFilters(Class<?> clase, String filters) 
			throws Exception {
		
		List<ConditionEntry> conditions = new ArrayList<ConditionEntry>();
		if(filters != null && !filters.equals("")){
			filters = URLDecoder.decode(filters, "UTF-8");
			JsonParser jsonParser = new JsonParser();
			JsonElement e = jsonParser.parse(filters);
			JsonObject jObj;
		    switch (ConditionEntry.getFilterType(filters))
		    {
		      case SIMPLE:
		    	  jObj = (JsonObject)e;
		    	  conditions.add(conditionSimpleParser(clase, jObj));
		    	  break;
		    	  
		      case GROUP:
		    	  jObj = (JsonObject)e;
					String groupOp  	= jObj.get("groupOp").getAsString();
					JsonArray rules 	= jObj.getAsJsonArray("rules");
					JsonArray groups 	= jObj.getAsJsonArray("groups");
					ConditionComplex cc = new ConditionComplex((groupOp.equalsIgnoreCase("OR")?Operator.OR:Operator.AND));
					List<ConditionEntry> subconditions = ConditionEntry.transformFilters(clase, (rules==null?null:rules.toString()));
					cc.getConditions().addAll(subconditions);
					if(groups!=null){
						for (int i = 0; i < groups.size(); i++) {
							List<ConditionEntry> subgroups = ConditionEntry.transformFilters(clase, groups.get(i).toString());
							cc.getConditions().addAll(subgroups);				
						}
					}
					conditions.add(cc);
			    	break;
		    	  
		      case ARRAY:
					JsonArray array = (JsonArray) e;
					for (int i = 0; i < array.size(); i++) {
						jObj = (JsonObject) array.get(i);
						conditions.add(conditionSimpleParser(clase, jObj));
					}
			    	break;
			    	
		      case EMPTY:
		    	  break;

		      case INVALID:
					throw new InvalidParameterException("los.parametros.obligatorios.no.pueden.ser.null");
					
		    }
		}
		return conditions;
	}
	
	public static FilterType getFilterType (String filters) throws Exception {

		if(filters != null && !filters.equals("")){
			JsonParser jsonParser = new JsonParser();
			JsonElement e = jsonParser.parse(filters);
			if(e.isJsonObject()){
				JsonObject jObj = (JsonObject)e;
				if(jObj.has("groupOp")){
					return FilterType.GROUP;
				} else if(jObj.has("field")){
					return FilterType.SIMPLE;
				} else {
					return FilterType.INVALID;
				}
			} else if (e.isJsonArray()){
				return FilterType.ARRAY;
			}
		}
		return FilterType.EMPTY;
	}
	
	
	public static ConditionSimple conditionSimpleParser(Class<?> clase, JsonObject jsonObject) throws Exception{
		String field 	= jsonObject.get("field")==null?null:jsonObject.get("field").getAsString();
		String op 		= jsonObject.get("op")   ==null?null:jsonObject.get("op").getAsString();
		String data		= jsonObject.get("data") ==null?null:jsonObject.get("data").getAsString();
		SearchOption searchOp = SearchOption.getSearchOption(op);
		return getConditionSimple(clase, field, searchOp, data);
	}
	
	/** Devuelve la condicion simple que representa la operacion indicada
	 * @param clase
	 * @param field
	 * @param searchOp
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static ConditionSimple getConditionSimple (Class<?> clase, String field, SearchOption searchOp, String data) throws Exception {
		Object value = null;
		if(clase==null || field==null || searchOp==null){
			throw new InvalidParameterException("los.parametros.obligatorios.no.pueden.ser.null");
		}
		if(SearchOption.isRequiredFieldValue(searchOp)){
			if(SearchOption.isRequiredArrayValue(searchOp)){
				value = transformJsonToList(clase, field, data);
			} else {
				value = ReflectionUtils.castField(clase, field, data);
			}
			if (value == null) {
				throw new InvalidParameterException("la.operacion.requiere.que.el.campo.data.no.sea.nulo");
			}
		}
		return new ConditionSimple(field, searchOp, value);	
	} 
	
	public static List<Object> transformJsonToList(Class<?> clase, String field, String data) 
			throws Exception {
		List<Object> lista = new ArrayList<Object>();
		JsonParser jsonParser = new JsonParser();
		JsonElement e = jsonParser.parse(data);
		if (e.isJsonArray()){
			JsonArray array = (JsonArray) e;
			for (int i = 0; i < array.size(); i++) {
				String obj = array.get(i).getAsString();
				lista.add(ReflectionUtils.castField(clase, field, obj));
			}
		}
		return lista;
	}
	
	public static String acumulateFilters(String filters01, String filters02) throws Exception {
		JsonObject o 	 = new JsonObject();
		JsonArray rules  = new JsonArray();
		JsonArray groups = new JsonArray();
		o.addProperty("groupOp", "AND");
		JsonParser jsonParser = new JsonParser();
		FilterType filterType01 = getFilterType(filters01); 
		FilterType filterType02 = getFilterType(filters02);
	    switch (filterType01) {
	      case SIMPLE: rules.add(jsonParser.parse(filters01)); 				 break;
	      case ARRAY:  rules.addAll((JsonArray)jsonParser.parse(filters01)); break;
	      case GROUP:  groups.add(jsonParser.parse(filters01)); 			 break;
	      case INVALID: throw new InvalidParameterException("los.parametros.obligatorios.no.pueden.ser.null");
	      default:
	    	  if (filterType02!=FilterType.EMPTY)
	    		  return filters02; 
	    }
	    switch (filterType02) {
	      case SIMPLE: rules.add(jsonParser.parse(filters02)); 				 break;
	      case ARRAY:  rules.addAll((JsonArray)jsonParser.parse(filters02)); break;
	      case GROUP:  groups.add(jsonParser.parse(filters02)); 			 break;
	      case INVALID: throw new InvalidParameterException("los.parametros.obligatorios.no.pueden.ser.null");

	      default: 
	    	  if (filterType01!=FilterType.EMPTY)
	    		  return filters01;
	    }
  	    if (filterType01!=FilterType.EMPTY && filterType02!=FilterType.EMPTY) {
  	    	o.add("rules", rules);
  	    	o.add("groups", groups);
  	    	return o.toString();  	    	
  	    } 
    	return null;

	    
	}
	
	public static List<ConditionEntry> transformNativeFilters(String filters) 
			throws Exception {
		
		List<ConditionEntry> conditions = new ArrayList<ConditionEntry>();
		if(filters != null && !filters.equals("")){
			filters = URLDecoder.decode(filters, "UTF-8");
			
			JsonParser jsonParser = new JsonParser();
			JsonElement e = jsonParser.parse(filters);
			if(e.isJsonObject()){
				JsonObject jObj = (JsonObject)e;
				if(jObj.has("groupOp")){
					String groupOp  	= jObj.get("groupOp").getAsString();
					JsonArray rules 	= jObj.getAsJsonArray("rules");
					JsonArray groups 	= jObj.getAsJsonArray("groups");
					ConditionComplex cc = new ConditionComplex((groupOp.equalsIgnoreCase("OR")?Operator.OR:Operator.AND));
					List<ConditionEntry> subconditions = transformNativeFilters((rules==null?null:rules.toString()));
					cc.getConditions().addAll(subconditions);
					if(groups!=null){
						for (int i = 0; i < groups.size(); i++) {
							List<ConditionEntry> subgroups = transformNativeFilters(groups.get(i).toString());
							cc.getConditions().addAll(subgroups);				
						}
					}
					conditions.add(cc);
				} else if(jObj.has("field")){
					conditions.add(conditionSimpleNativeParser(jObj));
				} else {
					throw new InvalidParameterException("los.parametros.obligatorios.no.pueden.ser.null");
				}
			} else if (e.isJsonArray()){
				JsonArray array = (JsonArray) e;
				for (int i = 0; i < array.size(); i++) {
					JsonObject jObj = (JsonObject) array.get(i);
					conditions.add(conditionSimpleNativeParser(jObj));
				}
			}
		}
		return conditions;
	}
	
	public static ConditionSimple conditionSimpleNativeParser(JsonObject jsonObject) throws Exception{
		String field 	= jsonObject.get("field")==null?null:jsonObject.get("field").getAsString();
		String op 		= jsonObject.get("op")   ==null?null:jsonObject.get("op").getAsString();
		String data		= jsonObject.get("data") ==null?null:jsonObject.get("data").getAsString();
		SearchOption searchOp = SearchOption.getSearchOption(op);
		return new ConditionSimple(field, searchOp, data);
	}
	
	public static boolean isDate(String value){
		 Pattern p_utc = Pattern.compile("(20|19)\\d\\d-(0[1-9]|1[012])-([012][0-9]|3[01])T([01][0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])Z");
		 Pattern p_standard = Pattern.compile("(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[012])/((19|20)\\d\\d)");	 
		 Pattern p_inverted = Pattern.compile("((19|20)\\d\\d)-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])");	 

		 Matcher m_utc 		= p_utc.matcher(value);
		 Matcher m_standard = p_standard.matcher(value);		 
		 Matcher m_inverted = p_inverted.matcher(value);
		 
		 return m_utc.matches() || m_standard.matches() || m_inverted.matches();
	}
		
}
