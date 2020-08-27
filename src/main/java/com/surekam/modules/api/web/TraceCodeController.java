package com.surekam.modules.api.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.surekam.common.config.Global;
import com.surekam.common.mapper.JsonMapper;
import com.surekam.common.persistence.Page;
import com.surekam.common.utils.ResultBean;
import com.surekam.common.utils.ResultEnum;
import com.surekam.common.utils.ResultUtil;
import com.surekam.common.utils.StringUtils;
import com.surekam.common.web.BaseController;
import com.surekam.modules.api.entity.Token;
import com.surekam.modules.api.service.ApiUserService;
import com.surekam.modules.api.utils.ApiUtil;
import com.surekam.modules.sys.entity.User;
import com.surekam.modules.tracecode.entity.CodeDataVO;
import com.surekam.modules.tracecode.entity.TraceCode;
import com.surekam.modules.tracecode.service.TraceCodeService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 溯源码管理
 * @author wangyuewen
 *
 */

@Api(value="溯源码接口Controller", description="溯源码的相关数据接口")
@Controller
@RequestMapping(value = "api/traceCode")
public class TraceCodeController extends BaseController {

	@Autowired
	private TraceCodeService traceCodeService;
	
	@Autowired
	private ApiUserService apiUserService;
	
	@ModelAttribute
	public TraceCode get(@RequestParam(required=false) String id) {
		if (StringUtils.isNotBlank(id)){
			return traceCodeService.get(id);
		}else{
			return new TraceCode();
		}
	}
	
	/**
	 * 查询溯源码列表
	 * @param request
	 * @param response
	 * @param productName
	 * @param batchCode
	 * @param pageno
	 * @param pagesize
	 * @return
	 */
	@RequestMapping(value = "/list",produces="application/json;charset=UTF-8", method = {RequestMethod.GET})
	@ResponseBody
	@ApiOperation(value = "查询溯源码列表", httpMethod = "GET", 
	notes = "查询溯源码列表",	consumes="application/x-www-form-urlencoded")
	public String listP(HttpServletRequest request,HttpServletResponse response,
			@RequestParam(required = false) String productName,
			@RequestParam(required = false) String batchCode,
			@RequestParam(required = false) String traceCode,
			@RequestParam(required = false) String applyId,
			@RequestParam(required = false) Integer serialNumberStart,
			@RequestParam(required = false) Integer serialNumberEnd,
			@RequestParam Integer pageno,
			@RequestParam Integer pagesize) {
		response.setContentType("application/json; charset=UTF-8");
		int pageNo = pageno==null?Global.DEFAULT_PAGENO:pageno;
		int pageSize = pagesize==null?Global.DEFAULT_PAGESIZE:pagesize;
		Page<TraceCode> page = new Page<TraceCode>(pageNo,pageSize); 
		String token = request.getHeader("X-Token");
		if(StringUtils.isBlank(token)){
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(),ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
		String corpCode = "";
		if(token != null) {
			User user = apiUserService.getUserByToken(token);
			//超级管理默认查询所有企业，其他默认查询本企业批次
			if(!user.isAdmin()) {
				corpCode = user.getCompany().getId();
			}
		}
		page = traceCodeService.find(page, productName, batchCode, traceCode, corpCode, applyId, serialNumberStart, serialNumberEnd);
		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(page));
	}
	
