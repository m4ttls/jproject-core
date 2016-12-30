package com.curcico.jproject.core.daos;

import com.curcico.jproject.core.entities.BaseEntity;

/**
 * @author Ing. Alejandro Daniel Curci (acurci@gmail.com)
 *
 * @param <T>
 */
public interface BaseEntityDao<T extends BaseEntity> extends BaseEntityDaoR<T>, BaseEntityDaoCUD<T>{

}