package com.surekam.modules.act.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.surekam.common.config.Global;
import com.surekam.common.persistence.Page;
import com.surekam.common.web.BaseController;
import com.surekam.common.utils.StringUtils;
import com.surekam.modules.act.entity.ActListener;
import com.surekam.modules.act.entity.ActListenerField;
import com.surekam.modules.act.service.ActListenerFieldService;
import com.surekam.modules.act.service.ActListenerService;

/**
 * activiti监听事件Controller
 * @author ludang
 * @version 2018-05-24
 */
@Controller
@RequestMapping(value = "${adminPath}/act/listener")
public class ActListenerController extends BaseController {

	@Autowired
	private ActListenerService actListenerService;
	
	@Autowired
	private ActListenerFieldService actListenerFieldService;
	
	@ModelAttribute
	public ActListener get(@RequestParam(required=false) String id) {
		if (StringUtils.isNotBlank(id)){
			return actListenerService.get(id);
		}else{
			return new ActListener();
		}
	}
	
	@RequiresPermissions("act:actListener:view")
	@RequestMapping(value = {"index"})
	public String index(Model model) {
		return "modules/" + "act/actListenerIndex";
	}
	
	@RequiresPermissions("act:actListener:view")
	@RequestMapping(value = {"list", ""})
	public String list(ActListener actListener, HttpServletRequest request, HttpServletResponse response, Model model) {
        Page<ActListener> page = actListenerService.find(new Page<ActListener>(request, response), actListener); 
        model.addAttribute("page", page);
        model.addAttribute("actListener", actListener);
		return "modules/" + "act/actListenerList";
	}

	@RequiresPermissions("act:actListener:view")
	@RequestMapping(value = "form")
	public String form(ActListener actListener, Model model) {
		model.addAttribute("actListener", actListener);
		return "modules/" + "act/actListenerForm";
	}

	@RequiresPermissions("act:actListener:edit")
	@RequestMapping(value = "save")
	public String save(ActListener actListener, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, actListener)){
			return form(actListener, model);
		}
		actListenerService.save(actListener);
		addMessage(redirectAttributes, "保存activiti监听事件成功");
		return "redirect:"+Global.getAdminPath()+"/act/listener/?repage";
	}
	
	@RequiresPermissions("act:actListener:edit")
	@RequestMapping(value = "delete")
	public String delete(String id, RedirectAttributes redirectAttributes) {
		actListenerService.delete(id);
		addMessage(redirectAttributes, "删除activiti监听事件成功");
		return "redirect:"+Global.getAdminPath()+"/act/listener/?repage";
	}
	
	//任务监听选择界面
	//type=task 任务监听  type=execution 执行监听
    @RequestMapping(value = "/select/{type}", method = RequestMethod.GET)
    public String selectUserPage(@PathVariable("type") String type, HttpServletRequest request) {
    	request.setAttribute("type", type);
        return "modules/act/listener_select";
    }

    @RequestMapping(value = "/select/list", method = RequestMethod.GET)
    @ResponseBody
	public Map<String, Object> listenerList(ActListener actListener, HttpServletRequest request, HttpServletResponse response) {
    	Page<ActListener> reqPage = new Page<ActListener>(request);
		Page<ActListener> page = actListenerService.find(reqPage, actListener);
		Map<String, Object> dataMap = new HashMap<String, Object>();
		dataMap.put("data",  page.getList());//需要在表格中显示的数据
		dataMap.put("draw",  reqPage.getDraw());//请求序号（DataTables强烈建议将此参数强制转换为int型，以阻止可能的XSS攻击）
		dataMap.put("recordsTotal",  page.getCount());//过滤之前的总数据量
		dataMap.put("recordsFiltered",  page.getCount());//过滤之后的总数据量
		return dataMap;
    }
    
    //********************************监听参数值管理"*************************************/
    @RequiresPermissions("act:actListenerField:view")
	@RequestMapping(value = {"listenerFieldList"})
	public String listenerFieldList(String listenerId, HttpServletRequest request, HttpServletResponse response, Model model) {
    	ActListener actListener = actListenerService.get(listenerId);
    	List<ActListenerField> fieldList = actListenerFieldService.findListenerFields(listenerId);
        model.addAttribute("listenerFields", fieldList);
        model.addAttribute("actListener", actListener);
		return "modules/" + "act/actListenerFieldList";
    }
    
    @RequiresPermissions("act:actListenerField:view")
	@RequestMapping(value = "listenerField")
	public String listenerFieldForm(String fieldId,String listenerId, Model model) {
		ActListenerField listenerField = new ActListenerField();
		ActListener actListener = actListenerService.get(listenerId);
		if(StringUtils.isNotBlank(fieldId)){
			listenerField = actListenerFieldService.get(fieldId);
		}else{
			listenerField.setActListener(actListener);
		}
		model.addAttribute("actListenerField", listenerField);
		model.addAttribute("actListener", actListener);
		
		return "modules/" + "act/actListenerFieldForm";
	}
    
    @RequiresPermissions("act:actListenerField:edit")
	@RequestMapping(value = "saveListenerField")
	public String saveListenerField(ActListenerField actListenerField, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, actListenerField)){
			return listenerFieldForm(actListenerField.getId(), actListenerField.getActListener().getId(), model);
		}
		actListenerFieldService.save(actListenerField);
		
		addMessage(redirectAttributes, "保存activiti监听事件参数成功");
		redirectAttributes.addAttribute("listenerId", actListenerField.getActListener().getId());
		
		return "redirect:"+Global.getAdminPath()+"/act/listener/listenerFieldList";
	}
    
    @RequiresPermissions("act:actListenerField:edit")
	@RequestMapping(value = "deleteListenerField")
	public String deleteListenerField(String[] ids ,String listenerId,  RedirectAttributes redirectAttributes) {
		redirectAttributes.addAttribute("listenerId", listenerId);
		try {
			actListenerFieldService.deleteListenerFields(ids);
			addMessage(redirectAttributes, "删除成功");
		} catch (Exception e) {
			addMessage(redirectAttributes, "删除失败");
		}
		return "redirect:"+Global.getAdminPath()+"/act/listener/listenerFieldList";
	}
}
