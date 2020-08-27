package com.surekam.modules.trace.tracelableelementnew.service;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.surekam.common.persistence.Page;
import com.surekam.common.service.BaseService;
import com.surekam.common.utils.StringUtils;
import com.surekam.modules.trace.tracelableelementnew.entity.TraceLableElementNew;
import com.surekam.modules.trace.tracelableelementnew.dao.TraceLableElementNewDao;

/**
 * 标签元素表Service
 * @author liw
 * @version 2019-10-14
 */
@Component
@Transactional(readOnly = true)
public class TraceLableElementNewService extends BaseService {

	@Autowired
	private TraceLableElementNewDao traceLableElementNewDao;
	
	public TraceLableElementNew get(String id) {
		return traceLableElementNewDao.get(id);
	}
	
	public Page<TraceLableElementNew> find(Page<TraceLableElementNew> page, TraceLableElementNew traceLableElementNew) {
		DetachedCriteria dc = traceLableElementNewDao.createDetachedCriteria();
		dc.add(Restrictions.eq(TraceLableElementNew.FIELD_DEL_FLAG, TraceLableElementNew.DEL_FLAG_NORMAL));
		return traceLableElementNewDao.find(page, dc);
	}
	
	public List<TraceLableElementNew> findByLabelId(String labelId) {
		DetachedCriteria dc = traceLableElementNewDao.createDetachedCriteria();
		dc.add(Restrictions.ne(TraceLableElementNew.FIELD_DEL_FLAG_XGXT, TraceLableElementNew.STATE_FLAG_DEL));
		if(StringUtils.isNotBlank(labelId)){
			dc.add(Restrictions.eq("lableTemplateId", labelId));
		}
		dc.addOrder(Order.asc("elementId"));
		return traceLableElementNewDao.find(dc);
	}
	
	@Transactional(readOnly = false)
	public void save(TraceLableElementNew traceLableElementNew) {
		traceLableElementNewDao.save(traceLableElementNew);
	}
	
	@Transactional(readOnly = false)
	public void delete(String id) {
		traceLableElementNewDao.deleteById(id);
	}
	
}
