package com.surekam.modules.Interface.web;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.surekam.common.mapper.JsonMapper;
import com.surekam.common.utils.ResultBean;
import com.surekam.common.utils.ResultEnum;
import com.surekam.common.utils.ResultUtil;
import com.surekam.modules.Interface.entity.TraceBaseTree;
import com.surekam.modules.Interface.service.TraceBaseTreeService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 保存基地信息接口
 * @author 腾农科技
 *
 */
@Api
@Controller
@RequestMapping(value = "api/")
public class BaseApiController {
	
	@Autowired
	private TraceBaseTreeService traceBaseTreeService;
	
	
	@RequestMapping(value = "addBase", produces = "application/json;charset=UTF-8", method = { RequestMethod.POST })
	@ResponseBody
	@ApiOperation(value = "保存基地信息", httpMethod = "POST", notes = "保存基地信息", consumes = "application/json")
	public String addBase(HttpServletRequest request,HttpServletResponse response,@RequestBody TraceBaseTree basetree) {
		response.setContentType("application/json; charset=UTF-8");
		JsonMapper jsonMapper = JsonMapper.getInstance();
		ResultBean<TraceBaseTree> resultBean = new ResultBean<TraceBaseTree>();
		try {
			traceBaseTreeService.save(basetree);
			return jsonMapper.toJson(ResultUtil.success());
		} catch (Exception e) {
			//logger.error("保存用户信息错误：" + e);
			resultBean = ResultUtil.error(ResultEnum.SYSTEM_ERROR.getCode(),ResultEnum.SYSTEM_ERROR.getMessage());
			return jsonMapper.toJson(resultBean);
		}
	}

}
