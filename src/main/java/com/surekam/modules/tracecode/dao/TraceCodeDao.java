package com.surekam.modules.tracecode.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.surekam.common.persistence.BaseDao;
import com.surekam.common.persistence.Parameter;
import com.surekam.modules.tracecode.entity.TraceCode;

/**
 * 溯源码DAO接口
 * 
 * @author liuyi
 * @version 2018-09-26
 */
@Repository
public class TraceCodeDao extends BaseDao<TraceCode> {

	public TraceCode findByCode(String code) {
		List<TraceCode> list = find("from TraceCode where traceCode = :p1 and states <> 'D'", new Parameter(code));
		if (list != null && list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}
	
	public TraceCode findByCodeAndStates(String code) {
		List<TraceCode> list = find("from TraceCode where traceCode = :p1 and states = 'D'", new Parameter(code));
		if (list != null && list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}

	public List<TraceCode> findByApplyId(String applyId) {
		return findBySql("SELECT * FROM t_trace_code_info WHERE states <> 'D' AND apply_id = :p1 ORDER BY serial_number", new Parameter(applyId), TraceCode.class);
	}

	public TraceCode findByOfficeIdAndBatchId(String officeId, String batchId) {
		String sql = "select * from t_trace_code_info a where a.office_id =:p1 and a.batch_id =:p2";
		List<TraceCode> findBySql = findBySql(sql, new Parameter(officeId, batchId), TraceCode.class);
		if (!findBySql.isEmpty()) {
			return findBySql.get(0);
		}
		return null;
	}
	
	
	public List<TraceCode> findByBatchId(String batchId) {
		String sql = "select * from t_trace_code_info a where a.batch_id =:p1 and a.states <> 'D' ";
		List<TraceCode> findBySql = findBySql(sql, new Parameter(batchId), TraceCode.class);
		return findBySql;
	}
}
