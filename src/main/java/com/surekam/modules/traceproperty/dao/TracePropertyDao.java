package com.surekam.modules.traceproperty.dao;

import org.springframework.stereotype.Repository;

import com.surekam.common.persistence.BaseDao;
import com.surekam.common.persistence.Parameter;
import com.surekam.modules.traceproperty.entity.TraceProperty;

/**
 * 溯源属性库管理DAO接口
 * @author liw
 * @version 2018-09-06
 */
@Repository
public class TracePropertyDao extends BaseDao<TraceProperty> {
	
	/**
	 * 删除
	 * @param id
	 * @return
	 */
	public int deleteById(String id){
		return update("delete TraceProperty where modelId = :p1", new Parameter(id));
	}
	
}
