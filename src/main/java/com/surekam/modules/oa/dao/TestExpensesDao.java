/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/sureserve/surekam">surekam</a> All rights reserved.
 */
package com.surekam.modules.oa.dao;

import org.springframework.stereotype.Repository;

import com.surekam.common.persistence.BaseDao;
import com.surekam.common.persistence.Parameter;
import com.surekam.modules.oa.entity.TestExpenses;

/**
 * 审批DAO接口
 */
@Repository
public class TestExpensesDao extends BaseDao<TestExpenses> {

	public TestExpenses getByProcInsId(String procInsId) {
		return (TestExpenses) this.findBySql("SELECT a.* FROM oa_test_audit a WHERE a.proc_ins_id = :p1",
				new Parameter(procInsId), TestExpenses.class).get(0);
	};

	public int updateInsId(TestExpenses testExpenses) {
		return this.updateBySql(
				"UPDATE oa_test_audit " + "SET proc_ins_id = :p1,"
						+ "update_by = :p2, update_date = :p3 WHERE id = :p4",
				new Parameter(testExpenses.getProcInsId(), testExpenses.getUpdateBy().getId(),
						testExpenses.getUpdateDate(), testExpenses.getId()));
	};

	public int updateHrText(TestExpenses testExpenses) {
		return this.updateBySql(
				"UPDATE oa_test_audit " + "SET hr_text = :p1,update_by = :p2," + "update_date = :p3 WHERE id = :p4",
				new Parameter(testExpenses.getHrText(), testExpenses.getUpdateBy().getId(),
						testExpenses.getUpdateDate(), testExpenses.getId()));
	};

	public int updateLeadText(TestExpenses testExpenses) {
		return this.updateBySql(
				"UPDATE oa_test_audit " + "SET lead_text = :p1,update_by = :p2" + ", update_date = :p3 WHERE id = :p4",
				new Parameter(testExpenses.getLeadText(), testExpenses.getUpdateBy().getId(),
						testExpenses.getUpdateDate(), testExpenses.getId()));
	};

}
