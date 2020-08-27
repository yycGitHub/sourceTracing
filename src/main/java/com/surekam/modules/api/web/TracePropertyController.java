package com.surekam.modules.api.web;

import java.util.ArrayList;
import java.util.Date;
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
import com.surekam.common.web.BaseController;
import com.surekam.common.utils.ResultBean;
import com.surekam.common.utils.ResultEnum;
import com.surekam.common.utils.ResultUtil;
import com.surekam.common.utils.StringUtils;
import com.surekam.modules.api.service.ApiUserService;
import com.surekam.modules.sys.dao.UserDao;
import com.surekam.modules.sys.entity.CommonUser;
import com.surekam.modules.sys.entity.Office;
import com.surekam.modules.sys.entity.User;
import com.surekam.modules.sys.service.OfficeService;
import com.surekam.modules.sys.utils.DictUtils;
import com.surekam.modules.sys.utils.UserUtils;
import com.surekam.modules.tracemodel.entity.TraceModel;
import com.surekam.modules.tracemodel.service.TraceModelService;
import com.surekam.modules.traceproperty.entity.TraceProperty;
import com.surekam.modules.traceproperty.service.TracePropertyService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 溯源属性库管理Controller
 * @author liw
 * @version 2018-09-06
 */

@Api(value="溯源属性库接口Controller", description="溯源属性库的相关数据接口")
@Controller
@RequestMapping(value = "api/traceProperty")
public class TracePropertyController extends BaseController {
	
	@Autowired
	private ApiUserService apiUserService;

	@Autowired
	private TracePropertyService tracePropertyService;
	
	@Autowired
	private CommonApiController commonApiController;
	
	@Autowired
	private OfficeService officeService;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private TraceModelService traceModelService;
	
	@ModelAttribute
	public TraceProperty get(@RequestParam(required=false) String id) {
		if (StringUtils.isNotBlank(id)){
			return tracePropertyService.get(id);
		}else{
			return new TraceProperty();
		}
	}
	
