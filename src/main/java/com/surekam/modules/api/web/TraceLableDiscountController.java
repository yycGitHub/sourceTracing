package com.surekam.modules.api.web;

import java.util.Date;

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
import com.surekam.modules.api.service.ApiUserService;
import com.surekam.modules.sys.dao.UserDao;
import com.surekam.modules.sys.entity.CommonUser;
import com.surekam.modules.sys.entity.User;
import com.surekam.modules.tracelablediscount.entity.DiscountPriceVO;
import com.surekam.modules.tracelablediscount.entity.TraceLableDiscount;
import com.surekam.modules.tracelablediscount.service.TraceLableDiscountService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 标签折扣优惠接口
 * @author xy
 * @version 2018-11-22
 */

@Api(value="溯源标签折扣接口Controller", description="溯源标签折扣的相关数据接口")
@Controller
@RequestMapping(value = "api/lableDiscount")
public class TraceLableDiscountController extends BaseController{
	
	@Autowired
	private TraceLableDiscountService traceLableDiscountService;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private ApiUserService apiUserService;
	
	@ModelAttribute
	public TraceLableDiscount get(@RequestParam(required=false) String id) {
		if (StringUtils.isNotBlank(id)){
			return traceLableDiscountService.get(id);
		}else{
			return new TraceLableDiscount();
		}
	}
	
	@RequestMapping(value = "traceLableDiscountList",produces="application/json;charset=UTF-8", method = {RequestMethod.GET})
	@ResponseBody
	@ApiOperation(value = "标签折扣信息查询", httpMethod = "GET", notes = "标签折扣信息查询",	consumes="application/x-www-form-urlencoded")
	public String TraceLableDiscountList(HttpServletRequest request,HttpServletResponse response,@RequestParam(required = false) String lableTemplateId,
			@RequestParam(required = false) String corpCode,
			@RequestParam(required = false) Integer pageno,
			@RequestParam(required = false) Integer pagesize) {
		response.setContentType("application/json; charset=UTF-8");
		int pageNo = pageno==null?Global.DEFAULT_PAGENO:pageno;
		int pageSize = pagesize==null?Global.DEFAULT_PAGESIZE:pagesize;
		String token = request.getHeader("X-Token");
		User user = apiUserService.getUserByToken(token);
		Page<TraceLableDiscount> page = new Page<TraceLableDiscount>(pageNo,pageSize); 
		page = traceLableDiscountService.find(page, lableTemplateId,corpCode,user);
  		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(page));
	}
	
	
	@RequestMapping(value = "traceLableCalculation",produces="application/json;charset=UTF-8", method = {RequestMethod.GET})
	@ResponseBody
	@ApiOperation(value = "标签总价计算", httpMethod = "GET", notes = "标签总价计算",	consumes="application/x-www-form-urlencoded")
	public String TraceLableCalculation(HttpServletRequest request,HttpServletResponse response,@RequestParam(required = false) String lableTemplateId,@RequestParam(required = false) String applyNum) {
		response.setContentType("application/json; charset=UTF-8");
		DiscountPriceVO discountPriceVO= new DiscountPriceVO();
		discountPriceVO = traceLableDiscountService.calculatedTotalPrice(lableTemplateId,applyNum);
  		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(discountPriceVO));
	}
	
	@RequestMapping(value = "addDiscountSave",produces="application/json;charset=UTF-8", method = {RequestMethod.POST})
	@ResponseBody
	@ApiOperation(value = "标签折扣新增保存", httpMethod = "POST", notes = "标签折扣新增保存",	consumes="application/x-www-form-urlencoded")
	public String addDiscountSave(HttpServletRequest request,
			@RequestParam  String officeId,
			@RequestParam  String lableTemplateId,
			@RequestParam  String numMin,
			@RequestParam  String numMax,
			@RequestParam  String discount) {
		String token = request.getHeader("X-Token");
		TraceLableDiscount traceLableDiscount = new TraceLableDiscount();
		if(StringUtils.isBlank(token)){
			token = "0021fa19-5f42-4d8f-921b-3dee9236c74a";
		}
		CommonUser user = userDao.findByToken(token);
		traceLableDiscount.setCreatUserid(user.getId());
		traceLableDiscount.setCreatTime(new Date());
		traceLableDiscount.setNumMax(numMax);
		traceLableDiscount.setNumMin(numMin);
		traceLableDiscount.setDiscount(Double.parseDouble(discount));
		traceLableDiscount.setLabelTemplateId(lableTemplateId);
		
		ResultBean<TraceLableDiscount> resultBean = new ResultBean<TraceLableDiscount>();
		try{
			traceLableDiscountService.save(traceLableDiscount);
			resultBean = ResultUtil.success(traceLableDiscount);
		}catch(Exception e){
			e.printStackTrace();
			resultBean = ResultUtil.error(ResultEnum.SYSTEM_ERROR.getCode(),ResultEnum.SYSTEM_ERROR.getMessage());
		}
		return JsonMapper.nonDefaultMapper().toJson(resultBean);
	}
	
	
	@RequestMapping(value = "updateDiscountSave",produces="application/json;charset=UTF-8", method = {RequestMethod.POST})
	@ResponseBody
	@ApiOperation(value = "标签折扣修改保存", httpMethod = "POST", notes = "标签折扣修改保存",	consumes="application/x-www-form-urlencoded")
	public String updateDiscountSave(HttpServletRequest request,
			@RequestParam  String id,
			@RequestParam  String officeId,
			@RequestParam  String labelTemplateId,
			@RequestParam  String numMin,
			@RequestParam  String numMax,
			@RequestParam  String discount) {
		String token = request.getParameter("token");
		TraceLableDiscount traceLableDiscount = traceLableDiscountService.get(id);
		
		if(StringUtils.isNotBlank(numMax)) {
			traceLableDiscount.setNumMax(numMax);
		}
		if(StringUtils.isNotBlank(numMin)) {
			traceLableDiscount.setNumMin(numMin);
		}
		if(StringUtils.isNotBlank(discount)) {
			traceLableDiscount.setDiscount(Double.parseDouble(discount));
		}
		if(StringUtils.isNotBlank(labelTemplateId)) {
			traceLableDiscount.setLabelTemplateId(labelTemplateId);
		}
		
		if(StringUtils.isBlank(token)){
			token = "0021fa19-5f42-4d8f-921b-3dee9236c74a";
		}
		CommonUser user = userDao.findByToken(token);
		traceLableDiscount.setUpdateTime(new Date());
		traceLableDiscount.setUpdateUserid(user.getId());
		
		ResultBean<TraceLableDiscount> resultBean = new ResultBean<TraceLableDiscount>();
		try{
			traceLableDiscountService.save(traceLableDiscount);
			resultBean = ResultUtil.success(traceLableDiscount);
		}catch(Exception e){
			e.printStackTrace();
			resultBean = ResultUtil.error(ResultEnum.SYSTEM_ERROR.getCode(),ResultEnum.SYSTEM_ERROR.getMessage());
		}
		return JsonMapper.nonDefaultMapper().toJson(resultBean);
	}
	
	
	/**
	  * 删除
	 * @return
	 */
	@RequestMapping(value = "delete",produces="application/json;charset=UTF-8", method = {RequestMethod.POST})
	@ResponseBody
	@ApiOperation(value = "属性删除", httpMethod = "POST", notes = "属性删除",	consumes="application/x-www-form-urlencoded")
	public String delete(HttpServletRequest request,@RequestParam String id){
		String token = request.getHeader("X-Token");
		if(StringUtils.isNotBlank(token)){
			traceLableDiscountService.delete(id);
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success());
		} else {
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
	}


}
