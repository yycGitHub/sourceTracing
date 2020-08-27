package com.surekam.modules.tracemodel.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.surekam.common.service.BaseService;
import com.surekam.modules.tracemodel.dao.TracePropertyModelDao;
import com.surekam.modules.tracemodel.entity.TraceModel;
import com.surekam.modules.tracemodel.entity.TracePropertyModel;

/**
 * 溯源模块关联属性Service
 * @author huangrd
 * @version 2018-12-04
 */
@Component
@Transactional(readOnly = true)
public class TracePropertyModelService extends BaseService {
	
	@Autowired
	private TracePropertyModelDao tracePropertyModelDao;
	
	
	public TracePropertyModel get(String id) {
		return tracePropertyModelDao.get(id);
	}


	public List<String> find(String id) {
		
		String sql = "SELECT t.property_id FROM t_trace_property_model t"
				+ " where "+ " model_id = '" + id + "'";
		
		return tracePropertyModelDao.findBySql(sql, null);
	}
	
	
	
}
