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

public class ConditionComplex extends ConditionEntry {

	public enum Operator {
	    OR, AND 
	}
	
	private Operator operator;

	private List<ConditionEntry> conditions;

	public ConditionComplex(Operator operator) {
		super();
		this.operator = operator;
		this.conditions = new ArrayList<ConditionEntry>();
	}
	
	public ConditionComplex(Operator operator, List<ConditionEntry> conditions) {
		super();
		this.operator = operator;
		if(conditions!=null)
			this.conditions = conditions;
		else
			this.conditions = new ArrayList<ConditionEntry>();			
	}

	public void addCondition(ConditionEntry condition){
		this.conditions.add(condition);
	}
	
	@Override
	public String toString() {
		return "ConditionComplex [operator=" + operator + ", conditions.size=" + conditions.size() + "]";
	}

	public Criterion resolve(Criteria criteria, Set<ManagerAlias> alias, Map<String, String> translations) {
		if(operator==Operator.OR){
			Disjunction cr = Restrictions.disjunction();
			for (ConditionEntry conditionEntry : conditions) {
				cr.add(conditionEntry.resolve(criteria, alias, translations));
			}
			return cr;
		} else {
			Conjunction cr = Restrictions.conjunction();
			for (ConditionEntry conditionEntry : conditions) {
				cr.add(conditionEntry.resolve(criteria, alias, translations));
			}
			return cr;
		}
	}

	public List<ConditionEntry> getConditions() {
		return conditions;
	}

	public Operator getOperator() {
		return operator;
	}
	
}
