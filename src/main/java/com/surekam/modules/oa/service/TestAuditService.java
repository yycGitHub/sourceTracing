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
import com.surekam.modules.oa.dao.TestAuditDao;
import com.surekam.modules.oa.entity.TestAudit;

/**
 * 审批Service
 * @author sureserve
 * @version 2014-05-16
 */
@Service
@Transactional(readOnly = true)
public class TestAuditService extends BaseService{

	@Autowired
	private ActTaskService actTaskService;
	@Autowired
	private TestAuditDao auditDao;
	
	public TestAudit getByProcInsId(String procInsId) {
		return auditDao.getByProcInsId(procInsId);
	}
	
	public Page<TestAudit> findPage(Page<TestAudit> page, TestAudit testAudit) {
		DetachedCriteria dc = auditDao.createDetachedCriteria();
		if(testAudit!=null && testAudit.getUser()!= null && StringUtils.isNotBlank(testAudit.getUser().getId())){
			dc.add(Restrictions.like("user.id", testAudit.getUser().getId(),MatchMode.ANYWHERE));
		}
		dc.add(Restrictions.eq(TestAudit.FIELD_DEL_FLAG, TestAudit.DEL_FLAG_NORMAL));
		dc.addOrder(Order.desc("updateDate"));
		return auditDao.find(page, dc);
	}
	
	/**
	 * 审核新增或编辑
	 * @param testAudit
	 */
	@Transactional(readOnly = false)
	public void save(TestAudit testAudit) {
		// 申请发起
		if (StringUtils.isBlank(testAudit.getId())){
			testAudit.preInsert();
			auditDao.save(testAudit);	
			auditDao.flush();
			// 启动流程
			String procInsId = actTaskService.startProcess(ActUtils.PD_TEST_AUDIT[0], ActUtils.PD_TEST_AUDIT[1], testAudit.getId(), testAudit.getContent());		
			testAudit.getAct().setProcInsId(procInsId);
		}
		
		// 重新编辑申请		
		else{
			testAudit.preUpdate();
			auditDao.save(testAudit);
			testAudit.getAct().setComment(("yes".equals(testAudit.getAct().getFlag())?"[重申] ":"[销毁] ")+testAudit.getAct().getComment());
			
			// 完成流程任务
			Map<String, Object> vars = Maps.newHashMap();
			vars.put("pass", "yes".equals(testAudit.getAct().getFlag())? "1" : "0");
			actTaskService.complete(testAudit.getAct().getTaskId(), testAudit.getAct().getProcInsId(), testAudit.getAct().getComment(), testAudit.getContent(), vars);
		}
	}

	/**
	 * 审核审批保存
	 * @param testAudit
	 */
	@Transactional(readOnly = false)
	public void auditSave(TestAudit testAudit) {
		
		// 设置意见
		testAudit.getAct().setComment(("yes".equals(testAudit.getAct().getFlag())?"[同意] ":"[驳回] ")+testAudit.getAct().getComment());
		
		testAudit.preUpdate();
		
		// 对不同环节的业务逻辑进行操作
		String taskDefKey = testAudit.getAct().getTaskDefKey();

		// 审核环节
		if ("audit".equals(taskDefKey)){
			
		}
		else if ("audit2".equals(taskDefKey)){
			testAudit.setHrText(testAudit.getAct().getComment());
			auditDao.updateHrText(testAudit);
		}
		else if ("audit3".equals(taskDefKey)){
			testAudit.setLeadText(testAudit.getAct().getComment());
			auditDao.updateLeadText(testAudit);
		}
		else if ("audit4".equals(taskDefKey)){
			testAudit.setMainLeadText(testAudit.getAct().getComment());
			auditDao.updateMainLeadText(testAudit);
		}
		else if ("apply_end".equals(taskDefKey)){
			
		}
		
		// 未知环节，直接返回
		else{
			return;
		}
		
		// 提交流程任务
		Map<String, Object> vars = Maps.newHashMap();
		vars.put("pass", "yes".equals(testAudit.getAct().getFlag())? "1" : "0");
		actTaskService.complete(testAudit.getAct().getTaskId(), testAudit.getAct().getProcInsId(), testAudit.getAct().getComment(), vars);

//		vars.put("var_test", "yes_no_test2");
//		actTaskService.getProcessEngine().getTaskService().addComment(testAudit.getAct().getTaskId(), testAudit.getAct().getProcInsId(), testAudit.getAct().getComment());
//		actTaskService.jumpTask(testAudit.getAct().getProcInsId(), testAudit.getAct().getTaskId(), "audit2", vars);
	}

	public TestAudit get(String id) {
		// TODO Auto-generated method stub
		return auditDao.get(id);
	}

	@Transactional(readOnly = false)
	public void delete(TestAudit testAudit) {
		// TODO Auto-generated method stub
		auditDao.deleteById(testAudit.getId());
	}
	
}
