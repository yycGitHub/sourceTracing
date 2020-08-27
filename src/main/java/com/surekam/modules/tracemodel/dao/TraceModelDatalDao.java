package com.surekam.modules.tracemodel.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.surekam.common.persistence.BaseDao;
import com.surekam.common.persistence.Parameter;
import com.surekam.modules.tracemodel.entity.TraceModelData;

/**
 * 溯源模块数据DAO接口
 * 
 * @author liuyi
 * @version 2018-09-25
 */
@Repository
public class TraceModelDatalDao extends BaseDao<TraceModelData> {

	public List<TraceModelData> findByBatchid(String batchId) {
		String sql = "select * from t_trace_model_data a where a.batch_id =:p1";
		List<TraceModelData> findBySql = findBySql(sql, new Parameter(batchId), TraceModelData.class);
		return findBySql;
	}

}
