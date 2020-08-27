package com.surekam.modules.traceproperty.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.surekam.common.persistence.BaseDao;
import com.surekam.common.persistence.Parameter;
import com.surekam.modules.traceproperty.entity.TracePropertyData;

/**
 * 溯源属性数据DAO接口
 * 
 * @author liuyi
 * @version 2018-09-25
 */
@Repository
public class TracePropertyDataDao extends BaseDao<TracePropertyData> {

	public List<TracePropertyData> findByModelDataId(String modelDataId) {
		String sql = "select * from t_trace_property_data a where a.model_data_id =:p1";
		List<TracePropertyData> findBySql = findBySql(sql, new Parameter(modelDataId), TracePropertyData.class);
		return findBySql;
	}

}
