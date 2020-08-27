package com.surekam.modules.trace.TraceLableApply.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.surekam.common.persistence.BaseDao;
import com.surekam.common.persistence.Page;
import com.surekam.common.persistence.Parameter;
import com.surekam.common.utils.StringUtils;
import com.surekam.modules.trace.TraceLableApply.entity.TraceLableApply;

/**
 * 标签申请管理
 * 
 * @author wangyuewen
 */
@Repository
public class TraceLableApplyDao extends BaseDao<TraceLableApply> {
	/**
	 * 获取该申请的最大和最小标签号
	 * 
	 * @param page
	 * @param productName
	 * @param officeId
	 * @return
	 */
	public String getbqd(String applyId) {
		String result = "";
		StringBuffer sqlString = new StringBuffer();
		sqlString.append(" SELECT min(b.serial_number),max(b.serial_number) FROM t_trace_code_info b ");
		sqlString.append(" WHERE b.states <> 'D' AND b.apply_id = :p1 ");
		List<Object> list = findBySql(sqlString.toString(), new Parameter(applyId));
		if (list.size() > 0) {
			Object[] obj = (Object[]) list.get(0);
			result = (obj[0] == null ? "0" : obj[0].toString()) + "-" + (obj[1] == null ? "0" : obj[1].toString());
		}
		return result;
	}
	
	public Page<Map<String, String>> pageList(int no, int size, String ofcStr, String idStr, String type) {
		Page<Map<String, String>> page = new Page<Map<String, String>>(no, size);
		String sql = "select * from (select "
				+ "max(a.id) as id, "
				+ "a.trace_product_id as traceProductId,"
				+ "MAX(b.id) as batchId, "
				+ "b.office_id as officeId, "
				+ "c.name as officeName, "
				+ "b.batch_code as batchCode,"
				+ "d.product_name as productName, "
				+ "MAX(e.id) as propertyDataId, "
				+ "MAX(e.creat_time) as creatTime "
				+ "from t_trace_lable_apply a "
				+ "INNER JOIN t_trace_product_batch b on a.batch_id = b.id AND a.sys_id !='0' "
				+ "LEFT JOIN sys_office c on b.office_id = c.id "
				+ "INNER JOIN t_trace_product d on a.trace_product_id = d.id "
				+ "INNER JOIN t_trace_model_data e on b.office_id = e.office_id AND b.id = e.batch_id AND e.model_name = '主模块' "
				+ "where b.office_id in (" + ofcStr + ") ";
		
		if ("0".equals(type)) {
			sql += "and b.id not in (" + idStr + ") ";
			sql += "and a.states <> 'D' and b.states <> 'D' and c.DEL_FLAG ='0' and d.states <> 'D' and e.states <> 'D' ";
		} else if ("1".equals(type)) {
			sql += "and b.id in (" + idStr + ") ";
			sql += "and a.states <> 'D' and b.states <> 'D' and c.DEL_FLAG ='0' and d.states <> 'D' and e.states <> 'D' ";
		} else if ("2".equals(type)) {
			sql += "and b.batch_code not in (" + idStr + ") ";
			sql += "and a.states = 'D' and b.states = 'D' and c.DEL_FLAG ='0' and d.states <> 'D' and e.states = 'D' ";
		}
		sql += "GROUP BY a.trace_product_id, b.office_id, c. NAME, b.batch_code, d.product_name) a order by a.creatTime desc";
		Page<Map<String, String>> findBySql = findBySql(page, sql, Map.class);
		return findBySql;
	}
	
	public Page<Map<String, String>> pageListNew(int no, int size, String ofcStr, String type) {
		Page<Map<String, String>> page = new Page<Map<String, String>>(no, size);
		String sql = "select aa.* from (SELECT a.id AS batchId,a.batch_code AS batchCode,b.product_name AS productName,c.id as officeId,c.NAME AS officeName,MAX(d.id) AS propertyDataId,MAX(d.creat_time) AS creatTime FROM"
			+" t_trace_product_batch a, t_trace_product b, sys_office c, t_trace_model_data d" 
			+" WHERE a.states<>'D' AND b.states<>'D' AND c.DEL_FLAG='0' AND d.states<>'D'"
			+" AND a.product_id = b.id  AND a.office_id = c.ID AND a.id = d.batch_id AND d.model_name='主模块' and a.sys_id='0'";
		if(StringUtils.isNotBlank(ofcStr)){
			sql+=" and a.office_id IN (" + ofcStr + ")";
		}
		if("0".equals(type)){
			sql+=" and a.audit_status in ('0','3')";
		}else{
			sql+=" and a.audit_status ='" + type + "'";
		}		
		sql+=" GROUP BY a.id, a.batch_code, b.product_name, c.id,c.NAME ) aa order by aa.creatTime desc";
		Page<Map<String, String>> findBySql = findBySql(page, sql, Map.class);
		return findBySql;
	}
	
