/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/sureserve/surekam">surekam</a> All rights reserved.
 */
package com.surekam.modules.oa.service;

import java.util.Map;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.surekam.common.persistence.Page;
import com.surekam.common.service.BaseService;
import com.surekam.common.utils.StringUtils;
import com.surekam.modules.act.service.ActTaskService;
import com.surekam.modules.act.utils.ActUtils;
import com.surekam.modules.oa.dao.TestExpensesDao;
import com.surekam.modules.oa.entity.TestExpenses;

/**
 * 审批Service
 * 
 * @author hegang
 * @version 2017-09-06
 */
@Service
@Transactional(readOnly = true)
public class TestExpensesService extends BaseService {

	@Autowired
	private ActTaskService actTaskService;
	@Autowired
	private TestExpensesDao testExpensesDao;

	public TestExpenses getByProcInsId(String procInsId) {
		return testExpensesDao.getByProcInsId(procInsId);
	}

	public Page<TestExpenses> findPage(Page<TestExpenses> page, TestExpenses testExpenses) {
		DetachedCriteria dc = testExpensesDao.createDetachedCriteria();
		if (testExpenses != null && testExpenses.getUser() != null
				&& StringUtils.isNotBlank(testExpenses.getUser().getId())) {
			dc.add(Restrictions.like("user.id", testExpenses.getUser().getId(), MatchMode.ANYWHERE));
		}
		dc.add(Restrictions.eq(TestExpenses.FIELD_DEL_FLAG, TestExpenses.DEL_FLAG_NORMAL));
		dc.addOrder(Order.desc("updateDate"));
		return testExpensesDao.find(page, dc);
	}

	/**
	 * 审核新增或编辑
	 * 
	 * @param testAudit
	 */
	@Transactional(readOnly = false)
	public void save(TestExpenses testExpenses) {
		// 申请发起
		if (StringUtils.isBlank(testExpenses.getId())) {
			testExpenses.preInsert();
			testExpensesDao.save(testExpenses);
			testExpensesDao.flush();
			// 启动流程
			actTaskService.startProcess(ActUtils.PD_EXPENSES_AUDIT[0], ActUtils.PD_EXPENSES_AUDIT[1],
					testExpenses.getId(), testExpenses.getContent());
		}

		// 重新编辑申请
		else {
			testExpenses.preUpdate();
			testExpensesDao.save(testExpenses);
			testExpenses.getAct().setComment(("yes".equals(testExpenses.getAct().getFlag()) ? "[重申] " : "[销毁] ")
					+ testExpenses.getAct().getComment());

			// 完成流程任务
			Map<String, Object> vars = Maps.newHashMap();
			// vars.put("pass", "yes".equals(testExpenses.getAct().getFlag()) ?
			// "1" : "0");
			vars.put("exp", testExpenses.getAddNum());
			actTaskService.complete(testExpenses.getAct().getTaskId(), testExpenses.getAct().getProcInsId(),
					testExpenses.getAct().getComment(), testExpenses.getContent(), vars);
		}
	}

	/**
	 * 审核审批保存
	 * 
	 * @param testExpenses
	 */
	@Transactional(readOnly = false)
	public void auditSave(TestExpenses testExpenses) {

		// 设置意见
		testExpenses.getAct().setComment(("yes".equals(testExpenses.getAct().getFlag()) ? "[同意] " : "[驳回] ")
				+ testExpenses.getAct().getComment());

		testExpenses.preUpdate();

		// 对不同环节的业务逻辑进行操作
		String taskDefKey = testExpenses.getAct().getTaskDefKey();

		// 审核环节
		if ("audit".equals(taskDefKey)) {

		} else if ("audit2".equals(taskDefKey)) {
			testExpenses.setHrText(testExpenses.getAct().getComment());
			testExpensesDao.updateHrText(testExpenses);
		} else if ("audit3".equals(taskDefKey)) {
			testExpenses.setLeadText(testExpenses.getAct().getComment());
			testExpensesDao.updateLeadText(testExpenses);
		} else if ("apply_end".equals(taskDefKey)) {

		}

		// 未知环节，直接返回
		else {
			return;
		}

		// 提交流程任务
		Map<String, Object> vars = Maps.newHashMap();
		// vars.put("pass", "yes".equals(testExpenses.getAct().getFlag()) ? "1"
		// : "0");
		vars.put("exp", testExpenses.getAddNum());
		actTaskService.complete(testExpenses.getAct().getTaskId(), testExpenses.getAct().getProcInsId(),
				testExpenses.getAct().getComment(), vars);

		// vars.put("var_test", "yes_no_test2");
		// actTaskService.getProcessEngine().getTaskService().addComment(testExpenses.getAct().getTaskId(),
		// testExpenses.getAct().getProcInsId(),
		// testExpenses.getAct().getComment());
		// actTaskService.jumpTask(testExpenses.getAct().getProcInsId(),
		// testExpenses.getAct().getTaskId(), "audit2", vars);
	}

	public TestExpenses get(String id) {
		// TODO Auto-generated method stub
		return testExpensesDao.get(id);
	}

	@Transactional(readOnly = false)
	public void delete(TestExpenses testExpenses) {
		// TODO Auto-generated method stub
		testExpensesDao.deleteById(testExpenses.getId());
	}

}
