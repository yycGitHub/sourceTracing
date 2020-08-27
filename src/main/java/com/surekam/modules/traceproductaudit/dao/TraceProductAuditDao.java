package com.surekam.modules.traceproductaudit.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import com.surekam.common.persistence.BaseDao;
import com.surekam.common.persistence.Parameter;
import com.surekam.modules.traceproductaudit.entity.TraceProductAudit;

/**
 * 产品审核DAO接口
 * 
 * @author 唐军
 * @version 2019-07-24
 */
@Repository
public class TraceProductAuditDao extends BaseDao<TraceProductAudit> {

	public List<TraceProductAudit> findByAuditState(String auditState) {
		String sql = "select a.* from t_trace_product_audit a "
				+ "inner join (select a.audit_id as audit_id, max(a.audit_time) as audit_time from t_trace_product_audit a "
				+ "group by a.audit_id) v on v.audit_id = a.audit_id and v.audit_time = a.audit_time and a.audit_state =:p1 ";
		List<TraceProductAudit> findBySql = findBySql(sql, new Parameter(auditState), TraceProductAudit.class);
		return findBySql;
	}
	
	public List<TraceProductAudit> findByAuditState1(String auditState) {
		String sql = "select a.* from t_trace_product_audit a where a.audit_state =:p1 ";
		List<TraceProductAudit> findBySql = findBySql(sql, new Parameter(auditState), TraceProductAudit.class);
		return findBySql;
	}

	public TraceProductAudit findByAuditId(String auditId) {
		String sql = "select * from t_trace_product_audit a where a.audit_id =:p1 order by a.audit_time asc";
		List<TraceProductAudit> findBySql = findBySql(sql, new Parameter(auditId), TraceProductAudit.class);
		if (!findBySql.isEmpty()) {
			return findBySql.get(0);
		} else {
			return null;
		}
	}
	
	public String findByState(String auditState) {
		String sql = "select * from t_trace_product_audit a where a.audit_state =:p1 ";
		List<TraceProductAudit> findBySql = findBySql(sql, new Parameter(auditState), TraceProductAudit.class);
		String auditStateStr = "";
		if (!findBySql.isEmpty()) {
			for (TraceProductAudit pojo : findBySql) {
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
