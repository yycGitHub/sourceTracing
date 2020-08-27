package com.surekam.modules.api.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.surekam.common.mapper.JsonMapper;
import com.surekam.common.utils.ResultEnum;
import com.surekam.common.utils.ResultUtil;
import com.surekam.common.utils.StringUtils;
import com.surekam.common.web.BaseController;
import com.surekam.modules.api.service.ApiUserService;
import com.surekam.modules.sys.entity.User;
import com.surekam.modules.trace.TraceLableTemplate.entity.TraceLableTemplate;
import com.surekam.modules.trace.TraceLableTemplate.service.TraceLableTemplateService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 标签模板接口
 * @author xy
 * @version 2018-11-22
 */

@Api(value="溯源模板接口Controller", description="溯源模板的相关数据接口")
@Controller
@RequestMapping(value = "api/traceLableTemplate")
public class TraceLableTemplateController extends BaseController{
	
	@Autowired
	private TraceLableTemplateService traceLableTemplateService;
	
	@Autowired
	private ApiUserService apiUserService;
	
	/**
	 * 标签下拉列表查询
	 * @param request
	 * @param response
	 * @param officeId公司id
	 * @return
	 */
	@RequestMapping(value = "labelTemplateList",produces="application/json;charset=UTF-8", method = {RequestMethod.POST})
	@ResponseBody
	@ApiOperation(value = "查询标签模板列表", httpMethod = "POST", notes = "查询标签模板列表",	consumes="application/x-www-form-urlencoded")
	public String labelTemplateList(HttpServletRequest request,HttpServletResponse response,@RequestParam String officeId) {
		response.setContentType("application/json; charset=UTF-8");
		String token = request.getHeader("X-Token");
		List<TraceLableTemplate> list =  new ArrayList<TraceLableTemplate>();
		if(token != null) {
			User user = apiUserService.getUserByToken(token);
			//超级管理默认查询所有企业，其他默认查询本企业批次
			if(StringUtils.isEmpty(officeId)) {
				if(!user.isAdmin()) {
					officeId = user.getCompany().getId();
				}
			}
			list = traceLableTemplateService.find(officeId);
 			List<Map<String, Object>> mapList = Lists.newArrayList();
			for (int i=0; i<list.size(); i++){
				TraceLableTemplate e = list.get(i);		
				Map<String, Object> map = Maps.newHashMap();
				map.put("id", e.getId());
				map.put("name", e.getLabelTemplateName());
				mapList.add(map);
			}
	        //返回没有信息提示
	  		if(0 == mapList.size()){
	  			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.COMPANY_LABEL_NULL.getCode(), ResultEnum.COMPANY_LABEL_NULL.getMessage()));
	  		}else{
	  			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(mapList));
	  		}
		} else {
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
		
	}

}
