package com.curcico.jproject.core.daos;

import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;

public abstract class ConditionEntry extends Condition {

	public abstract Criterion resolve(Criteria criteria, Set<ManagerAlias> alias,
			Map<String, String> translations);

	public static String getFieldWithAlias(String path) {
		if(path.contains(".")){
			String fields[] = path.split("\\.");
			return fields[fields.length-2]+"."+fields[fields.length-1];
		}else{
			return path;
		}
	}
	
}
