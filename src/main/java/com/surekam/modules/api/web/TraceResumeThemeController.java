package com.surekam.modules.api.web;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

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
import com.surekam.modules.sys.entity.Office;
import com.surekam.modules.sys.entity.User;
import com.surekam.modules.sys.service.OfficeService;
import com.surekam.modules.sys.utils.DictUtils;
import com.surekam.modules.sys.utils.UserUtils;
import com.surekam.modules.traceproduct.entity.TraceProduct;
import com.surekam.modules.traceproduct.service.TraceProductService;
import com.surekam.modules.traceresumetheme.entity.TraceResumeTheme;
import com.surekam.modules.traceresumetheme.service.TraceResumeThemeService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * 溯源主题管理接口
 * @author liw
 * @version 2018-08-29
 */

@Api(value="溯源主题接口Controller", description="溯源主题的相关数据接口")
@Controller
@RequestMapping(value = "api/traceResumeTheme")
public class TraceResumeThemeController extends BaseController {

	@Autowired
	private TraceResumeThemeService traceResumeThemeService;
	
	@Autowired
	private OfficeService officeService;
	
	@Autowired
	private CommonApiController commonApiController;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private ApiUserService apiUserService;
	
	@Autowired
	private TraceProductService traceProductService;
	
	@ModelAttribute
	public TraceResumeTheme get(@RequestParam(required=false) String id) {
		if (StringUtils.isNotBlank(id)){
			return traceResumeThemeService.get(id);
		}else{
			return new TraceResumeTheme();
		}
	}
	
