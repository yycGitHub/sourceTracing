package com.surekam.modules.productbatch.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import com.surekam.common.persistence.BaseDao;
import com.surekam.common.persistence.Page;
import com.surekam.common.persistence.Parameter;
import com.surekam.modules.productbatch.entity.ProductBatch;
import com.surekam.modules.sys.entity.Role;
import com.surekam.modules.sys.entity.User;

/**
 * 产品批次DAO接口
 * 
 * @author liuyi
 * @version 2018-09-04
 */
@Repository
public class ProductBatchDao extends BaseDao<ProductBatch> {
	
	// 查看某个创建人创建的的批次号
	public List<String> getbatchCodeList(User user) {
		String hql = "select a.batchCode from ProductBatch a ";
		hql += " where a.states <> 'D'";
		hql += " and a.creatUserid = '" + user.getId() + "'";
		return find(hql, null);
	}
	
	public Page<ProductBatch> find(Page<ProductBatch> page, String batchName, String batchCode, String isEnd, String officeId, String productId, User user, String auditStateStr) {
		String sql = "select a.* from t_trace_product_batch a where a.sys_id = '0' and a.states<>'D'";
		if (StringUtils.isNotEmpty(batchName)) {
			sql += "and a.batch_name = '" + batchName + "' ";
		}
		if (StringUtils.isNotEmpty(batchCode)) {
			sql += "and a.batch_code = '" + batchCode + "' ";
		}
		if (StringUtils.isNotEmpty(isEnd)) {
			sql += "and a.is_end = '" + isEnd + "' ";
		}
		if (StringUtils.isNotEmpty(officeId)) {
			sql += "and a.office_id =  '" + officeId + "' ";
		}
		if (StringUtils.isNotEmpty(productId)) {
			sql += "and a.product_id = '" + productId + "' ";
		}
//		if (user.getRoleList() != null) {
//			List<Role> roleList = user.getRoleList();
//			if (roleList.size() == 1) {
//				Role role = roleList.get(0);
//				// 只能看自己的数据的角色
//				if (role.getDataScope().equals("8")) {
//					sql += "and a.creat_userid = '" + user.getId() + "' ";
//				}
//			}
//		}
		
		sql += " union ";
		sql += "select * from t_trace_product_batch b where (b.sys_id='2' or b.sys_id='1') ";
		if (StringUtils.isNotEmpty(batchName)) {
			sql += "and b.batch_name = '" + batchName + "' ";
		}
		if (StringUtils.isNotEmpty(batchCode)) {
			sql += "and b.batch_code = '" + batchCode + "' ";
		}
		if (StringUtils.isNotEmpty(isEnd)) {
			sql += "and b.is_end = '" + isEnd + "' ";
		}
		if (StringUtils.isNotEmpty(officeId)) {
			sql += "and b.office_id =  '" + officeId + "' ";
		}
		if (StringUtils.isNotEmpty(productId)) {
			sql += "and b.product_id = '" + productId + "' ";
		}
//		if (user.getRoleList() != null) {
//			List<Role> roleList = user.getRoleList();
//			if (roleList.size() == 1) {
//				Role role = roleList.get(0);
//				// 只能看自己的数据的角色
//				if (role.getDataScope().equals("8")) {
//					sql += "and b.creat_userid = '" + user.getId() + "' ";
//				}
//			}
//		}
		sql += "and b.id in (" + auditStateStr + ") ";
		sql += "order by creat_time desc ";
		Page<ProductBatch> findBySql = findBySql(page, sql, ProductBatch.class);
		return findBySql;
	}
	
	public List<ProductBatch> findByProductId(String productId, String auditStateStr) {
		String sql = "select * from t_trace_product_batch a  where a.product_id = '" + productId + "' and a.sys_id ='0' and a.states<>'D' and a.audit_status = '1'";
		sql += " union  ";
		sql += " select * from t_trace_product_batch a where (a.sys_id ='1' or a.sys_id ='2') and a.product_id = '" + productId + "' and a.id in (" + auditStateStr + ") order by creat_time desc ";
		List<ProductBatch> findBySql = findBySql(sql, null, ProductBatch.class);
		return findBySql;
	}
	
	public List<ProductBatch> findByBatchCode(String batchCode) {
		String sql = "select * from t_trace_product_batch a  where a.batch_code =:p1 order by a.creat_time desc ";
		List<ProductBatch> findBySql = findBySql(sql, new Parameter(batchCode), ProductBatch.class);
		return findBySql;
	}
	
	public List<ProductBatch> findBy(String states) {
		String sql = "select * from t_trace_product_batch a  where 1=1 ";
		if ("A".equals(states)) {
			sql += " and a.states <> 'D' and a.sys_id != '0' ";
		} else {
			sql += " and a.states = 'D' and a.sys_id != '0' ";
		}
		List<ProductBatch> findBySql = findBySql(sql, null, ProductBatch.class);
		return findBySql;
	}
	
}
