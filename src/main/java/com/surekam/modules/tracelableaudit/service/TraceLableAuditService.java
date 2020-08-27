package com.surekam.modules.tracelableaudit.service;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.surekam.common.persistence.Page;
import com.surekam.common.service.BaseService;
import com.surekam.modules.tracelableaudit.entity.TraceLableAudit;
import com.surekam.modules.tracelableaudit.dao.TraceLableAuditDao;

/**
 * 标签审核Service
 * @author xy
 * @version 2018-11-28
 */
@Component
@Transactional(readOnly = true)
public class TraceLableAuditService extends BaseService {

	@Autowired
	private TraceLableAuditDao traceLableAuditDao;
	
	public TraceLableAudit get(String id) {
		return traceLableAuditDao.get(id);
	}
	
	public Page<TraceLableAudit> find(Page<TraceLableAudit> page, TraceLableAudit traceLableAudit) {
		DetachedCriteria dc = traceLableAuditDao.createDetachedCriteria();
		dc.add(Restrictions.eq(TraceLableAudit.FIELD_DEL_FLAG, TraceLableAudit.DEL_FLAG_NORMAL));
		return traceLableAuditDao.find(page, dc);
	}
	
	@Transactional(readOnly = false)
	public void save(TraceLableAudit traceLableAudit) {
		traceLableAuditDao.save(traceLableAudit);
	}
	
	@Transactional(readOnly = false)
	public void delete(String id) {
		traceLableAuditDao.deleteById(id);
	}
	
}
