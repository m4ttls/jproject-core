package com.curcico.jproject.core.utils;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.curcico.jproject.core.daos.ConditionComplex;
import com.curcico.jproject.core.daos.ConditionComplex.Operator;
import com.curcico.jproject.core.daos.ConditionEntry;
import com.curcico.jproject.core.daos.ConditionSimple;
import com.curcico.jproject.core.daos.SearchOption;
import com.curcico.jproject.core.exception.BaseException;
import com.curcico.jproject.core.exception.BusinessException;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ConditionsUtils {

	
	/** Deprecated, utilizar el ConditionEntry.transformFilters
	 * @param clase
	 * @param filters
	 * @return
	 * @throws BaseException
	 */
	@Deprecated
	public static List<ConditionEntry> transformFilters(Class<?> clase,
			String filters) throws BaseException {
		try {
			List<ConditionEntry> parametersFilters = new ArrayList<ConditionEntry>();
			if (filters != null && !filters.equals("")) {
				boolean filter = true;
				JSONParser jsonParser = new JSONParser();
				JSONObject jsonObject = (JSONObject) jsonParser.parse(filters);
	
				JSONArray lang = (JSONArray) jsonObject.get("filters");
				if (lang == null) {
					filter = false;
					lang = (JSONArray) jsonObject.get("rules");
				}
	
				for (int i = 0; i < lang.size(); i++) {
					jsonObject = (JSONObject) lang.get(i);
	
					String searchField = null;
					String searchOper = null;
					String searchString = null;
					if (filter) {
						searchField = (String) jsonObject.get("searchField");
						searchOper = (String) jsonObject.get("searchOper");
						searchString = (String) jsonObject.get("searchString");
					} else {
						searchField = (String) jsonObject.get("field");
						searchOper = (String) jsonObject.get("op");
						searchString = (String) jsonObject.get("data");
					}
	
					Object value = ReflectionUtils.castField(clase,
							searchField, searchString);
					if (value != null) {
						parametersFilters.add(
								new ConditionSimple(searchField, SearchOption
												.getSearchOption(searchOper), value));
					}
				}
	
				return parametersFilters;
			}
		} catch (Exception e) {
			throw new BusinessException("transformFilters.error");
		}
	
		return null;
	}

	/** Deprecated, utilizar el ConditionEntry.transformFilters
	 * @param clase
	 * @param filters
	 * @return
	 * @throws Exception
	 */
	@Deprecated
	public static List<ConditionEntry> transformFiltersComplex(Class<?> clase,
			String filters) throws Exception {
		List<ConditionEntry> conditions = new ArrayList<ConditionEntry>();
		if (filters != null && !filters.equals("")) {
			JsonParser jsonParser = new JsonParser();
			JsonElement e = jsonParser.parse(filters);
			if (e.isJsonObject()) {
				JsonObject jObj = (JsonObject) e;
				if (jObj.has("groupOp")) {
					String groupOp = jObj.get("groupOp").getAsString();
					JsonArray rules = jObj.getAsJsonArray("rules");
					JsonArray groups = jObj.getAsJsonArray("groups");
					ConditionComplex cc = new ConditionComplex(
							(groupOp.equalsIgnoreCase("OR") ? Operator.OR
									: Operator.AND));
					List<ConditionEntry> subconditions = transformFiltersComplex(
							clase, (rules == null ? null : rules.toString()));
					cc.getConditions().addAll(subconditions);
					if (groups != null) {
						for (int i = 0; i < groups.size(); i++) {
							List<ConditionEntry> subgroups = transformFiltersComplex(
									clase, groups.get(i).toString());
							cc.getConditions().addAll(subgroups);
						}
					}
					conditions.add(cc);
				} else if (jObj.has("field")) {
					conditions.add(ConditionsUtils.conditionSimpleParser(clase, jObj));
				} else {
					throw new InvalidParameterException(
							"los.parametros.obligatorios.no.pueden.ser.null");
				}
			} else if (e.isJsonArray()) {
				JsonArray array = (JsonArray) e;
				for (int i = 0; i < array.size(); i++) {
					JsonObject jObj = (JsonObject) array.get(i);
					conditions.add(ConditionsUtils.conditionSimpleParser(clase, jObj));
				}
			}
		}
		return conditions;
	}

	@Deprecated
	static ConditionSimple conditionSimpleParser(Class<?> clase,
			JsonObject jsonObject) throws Exception {
		String field = jsonObject.get("field") == null ? null : jsonObject.get(
				"field").getAsString();
		String op = jsonObject.get("op") == null ? null : jsonObject.get("op")
				.getAsString();
		String data = jsonObject.get("data") == null ? null : jsonObject.get(
				"data").getAsString();
		SearchOption searchOp = SearchOption.getSearchOption(op);
		return ConditionsUtils.getConditionSimple(clase, field, searchOp, data);
	}

	@Deprecated
	public static ConditionSimple getConditionSimple(Class<?> clase,
			String field, SearchOption searchOp, String data) throws Exception {
		Object value = null;
		if (clase == null || field == null || searchOp == null) {
			throw new InvalidParameterException(
					"los.parametros.obligatorios.no.pueden.ser.null");
		}
		if (SearchOption.isRequiredFieldValue(searchOp)) {
			value = ReflectionUtils.castField(clase, field, data);
			if (value == null) {
				throw new InvalidParameterException(
						"la.operacion.requiere.que.el.campo.data.no.sea.nulo");
			}
		}
		return new ConditionSimple(field, searchOp, value);
	}

	public static List<String> getNameSequence(String strJson)
			throws BaseException {
		List<String> nameSequence = new LinkedList<String>();
		try {
			JSONParser jsonParser = new JSONParser();
			Object obj = jsonParser.parse(strJson);
			JSONArray array = (JSONArray) obj;
			for (Object object : array) {
				nameSequence.add(object.toString());
			}
	
			return nameSequence;
	
		} catch (Exception e) {
			throw new BusinessException("transformFilters.error");
		}
	}

	public static Map<String, String> transformOrderBy(String orderBy) {
		Map<String, String> result = null;
		result = new LinkedHashMap<String, String>();
		String[] array = null;
		if (!orderBy.isEmpty()) {
			if (orderBy != null && orderBy.contains(",")) {
				array = orderBy.split(",");
			} else {
				array = new String[] { orderBy };
			}
			for (int i = 0; i < array.length; i++) {
				String[] tmp = null;
				if (array[i].contains("asc") || array[i].contains("desc")) {
					tmp = array[i].trim().split("\\s");
					result.put(ConditionSimple.transform(tmp[0].trim()), tmp[1]);
				} else {
					result.put(ConditionSimple.transform(array[i].trim()), null);
				}
			}
		}
	
		return result;
	}

	@Deprecated
	public static void addConditionSimple(List<ConditionEntry> filters, Class<?> class1,
			String searchField, String searchOper, String searchString) throws Exception {
		if(filters != null && !filters.isEmpty() && 
				class1 != null && searchField != null && !searchField.isEmpty() &&
				searchString != null && !searchString.isEmpty()){
			filters.add(getConditionSimple(class1, searchField, SearchOption.getSearchOption(searchOper), searchString));
		}
	}

	public static List<ConditionSimple> getFilterByMap(Map<String, Object> parameters) throws BaseException {
		List<ConditionSimple> filters = null;
		if(parameters != null){
			filters = new ArrayList<ConditionSimple>();
			for (Entry<String, Object> e : parameters.entrySet()) {
				filters.add(new ConditionSimple(e.getKey(), SearchOption.EQUAL, e.getValue()));
			}
		}
		return filters;
	}

	public static List<ConditionSimple> getFilterByMapLike(Map<String, Object> parameters) throws BaseException {
		List<ConditionSimple> filters = null;
		if(parameters != null){
			filters = new ArrayList<ConditionSimple>();
			for (Entry<String, Object> e : parameters.entrySet()) {
				filters.add(new ConditionSimple(e.getKey(), SearchOption.CONTAIN, e.getValue()));
			}
		}
		return filters;
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

}
