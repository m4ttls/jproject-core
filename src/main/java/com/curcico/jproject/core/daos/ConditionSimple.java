package com.curcico.jproject.core.daos;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.RestrictionsWithWildcards;

import com.curcico.jproject.core.entities.BaseTimeRangeEntity;
import com.curcico.jproject.core.entities.OneBaseEntity;
import com.curcico.jproject.core.exception.InternalErrorException;
import com.curcico.jproject.core.utils.ReflectionUtils;



public class ConditionSimple extends ConditionEntry {

	public static final Character ESCAPE_CHARACTER = '!';

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
		return "ConditionEntry [column=" + column + ", condition=" + condition + ", value=" + value + "]";
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
	
	@Override
	@SuppressWarnings("unchecked")
	public Criterion resolve(Class<?> clase, Criteria criteria, Set<ManagerAlias> alias, Map<String, String> translations) 
			throws InternalErrorException{
		Criterion criterion = null;
		String field = transform(column);
		Object value = this.getValue();
		Set<ManagerAlias> aliasByColumn = getAlias(column);
		if(aliasByColumn != null && alias !=null){
			addAlias(criteria, alias, aliasByColumn, translations);
		}
		switch (this.getCondition()) {
			case EQUAL:{
				Criterion r = getCriterionForActiveEntity(clase, field, true);
				if(r!=null){
					criterion = r;
					break;
				}
				
				if(java.util.Date.class.isInstance(value)){
					Criterion fechaGe = Restrictions.ge(field, value);// tomamos la fecha inicial(la que se va a buscar)
					Calendar value1 = Calendar.getInstance();
					value1.setTime((Date) value) ;
					value1.add(Calendar.DAY_OF_MONTH, 1 );//le agregamos 1 en el día del mes
					Date value2 = new Date(value1.getTimeInMillis());
					Criterion fechaLe = Restrictions.le(field, value2) ;
					criterion = Restrictions.and(fechaGe, fechaLe);//que sea mayor igual que GE y menor igual que LE
				} else {
					if(value==null)
						criterion =(Restrictions.isNull(field));
					else
						criterion =(Restrictions.eq(field, value));
				}
				break;
			}
			case NOT_EQUAL:
				Criterion r = getCriterionForActiveEntity(clase, field, false);
				if(r!=null){
					criterion = r;
					break;
				}
				criterion =(Restrictions.ne(field, value));
				break;
			case BEGIN:
				criterion =(RestrictionsWithWildcards.ilike(field, escapeWildCardsLikeSearch(value.toString()),MatchMode.START, ESCAPE_CHARACTER ));
				break;
			case NOT_BEGIN:
				criterion =(Restrictions.not(RestrictionsWithWildcards.ilike(field, escapeWildCardsLikeSearch(value.toString()), MatchMode.START, ESCAPE_CHARACTER)));
				break;
			case END:
				criterion =(RestrictionsWithWildcards.ilike(field, escapeWildCardsLikeSearch(value.toString()),MatchMode.END, ESCAPE_CHARACTER));
				break;
			case NOT_END:
				criterion =(Restrictions.not(RestrictionsWithWildcards.ilike(field, escapeWildCardsLikeSearch(value.toString()), MatchMode.END, ESCAPE_CHARACTER)));
				break;
			case CONTAIN:
				criterion =(RestrictionsWithWildcards.ilike(field, escapeWildCardsLikeSearch(value.toString()), MatchMode.ANYWHERE, ESCAPE_CHARACTER));
				break;
			case NOT_CONTAIN:
				criterion =(Restrictions.not(RestrictionsWithWildcards.ilike(field, escapeWildCardsLikeSearch(value.toString()), MatchMode.ANYWHERE, ESCAPE_CHARACTER)));
				break;
			case LIKE:
				criterion =(RestrictionsWithWildcards.ilike(field, value.toString(), MatchMode.EXACT, ESCAPE_CHARACTER));
				break;				
			case NOT_LIKE:
				criterion =Restrictions.not((RestrictionsWithWildcards.ilike(field, value.toString(), MatchMode.EXACT, ESCAPE_CHARACTER)));
				break;				
			case NULL:
				criterion =(Restrictions.isNull(field));
				break;
			case NOT_NULL:
				criterion =(Restrictions.isNotNull(field));
				break;
			case IN:
				if(value instanceof Collection && !((Collection<Object>)value).isEmpty())
					criterion =(Restrictions.in(field, (Collection<Object>) value));
				else if(value.getClass().isArray() & !(ArrayUtils.isEmpty((Object[])value)))
						criterion =(Restrictions.in(field, (Object[]) value));
					 else
						criterion = Restrictions.sqlRestriction("(1=0)");
				break;
			case NOT_IN:
				if(value instanceof Collection && !((Collection<Object>)value).isEmpty())
					criterion =(Restrictions.not(Restrictions.in(field, (Collection<Object>) value)));
				else if(value.getClass().isArray() & !(ArrayUtils.isEmpty((Object[])value)))
						 criterion =(Restrictions.not(Restrictions.in(field, (Object[]) value)));
				 	 else
				 		 criterion = Restrictions.sqlRestriction("(1=1)");
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
			case REGEX:
				String columnName = field;
				try {
					PropertyDescriptor[] desc = 
							Introspector.getBeanInfo(clase).getPropertyDescriptors();
					for (PropertyDescriptor propertyDescriptor : desc) {
						if(propertyDescriptor.getName().equals(field)){
							javax.persistence.Column a = propertyDescriptor.getReadMethod().getAnnotation(javax.persistence.Column.class);
							columnName = a.name();
						}
					}
				} catch (SecurityException | IntrospectionException e) {
					throw new InternalErrorException(e);
				}
				criterion =(Restrictions.sqlRestriction(" regexp_like({alias}." + columnName + ", '" + value + "' , 'i') "));
				break;
			default:
				break;
			}
		if(criterion!=null)
			return criterion;
		return null;
	}
	
