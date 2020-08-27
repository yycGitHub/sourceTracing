package com.surekam.modules.traceproductauditnew.dao;

import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import com.surekam.common.persistence.BaseDao;
import com.surekam.common.persistence.Parameter;
import com.surekam.modules.traceproductaudit.entity.TraceProductAudit;
import com.surekam.modules.traceproductauditnew.entity.TraceProductAuditNew;

/**
 * 产品审核DAO接口
 * 
 * @author liwei
 * @version 2020-04-10
 */
@Repository
public class TraceProductAuditNewDao extends BaseDao<TraceProductAuditNew> {

	public List<TraceProductAuditNew> findByAuditState(String auditState) {
		String sql = "select a.* from t_trace_product_audit_new a "
				+ "inner join (select a.audit_id as audit_id, max(a.audit_time) as audit_time from t_trace_product_audit_new a "
				+ "group by a.audit_id) v on v.audit_id = a.audit_id and v.audit_time = a.audit_time and a.audit_state =:p1 ";
		List<TraceProductAuditNew> findBySql = findBySql(sql, new Parameter(auditState), TraceProductAuditNew.class);
		return findBySql;
	}
	
	public List<TraceProductAuditNew> findByAuditState1(String auditState) {
		String sql = "select a.* from t_trace_product_audit_new a where a.audit_state =:p1 ";
		List<TraceProductAuditNew> findBySql = findBySql(sql, new Parameter(auditState), TraceProductAuditNew.class);
		return findBySql;
	}
	
	public List<TraceProductAuditNew> findByAuditIdInfo(String id) {
		String sql = "select a.* from t_trace_product_audit_new a where a.audit_id =:p1 ORDER BY a.audit_time ASC";
		return findBySql(sql, new Parameter(id), TraceProductAuditNew.class);
	}

	public TraceProductAuditNew findByAuditId(String auditId) {
		String sql = "select * from t_trace_product_audit_new a where a.audit_id =:p1 order by a.audit_time asc";
		List<TraceProductAuditNew> findBySql = findBySql(sql, new Parameter(auditId), TraceProductAuditNew.class);
		if (!findBySql.isEmpty()) {
			return findBySql.get(0);
		} else {
			return null;
		}
	}
	
	public String getBhReason(String auditId) {
		String sql = "select * from t_trace_product_audit_new a where a.audit_id =:p1 and a.audit_state='3' order by a.audit_time desc";
		List<TraceProductAuditNew> findBySql = findBySql(sql, new Parameter(auditId), TraceProductAuditNew.class);
		if (!findBySql.isEmpty()) {
			return findBySql.get(0).getReason();
		} else {
			return "";
		}
	}

	public String findByState(String auditState) {
		String sql = "select * from t_trace_product_audit_new a where a.audit_state =:p1 ";
		List<TraceProductAuditNew> findBySql = findBySql(sql, new Parameter(auditState), TraceProductAuditNew.class);
		String auditStateStr = "";
		if (!findBySql.isEmpty()) {
			for (TraceProductAuditNew pojo : findBySql) {
				auditStateStr += "'" + pojo.getAuditId() + "'" + ",";
			}
			if (StringUtils.isNotBlank(auditStateStr)) {
				auditStateStr = auditStateStr.substring(0, auditStateStr.length() - 1);
			}
		} else {
			auditStateStr = "''";
		}
		return auditStateStr;
	}
	
}
