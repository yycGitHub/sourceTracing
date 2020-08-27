package com.surekam.modules.act.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.surekam.common.persistence.Page;
import com.surekam.common.web.BaseController;
import com.surekam.modules.sys.entity.Role;
import com.surekam.modules.sys.entity.User;
import com.surekam.modules.sys.service.SystemService;

@Controller
@RequestMapping("${adminPath}/act")
public class IdentityController extends BaseController {
	
	@Autowired
	private SystemService systemService;
	
	//用户选择界面
    //multiple=0 单选  multiple=1 多选
    @RequestMapping(value = "/user/select/{multiple}/{ids}", method = RequestMethod.GET)
    public String selectUserPage(@PathVariable("multiple") String multiple,
                                 @PathVariable("ids") String ids,  HttpServletRequest request) {
        request.setAttribute("multiple", multiple);
        request.setAttribute("ids", ids);
        return "modules/act/id_user_select";
    }

    //用户组选择界面
    @RequestMapping(value = "/group/select/{ids}", method = RequestMethod.GET)
    public String selectGroupPage(@PathVariable("ids") String ids, HttpServletRequest request) {
        request.setAttribute("ids", ids);
        return "modules/act/id_group_select";
    }

    @RequestMapping(value = "/{type}/names", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> getNamesByIds(@PathVariable("type") String type, String ids) {
        Map<String,String> map=new HashMap<String, String>();
        if ("user".equals(type)) {
            String names=systemService.getUserNamesByUserIds(ids);
            map.put("name",names);
            return map;
        } else {
            String names=systemService.getGroupNamesByGroupIds(ids);
            map.put("name",names);
            return map;
        }
    }
    
    @RequestMapping(value = "/user/list", method = RequestMethod.GET)
    @ResponseBody
	public Map<String, Object> userList(User user, Model model,HttpServletRequest request, HttpServletResponse response) {
    	Page<User> reqPage = new Page<User>(request);
		Page<User> page = systemService.findUser(reqPage,user);
		Map<String, Object> dataMap = new HashMap<String, Object>();
		dataMap.put("data",  page.getList());//需要在表格中显示的数据
		dataMap.put("draw",  reqPage.getDraw());//请求序号（DataTables强烈建议将此参数强制转换为int型，以阻止可能的XSS攻击）
		dataMap.put("recordsTotal",  page.getCount());//过滤之前的总数据量
		dataMap.put("recordsFiltered",  page.getCount());//过滤之后的总数据量
		return dataMap;
	}
    
    @RequestMapping(value = "/role/list", method = RequestMethod.GET)
    @ResponseBody
	public Map<String, Object> roleList(Role role, Model model,HttpServletRequest request, HttpServletResponse response) {
    	Page<Role> reqPage = new Page<Role>(request);
		Page<Role> page = systemService.findRole(reqPage,role);
		Map<String, Object> dataMap = new HashMap<String, Object>();
		dataMap.put("data",  page.getList());
		dataMap.put("draw",  reqPage.getDraw());
		dataMap.put("recordsTotal",  page.getCount());
		dataMap.put("recordsFiltered",  page.getCount());
		return dataMap;
	}

}
