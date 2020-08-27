package com.surekam.modules.act.service;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.surekam.common.persistence.Page;
import com.surekam.common.service.BaseService;
import com.surekam.common.utils.StringUtils;
import com.surekam.modules.act.entity.ActListener;
import com.surekam.modules.act.dao.ActListenerDao;

/**
 * activiti监听事件Service
 * @author ludang
 * @version 2018-05-24
 */
@Component
@Transactional(readOnly = true)
public class ActListenerService extends BaseService {

	@Autowired
	private ActListenerDao actListenerDao;
	
	public ActListener get(String id) {
		return actListenerDao.get(id);
	}
	
	public Page<ActListener> find(Page<ActListener> page, ActListener actListener) {
		DetachedCriteria dc = actListenerDao.createDetachedCriteria();
		if (StringUtils.isNotEmpty(page.getSearchValue())){
			dc.add(Restrictions.like("name_", "%" + page.getSearchValue() + "%"));
		}
		if (StringUtils.isNotEmpty(actListener.getName_())){
			dc.add(Restrictions.like("name_", "%" + actListener.getName_() + "%"));
		}
		if (StringUtils.isNotEmpty(actListener.getType_())){
			dc.add(Restrictions.eq("type_", actListener.getType_()));
		}
		dc.add(Restrictions.eq(ActListener.FIELD_DEL_FLAG, ActListener.DEL_FLAG_NORMAL));
		return actListenerDao.find(page, dc);
	}
	
	@Transactional(readOnly = false)
	public void save(ActListener actListener) {
		actListenerDao.save(actListener);
	}
	
	@Transactional(readOnly = false)
	public void delete(String id) {
		actListenerDao.deleteById(id);
	}
	
}
