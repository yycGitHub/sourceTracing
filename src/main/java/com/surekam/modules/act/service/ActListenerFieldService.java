package com.surekam.modules.act.service;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.surekam.common.persistence.Page;
import com.surekam.common.service.BaseService;
import com.surekam.modules.act.entity.ActListenerField;
import com.surekam.modules.act.dao.ActListenerFieldDao;
import com.surekam.common.persistence.Parameter;
import com.surekam.common.service.ServiceException;

/**
 * activiti监听事件参数Service
 * @author ludang
 * @version 2018-06-12
 */
@Component
@Transactional(readOnly = true)
public class ActListenerFieldService extends BaseService {

	@Autowired
	private ActListenerFieldDao actListenerFieldDao;
	
	public ActListenerField get(String id) {
		return actListenerFieldDao.get(id);
	}
	
	public Page<ActListenerField> find(Page<ActListenerField> page, ActListenerField actListenerField) {
		DetachedCriteria dc = actListenerFieldDao.createDetachedCriteria();
		dc.add(Restrictions.eq(ActListenerField.FIELD_DEL_FLAG, ActListenerField.DEL_FLAG_NORMAL));
		return actListenerFieldDao.find(page, dc);
	}
	
	public List<ActListenerField> findListenerFields(String listenerId) {
		String hql = "from ActListenerField where delFlag=:p1 and actListener.id=:p2 order by updateDate";
		return actListenerFieldDao.find(hql,new Parameter(ActListenerField.DEL_FLAG_NORMAL,listenerId));
	}
	
	@Transactional(readOnly = false)
	public void save(ActListenerField actListenerField) {
		actListenerFieldDao.save(actListenerField);
	}
	
	@Transactional(readOnly = false)
	public void delete(String id) {
		actListenerFieldDao.deleteById(id);
	}
	
	@Transactional(readOnly = false)
	public String deleteListenerFields(String[] ids) {
		try {
			for(String id:ids){
				actListenerFieldDao.deleteById(id);
			}
			return "删除成功";
		} catch (Exception e) {
			throw new ServiceException("删除失败:"+e.getMessage());
		}
	}
	
}