	/**
	 * 查询溯源码是否存在
	 * @param request
	 * @param response
	 * @param code
	 * @return
	 */
	@RequestMapping(value = "/findTraceCode",produces="application/json;charset=UTF-8", method = {RequestMethod.GET})
	@ResponseBody
	@ApiOperation(value = "查询是否存在溯源码", httpMethod = "GET", 
	notes = "查询是否存在溯源码",	consumes="application/x-www-form-urlencoded")
	public String findTraceCode(HttpServletRequest request,HttpServletResponse response,
			@RequestParam(required = false) String code) {
		response.setContentType("application/json; charset=UTF-8");
		if(StringUtils.isBlank(code)){
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.BAD_REQ_PARAM.getCode(),ResultEnum.BAD_REQ_PARAM.getMessage()));
		}
		TraceCode traceCode = traceCodeService.getEntityByCode(code);
		if(traceCode != null && traceCode.getId() != null) {
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(traceCode));
		} else {
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.DATA_NOT_EXIST.getCode(),ResultEnum.DATA_NOT_EXIST.getMessage()));
		}
	}

	/**
	 * 删除溯源码
	 * @param request
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/delete",produces="application/json;charset=UTF-8", method = {RequestMethod.POST})
	@ResponseBody
	@ApiOperation(value = "溯源码删除", httpMethod = "POST", 
	notes = "溯源码删除",	consumes="application/x-www-form-urlencoded")
	public String batchDelete(HttpServletRequest request,@RequestParam String id){
		traceCodeService.delete(id);
		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success());
	}
	
	
	
	/**
	 * 获取产品未激活溯源码信息
	 * @param request
	 * @param response
	 * @param productId
	 * @return
	 */
	@RequestMapping(value = "/getNoActivationTraceCode",produces="application/json;charset=UTF-8", method = {RequestMethod.GET})
	@ResponseBody
	@ApiOperation(value = "获取产品未激活溯源码信息", httpMethod = "GET", 
	notes = "获取产品未激活溯源码信息",	consumes="application/x-www-form-urlencoded")
	public String getNoActivationTraceCode(HttpServletRequest request,HttpServletResponse response,
			@RequestParam(required = true) String productId) {
		response.setContentType("application/json; charset=UTF-8");
		String token = request.getHeader("X-Token");
		if(StringUtils.isBlank(token)){
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(),ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
		List<CodeDataVO> dataList =  traceCodeService.getTraceCodeByProductId(productId);
		//page = traceCodeService.find(page, productName, batchCode, traceCode, corpCode);
		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(dataList));
	}
	
	/**
	 * 手动绑定标签
	 * @param response
	 * @param batchId
	 * @param min
	 * @param max
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/activationTraceCode",method=RequestMethod.POST)
	@ApiOperation(value = "手动绑定标签", httpMethod = "POST", notes = "手动绑定标签",
				consumes="application/x-www-form-urlencoded")
	public String activationTraceCode(HttpServletResponse response,@RequestParam String batchId,@RequestParam String productId,@RequestParam String paramString) {
		response.setContentType("application/json; charset=UTF-8");
		try{
			if(StringUtils.isNotBlank(paramString)){
				String bq = traceCodeService.existSameBq(batchId, productId, paramString);
				if(StringUtils.isNotEmpty(bq)){
					String[] arr = bq.split("_");
					if(arr[0].equals("1")){
						return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.THEME_LABEL_NOTEXIST.getCode(), arr[1]+ResultEnum.THEME_LABEL_NOTEXIST.getMessage()));
					}else{
						return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.THEME_LABEL_EXIST.getCode(), arr[1]+ResultEnum.THEME_LABEL_EXIST.getMessage()));
					}
				}else{
					traceCodeService.activationTraceCode(batchId, productId, paramString);
				}
			}else{
				return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.THEME_LABEL_NULL.getCode(), ResultEnum.THEME_LABEL_NULL.getMessage()));
			}
		}catch(Exception e){
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.ACT_TRACE_CODE_ERROR.getCode(), ResultEnum.ACT_TRACE_CODE_ERROR.getMessage()));
		}
		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success());
	}
	
	/**
	 * 获取该批次的标签段
	 * @param request
	 * @param response
	 * @param productId
	 * @return
	 */
	@RequestMapping(value = "/getBqd",produces="application/json;charset=UTF-8", method = {RequestMethod.GET})
	@ResponseBody
	@ApiOperation(value = "获取产品未激活溯源码信息", httpMethod = "GET", 
	notes = "获取产品未激活溯源码信息",	consumes="application/x-www-form-urlencoded")
	public String getBqd(HttpServletRequest request,HttpServletResponse response,
			@RequestParam(required = true) String productId, @RequestParam(required = true) String batchId) {
		response.setContentType("application/json; charset=UTF-8");
		String qjjhm = "";
		String token = request.getHeader("X-Token");
		if(StringUtils.isBlank(token)){
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(),ResultEnum.TOKEN_IS_NULL.getMessage()));
		}else{
			//获取激活码
			List<Integer> jhdList = traceCodeService.getBqjhm(productId, batchId);
			qjjhm = ApiUtil.getQjjhm(jhdList);
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(qjjhm));
		}
	}
	
	
	@RequestMapping(value = "/getCodeListByApplyId",produces="application/json;charset=UTF-8", method = {RequestMethod.GET})
	@ResponseBody
	@ApiOperation(value = "标签id查溯源码", httpMethod = "GET", notes = "标签id查溯源码",	consumes="application/x-www-form-urlencoded")
	public String getCodeListByApplyId(HttpServletRequest request,HttpServletResponse response,
			@RequestParam(required = false) String applyId,
			@RequestParam Integer pageno,
			@RequestParam Integer pagesize) {
		response.setContentType("application/json; charset=UTF-8");
		int pageNo = pageno==null?Global.DEFAULT_PAGENO:pageno;
		int pageSize = pagesize==null?Global.DEFAULT_PAGESIZE:pagesize;
		Page<TraceCode> page = new Page<TraceCode>(pageNo,pageSize); 
		String token = request.getHeader("X-Token");
		if(StringUtils.isBlank(token)){
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(),ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
		page = traceCodeService.find(page, applyId);
		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(page));
	}
	
}
