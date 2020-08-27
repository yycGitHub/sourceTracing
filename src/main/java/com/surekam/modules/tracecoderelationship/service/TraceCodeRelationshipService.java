package com.surekam.modules.tracecoderelationship.service;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.surekam.common.persistence.Page;
import com.surekam.common.service.BaseService;
import com.surekam.modules.tracecoderelationship.entity.TraceCodeRelationship;
import com.surekam.modules.tracecoderelationship.dao.TraceCodeRelationshipDao;

/**
 * 标签层级关系表Service
 * @author liw
 * @version 2019-07-05
 */
@Component
@Transactional(readOnly = true)
public class TraceCodeRelationshipService extends BaseService {

	@Autowired
	private TraceCodeRelationshipDao traceCodeRelationshipDao;
	
	public TraceCodeRelationship get(String id) {
		return traceCodeRelationshipDao.get(id);
	}
	
	public Page<TraceCodeRelationship> find(Page<TraceCodeRelationship> page, TraceCodeRelationship traceCodeRelationship) {
		DetachedCriteria dc = traceCodeRelationshipDao.createDetachedCriteria();
		dc.add(Restrictions.eq(TraceCodeRelationship.FIELD_DEL_FLAG, TraceCodeRelationship.DEL_FLAG_NORMAL));
		return traceCodeRelationshipDao.find(page, dc);
	}
	
	@Transactional(readOnly = false)
	public void save(TraceCodeRelationship traceCodeRelationship) {
		traceCodeRelationshipDao.save(traceCodeRelationship);
	}
	
	@Transactional(readOnly = false)
	public void delete(String id) {
		traceCodeRelationshipDao.deleteById(id);
	}
	
}
