package com.curcico.jproject.core.services;

import org.springframework.stereotype.Service;

import com.curcico.jproject.core.daos.OneBaseTimeRangeEntityDao;
import com.curcico.jproject.core.entities.OneBaseTimeRangeEntity;

@Service
public class OneBaseTimeRangeEntityServiceImpl 
	extends BaseTimeRangeEntityServiceImpl<OneBaseTimeRangeEntity, OneBaseTimeRangeEntityDao>
		implements OneBaseTimeRangeEntityService {
}
