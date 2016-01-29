package com.curcico.jproject.core.daos;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
/**
 * Clase que abarca los métodos 
 * comunes para realizar las consultas
 * 
 * @author dumar
 *
 */
public abstract class Condition {

	/**
	 * Permite actualizar el objeto alias, agregandole sólo
	 * los alias que se crearon automaticamente y que no
	 * fueron definidos por el dao
	 * 
	 * @param alias
	 * @param aliasByColumn
	 */
	protected void updateAlias(Set<ManagerAlias> alias,
			Set<ManagerAlias> aliasByColumn) {
		if (!alias.isEmpty()) {
			Set<ManagerAlias> aliasResult = new LinkedHashSet<ManagerAlias>();
			aliasResult.addAll(alias);
			aliasResult.retainAll(aliasByColumn);

			aliasResult.addAll(aliasByColumn);
			aliasByColumn.clear();
			aliasByColumn.addAll(aliasResult);
			alias.removeAll(aliasResult);
		}
	}

	/**
	 * Se agregan al criteria los alias nuevos, además
	 * se agregan las traducciones de los mismos.
	 * Al final todos los alias se agregan al objeto alias
	 * 
	 * @param criteria
	 * @param alias
	 * @param aliasByColumn
	 * @param translations
	 */
	protected void addAlias(Criteria criteria, Set<ManagerAlias> alias,
			Set<ManagerAlias> aliasByColumn, Map<String, String> translations) {
		updateAlias(alias, aliasByColumn);

		for (ManagerAlias managerAlias : aliasByColumn) {
			if (!managerAlias.isInserted()) {
				criteria.createAlias(managerAlias.getPath(),
						managerAlias.getAlias(), managerAlias.getJoinType());
				translations.put(managerAlias.getPath(),
						managerAlias.getAlias());
				managerAlias.setInserted(true);
			}
		}

		alias.addAll(aliasByColumn);
	}
	
	/**
	 * Se obtienen los alias automaticamente a partir de las
	 * condiciones del query
	 * 
	 * @param columnCondition
	 * @return
	 */
	public Set<ManagerAlias> getAlias(String columnCondition) {
		Set<ManagerAlias> alias = new LinkedHashSet<ManagerAlias>();
		
		if(columnCondition.contains(".")){
			String fields[] = columnCondition.split("\\.");
			int length = fields.length;
			if(this instanceof ConditionEntry){
				length--;
			}
			for (int i = 0; i < length; i++) {
				if(i == 0){
					alias.add(new ManagerAlias(fields[i], fields[i]));
				}else{
					alias.add(new ManagerAlias(getAliasName(fields,i), getAliasName(fields,i-1)+"."+fields[i]));
				}
			}
			return alias;
		}else{
			if(this instanceof ConditionEntry){
				return null;
			}else if(this instanceof ConditionFetch){
				alias.add(new ManagerAlias(columnCondition, columnCondition));
			}
		}
		
		return alias;
	}
	
	protected static String getAliasName(String[] array, int index){
		String result = "";
		for (int i = 0; i < index+1; i++) {
			if(i != index){
				result += array[i] + "_";
			}else{
				result += array[i];
			}
		}
		return result;
	}
	
}
