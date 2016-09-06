package com.curcico.jproject.core.services;

import java.sql.Timestamp;
import java.util.Calendar;

import org.springframework.transaction.annotation.Transactional;

import com.curcico.jproject.core.daos.Dao;
import com.curcico.jproject.core.entities.BaseAuditedEntity;
import com.curcico.jproject.core.exception.BaseException;
import com.curcico.jproject.core.exception.BusinessException;


/**
 * Clase abstracta que implementa los métodos mas comunes de los servicios considerando 
 * la actualización de los campos de auditoría.
*/
public abstract class CommonsAuditedService<T extends BaseAuditedEntity, U extends Dao<T>> extends CommonsService<T, U>{
	
	@Transactional(rollbackFor=Exception.class)
	public T createOrUpdate(T entity, Integer userId) throws BaseException {
			if(entity != null && userId != null){
				if(entityValidate(entity)){
					beforeSave(entity,userId);
						Timestamp now = new Timestamp(Calendar.getInstance().getTimeInMillis());
						if (entity.getId()==null || entity.getId().equals(0)){
							
							if (entity.getCreatedByUser() == null || entity.getCreatedByUser()==0){
								entity.setCreatedByUser(userId);
							}
							if (entity.getCreatedTime()==null){
								entity.setCreatedTime(now);
							}
							dao.save(entity);
							return entity;
						} else {
							entity.setUpdatedByUser(userId);
							entity.setUpdatedTime(now);
							dao.update(entity);
							return entity;
						}
				}
			}
			logger.error("Some parameters (entity or userId) are invalid.");
			throw new BusinessException("invalid.parameters");
	}


	@Transactional(rollbackFor=Exception.class)
	public void delete(T entity, Integer userId) throws BaseException{
		if(entity!=null && entity.getId()!=null && userId!=null){
			entity = loadEntityById(entity.getId());
			dao.delete(entity);
		} else {
			throw new BusinessException("invalid.parameters");
		}	
	}
}