	private Criterion getCriterionForActiveEntity(Class<?> clase, String field, Boolean isEquals){
		
		if(field.endsWith("active") && Boolean.class.isAssignableFrom(value.getClass())){
			String path = "";
			if(field.contains(".")){
				path = field.substring(0, field.lastIndexOf("."));
				clase = ReflectionUtils.getCast(OneBaseEntity.class, path);				
				path+=".";
			}
			if( (field.equals(path + "active") && BaseTimeRangeEntity.class.isAssignableFrom(clase) ) ){
				Date now = new Date();
				Criterion deleted 	= Restrictions.isNull(path + "deleted");
				Criterion validFrom = Restrictions.le(path + "validFrom", now);
				Criterion validTo 	= Restrictions.or(Restrictions.ge(path + "validTo", now), Restrictions.isNull(path + "validTo"));
				Criterion vigencia  = Restrictions.and(validFrom, validTo);
				if (isEquals)
					return ( (Boolean)value)?Restrictions.and(deleted, vigencia):Restrictions.not(Restrictions.and(deleted, vigencia));
				else	
					return ( (Boolean)value)?Restrictions.not(Restrictions.and(deleted, vigencia)):Restrictions.and(deleted, vigencia);
			}
		}
		return null;
	}
	
	
	
	/**
	 * Escape de comodines propios de la busqueda por Like
	 * @param value
	 * @return
	 */
	public String escapeWildCardsLikeSearch(String value) {
		value = value.replaceAll(ESCAPE_CHARACTER.toString(), ESCAPE_CHARACTER.toString()+ESCAPE_CHARACTER.toString());
		value = value.replaceAll("%", ESCAPE_CHARACTER.toString() + "%");
		value = value.replaceAll("_", ESCAPE_CHARACTER.toString() + "_");
		return value;
	}

	
	@Override
	public String resolveNativeQuery(Map<String, Object> parameters) {
		if(parameters==null) parameters = new HashMap<String, Object>();
		String restriction = null;
		Object auxiliarValue = this.value ;
		String parameterName = "prm_" + this.column + "_" + (parameters.size() + 1);
		switch (this.getCondition()) {
			case EQUAL:
				restriction = this.getColumn() + "=:" + parameterName ;
				break;
			case NOT_EQUAL:
				restriction = this.getColumn() + "<>:" + parameterName ;
				break;
			case BEGIN:
				restriction = "LOWER(" + this.getColumn() + ") like LOWER(:" + parameterName + ") ESCAPE '" + ESCAPE_CHARACTER + "' " ;
				auxiliarValue = escapeWildCardsLikeSearch(this.value.toString()) + "%";
				break;
			case NOT_BEGIN:
				restriction = "NOT LOWER(" + this.getColumn() + ") like LOWER(:" + parameterName + ") ESCAPE '" + ESCAPE_CHARACTER + "' " ;
				auxiliarValue = escapeWildCardsLikeSearch(this.value.toString()) + "%";
				break;
			case END:
				restriction = "LOWER(" + this.getColumn() + ") like LOWER(:" + parameterName + ") ESCAPE '" + ESCAPE_CHARACTER + "' " ;
				auxiliarValue = "%" + escapeWildCardsLikeSearch(this.value.toString());
				break;
			case NOT_END:
				restriction = "NOT LOWER(" + this.getColumn() + ") like LOWER(:" + parameterName + ") ESCAPE '" + ESCAPE_CHARACTER + "' " ;
				auxiliarValue = "%" + escapeWildCardsLikeSearch(this.value.toString());
				break;
			case CONTAIN:
				restriction = "LOWER(" + this.getColumn() + ") like LOWER(:" + parameterName + ") ESCAPE '" + ESCAPE_CHARACTER + "' " ;
				auxiliarValue = "%" + escapeWildCardsLikeSearch(this.value.toString()) + "%";
				break;
			case NOT_CONTAIN:
				restriction = "NOT LOWER(" + this.getColumn() + ") like LOWER(:" + parameterName + ") ESCAPE '" + ESCAPE_CHARACTER + "' " ;
				auxiliarValue = "%" + escapeWildCardsLikeSearch(this.value.toString()) + "%";
				break;
			case LIKE:
				restriction = "LOWER(" + this.getColumn() + ") like LOWER(:" + parameterName + ") ESCAPE '" + ESCAPE_CHARACTER + "' ";
				auxiliarValue = this.value.toString();
				break;				
			case NOT_LIKE:
				restriction = "LOWER(" + this.getColumn() + ") NOT LIKE LOWER(:" + parameterName + ") ESCAPE '" + ESCAPE_CHARACTER + "' ";
				auxiliarValue = this.value.toString();
				break;				
			case NULL:
				restriction = this.getColumn() + " is null";
				break;
			case NOT_NULL:
				restriction = this.getColumn() + " is not null";
				break;
			case IN:
				restriction = this.getColumn() + " IN (:" + parameterName + ")" ;
				break;
			case NOT_IN:
				restriction = this.getColumn() + " NOT IN (:" + parameterName + ")" ;
				break;
			case LESS:
				restriction = this.getColumn() + "<:" + parameterName ;
				break;
			case LESS_EQUAL:
				restriction = this.getColumn() + "<=:" + parameterName ;
				break;
			case GREATER:
				restriction = this.getColumn() + ">:" + parameterName ;
				break;
			case GREATER_EQUAL:
				restriction = this.getColumn() + ">=:" + parameterName ;
				break;	
			case REGEX:
				// funciona para string, numeros, fechas y booleanos
				restriction = "REGEXP_LIKE(" + this.getColumn() + ", :" + parameterName + ", 'i')" ;
				break;
			default:
				break;
		}
		if(restriction!=null 
				&& !(condition.equals(SearchOption.NULL) 
						|| condition.equals(SearchOption.NOT_NULL)
					)
			) 
			parameters.put(parameterName, auxiliarValue);
	return restriction;
	}

}
