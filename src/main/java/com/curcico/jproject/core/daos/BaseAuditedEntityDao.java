package com.curcico.jproject.core.daos;

import com.curcico.jproject.core.entities.BaseAuditedEntity;

public interface BaseAuditedEntityDao<T extends BaseAuditedEntity> extends BaseEntityDaoR<T>, BaseAuditedEntityDaoCUD<T> {

}
