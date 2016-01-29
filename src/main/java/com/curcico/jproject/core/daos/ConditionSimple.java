package com.curcico.jproject.core.daos;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;


public class ConditionSimple extends ConditionEntry {
	private String column;
	private SearchOption condition;
	private Object value;

	public ConditionSimple(String column, SearchOption condition, Object value) {
		super();
		this.column = column;
		this.condition = condition;
		this.value = value;
	}

	public static String transform(String column) {
		if(column.contains(".")){
			String fields[] = column.split("\\.");
			if(fields.length > 2){
				return getAliasName(fields, fields.length - 2) + "." + fields[fields.length-1];
			}else{
				return column;
			}
		}else{
			return column;
		}
	}

	public ConditionSimple(String column, Object value) {
		this.column = column;
		this.value = value;
		
		if(String.class.isInstance(value)){
			this.condition = SearchOption.CONTAIN;
		} else {
			if(java.util.Date.class.isInstance(value) || 
					((Integer.class.isInstance(value) || BigDecimal.class.isInstance(value)) && column.contains("_"))){
				if(column.contains("_")){
					String originalField = column.split("_")[0];
					this.column = originalField;
					String restriction = column.split("_")[1];
					if(restriction.equalsIgnoreCase("from")){
						this.condition = SearchOption.GREATER_EQUAL;
					}
					
					if(restriction.equalsIgnoreCase("to")){
						this.condition = SearchOption.LESS_EQUAL;
					}
				}
			} else {					
				this.condition = SearchOption.EQUAL;
			}
		}
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public SearchOption getCondition() {
		return condition;
	}

	public void setCondition(SearchOption condition) {
		this.condition = condition;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "ConditionEntry [column=" + column + ", condition=" + condition
				+ ", value=" + value + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((column == null) ? 0 : column.hashCode());
		result = prime * result
				+ ((condition == null) ? 0 : condition.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ConditionSimple other = (ConditionSimple) obj;
		if (column == null) {
			if (other.column != null)
				return false;
		} else if (!column.equals(other.column))
			return false;
		if (condition != other.condition)
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public Criterion resolve(Criteria criteria, Set<ManagerAlias> alias, Map<String, String> translations) {
		Criterion criterion = null;
		String field = transform(column);
		Object value = this.getValue();
		Set<ManagerAlias> aliasByColumn = getAlias(column);
		if(aliasByColumn != null && alias !=null){
			addAlias(criteria, alias, aliasByColumn, translations);
		}
		switch (this.getCondition()) {
			case EQUAL:{
				if(java.util.Date.class.isInstance(value)){
					Criterion fechaGe = Restrictions.ge(field, value);// tomamos la fecha inicial(la que se va a buscar)
					Calendar value1 = Calendar.getInstance();
					value1.setTime((Date) value) ;
					value1.add(Calendar.DAY_OF_MONTH, 1 );//le agregamos 1 en el día del mes
					Date value2 = new Date(value1.getTimeInMillis());
					Criterion fechaLe = Restrictions.le(field, value2) ;
					criterion = Restrictions.and(fechaGe, fechaLe);//que sea mayor igual que GE y menor igual que LE
				} else {
					criterion =(Restrictions.eq(field, value));
				}
				break;
			}
			case NOT_EQUAL:
				criterion =(Restrictions.ne(field, value));
				break;
			case BEGIN:
				criterion =(Restrictions.ilike(field, value.toString(),MatchMode.START));
				break;
			case NOT_BEGIN:
				criterion =(Restrictions.not(Restrictions.ilike(field, value.toString(), MatchMode.START)));
				break;
			case END:
				criterion =(Restrictions.ilike(field, value.toString(),MatchMode.END));
				break;
			case NOT_END:
				criterion =(Restrictions.not(Restrictions.ilike(field, value.toString(), MatchMode.END)));
				break;
			case CONTAIN:
				criterion =(Restrictions.ilike(field, value.toString(), MatchMode.ANYWHERE));
				break;
			case NOT_CONTAIN:
				criterion =(Restrictions.not(Restrictions.ilike(field, value.toString(), MatchMode.ANYWHERE)));
				break;
			case NULL:
				criterion =(Restrictions.isNull(field));
				break;
			case NOT_NULL:
				criterion =(Restrictions.isNotNull(field));
				break;
			case IN:
				if(value instanceof List)
					criterion =(Restrictions.in(field, (List<Object>) value));
				break;
			case NOT_IN:
				if(value instanceof List)
					criterion =(Restrictions.not(Restrictions.in(field, (List<Object>) value)));
				break;
			case LESS:
				criterion =(Restrictions.lt(field, value));
				break;
			case LESS_EQUAL:
				criterion =(Restrictions.le(field, value));
				break;
			case GREATER:
				criterion =(Restrictions.gt(field, value));
				break;
			case GREATER_EQUAL:
				criterion =(Restrictions.ge(field, value));
				break;	
			default:
				break;
			}
		if(criterion!=null)
			return criterion;
		return null;
	}
	
}
