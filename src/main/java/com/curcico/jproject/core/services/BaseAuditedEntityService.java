package com.curcico.jproject.core.services;

import com.curcico.jproject.core.entities.BaseAuditedEntity;
import com.curcico.jproject.core.exception.BaseException;

public interface BaseAuditedEntityService<T extends BaseAuditedEntity> 
			extends BaseEntityServiceR<T>, BaseAuditedEntityServiceCUD<T> {
	
}
