package com.surekam.modules.act.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.surekam.common.config.Global;
import com.surekam.common.persistence.Page;
import com.surekam.common.web.BaseController;
import com.surekam.common.utils.StringUtils;
import com.surekam.modules.sys.entity.User;
import com.surekam.modules.sys.utils.UserUtils;
import com.surekam.modules.act.entity.ActListenerField;
import com.surekam.modules.act.service.ActListenerFieldService;

/**
 * activiti监听事件参数Controller
 * @author ludang
 * @version 2018-06-12
 */
@Controller
@RequestMapping(value = "${adminPath}/actfdfd/actListenerField")
public class ActListenerFieldController extends BaseController {

	@Autowired
	private ActListenerFieldService actListenerFieldService;
	
	@ModelAttribute
	public ActListenerField get(@RequestParam(required=false) String id) {
		if (StringUtils.isNotBlank(id)){
			return actListenerFieldService.get(id);
		}else{
			return new ActListenerField();
		}
	}
	
	@RequiresPermissions("act:actListenerField:view")
	@RequestMapping(value = {"list", ""})
	public String list(ActListenerField actListenerField, HttpServletRequest request, HttpServletResponse response, Model model) {
        Page<ActListenerField> page = actListenerFieldService.find(new Page<ActListenerField>(request, response), actListenerField); 
        model.addAttribute("page", page);
		return "modules/" + "act/actListenerFieldList";
	}

	@RequiresPermissions("act:actListenerField:view")
	@RequestMapping(value = "form")
	public String form(ActListenerField actListenerField, Model model) {
		model.addAttribute("actListenerField", actListenerField);
		return "modules/" + "act/actListenerFieldForm";
	}

	@RequiresPermissions("act:actListenerField:edit")
	@RequestMapping(value = "save")
	public String save(ActListenerField actListenerField, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, actListenerField)){
			return form(actListenerField, model);
		}
		actListenerFieldService.save(actListenerField);
		addMessage(redirectAttributes, "保存activiti监听事件参数成功");
		return "redirect:"+Global.getAdminPath()+"/act/actListenerField/?repage";
	}
	
	@RequiresPermissions("act:actListenerField:edit")
	@RequestMapping(value = "delete")
	public String delete(String id, RedirectAttributes redirectAttributes) {
		actListenerFieldService.delete(id);
		addMessage(redirectAttributes, "删除activiti监听事件参数成功");
		return "redirect:"+Global.getAdminPath()+"/act/actListenerField/?repage";
	}

}
