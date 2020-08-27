package com.surekam.modules.trace.tracelablespecification.service;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.surekam.common.persistence.Page;
import com.surekam.common.service.BaseService;
import com.surekam.modules.trace.tracelablespecification.entity.TraceLableSpecification;
import com.surekam.modules.trace.tracelablespecification.dao.TraceLableSpecificationDao;

/**
 * 标签卷纸规格表Service
 * @author liw
 * @version 2019-10-14
 */
@Component
@Transactional(readOnly = true)
public class TraceLableSpecificationService extends BaseService {

	@Autowired
	private TraceLableSpecificationDao traceLableSpecificationDao;
	
	public TraceLableSpecification get(String id) {
		return traceLableSpecificationDao.get(id);
	}
	
	public Page<TraceLableSpecification> find(Page<TraceLableSpecification> page, TraceLableSpecification traceLableSpecification) {
		DetachedCriteria dc = traceLableSpecificationDao.createDetachedCriteria();
		dc.add(Restrictions.eq(TraceLableSpecification.FIELD_DEL_FLAG, TraceLableSpecification.DEL_FLAG_NORMAL));
		return traceLableSpecificationDao.find(page, dc);
	}
	
	public List<TraceLableSpecification> findTraceLableSpecificationListByLabelId(String labelId) {
		DetachedCriteria dc = traceLableSpecificationDao.createDetachedCriteria();
		dc.add(Restrictions.ne(TraceLableSpecification.FIELD_DEL_FLAG_XGXT, TraceLableSpecification.STATE_FLAG_DEL));
		dc.add(Restrictions.eq("lableTemplateId", labelId));
		return traceLableSpecificationDao.find(dc);
	}
	
	public TraceLableSpecification findTraceLableSpecificationByLabelId(String labelId) {
		DetachedCriteria dc = traceLableSpecificationDao.createDetachedCriteria();
		dc.add(Restrictions.ne(TraceLableSpecification.FIELD_DEL_FLAG_XGXT, TraceLableSpecification.STATE_FLAG_DEL));
		dc.add(Restrictions.eq("lableTemplateId", labelId));
		List<TraceLableSpecification> list = traceLableSpecificationDao.find(dc);
		if(list.size()>0){
			return list.get(0);
		}else{
			return new TraceLableSpecification();
		}
	}
	
	@Transactional(readOnly = false)
	public void save(TraceLableSpecification traceLableSpecification) {
		traceLableSpecificationDao.save(traceLableSpecification);
	}
	
	@Transactional(readOnly = false)
	public void delete(String id) {
		traceLableSpecificationDao.deleteById(id);
	}
	
}
