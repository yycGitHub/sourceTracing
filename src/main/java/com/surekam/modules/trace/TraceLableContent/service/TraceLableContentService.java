package com.surekam.modules.trace.TraceLableContent.service;

import java.util.Date;
import java.util.Iterator;
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
import com.surekam.modules.sys.entity.User;
import com.surekam.modules.trace.TraceLableApply.entity.TraceLableApply;
import com.surekam.modules.trace.TraceLableContent.dao.TraceLableContentDao;
import com.surekam.modules.trace.TraceLableContent.entity.TraceLableContent;
import com.surekam.modules.trace.TraceLableElement.dao.TraceLableElementDao;
import com.surekam.modules.trace.TraceLableElement.entity.TraceLableElement;

/**
 * 标签元素
 * @author wangyuewen
 * @param <TraceLableApplyService>
 *
 */
@Component
@Transactional(readOnly = true)
public class TraceLableContentService extends BaseService {

	@Autowired
	private TraceLableContentDao traceLableContentDao;
	
	@Autowired
	private TraceLableElementDao traceLableElementDao;
	
	public TraceLableContent get(String id) {
		return traceLableContentDao.get(id);
	}
	
	public Page<TraceLableContent> find(Page<TraceLableContent> page, TraceLableContent traceLableContent) {
		DetachedCriteria dc = traceLableContentDao.createDetachedCriteria();
		dc.add(Restrictions.eq(TraceLableContent.FIELD_DEL_FLAG, TraceLableContent.DEL_FLAG_NORMAL));
		return traceLableContentDao.find(page, dc);
	}
	
	/**
	 * 标签id查询标签内容
	 * @param id
	 * @return
	 */
	public List<TraceLableContent> find(String id) {
		DetachedCriteria dc = traceLableContentDao.createDetachedCriteria();
		dc.add(Restrictions.ne(TraceLableContent.FIELD_DEL_FLAG_XGXT, TraceLableContent.STATE_FLAG_DEL));
		if(StringUtils.isNotBlank(id)) {
			dc.add(Restrictions.eq("applyId",id));
		}
		dc.addOrder(Order.asc("elementId"));
		List<TraceLableContent> list = traceLableContentDao.find(dc);
		if(null != list && 0 != list.size()){
			for (Iterator<TraceLableContent> iterator = list.iterator(); iterator.hasNext();) {
				TraceLableContent traceLableContent = (TraceLableContent) iterator.next();
				//申请标签预览内容信息列不为空则查询标题头
				if(StringUtils.isNotBlank(traceLableContent.getApplyElementContent())) {
					if(StringUtils.isNotBlank(traceLableContent.getElementId())){
						TraceLableElement t = new TraceLableElement();
						t = traceLableElementDao.get(traceLableContent.getElementId());
						traceLableContent.setElementName(t.getElementName());
					}
				}else {
					iterator.remove();
				}
			}
		}
		return list;
	}
	
	@Transactional(readOnly = false)
	public void save(String applyId,String[] content,User user) {
		List<TraceLableContent> list = find(applyId);
		for(int i=0;i<list.size();i++){
			TraceLableContent traceLableContent = list.get(i);
			traceLableContent.setApplyElementContent(content[i]);
			traceLableContent.setStates("U");
			traceLableContent.setUpdateTime(new Date());
			traceLableContent.setUpdateUserid(user.getId());
			traceLableContentDao.save(traceLableContent);
		}
	}
	
	@Transactional(readOnly = false)
	public void delete(String id) {
		traceLableContentDao.deleteById(id);
	}
	
}
