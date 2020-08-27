package com.surekam.modules.tracemodel.dao;


import org.springframework.stereotype.Repository;

import com.surekam.common.persistence.BaseDao;
import com.surekam.common.persistence.Parameter;
import com.surekam.modules.tracemodel.entity.TracePropertyModel;

/**
 * 溯源模块关联属性DAO接口
 * @author huangrd
 * @version 2018-12-04
 */
@Repository
public class TracePropertyModelDao extends BaseDao<TracePropertyModel>{
	
	
	/**
	 * 删除
	 * @param id
	 * @return
	 */
	public int deleteDataById(String id){
		return update("delete TracePropertyModel where modelId = :p1", new Parameter(id));
	}
	
}
