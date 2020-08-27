package com.surekam.modules.api.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.surekam.common.mapper.JsonMapper;
import com.surekam.common.utils.ResultBean;
import com.surekam.common.utils.ResultEnum;
import com.surekam.common.utils.ResultUtil;
import com.surekam.common.utils.StringUtils;
import com.surekam.common.web.BaseController;
import com.surekam.modules.api.entity.Token;
import com.surekam.modules.api.service.ApiUserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value="同步token接口Controller", description="同步token接口")
@Controller
@RequestMapping(value = "api/synchronizationToken/")
public class SynchronizationToken extends BaseController {
	
	@Autowired
	private ApiUserService apiUserService;
	
	/**
	 * 农事系统登录时写入相同的token
	 * @param response
	 * @param loginName
	 * @param token
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "writeToken",method=RequestMethod.POST)
	@ApiOperation(value = "农事系统登录时写入相同的token", httpMethod = "POST", notes = "农事系统登录时写入相同的token",consumes="application/x-www-form-urlencoded")
	public String writeToken(HttpServletResponse response,HttpServletRequest request,String loginName,String token) {
		response.setContentType("application/json; charset=UTF-8");
		JsonMapper jsonMapper = JsonMapper.getInstance();
		ResultBean<Token> resultBean = new ResultBean<Token>();
		try {
			if(StringUtils.isNotBlank(loginName) && StringUtils.isNotBlank(token)){
				apiUserService.saveToken(loginName,token);
				resultBean = ResultUtil.success(new Token(token));
			}else{
				resultBean = ResultUtil.error(ResultEnum.LOGIN_FAILED.getCode(),ResultEnum.LOGIN_FAILED.getMessage());
			}
			return jsonMapper.toJson(resultBean);
		} catch (Exception e) {
			resultBean = ResultUtil.error(ResultEnum.SYSTEM_ERROR.getCode(),ResultEnum.SYSTEM_ERROR.getMessage());
			return jsonMapper.toJson(resultBean);
		}
	}
	
	
}