/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/sureserve/surekam">surekam</a> All rights reserved.
 */
package com.surekam.modules.oa.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.surekam.common.persistence.Page;
import com.surekam.common.web.BaseController;
import com.surekam.modules.oa.entity.TestExpenses;
import com.surekam.modules.oa.service.TestExpensesService;
import com.surekam.modules.sys.entity.User;
import com.surekam.modules.sys.utils.UserUtils;

/**
 * 费用审批Controller
 * 
 * @author hegang
 * @version 2017-09-06
 */
@Controller
@RequestMapping(value = "${adminPath}/oa/testExpenses")
public class TestExpensesController extends BaseController {

	@Autowired
	private TestExpensesService testExpensesService;

	@ModelAttribute
	public TestExpenses get(@RequestParam(required = false) String id) {// ,
		// @RequestParam(value="act.procInsId", required=false) String
		// procInsId) {
		TestExpenses testExpenses = null;
		if (StringUtils.isNotBlank(id)) {
			testExpenses = testExpensesService.get(id);
			// }else if (StringUtils.isNotBlank(procInsId)){
			// testExpenses = testExpensesService.getByProcInsId(procInsId);
		}
		if (testExpenses == null) {
			testExpenses = new TestExpenses();
		}
		return testExpenses;
	}

	@RequestMapping(value = { "list", "" })
	public String list(TestExpenses testExpenses, HttpServletRequest request, HttpServletResponse response,
			Model model) {
		User user = UserUtils.getUser();
		if (!user.isAdmin()) {
			testExpenses.setCreateBy(user);
		}
		Page<TestExpenses> page = testExpensesService.findPage(new Page<TestExpenses>(request, response), testExpenses);
		model.addAttribute("page", page);
		return "modules/oa/testExpensesList";
	}

	/**
	 * 申请单填写
	 * 
	 * @param testExpenses
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "form")
	public String form(TestExpenses testExpenses, Model model) {

		String view = "testExpensesForm";
		// 查看审批申请单
		if (StringUtils.isNotBlank(testExpenses.getId())) {// .getAct().getProcInsId())){

			// 环节编号
			String taskDefKey = testExpenses.getAct().getTaskDefKey();

			// 查看工单
			if (testExpenses.getAct().isFinishTask()) {
				view = "testExpensesView";
			}
			// 修改环节
			else if ("modify".equals(taskDefKey)) {
				view = "testExpensesForm";
			}
			// 审核环节
			else if ("audit".equals(taskDefKey)) {
				view = "testExpensesAudit";
				// String formKey = "/oa/testExpenses";
				// return "redirect:" + ActUtils.getFormUrl(formKey,
				// testExpenses.getAct());
			}
			// 审核环节2
			else if ("audit2".equals(taskDefKey)) {
				view = "testExpensesAudit";
			}
			// 审核环节3
			else if ("audit3".equals(taskDefKey)) {
				view = "testExpensesAudit";
			}
			// 审核环节4
			else if ("audit4".equals(taskDefKey)) {
				view = "testExpensesAudit";
			}
			// 兑现环节
			else if ("apply_end".equals(taskDefKey)) {
				view = "testExpensesAudit";
			}
		}

		model.addAttribute("testExpenses", testExpenses);
		model.addAttribute("user", UserUtils.getUser());
		return "modules/oa/" + view;
	}

	/**
	 * 申请单保存/修改
	 * 
	 * @param testExpenses
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(value = "save")
	public String save(TestExpenses testExpenses, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, testExpenses)) {
			return form(testExpenses, model);
		}
		User user = UserUtils.getUser();
		testExpenses.setCreateBy(user);
		testExpenses.setOffice(user.getOffice());
		testExpensesService.save(testExpenses);
		addMessage(redirectAttributes, "提交审批'" + testExpenses.getUser().getName() + "'成功");
		return "redirect:" + adminPath + "/act/task/todo/";
	}

	/**
	 * 工单执行（完成任务）
	 * 
	 * @param testExpenses
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "saveAudit")
	public String saveAudit(TestExpenses testExpenses, Model model) {
		if (StringUtils.isBlank(testExpenses.getAct().getFlag())
				|| StringUtils.isBlank(testExpenses.getAct().getComment())) {
			addMessage(model, "请填写审核意见。");
			return form(testExpenses, model);
		}
		testExpensesService.auditSave(testExpenses);
		return "redirect:" + adminPath + "/act/task/todo/";
	}

	/**
	 * 删除工单
	 * 
	 * @param id
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(value = "delete")
	public String delete(TestExpenses testExpenses, RedirectAttributes redirectAttributes) {
		testExpensesService.delete(testExpenses);
		addMessage(redirectAttributes, "删除审批成功");
		return "redirect:" + adminPath + "/oa/testExpenses/?repage";
	}

}
