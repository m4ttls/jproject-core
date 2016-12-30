package com.curcico.jproject.core.services;

import com.curcico.jproject.core.entities.BaseEntity;




/** 
 * @author Ing. Alejandro Daniel Curci (acurci@gmail.com)
 * Interface que indica los metodos base para cada entidad, esta interfaz esta implementada en la clase abstracta CommonsService<T>
 *  por lo tanto, todo método declarado aquí debe ser implementado en dicha clase (o por lo menos declarado para que las clases hijas
 *  lo implementen)
 * */
public interface BaseEntityService<T extends BaseEntity> extends BaseEntityServiceR<T>, BaseEntityServiceCUD<T> {
	
}
