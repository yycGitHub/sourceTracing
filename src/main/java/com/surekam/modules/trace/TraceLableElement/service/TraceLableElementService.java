package com.surekam.modules.trace.TraceLableElement.service;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.surekam.common.persistence.Parameter;
import com.surekam.common.service.BaseService;
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
public class TraceLableElementService extends BaseService {

	@Autowired
	private TraceLableElementDao traceLableElementDao;
	
	public TraceLableElement get(String id) {
		return traceLableElementDao.get(id);
	}
	
	public List<Map<String,Object>> getTraceLableElementList(String labelTemplateId) {
		String sql = "SELECT t.element_code,t.element_name,t.default_content FROM t_trace_lable_element t WHERE t.label_template_id= :p1 AND t.states<>'D'";
		return traceLableElementDao.findBySql(sql,new Parameter(labelTemplateId),Map.class);
	}
	
	public List<TraceLableElement> getTraceLableElementList() {
		String sql = "SELECT t.* FROM t_trace_lable_element t WHERE t.label_template_id= '1' AND t.states<>'D'";
		return traceLableElementDao.findBySql(sql,new Parameter(),TraceLableElement.class);
	}
}