	public List<Map<String, String>> list(String ofcStr, String idStr, String type) {
		String sql = "select "
				+ "max(a.id) as id, "
				+ "a.trace_product_id as traceProductId,"
				+ "MAX(b.id) as batchId, "
				+ "b.office_id as officeId, "
				+ "c.name as officeName, "
				+ "b.batch_code as batchCode,"
				+ "d.product_name as productName, "
				+ "MAX(e.id) as propertyDataId, "
				+ "MAX(e.creat_time) as creatTime "
				
				+ "from t_trace_lable_apply a "
				+ "INNER JOIN t_trace_product_batch b on a.batch_id = b.id AND a.sys_id !='0' "
				+ "LEFT JOIN sys_office c on b.office_id = c.id "
				+ "INNER JOIN t_trace_product d on a.trace_product_id = d.id "
				+ "INNER JOIN t_trace_model_data e on b.office_id = e.office_id AND b.id = e.batch_id AND e.model_name = '主模块' "
				+ "where b.office_id in (" + ofcStr + ") ";
		if ("0".equals(type)) {
			sql += "and b.id not in (" + idStr + ") ";
			sql += "and a.states <> 'D' and b.states <> 'D' and c.DEL_FLAG ='0' and d.states <> 'D' and e.states <> 'D' ";
		} else if ("1".equals(type)) {
			sql += "and b.id in (" + idStr + ") ";
			sql += "and a.states <> 'D' and b.states <> 'D' and c.DEL_FLAG ='0' and d.states <> 'D' and e.states <> 'D' ";
		} else if ("2".equals(type)) {
			sql += "and b.batch_code not in (" + idStr + ") ";
			sql += "and a.states = 'D' and b.states = 'D' and c.DEL_FLAG ='0' and d.states <> 'D' and e.states = 'D' ";
		}
		sql += "GROUP BY a.trace_product_id, b.office_id, c. NAME, b.batch_code, d.product_name order by creatTime desc";
		List<Map<String, String>> findBySql = findBySql(sql, null, Map.class);
		return findBySql;
	}

	public Map<String, String> getParam(String propertyDataId, String officeId, String batchId) {
		String sql = "select a.property_value as propertyValue, c.pack_type_name as packTypeName "
				+ "from t_trace_property_data a inner join t_trace_property_new b on b.id = a.property_id and b.property_name_zh = '生产日期'"
				+ "inner join t_trace_code_info c where a.model_data_id =:p1 and c.office_id =:p2 and c.batch_id =:p3 ";
		List<Map<String, String>> findBySql = findBySql(sql, new Parameter(propertyDataId, officeId, batchId), Map.class);
		if (!findBySql.isEmpty()) {
			return findBySql.get(0);
		} else {
			return null;
		}
	}
	
	public Map<String, String> getParamNew(String propertyDataId) {
		String sql = "select a.property_value as propertyValue"
				+ " from t_trace_property_data a inner join t_trace_property_new b on b.id = a.property_id and b.property_name_zh = '生产日期'"
				+ " where a.model_data_id =:p1";
		List<Map<String, String>> findBySql = findBySql(sql, new Parameter(propertyDataId), Map.class);
		if (!findBySql.isEmpty()) {
			return findBySql.get(0);
		} else {
			return null;
		}
	}

	public List<Map<String, String>> getParam(String batchId) {
		String sql = "SELECT a.property_value as propertyValue,b.property_name_zh as propertyName"
				+ " FROM t_trace_model_data d "
				+ " LEFT JOIN t_trace_property_data a ON a.model_data_id =d.id"
				+ " LEFT join t_trace_property_new b ON b.id = a.property_id"
				+ " WHERE d.batch_id =:p1 AND d.model_name='基本信息'";
		return findBySql(sql, new Parameter(batchId), Map.class);
	}
	public List<Map<String, String>> getTemplate(String id, String modelName) {
		String sql = "select * from t_trace_lable_apply a "
				+ "INNER JOIN t_trace_product_batch b on a.sys_id = '1' and a.batch_id = b.id "
				+ "INNER JOIN t_trace_model_data c on b.office_id = c.office_id and b.id = c.batch_id "
				+ "where a.id =:p1 and c.model_name =:p2 ";
		List<Map<String, String>> findBySql = findBySql(sql, new Parameter(id, modelName), Map.class);
		return findBySql;
	}
	
	public List<TraceLableApply> findBybatchId(String batchId) {
		String sql = "select * from t_trace_lable_apply a where a.batch_id =:p1";
		List<TraceLableApply> findBySql = findBySql(sql, new Parameter(batchId), TraceLableApply.class);
		return findBySql;
	}

}
