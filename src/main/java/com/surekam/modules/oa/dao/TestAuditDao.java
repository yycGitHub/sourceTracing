/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/sureserve/surekam">surekam</a> All rights reserved.
 */
package com.surekam.modules.oa.dao;

import org.springframework.stereotype.Repository;

import com.surekam.common.persistence.BaseDao;
import com.surekam.common.persistence.Parameter;
import com.surekam.modules.oa.entity.TestAudit;

/**
 * 审批DAO接口
 */
@Repository
public class TestAuditDao extends BaseDao<TestAudit> {

	public TestAudit getByProcInsId(String procInsId){
		return (TestAudit)this.findBySql("SELECT a.* FROM oa_test_audit a WHERE a.proc_ins_id = :p1", new Parameter(procInsId), TestAudit.class).get(0);
	};
	
	public int updateInsId(TestAudit testAudit){
		return this.updateBySql("UPDATE oa_test_audit "
				+ "SET proc_ins_id = :p1,"
				+ "update_by = :p2, update_date = :p3 WHERE id = :p4"
				,new Parameter(testAudit.getProcInsId(),testAudit.getUpdateBy().getId(),testAudit.getUpdateDate(),testAudit.getId()));
	};
	
	public int updateHrText(TestAudit testAudit){
		return this.updateBySql("UPDATE oa_test_audit "
				+ "SET hr_text = :p1,update_by = :p2,"
				+ "update_date = :p3 WHERE id = :p4"
				,new Parameter(testAudit.getHrText(),testAudit.getUpdateBy().getId(),testAudit.getUpdateDate(),testAudit.getId()));
	};
	
	public int updateLeadText(TestAudit testAudit){
		return this.updateBySql("UPDATE oa_test_audit "
				+ "SET lead_text = :p1,update_by = :p2"
				+ ", update_date = :p3 WHERE id = :p4"
				,new Parameter(testAudit.getLeadText(),testAudit.getUpdateBy().getId(),testAudit.getUpdateDate(),testAudit.getId()));
	};
	
	public int updateMainLeadText(TestAudit testAudit){
		return this.updateBySql("UPDATE oa_test_audit "
				+ "SET main_lead_text = :p1,update_by = :p2"
				+ ", update_date = :p3 WHERE id = :p4"
				,new Parameter(testAudit.getMainLeadText(),testAudit.getUpdateBy().getId(),testAudit.getUpdateDate(),testAudit.getId()));
	}
}
