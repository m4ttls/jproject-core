package com.curcico.jproject.core.services;

import org.springframework.stereotype.Service;

import com.curcico.jproject.core.daos.OneBaseEntityDao;
import com.curcico.jproject.core.entities.OneBaseEntity;

@Service
public class OneBaseEntityServiceImpl extends BaseEntityServiceImpl<OneBaseEntity, OneBaseEntityDao>
		implements OneBaseEntityService {

}