	/**
	 * 根据企业编号获取对应的属性数据 分页
	 * add by liw 2018-9-6
	 * @param corpCode  企业编号
	 * @param pageno 第几页
	 * @param pagesize 每页条数  默认为系统配置的值
	 * 测试地址：   http://localhost:8080/sureserve-admin/api/traceProperty/list?corpCode=1&pageno=1&pagesize=20
	 * @return
	 */
	@RequestMapping(value = "list",produces="application/json;charset=UTF-8", method = {RequestMethod.GET})
	@ResponseBody
	@ApiOperation(value = "属性列表查询", httpMethod = "GET", 
	notes = "属性列表查询",	consumes="application/x-www-form-urlencoded")
	public String list(HttpServletRequest request,HttpServletResponse response,
			@RequestParam(required = false) String propertyNameEn,
			@RequestParam(required = false) String propertyNameZh,
			@RequestParam(required = false) String propertyCode,
			@RequestParam(required = false) String propertyType,
			@RequestParam(required = false) String status,
			@RequestParam(required = false) String corpCode,
			@RequestParam Integer pageno,
			@RequestParam Integer pagesize) {
		response.setContentType("application/json; charset=UTF-8");
		int pageNo = pageno==null?Global.DEFAULT_PAGENO:pageno;
		int pageSize = pagesize==null?Global.DEFAULT_PAGESIZE:pagesize;
		Page<TraceProperty> page = new Page<TraceProperty>(pageNo,pageSize); 
		String token = request.getHeader("X-Token");
		if(!StringUtils.isNotBlank(corpCode)) {
			if(token != null) {
				User user = apiUserService.getUserByToken(token);
				if(!user.isAdmin()) {
					corpCode = user.getCompany().getId();
				}
			}
		}
        page = tracePropertyService.find(page, propertyNameEn, propertyNameZh, propertyCode,propertyType, status, corpCode); 
        //返回没有信息提示
  		if(0 == page.getCount()){
  			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.DATA_COUNT_ZERO.getCode(), ResultEnum.DATA_COUNT_ZERO.getMessage()));
  		}else{
  			List<TraceProperty> list = new ArrayList<TraceProperty>();
  			for(int i=0;i<page.getList().size();i++){
  				TraceProperty property = page.getList().get(i);
  				//字典转义
  				property.setPropertyTypeName(DictUtils.getDictLabel(property.getPropertyType(), "property_type", ""));
  				property.setStatusName(DictUtils.getDictLabel(property.getStatus(), "PRODUCT_IS_USED", ""));
  				User user = UserUtils.getUserById(property.getCreatUserid());
  				property.setCreatUserName(user.getName());
  				
  				Office office = officeService.get(property.getOfficeId());
  				property.setCorpName(office.getName());
  				
  				TraceModel model = traceModelService.get(property.getModelId());
  				if(model != null) {
  					property.setModelName(model.getModelName());
  				}
  				list.add(property);
  			}
  			page.setList(list);
  			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(page));
  		}
	}

	/**
	 * 获取某个属性信息
	 * add by liw 2018-9-6
	 * @param id
	 * 测试地址：   http://localhost:8080/sureserve-admin/api/traceProperty/getInformation?id=1
	 * @return
	 */
	@RequestMapping(value = "getInformation",produces="application/json;charset=UTF-8", method = {RequestMethod.GET})
	@ResponseBody
	@ApiOperation(value = "属性详情", httpMethod = "GET", 
	notes = "属性详情",	consumes="application/x-www-form-urlencoded")
	public String getInformation(HttpServletRequest request,
			@RequestParam String id) {
		if(StringUtils.isNotBlank(id)){
			TraceProperty TraceProperty = tracePropertyService.get(id);
			
			Office office = officeService.get(TraceProperty.getOfficeId());
			TraceProperty.setCorpName(office.getName());
			
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(TraceProperty));
		}else{
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.DATA_NOT_EXIST.getCode(), ResultEnum.DATA_NOT_EXIST.getMessage()));
		}
	}

	/**
	 * 属性新增
	 * add by liw 2018-9-6
	 * 测试地址：  http://localhost:8080/sureserve-admin/api/traceProperty/addSave  
	 * officeId       企业ID  
	 * propertyNameEn 属性英文名
	 * propertyNameZh 属性中文名
	 * propertyType   属性类型
	 * sort           排序号
	 * @return
	 */
	@RequestMapping(value = "addSave",produces="text/plain;charset=UTF-8", method = {RequestMethod.POST})
	@ResponseBody
	@ApiOperation(value = "属性新增保存", httpMethod = "POST", 
	notes = "属性新增保存",	consumes="application/x-www-form-urlencoded")
	public String addSave(HttpServletRequest request,
			@RequestParam  String officeId,
			@RequestParam  String propertyNameEn,
			@RequestParam  String propertyNameZh,
			@RequestParam  String propertyType,
			@RequestParam  String modelId,
			@RequestParam  String sort) {
		String token = request.getHeader("X-Token");
		TraceProperty traceProperty = new TraceProperty();
		traceProperty.setOfficeId(officeId);
		String propertyCode = commonApiController.getCode(TraceProperty.SY_PROPERTY_CODE);
		traceProperty.setPropertyCode(propertyCode);
		traceProperty.setPropertyNameEn(propertyNameEn);
		traceProperty.setPropertyNameZh(propertyNameZh);
		traceProperty.setPropertyType(propertyType);
		traceProperty.setSort(Integer.parseInt(sort));
		traceProperty.setModelId(modelId);
		traceProperty.setStatus(TraceProperty.OPEN);
		if(StringUtils.isBlank(token)){
			token = "0021fa19-5f42-4d8f-921b-3dee9236c74a";
		}
		CommonUser user = userDao.findByToken(token);
		traceProperty.setCreatTime(new Date());
		traceProperty.setCreatUserid(user.getId());
		
		ResultBean<TraceProperty> resultBean = new ResultBean<TraceProperty>();
		try{
			tracePropertyService.save(traceProperty);
			resultBean = ResultUtil.success();
		}catch(Exception e){
			e.printStackTrace();
			resultBean = ResultUtil.error(ResultEnum.SYSTEM_ERROR.getCode(),ResultEnum.SYSTEM_ERROR.getMessage());
		}
		return JsonMapper.nonDefaultMapper().toJson(resultBean);
	}
	
	/**
	 * 属性修改
	 * add by liw 2018-9-6
	 * 测试地址：  http://localhost:8080/sureserve-admin/api/traceProperty/updateSave   
	 * id  属性ID
	 * officeId 企业ID 
	 * propertyNameEn 属性英文名
	 * propertyNameZh 属性中文名
	 * propertyType   属性类型
	 * sort           排序
	 * @return
	 */
	@RequestMapping(value = "updateSave",produces="application/json;charset=UTF-8", method = {RequestMethod.POST})
	@ResponseBody
	@ApiOperation(value = "属性修改保存", httpMethod = "POST", 
	notes = "属性修改保存",	consumes="application/x-www-form-urlencoded")
	public String updateSave(HttpServletRequest request,
			@RequestParam  String id,
			@RequestParam  String officeId,
			@RequestParam  String propertyNameEn,
			@RequestParam  String propertyNameZh,
			@RequestParam  String propertyType,
			@RequestParam  String modelId,
			@RequestParam  String sort) {
		String token = request.getParameter("token");
		TraceProperty property = tracePropertyService.get(id);
		property.setOfficeId(officeId);
		property.setPropertyNameEn(propertyNameEn);
		property.setPropertyNameZh(propertyNameZh);
		property.setPropertyType(propertyType);
		property.setSort(Integer.parseInt(sort));
		property.setModelId(modelId);
		if(StringUtils.isBlank(token)){
			token = "0021fa19-5f42-4d8f-921b-3dee9236c74a";
		}
		CommonUser user = userDao.findByToken(token);
		property.setUpdateTime(new Date());
		property.setUpdateUserid(user.getId());
		
		ResultBean<TraceProperty> resultBean = new ResultBean<TraceProperty>();
		try{
			tracePropertyService.save(property);
			resultBean = ResultUtil.success();
		}catch(Exception e){
			e.printStackTrace();
			resultBean = ResultUtil.error(ResultEnum.SYSTEM_ERROR.getCode(),ResultEnum.SYSTEM_ERROR.getMessage());
		}
		return JsonMapper.nonDefaultMapper().toJson(resultBean);
	}
	
	/**
	 * 启用溯源属性
	 * add by liw 2018-9-6
	 * 测试地址：  http://localhost:8080/sureserve-admin/api/traceProperty/enable   
	 * id  属性id
	 * @return
	 */
	@RequestMapping(value = "enable",produces="application/json;charset=UTF-8", method = {RequestMethod.POST})
	@ResponseBody
	@ApiOperation(value = "属性启用", httpMethod = "POST", 
	notes = "属性启用",	consumes="application/x-www-form-urlencoded")
	public String enable(HttpServletRequest request,@RequestParam String id){
		tracePropertyService.enable(id);
		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success());
	}
	
	/**
	 * 停用溯源属性
	 * add by liw 2018-9-6
	 * 测试地址：  http://localhost:8080/sureserve-admin/api/traceProperty/disable   
	 * id  属性id
	 * @return
	 */
	@RequestMapping(value = "disable",produces="application/json;charset=UTF-8", method = {RequestMethod.POST})
	@ResponseBody
	@ApiOperation(value = "属性停用", httpMethod = "POST", 
	notes = "属性停用",	consumes="application/x-www-form-urlencoded")
	public String disable(HttpServletRequest request,@RequestParam String id){
		tracePropertyService.disable(id);
		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success());
	}
	
	/**
	 * 删除
	 * add by liw 2018-9-6
	 * 测试地址：  http://localhost:8080/sureserve-admin/api/traceProperty/delete   
	 * id  属性id
	 * @return
	 */
	@RequestMapping(value = "delete",produces="application/json;charset=UTF-8", method = {RequestMethod.POST})
	@ResponseBody
	@ApiOperation(value = "属性删除", httpMethod = "POST", notes = "属性删除",	consumes="application/x-www-form-urlencoded")
	public String delete(HttpServletRequest request,@RequestParam String id){
		String resultFlag = tracePropertyService.delete(id);
		if(StringUtils.isNotBlank(resultFlag) && "1".equals(resultFlag)) {
			//System.out.println(JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.PRODUCT_LABEL_PRINTED.getCode(),"启用的属性不能被删除")));
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.PRODUCT_LABEL_PRINTED.getCode(),"启用的属性不能被删除"));
		}
		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success());
	}

}
