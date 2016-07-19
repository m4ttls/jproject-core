package com.curcico.jproject.core.daos;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;

import com.curcico.jproject.core.exception.InternalErrorException;

/** Ofrece la posibilidad de agrupar ConditiosEntry bajo un agrupador AND u OR
 * @author alejandro
 *
 */
public class ConditionComplex extends ConditionEntry {

	public enum Operator {
	    OR, AND 
	}
	
	private Operator operator;

	private List<ConditionEntry> conditions;

	/** Constructor de una condición compleja
	 * @param operator AND u OR
	 */
	public ConditionComplex(Operator operator) {
		super();
		this.operator = operator;
		this.conditions = new ArrayList<ConditionEntry>();
	}
	
	/** Contructor
	 * @param operator AND u OR
	 * @param conditions Lista de conditionsEntry para agrupar
	 */
	public ConditionComplex(Operator operator, List<ConditionEntry> conditions) {
		super();
		this.operator = operator;
		if(conditions!=null)
			this.conditions = conditions;
		else
			this.conditions = new ArrayList<ConditionEntry>();			
	}

	/** Agrega una condición a la lista
	 * @param condition
	 */
	public void addCondition(ConditionEntry condition){
		this.conditions.add(condition);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ConditionComplex [operator=" + operator + ", conditions.size=" + conditions.size() + "]";
	}

	@Override
	public Criterion resolve(Class<?> clase, Criteria criteria, Set<ManagerAlias> alias, Map<String, String> translations) 
		throws InternalErrorException{
		if(operator==Operator.OR){
			Disjunction cr = Restrictions.disjunction();
			for (ConditionEntry conditionEntry : conditions) {
				cr.add(conditionEntry.resolve(clase, criteria, alias, translations));
			}
			return cr;
		} else {
			Conjunction cr = Restrictions.conjunction();
			for (ConditionEntry conditionEntry : conditions) {
				cr.add(conditionEntry.resolve(clase, criteria, alias, translations));
			}
			return cr;
		}
	}

	/**
	 * @return retorna la lista de condiciones
	 */
	public List<ConditionEntry> getConditions() {
		return conditions;
	}

	/** 
	 * @return retorna el operador (AND u OR) que agrupa las condiciones
	 */
	public Operator getOperator() {
		return operator;
	}
	
	/* (non-Javadoc)
	 * @see com.curcico.jproject.core.daos.ConditionEntry#resolveNativeQuery(java.util.Map)
	 */
	@Override
	public String resolveNativeQuery(Map<String, Object> parameters) {
		Boolean first = true;
		String condicion = "(";
		if(operator==Operator.OR){
			for (ConditionEntry conditionEntry : conditions) {
				condicion += (first?"":" OR ") + conditionEntry.resolveNativeQuery(parameters);
				first = false;
			}
		} else {
			for (ConditionEntry conditionEntry : conditions) {
				condicion += (first?"":" AND ") + conditionEntry.resolveNativeQuery(parameters);
				first = false;
			}
		}
		return condicion + ")";		
	}
}
