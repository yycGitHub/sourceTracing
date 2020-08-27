package com.surekam.modules.Interface.service;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.surekam.common.persistence.Page;
import com.surekam.common.service.BaseService;
import com.surekam.modules.Interface.dao.TraceBaseTreeDao;
import com.surekam.modules.Interface.entity.TraceBaseTree;

/**
 * 基地信息Service
 * @author xy
 * @version 2019-05-29
 */
@Component
@Transactional(readOnly = true)
public class TraceBaseTreeService extends BaseService {

	@Autowired
	private TraceBaseTreeDao traceBaseTreeDao;
	
	public TraceBaseTree get(String id) {
		return traceBaseTreeDao.get(id);
	}
	
	public Page<TraceBaseTree> find(Page<TraceBaseTree> page, TraceBaseTree traceBaseTree) {
		DetachedCriteria dc = traceBaseTreeDao.createDetachedCriteria();
		dc.add(Restrictions.eq(TraceBaseTree.FIELD_DEL_FLAG, TraceBaseTree.DEL_FLAG_NORMAL));
		return traceBaseTreeDao.find(page, dc);
	}
	
	@Transactional(readOnly = false)
	public void save(TraceBaseTree traceBaseTree) {
		traceBaseTreeDao.save(traceBaseTree);
	}
	
	@Transactional(readOnly = false)
	public void delete(String id) {
		traceBaseTreeDao.deleteById(id);
	}
	
}
