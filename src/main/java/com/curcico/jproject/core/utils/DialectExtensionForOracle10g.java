package com.curcico.jproject.core.utils;

import java.sql.Types;

import org.hibernate.dialect.Oracle10gDialect;
import org.hibernate.type.StandardBasicTypes;

public class DialectExtensionForOracle10g extends Oracle10gDialect{

	public DialectExtensionForOracle10g() { 
		super();
		registerHibernateType(Types.NVARCHAR, StandardBasicTypes.STRING.getName());//StringType.INSTANCE.getName());
	}

}
