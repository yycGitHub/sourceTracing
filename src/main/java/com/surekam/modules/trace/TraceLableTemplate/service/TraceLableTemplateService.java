package com.surekam.modules.trace.TraceLableTemplate.service;

import java.util.ArrayList;
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
import com.surekam.modules.trace.TraceLableTemplate.dao.TraceLableTemplateDao;
import com.surekam.modules.trace.TraceLableTemplate.entity.TraceLableTemplate;
import com.surekam.modules.traceproduct.entity.TraceProduct;

/**
 * 标签模板
 * @author wangyuewen
 * @param <TraceLableApplyService>
 *
 */
@Component
@Transactional(readOnly = true)
public class TraceLableTemplateService extends BaseService {

	@Autowired
	private TraceLableTemplateDao traceLableTemplateDao;
	
	public TraceLableTemplate get(String id) {
		return traceLableTemplateDao.get(id);
	}
	
	public Page<TraceLableTemplate> find(Page<TraceLableTemplate> page, TraceLableTemplate labelTemplate) {
		DetachedCriteria dc = traceLableTemplateDao.createDetachedCriteria();
		dc.add(Restrictions.ne(TraceProduct.FIELD_DEL_FLAG_XGXT, TraceProduct.STATE_FLAG_DEL));
		List<String> officeIds = new ArrayList<String>();
		if(labelTemplate!=null && labelTemplate.getOfficeId() != null) {
			officeIds.add(labelTemplate.getOfficeId());
			officeIds.add("2");
			dc.add(Restrictions.in("officeId", officeIds));
		}
		return traceLableTemplateDao.find(page, dc);
	}
	
	public List<TraceLableTemplate> find(String officeId){
		DetachedCriteria dc = traceLableTemplateDao.createDetachedCriteria();
		dc.add(Restrictions.ne(TraceProduct.FIELD_DEL_FLAG_XGXT, TraceProduct.STATE_FLAG_DEL));
		if(StringUtils.isNotBlank(officeId)) {
			dc.add(Restrictions.eq("officeId", officeId));
		}
		dc.addOrder(Order.asc("sort"));
		return traceLableTemplateDao.find(dc);
	}
	
}