	/**
	 * 根据企业ID获取通用主题和本公司开启状态的主题  不分页
	 * add by ligm 2018-08-29
	 * @param corpCode  企业编号
	 * @param pageno 第几页
	 * @param pagesize 每页条数  默认为系统配置的值
	 * @return
	 */
	@RequestMapping(value = "getCommonThemeListByOfficeId",produces="application/json;charset=UTF-8", method = {RequestMethod.GET})
	@ResponseBody
	@ApiOperation(value = "主题列表查询", httpMethod = "GET", 
	notes = "根据企业ID获取通用主题和本公司开启状态的主题 ，不分页",	consumes="application/x-www-form-urlencoded")
	public String getCommonThemeListByOfficeId(HttpServletRequest request,HttpServletResponse response,
			@RequestParam(required = true) String officeId) {
		response.setContentType("application/json; charset=UTF-8");
		List<TraceResumeTheme> themeList = traceResumeThemeService.findCommonAndOwnedThemes(officeId); 
		List<TraceResumeTheme> themeListResult = new ArrayList<TraceResumeTheme>();
		for(int i=0; i < themeList.size(); i++){
			TraceResumeTheme theme = themeList.get(i);
			//字典转义
			theme.setThemeTypeName(DictUtils.getDictLabel(theme.getThemeType(), "theme_type", ""));
			theme.setStatusName(DictUtils.getDictLabel(theme.getStatus(), "PRODUCT_IS_USED", ""));
			User user = UserUtils.getUserById(theme.getCreatUserid());
			theme.setCreatUserName(user.getName());
			
			Office office = officeService.get(theme.getOfficeId());
			theme.setCorpName(office.getName());
			
			String imgUrl = Global.getConfig("page_url");
			theme.setPageUrl(imgUrl+theme.getPageUrl());
			themeListResult.add(theme);
		}
		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(themeListResult));
  		//}
	}
	
	/**
	 * 根据企业编号获取对应的主题数据  分页
	 * add by ligm 2018-08-29
	 * @param corpCode  企业编号
	 * @param pageno 第几页
	 * @param pagesize 每页条数  默认为系统配置的值
	 * 测试地址：   http://localhost:8080/sureserve-admin/api/traceResumeTheme/themeList?corpCode=1&pageno=1&pagesize=20
	 * @return
	 */
	@RequestMapping(value = "themeList",produces="application/json;charset=UTF-8", method = {RequestMethod.GET})
	@ResponseBody
	@ApiOperation(value = "主题列表查询", httpMethod = "GET", 
	notes = "主题列表查询",	consumes="application/x-www-form-urlencoded")
	public String themeList(HttpServletRequest request,HttpServletResponse response,
			@RequestParam(required = false) String themeName,
			@RequestParam(required = false) String themeType,
			@RequestParam(required = false) String status,
			@RequestParam(required = false) String templet_product_id,
			@RequestParam(required = false) String corpCode,
			@RequestParam Integer pageno,
			@RequestParam Integer pagesize) {
		response.setContentType("application/json; charset=UTF-8");
		int pageNo = pageno==null?Global.DEFAULT_PAGENO:pageno;
		int pageSize = pagesize==null?Global.DEFAULT_PAGESIZE:pagesize;
		Page<TraceResumeTheme> page = new Page<TraceResumeTheme>(pageNo,pageSize); 
		String token = request.getHeader("X-Token");
		if(StringUtils.isBlank(token)) {
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
		if(token != null) {
			User user = apiUserService.getUserByToken(token);
			//超级管理员默认查询所有企业，其它默认查询本企业主题
			if(StringUtils.isEmpty(corpCode)) {
				if(!user.isAdmin()) {
					corpCode = user.getCompany().getId();
				}
			}		
		}
		page = traceResumeThemeService.find(page, themeName, themeType,status,templet_product_id,corpCode); 
		
		//返回没有信息提示
		//if(0 == page.getCount()){
		//	return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.DATA_COUNT_ZERO.getCode(), ResultEnum.DATA_COUNT_ZERO.getMessage()));
		//}else{
		List<TraceResumeTheme> list = new ArrayList<TraceResumeTheme>();
		for(int i=0;i<page.getList().size();i++){
			TraceResumeTheme theme = page.getList().get(i);
			//字典转义
			theme.setThemeTypeName(DictUtils.getDictLabel(theme.getThemeType(), "theme_type", ""));
			theme.setStatusName(DictUtils.getDictLabel(theme.getStatus(), "PRODUCT_IS_USED", ""));
			User user = UserUtils.getUserById(theme.getCreatUserid());
			theme.setCreatUserName(user.getName());
			
			Office office = officeService.get(theme.getOfficeId());
			theme.setCorpName(office.getName());
			//根据产品id查询产品名称
			if(StringUtils.isNotBlank(theme.getTemplet_product_id())) {
				TraceProduct product = traceProductService.get(theme.getTemplet_product_id());
				if(product != null) {
					theme.setProductName(product.getProductName());
				}
			}
			
			String imgUrl = Global.getConfig("sy_img_url");
			if(StringUtils.isNotBlank(theme.getThemeImg())){
				theme.setThemeImgUrl(imgUrl+theme.getThemeImg());	
			}else{
				theme.setThemeImgUrl("");
			}				
			list.add(theme);
		}
		page.setList(list);
		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(page));
		//}
	}

	/**
	 * 获取某个主题信息
	 * add by ligm 2018-08-21
	 * @param id
	 * 测试地址：   http://localhost:8080/sureserve-admin/api/traceResumeTheme/getThemeInformation?id=1
	 * @return
	 */
	@RequestMapping(value = "getThemeInformation",produces="application/json;charset=UTF-8", method = {RequestMethod.GET})
	@ResponseBody
	@ApiOperation(value = "主题详情", httpMethod = "GET", 
	notes = "主题详情",	consumes="application/x-www-form-urlencoded")
	public String getThemeInformation(HttpServletRequest request,
			@RequestParam String id) {
		if(StringUtils.isNotBlank(id)){
			TraceResumeTheme theme = traceResumeThemeService.get(id);
			String imgUrl = Global.getConfig("sy_img_url");
			if(StringUtils.isNotBlank(theme.getPageUrl())){
				theme.setPageUrl(imgUrl+theme.getPageUrl());
			}else{
				theme.setPageUrl("");
			}
			Office office = officeService.get(theme.getOfficeId());
			theme.setCorpName(office.getName());
			
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(theme));
		}else{
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.DATA_NOT_EXIST.getCode(), ResultEnum.DATA_NOT_EXIST.getMessage()));
		}
	}

	/**
	 * 主题新增
	 * add by liw 2018-08-29
	 * 测试地址：  http://localhost:8080/sureserve-admin/api/traceResumeTheme/addThemeSave 
	 * corpCode 企业号 
	 * themeCode 主题编号
	 * themeName 主题名称
	 * themeType 主题类型
	 * pageUrl   主题页面路径
	 * @return
	 */
	@RequestMapping(value = "addThemeSave",produces="text/plain;charset=UTF-8", method = {RequestMethod.POST})
	@ResponseBody
	@ApiOperation(value = "主题新增保存", httpMethod = "POST", 
	notes = "主题新增保存",	consumes="application/json")
	public String addThemeSave(HttpServletRequest request,
			@RequestBody @ApiParam(name="主题对象",value="传入json格式",required=true) TraceResumeTheme traceResumeTheme) {
		String token = request.getHeader("X-Token");
		User user = apiUserService.getUserByToken(token);
		traceResumeTheme.setOfficeId(user.getCompany().getId());
		String themeCode = commonApiController.getCode(TraceResumeTheme.SY_THEME_CODE);
		traceResumeTheme.setThemeCode(themeCode);
		traceResumeTheme.setStatus(TraceResumeTheme.OPEN);
		if(StringUtils.isBlank(token)){
			//token = "0021fa19-5f42-4d8f-921b-3dee9236c74a";
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(),ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
		traceResumeTheme.setCreatUserid(user.getId());
		TraceResumeTheme tempTheme = traceResumeThemeService.find(traceResumeTheme.getThemeName(), traceResumeTheme.getTemplet_product_id());
		if(tempTheme != null) {
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.THEME_NAME_EXIST.getCode(),ResultEnum.THEME_NAME_EXIST.getMessage()));
		}
		ResultBean<TraceResumeTheme> resultBean = new ResultBean<TraceResumeTheme>();
		try{
			traceResumeThemeService.save(traceResumeTheme);
			resultBean = ResultUtil.success();
		}catch(Exception e){
			e.printStackTrace();
			resultBean = ResultUtil.error(ResultEnum.SYSTEM_ERROR.getCode(),ResultEnum.SYSTEM_ERROR.getMessage());
		}
		return JsonMapper.nonDefaultMapper().toJson(resultBean);
	}
	
	/*
	 * 主题修改
	 * add by liw 2018-08-29
	 * 测试地址：  http://localhost:8080/sureserve-admin/api/traceResumeTheme/updateThemeSave   
	 * id  产品ID
	 * corpCode 企业号 
	 * themeCode 主题编号
	 * themeName 主题名称
	 * themeType 主题类型
	 * pageUrl   主题页面路径
	 * file 文件
	 * @return
	 */
	@RequestMapping(value = "updateThemeSave",produces="text/plain;charset=UTF-8", method = {RequestMethod.POST})
	@ResponseBody
	@ApiOperation(value = "主题修改保存", httpMethod = "POST", 
	notes = "主题修改保存",	consumes="application/json")
	public String updateThemeSave(HttpServletRequest request,
			@RequestBody @ApiParam(name="主题对象",value="传入json格式",required=true) TraceResumeTheme traceResumeTheme) {
		String token = request.getHeader("X-Token");
		TraceResumeTheme theme = traceResumeThemeService.get(traceResumeTheme.getId());
		if(traceResumeTheme.getThemeName() != null) {
			theme.setThemeName(traceResumeTheme.getThemeName());
		}
		if(traceResumeTheme.getThemeType() != null) {
			theme.setThemeType(traceResumeTheme.getThemeType());
		}
		if(traceResumeTheme.getTemplet_product_id() != null) {
			theme.setTemplet_product_id(traceResumeTheme.getTemplet_product_id());
		}
		if(traceResumeTheme.getThemeImg() != null) {
			theme.setThemeImg(traceResumeTheme.getThemeImg());
		}
		if(StringUtils.isBlank(token)){
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(),ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
		User user = apiUserService.getUserByToken(token);
		theme.setUpdateTime(new Date());
		theme.setUpdateUserid(user.getId());
		
		ResultBean<TraceResumeTheme> resultBean = new ResultBean<TraceResumeTheme>();
		try{
			traceResumeThemeService.save(theme);
			resultBean = ResultUtil.success();
		}catch(Exception e){
			e.printStackTrace();
			resultBean = ResultUtil.error(ResultEnum.SYSTEM_ERROR.getCode(),ResultEnum.SYSTEM_ERROR.getMessage());
		}
		return JsonMapper.nonDefaultMapper().toJson(resultBean);
	}
	
	/**
	 * 启用溯源主题
	 * add by liw 2018-08-30
	 * 测试地址：  http://localhost:8080/sureserve-admin/api/traceResumeTheme/themeEnable   
	 * id  主题id
	 * @return
	 */
	@RequestMapping(value = "themeEnable",produces="application/json;charset=UTF-8", method = {RequestMethod.POST})
	@ResponseBody
	@ApiOperation(value = "主题启用", httpMethod = "POST", 
	notes = "主题启用",	consumes="application/x-www-form-urlencoded")
	public String themeEnable(HttpServletRequest request,@RequestParam String id){
		traceResumeThemeService.enable(id);
		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success());
	}
	
	/**
	 * 停用溯源主题
	 * add by liw 2018-08-30
	 * 测试地址：  http://localhost:8080/sureserve-admin/api/traceResumeTheme/themeDisable  
	 * id  主题id
	 * @return
	 */
	@RequestMapping(value = "themeDisable",produces="application/json;charset=UTF-8", method = {RequestMethod.POST})
	@ResponseBody
	@ApiOperation(value = "主题停用", httpMethod = "POST", 
	notes = "主题停用",	consumes="application/x-www-form-urlencoded")
	public String themeDisable(HttpServletRequest request,@RequestParam String id){
		traceResumeThemeService.disable(id);
		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success());
	}
	
	/**
	 * 删除主题
	 * add by liw 2018-08-30
	 * 测试地址：  http://localhost:8080/sureserve-admin/api/traceResumeTheme/themeDelete   
	 * id  主题id
	 * @return
	 */
	@RequestMapping(value = "themeDelete",produces="application/json;charset=UTF-8", method = {RequestMethod.POST})
	@ResponseBody
	@ApiOperation(value = "主题删除", httpMethod = "POST", 
	notes = "主题删除",	consumes="application/x-www-form-urlencoded")
	public String themeDelete(HttpServletRequest request,@RequestParam String id){
		String resultFlag = traceResumeThemeService.delete(id);
		if(StringUtils.isNotBlank(resultFlag) && "1".equals(resultFlag)) {
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.PRODUCT_LABEL_PRINTED.getCode(),"使用的主题不能删除"));
		}
		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success());
	}
	
	/**
	 * 文件上传
	 * add by liw 2018-08-30
	 * 测试地址：  http://localhost:8080/sureserve-admin-dev/api/traceResumeTheme/themeFileUpload  
	 * file 文件
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value = "themeFileUpload",produces="text/plain;charset=UTF-8", method = {RequestMethod.POST})
	@ResponseBody
	@ApiOperation(value = "文件上传", httpMethod = "POST", 
	notes = "文件上传",	consumes="application/x-www-form-urlencoded")
	public String themeFileUpload(HttpServletRequest request,@RequestParam MultipartFile file) throws Exception {
		String path = "";
		if(file!=null && !file.getOriginalFilename().isEmpty()){
			//绝对路径
			String save_path = request.getSession().getServletContext().getRealPath("");
			//服务器IP 端口拼接 http://127.0.0.1:8080/
			path = save_path.substring(0,save_path.lastIndexOf(File.separator))+"/upload/theme_model/";
			File fiel = new File(path);
			if(!fiel.exists()){
				fiel.mkdirs();//如不存在路径则创建路径
			}			
		}
		
		List<Map<String,Object>> list =  new ArrayList<Map<String,Object>>();
		ResultBean<List<Map<String,Object>>> resultBean = new ResultBean<List<Map<String,Object>>>();
		try{
			Map<String,Object> map = new HashMap<String, Object>();
			String picUrl = traceResumeThemeService.saveAttach(file,path);
			if(StringUtils.isNotBlank(picUrl)){
				map.put("relative_url", picUrl);
				String imgUrl = Global.getConfig("sy_img_url");
				map.put("absolute_url",imgUrl+picUrl);
			}else{
				map.put("relative_url", "");
				map.put("absolute_url", "");
			}
			list.add(map);
			resultBean = ResultUtil.success(list);
		}catch(Exception e){
			e.printStackTrace();
			resultBean = ResultUtil.error(ResultEnum.SYSTEM_ERROR.getCode(),ResultEnum.SYSTEM_ERROR.getMessage());
		}
		return JsonMapper.nonDefaultMapper().toJson(resultBean);
	}
	
}
