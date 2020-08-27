package com.surekam.modules.api.web;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.client.methods.HttpPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.surekam.common.config.Global;
import com.surekam.common.mapper.JsonMapper;
import com.surekam.common.persistence.Page;
import com.surekam.common.utils.DateUtils;
import com.surekam.common.utils.HttpClientUtil;
import com.surekam.common.utils.IdGen;
import com.surekam.common.utils.ResultBean;
import com.surekam.common.utils.ResultEnum;
import com.surekam.common.utils.ResultUtil;
import com.surekam.common.utils.StringUtils;
import com.surekam.common.web.BaseController;
import com.surekam.modules.api.entity.TreeNode;
import com.surekam.modules.api.service.ApiService;
import com.surekam.modules.api.service.ApiUserService;
import com.surekam.modules.productbatch.service.ProductBatchService;
import com.surekam.modules.sample.entity.YXImage;
import com.surekam.modules.sample.entity.YXLsxx;
import com.surekam.modules.sample.entity.YXZtb;
import com.surekam.modules.sample.service.YXImageService;
import com.surekam.modules.sample.service.YXLsxxService;
import com.surekam.modules.sample.service.YXZtbService;
import com.surekam.modules.sys.dao.UserDao;
import com.surekam.modules.sys.entity.CommonUser;
import com.surekam.modules.sys.entity.Dict;
import com.surekam.modules.sys.entity.Menu;
import com.surekam.modules.sys.entity.Office;
import com.surekam.modules.sys.entity.User;
import com.surekam.modules.sys.service.OfficeService;
import com.surekam.modules.sys.service.SystemService;
import com.surekam.modules.sys.utils.DictUtils;
import com.surekam.modules.sys.utils.HttpXmlClient;
import com.surekam.modules.sys.utils.base64ToImage;
import com.surekam.modules.trace.TraceDayInfo.entity.TraceInfoReport;
import com.surekam.modules.trace.TraceDayInfo.service.TraceDayInfoService;
import com.surekam.modules.trace.TraceInfo.service.TraceInfoService;
import com.surekam.modules.trace.TraceLableContent.entity.TraceLableContent;
import com.surekam.modules.trace.TraceLableContent.service.TraceLableContentService;
import com.surekam.modules.trace.entity.Content;
import com.surekam.modules.trace.entity.Element;
import com.surekam.modules.trace.tracePrintRecord.entity.TracePrintRecord;
import com.surekam.modules.trace.tracePrintRecord.service.TracePrintRecordService;
import com.surekam.modules.trace.tracelableelementnew.entity.TraceLableElementNew;
import com.surekam.modules.trace.tracelableelementnew.service.TraceLableElementNewService;
import com.surekam.modules.trace.tracelableprinter.entity.TraceLablePrinter;
import com.surekam.modules.trace.tracelableprinter.service.TraceLablePrinterService;
import com.surekam.modules.trace.tracelableselectprinter.entity.TraceLableSelectPrinter;
import com.surekam.modules.trace.tracelableselectprinter.service.TraceLableSelectPrinterService;
import com.surekam.modules.trace.tracelablespecification.entity.TraceLableSpecification;
import com.surekam.modules.trace.tracelablespecification.service.TraceLableSpecificationService;
import com.surekam.modules.tracecode.entity.TraceCode;
import com.surekam.modules.tracecode.entity.TraceCodeVo;
import com.surekam.modules.tracecode.service.TraceCodeService;
import com.surekam.modules.tracemodel.service.TraceModelService;
import com.surekam.modules.traceproduct.entity.TraceProduct;
import com.surekam.modules.traceproduct.entity.TraceProductP;
import com.surekam.modules.traceproduct.service.TraceProductService;
import com.surekam.modules.traceresumetheme.service.TraceResumeThemeService;
import com.surekam.modules.tracewxtoken.entity.TraceWxToken;
import com.surekam.modules.tracewxtoken.service.TraceWxTokenService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Api(value="公共接口Controller", description="公共接口（无需token）")
@Controller
@RequestMapping(value = "api/common")

public class CommonApiController extends BaseController {
	
	@Autowired
	private ApiService apiService;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private TraceProductService traceProductService;
	
	@Autowired
	private ProductBatchService productBatchService;
	
	@Autowired
	private TraceResumeThemeService traceResumeThemeService;
	
	@Autowired
	private TraceModelService traceModelService;
	
	@Autowired
	private SystemService systemService;
	
	@Autowired
	private OfficeService officeService;
	
	@Autowired
	private TraceDayInfoService traceDayInfoService;
	
	@Autowired
	private ApiUserService apiUserService;
	
	@Autowired
	private TraceCodeService traceCodeService;
	
	@Autowired
	private TraceLableContentService traceLableContentService;
	
	@Autowired
	private TracePrintRecordService tracePrintRecordService;
	
	@Autowired
	private YXImageService yXImageService;
	
	@Autowired
	private YXLsxxService yXLsxxService;
	
	@Autowired
	private YXZtbService yXZtbService;
	
	@Autowired
	private TraceWxTokenService traceWxTokenService;
	
	private Timer timer;
	
	@Autowired
	private TraceInfoService traceInfoService;
	
	@Autowired
	private TraceLableSpecificationService traceLableSpecificationService;
	
	@Autowired
	private TraceLablePrinterService traceLablePrinterService;
	
	@Autowired
	private TraceLableSelectPrinterService traceLableSelectPrinterService;
	
	@Autowired
	private TraceLableElementNewService traceLableElementNewService;
	
	/**
	 * 根据token获取用户信息
	 * add by liw 2018-08-21
	 * 测试地址：  http://localhost:8080/sureserve-admin/api/common/getUserByToken   
	 * token
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/getUserByToken",method=RequestMethod.GET)
	@ApiOperation(value = "根据token获取用户信息", httpMethod = "GET", notes = "根据token获取用户信息",
				consumes="application/x-www-form-urlencoded")
	public String getUserByToken(@RequestParam String token) {
		JsonMapper jsonMapper = JsonMapper.getInstance();
		ResultBean<CommonUser> resultBean = new ResultBean<CommonUser>();
		try {
			if(StringUtils.isNotBlank(token)){
				CommonUser user = userDao.findByToken(token);
				//List<Menu> menuList =  menuDao.findByUserId(user.getId());
				//user.setMenuList(menuList);
				resultBean = ResultUtil.success(user);
			}else{
				resultBean = ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(),ResultEnum.TOKEN_IS_NULL.getMessage());
			}
			return jsonMapper.toJson(resultBean);
		} catch (Exception e) {
			resultBean = ResultUtil.error(ResultEnum.SYSTEM_ERROR.getCode(),ResultEnum.SYSTEM_ERROR.getMessage());
			return jsonMapper.toJson(resultBean);
		}
	}
	
	/**
	 * 获取菜单
	 * add by liw 2018-08-28
	 * 测试地址：   http://localhost:8080/sureserve-admin/api/common/getMenuData
	 * @return
	 */
	@RequestMapping(value = "getMenuData",produces="application/json;charset=UTF-8", method = {RequestMethod.GET})
	@ResponseBody
	@ApiOperation(value = "获取菜单", httpMethod = "GET", notes = "获取菜单",	consumes="application/x-www-form-urlencoded")
	public String getMenuData(HttpServletRequest request) {
		String token = request.getHeader("X-Token");
		if(StringUtils.isBlank(token)){
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(),ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
		CommonUser user = userDao.findByToken(token);
		List<TreeNode> list = treeMenuList(user.getMenuList(),"74bf19e8aa8246469af10a95b95d130f");
        //返回没有信息提示
  		//if(0 == list.size()){
  		//	return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.DATA_COUNT_ZERO.getCode(), ResultEnum.DATA_COUNT_ZERO.getMessage()));
  		//}else{
  			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(list));
  		//}
	}
	
	public List<TreeNode> treeMenuList(List<Menu> menuList ,String pid) {
		List<TreeNode> list = new ArrayList<TreeNode>(); 
		for(Menu menu: menuList){
            //遍历出父id等于参数的id，add进子节点集合
			String id = menu.getParent()==null?"":menu.getParent().getId();
            if(id.equals(pid)){
            	TreeNode treeNode = new TreeNode();
            	treeNode.setId(menu.getId());
            	treeNode.setPid(pid);
            	treeNode.setName(menu.getName());
            	List<TreeNode> childNode = treeMenuList(menuList,menu.getId());
            	if(childNode.size()>0){
            		treeNode.setChildNode(treeMenuList(menuList,menu.getId()));
            	}
                //递归遍历下一级
                list.add(treeNode);
            }
        }
		return list;
	}
	
	//获取产品编码
	public String getCode(String type) {
		String code = "";
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		String day = df.format(new Date());
		String maxCode = "";
		if(TraceProduct.SY_PRODUCT_CODE.equals(type)){
			maxCode = traceProductService.getMaxProductCode(type);
		}else if(TraceProduct.SY_THEME_CODE.equals(type)){
			maxCode = traceResumeThemeService.getMaxThemeCode(type);
		}else if(TraceProduct.SY_MODEL_CODE.equals(type)) {
			maxCode = traceModelService.getMaxModelCode(type);
		}
		if(StringUtils.isNotBlank(maxCode)){//CP20180828
			if(maxCode.substring(0, 10).equals(type+day)){
				String ws = maxCode.substring(10,14);
				code = type + day + String.format("%0" + 4 + "d", Integer.parseInt(ws) + 1);
			}else{
				code = type + day + "0001";
			}
		}else{
			code = type + day + "0001";
		}
		
		return code;
	}
	
	/**
	 * 根据数据字典类型获取数据字典列表
	 * add by ligm 2018-09-19
	 * 测试地址：   http://localhost:8080/sureserve-admin/api/common/getDictList
	 * @return
	 */
	@RequestMapping(value = "getDictList",produces="application/json;charset=UTF-8", method = {RequestMethod.GET})
	@ResponseBody
	@ApiOperation(value = "获取数据字典列表", httpMethod = "GET", 
	notes = "根据数据字典类型获取数据字典列表",	consumes="application/x-www-form-urlencoded")
	public String getDictList(HttpServletRequest request, 
			@RequestParam(required = true)  String type) {
		List<Dict> list = DictUtils.getDictList(type);
		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(list));
	}
	
	/**
	 * 根据数据字典类型及值获取标签名称
	 * add by ligm 2018-09-19
	 * @return
	 */
	@RequestMapping(value = "getDictLabel",produces="application/json;charset=UTF-8", method = {RequestMethod.GET})
	@ResponseBody
	@ApiOperation(value = "获取数据字典标签名", httpMethod = "GET", 
	notes = "根据数据字典类型及值获取标签名称",	consumes="application/x-www-form-urlencoded")
	public String getDictLabel(HttpServletRequest request, 
			@RequestParam(required = true) String value, 
			@RequestParam(required = true) String type, 
			String defaultValue) {
		String labelString = DictUtils.getDictLabel(value, type, defaultValue);
		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(labelString));
	}
	
	/**
	 * 根据数据字典类型及标签名获取对应的值
	 * add by ligm 2018-09-19
	 * @return
	 */
	@RequestMapping(value = "getDictValue",produces="application/json;charset=UTF-8", method = {RequestMethod.GET})
	@ResponseBody
	@ApiOperation(value = "获取数据字典标签对应值", httpMethod = "GET", 
	notes = "根据数据字典类型及标签名获取对应的值",	consumes="application/x-www-form-urlencoded")
	public String getDictValue(HttpServletRequest request, 
			@RequestParam(required = true) String label, 
			@RequestParam(required = true) String type, 
			String defaultLabel) {
		String labelString = DictUtils.getDictValue(label, type, defaultLabel);
		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(labelString));
	}
	
	/**
	 * 获取用户企业列表
	 * add by xiaowangzi 2018-09-29
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "getCompanyData",produces="application/json;charset=UTF-8", method = {RequestMethod.GET})
	@ResponseBody
	@ApiOperation(value = "获取企业列表", httpMethod = "GET", 
	notes = "获取企业列表",	consumes="application/x-www-form-urlencoded")
	public String getCompanyData(HttpServletRequest request) {
		String token = request.getHeader("X-Token");
		if(StringUtils.isBlank(token)){
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(),ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
		CommonUser commonUser = systemService.findByToken(token);
		User user = systemService.getUserByLoginName(commonUser.getLoginName());
		List<Office> list = officeService.findOfficesByUser(user);
		List<Map<String, Object>> mapList = Lists.newArrayList();
		for (int i=0; i<list.size(); i++){
			Office e = list.get(i);		
			Map<String, Object> map = Maps.newHashMap();
			map.put("id", e.getId());
			map.put("name", e.getName());
			map.put("image", e.getOfficeLogo());
			mapList.add(map);
		}
        //返回没有信息提示
  		if(0 == mapList.size()){
  			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.DATA_NOT_EXIST.getCode(), ResultEnum.DATA_NOT_EXIST.getMessage()));
  		}else{
  			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(mapList));
  		}
	}
	
	/**
	 * 上传base64图片
	 * @author wangyuewen
	 * @param request
	 * @param imgStr
	 * @param extName
	 * @return
	 */
	@RequestMapping(value = "fileUpload",produces="text/plain;charset=UTF-8", method = {RequestMethod.POST})
	@ResponseBody
	@ApiOperation(value = "base64图片上传", httpMethod = "POST", 
	notes = "base64图片上传",	consumes="application/json")
	public String fileUpload(HttpServletRequest request,@RequestBody @ApiParam JSONObject paramJson) {
		String imgStr = "";
		String extName = "";
		if(paramJson.containsKey("imgStr")) {
			imgStr = paramJson.getString("imgStr");
		}
		if(paramJson.containsKey("extName")) {
			extName = paramJson.getString("extName");
		}
		String save_path = request.getSession().getServletContext().getRealPath("");
		String relative_path = "/upload/trace/"+DateUtils.getDate("yyyy-MM") + "/";
		String path = save_path.substring(0,save_path.lastIndexOf(File.separator)) + relative_path;
		File file = new File(path);
		if(!file.exists()){
			file.mkdirs();//如不存在路径则创建路径
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		String dateStr = sdf.format(new Date());
		path = path + dateStr + "." + extName;
		relative_path = relative_path + dateStr + "." + extName;
		Boolean flag = base64ToImage.generateImage(imgStr, path);
		if(!flag) {
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.OPERATION_FAILED.getCode(), ResultEnum.OPERATION_FAILED.getMessage()));
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("relative_path", relative_path);
		map.put("absolute_url", Global.getConfig("sy_img_url") + relative_path);
		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(map));
	}
	
	/**
	 * 获取扫码统计信息
	 * @author wangyuewen
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "getTraceInfoReport",produces="application/json;charset=UTF-8", method = {RequestMethod.GET})
	@ResponseBody
	@ApiOperation(value = "获取扫码信息报表", httpMethod = "GET", 
	notes = "获取扫码信息报表",	consumes="application/x-www-form-urlencoded")
	public String getTraceInfoReport(HttpServletRequest request) {
		String token = request.getHeader("X-Token");
		String officeId = "";
		if(token != null) {
			User user = apiUserService.getUserByToken(token);
			if(user != null && user.getCompany() != null) {
				officeId = user.getCompany().getId();
			}
		}
		TraceInfoReport traceInfoReport = new TraceInfoReport();
		//本周
		traceInfoReport.setWeekTraceCount(traceDayInfoService.findTraceCount(DateUtils.getBeginDayOfWeek(), "", officeId));
	    //本月
		traceInfoReport.setMonthTraceCount(traceDayInfoService.findTraceCount(DateUtils.getBeginDayOfMonth(), "", officeId));
	    //总扫码次数
		traceInfoReport.setTraceCount(traceDayInfoService.findTraceCount("", "", officeId));
		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(traceInfoReport));
	}
	
	/**
	 * 文件上传
	 * add by liw 2018-08-21
	 * 测试地址：  http://localhost:8080/sureserve-admin/api/common/upload   
	 * file 文件
	 * @return
	 */
	@RequestMapping(value = "upload",produces="text/plain;charset=UTF-8", method = {RequestMethod.POST})
	@ResponseBody
	@ApiOperation(value = "文件上传", httpMethod = "POST", 
	notes = "文件上传",	consumes="application/x-www-form-urlencoded")
	public String upload(HttpServletRequest request,@RequestParam MultipartFile file) {
		String path = "";
		if(file!=null && !file.getOriginalFilename().isEmpty()){
			//绝对路径
			String save_path = request.getSession().getServletContext().getRealPath("");
			//服务器IP 端口拼接 http://127.0.0.1:8080/
			path = save_path.substring(0,save_path.lastIndexOf(File.separator))+"/upload/product_model/";
			File fiel = new File(path);
			if(!fiel.exists()){
				fiel.mkdirs();//如不存在路径则创建路径
			}
		}
		List<Map<String,Object>> list =  new ArrayList<Map<String,Object>>();
		ResultBean<List<Map<String,Object>>> resultBean = new ResultBean<List<Map<String,Object>>>();
		try{
			Map<String,Object> map = new HashMap<String, Object>();
			String picUrl = traceProductService.saveAttach(file,path);
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
	
	/***************************************************************打印**********************************************************************/
	/**
	 * @description 跳转到打印页面；
	 * @return
	 */
	@RequestMapping(value = "/getPrintPage", produces="application/json;charset=UTF-8", method = {RequestMethod.GET})
	@ApiOperation(value = "跳转到打印页面", httpMethod = "GET", notes = "跳转到打印页面",	consumes="application/x-www-form-urlencoded")
	public String getPrintPage(@RequestParam String applyId, @RequestParam String token, @RequestParam String serialNumberStart, @RequestParam String serialNumberEnd, @RequestParam String labelId, @RequestParam String printId, @RequestParam String jzId, @RequestParam String xoffset, @RequestParam String yoffset, Model model, HttpServletRequest request){
		model.addAttribute("applyId", applyId);
		model.addAttribute("token", token);
		model.addAttribute("serialNumberStart", serialNumberStart);
		model.addAttribute("serialNumberEnd", serialNumberEnd);
		model.addAttribute("type", "1");
		model.addAttribute("labelId", labelId);
		model.addAttribute("printId", printId);
		model.addAttribute("jzId", jzId);
		model.addAttribute("xoffset", xoffset);
		model.addAttribute("yoffset", yoffset);
		return "modules/" + "common/temUploadForm_new";
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
	@RequestMapping(value = "/codelist",produces="application/json;charset=UTF-8", method = {RequestMethod.GET})
	@ResponseBody
	@ApiOperation(value = "查询溯源码列表", httpMethod = "GET", 
	notes = "查询溯源码列表",	consumes="application/x-www-form-urlencoded")
	public String codelist(HttpServletRequest request,HttpServletResponse response,
			@RequestParam(required = false) String applyId,
			@RequestParam(required = false) String token,
			@RequestParam(required = false) Integer serialNumberStart,
			@RequestParam(required = false) Integer serialNumberEnd,
			@RequestParam Integer pageno,
			@RequestParam Integer pagesize) {
		response.setContentType("application/json; charset=UTF-8");
		int pageNo = pageno==null?Global.DEFAULT_PAGENO:pageno;
		int pageSize = pagesize==null?Global.DEFAULT_PAGESIZE:pagesize;
		Page<TraceCode> page = new Page<TraceCode>(pageNo,pageSize); 
		String corpCode = "";
		User user = apiUserService.getUserByToken(token);
		//超级管理默认查询所有企业，其他默认查询本企业批次
		if(!user.isAdmin()) {
			corpCode = user.getCompany().getId();
		}
		page = traceCodeService.find(page, "", "", "", corpCode, applyId, serialNumberStart, serialNumberEnd);
		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(page));
	}
	
	/**
	 * 查询溯源码列表（新）
	 * @param request
	 * @param response
	 * @param productName
	 * @param batchCode
	 * @return
	 */
	@RequestMapping(value = "/codelist_new",produces="application/json;charset=UTF-8", method = {RequestMethod.GET})
	@ResponseBody
	@ApiOperation(value = "查询溯源码列表（新）", httpMethod = "GET", 
	notes = "查询溯源码列表（新）",	consumes="application/x-www-form-urlencoded")
	public String codelist_new(HttpServletRequest request,HttpServletResponse response,
			@RequestParam String applyId,
			@RequestParam Integer serialNumberStart,
			@RequestParam Integer serialNumberEnd) {
		response.setContentType("application/json; charset=UTF-8");
		List<TraceCodeVo> traceCodeList = traceCodeService.findTraceCodeList(applyId, serialNumberStart, serialNumberEnd);
		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(traceCodeList));
	}
	
	@RequestMapping(value = "traceLableContentList",produces="application/json;charset=UTF-8", method = {RequestMethod.GET})
	@ResponseBody
	@ApiOperation(value = "标签内容查询", httpMethod = "GET", notes = "标签内容查询",	consumes="application/x-www-form-urlencoded")
	public String TraceLableContentList(HttpServletRequest request,HttpServletResponse response,@RequestParam(required = false) String id) {
		response.setContentType("application/json; charset=UTF-8");
		List<TraceLableContent> list = new ArrayList<TraceLableContent>(8);
		list = traceLableContentService.find(id);
  		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(list));
	}
	
	/**
	 * 封装打印数据
	 * @param request
	 * @param response
	 * @param productName
	 * @param batchCode
	 * @return
	 */
	@RequestMapping(value = "/printData",produces="application/json;charset=UTF-8", method = {RequestMethod.GET})
	@ResponseBody
	@ApiOperation(value = "查询溯源码列表", httpMethod = "GET", 
	notes = "查询溯源码列表",	consumes="application/x-www-form-urlencoded")
	public String printData(HttpServletRequest request,HttpServletResponse response,
			@RequestParam String token,
			@RequestParam String applyId,
			@RequestParam String labelId,
			@RequestParam String printId,
			@RequestParam String jzId,
			@RequestParam String xoffset,
			@RequestParam String yoffset,
			@RequestParam Integer serialNumberStart,
			@RequestParam Integer serialNumberEnd) {
		Map<String,Object> data = new HashMap<String,Object>();
		User user = apiUserService.getUserByToken(token);
		TraceLableSelectPrinter traceLableSelectPrinter = traceLableSelectPrinterService.findTraceLableSelectPrinter(user.getOffice().getKuid(), printId);
		if(traceLableSelectPrinter == null){
			traceLableSelectPrinter = new TraceLableSelectPrinter();
			traceLableSelectPrinter.setPrinterId(printId);
			traceLableSelectPrinter.setOfficeId(user.getOffice().getKuid());
			traceLableSelectPrinter.setXoffset(xoffset);
			traceLableSelectPrinter.setYoffset(yoffset);
			traceLableSelectPrinter.setCreatUserid(user.getId());
			traceLableSelectPrinterService.save(traceLableSelectPrinter);
		}else{
			traceLableSelectPrinter.setXoffset(xoffset);
			traceLableSelectPrinter.setYoffset(yoffset);
			traceLableSelectPrinter.setUpdateTime(new Date());
			traceLableSelectPrinter.setUpdateUserid(user.getId());
			traceLableSelectPrinterService.save(traceLableSelectPrinter);
		}
		List<TraceCodeVo> traceCodeList = traceCodeService.findTraceCodeList(applyId, serialNumberStart, serialNumberEnd);
		List<Content> contentList = new ArrayList<Content>();
		
		List<TraceLableContent> lableContentList = traceLableContentService.find(applyId);
		TraceLablePrinter traceLablePrinter = traceLablePrinterService.get(printId);
		traceLablePrinter.setXoffset(new BigDecimal(xoffset));
		traceLablePrinter.setYoffset(new BigDecimal(yoffset));
		data.put("printer", traceLablePrinter);
		TraceLableSpecification traceLableSpecification = traceLableSpecificationService.get(jzId);
		data.put("labelSpecification", traceLableSpecification);
		List<TraceLableElementNew> traceLableElementNewList = traceLableElementNewService.findByLabelId(labelId);
		data.put("labelElementList", traceLableElementNewList);
		
		for (TraceCodeVo traceCodeVo : traceCodeList) {
			String code = traceCodeVo.getTraceCode();
			Content content = new Content();
			content.setQrcode(code);
			content.setIsPrint(false);
			List<Element> elist = new ArrayList<Element>();
			String imgUrl = Global.getConfig("sy_img_url");
			String url = imgUrl + "/v.html?traceCode=" + code;
			for(int i=0;i<traceLableElementNewList.size();i++){
				Element element = new Element();
				TraceLableElementNew traceLableElementNew = traceLableElementNewList.get(i);
				String elementId = traceLableElementNew.getElementId();
				if(elementId.equals("1")){
					element.setElementId("1");
					element.setElementData(url);
					elist.add(element);
				}else if(elementId.equals("20")){
					element.setElementId("20");
					element.setElementData("身份编码:" + code);
					elist.add(element);
				}else{
					for(int j=0;j<lableContentList.size();j++){
						TraceLableContent traceLableContent = lableContentList.get(j);
						String eId = traceLableContent.getElementId();
						if(eId.equals(elementId)){
							element.setElementId(eId);
							element.setElementData(traceLableContent.getApplyElementContent());
							elist.add(element);
							break;
						}
					}
				}
			}
			content.setDataList(elist);
			contentList.add(content);
		}
		data.put("labelContentList", contentList);
		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(data));
	}
	
	/**
	 * @author liwei
	 * 保存标签打印记录
	 * @param request
	 * @param traceProduct
	 * @return
	 */
	@RequestMapping(value = "saveTracePrintRecord",produces="text/plain;charset=UTF-8", method = {RequestMethod.POST})
	@ResponseBody
	@ApiOperation(value = "保存标签打印记录", httpMethod = "POST", notes = "保存标签打印记录",	consumes="application/json")
	public String saveLableApply(HttpServletRequest request,
			@RequestBody @ApiParam(name="标签打印记录对象",value="传入json格式",required=true) TracePrintRecord tracePrintRecord) {
		String token = request.getHeader("X-Token");
		if(StringUtils.isNotBlank(token)){
			User user = apiUserService.getUserByToken(token);
			tracePrintRecord.setCreatUserid(user.getId());
			try {
				tracePrintRecordService.save(tracePrintRecord);
			} catch (Exception e) {
				return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.SYSTEM_ERROR.getCode(), ResultEnum.SYSTEM_ERROR.getMessage()));
			}
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(tracePrintRecord));
		} else {
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
	}
	
	//--------------------------------------------------微信配置---------------------------------------------------------------------//
	/**
	 * @description 获取微信配置,用于获取前端定位信息
	 * @return
	 */
	@ResponseBody 
	@RequestMapping(value="getLocation")
	@ApiOperation(value = "获取微信配置,用于获取前端定位信", httpMethod = "GET", notes = "获取微信配置,用于获取前端定位信",	consumes="application/x-www-form-urlencoded")
	public String getLocation(@RequestParam String url,@RequestParam String code,@RequestParam String state, HttpServletRequest request){ 
		if(StringUtils.isNotBlank(code)){
			if(code.contains("code=")){
				url+="&"+code;
			}else{
				url+="&code="+code;
			}
		}
		if(StringUtils.isNotBlank(state)){
			if(state.contains("state=")){
				url+="&"+state;
			}else{
				url+="&state="+state;
			}
		}
		System.out.println("url============================================="+url);
		JsonMapper jsonMapper = JsonMapper.getInstance();
		ResultBean<Object> resultBean = new ResultBean<Object>();
		String appID = Global.getConfig("appID");
		String appSecret = Global.getConfig("appSecret");
		System.out.println(appID+"                           "+appSecret);
		HttpClientUtil httpClientUtil = HttpClientUtil.getInstance();
		Map<String, String> params = new HashMap<String, String>();
        params.put("appid",appID);
        params.put("secret",appSecret);
        params.put("grant_type","client_credential");
        String noncestr = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 32);
        String timestamp = Long.toString(System.currentTimeMillis() / 1000);
        String access_token = "";
        String jsapi_ticket = "";
        String signature = "";
		TraceWxToken traceWxToken = traceWxTokenService.findTraceWxToken("");
		if(StringUtils.isNotBlank(traceWxToken.getAccessToken())){
			Date createTime = traceWxToken.getCreateTime();
			if(differentDaysByMillisecond(createTime,new Date())<1.6){
				//String accessToken = traceWxToken.getAccessToken();
				jsapi_ticket = traceWxToken.getJsapiTicket();
				noncestr = traceWxToken.getNoncestr();
				timestamp = traceWxToken.getTimestamp();
				
				String str = "jsapi_ticket=" + jsapi_ticket +
			                "&noncestr=" + noncestr +
			                "&timestamp=" + timestamp +
			                "&url=" + url;
		        System.out.println("signature========================"+str);
		        //sha1加密
		        signature = HttpXmlClient.SHA1(str);
			}else{
				HttpPost httpPost = HttpXmlClient.postForm("https://api.weixin.qq.com/cgi-bin/token",params);
				String at = httpClientUtil.sendHttpPost(httpPost);
				JSONObject at_obj = JSONObject.fromObject(at); 
				System.out.println("access_token ====== "+at_obj);
				if(StringUtils.isNotBlank(at_obj.getString("access_token"))){
					access_token = at_obj.getString("access_token");
					System.out.println("access_token ====== "+access_token);
					 //获取ticket
					params = new HashMap<String, String>();
					params.put("access_token",access_token);
					params.put("type","jsapi");
					httpPost = HttpXmlClient.postForm("https://api.weixin.qq.com/cgi-bin/ticket/getticket",params);
					String jt = httpClientUtil.sendHttpPost(httpPost);
					System.out.println(jt);
					JSONObject jt_obj = JSONObject.fromObject(jt); 
					if(StringUtils.isNotBlank(jt_obj.getString("ticket"))){
						jsapi_ticket = jt_obj.getString("ticket");
						System.out.println("jsapi_ticket ====== "+jsapi_ticket);
						 //获取签名signature
				        String str = "jsapi_ticket=" + jsapi_ticket +
				                "&noncestr=" + noncestr +
				                "&timestamp=" + timestamp +
				                "&url=" + url;
				        System.out.println("signature========================"+str);
				        //sha1加密
				        signature = HttpXmlClient.SHA1(str);
				        
				        traceWxToken.setAccessToken(access_token);
				        traceWxToken.setCreateTime(new Date());
				        traceWxToken.setJsapiTicket(jsapi_ticket);
				        traceWxToken.setTimestamp(timestamp);
				        traceWxToken.setNoncestr(noncestr);
				        traceWxToken.setSignature(signature);
				        traceWxTokenService.save(traceWxToken);
					}else{
						resultBean = ResultUtil.error(ResultEnum.WX_GET_NULL.getCode(),ResultEnum.WX_GET_NULL.getMessage());
						return jsonMapper.toJson(resultBean);
					}
				}else{
					resultBean = ResultUtil.error(ResultEnum.WX_GET_NULL.getCode(),ResultEnum.WX_GET_NULL.getMessage());
					return jsonMapper.toJson(resultBean);
				}
			}
		}else{
			HttpPost httpPost = HttpXmlClient.postForm("https://api.weixin.qq.com/cgi-bin/token",params);
			String at = httpClientUtil.sendHttpPost(httpPost);
			System.out.println(at);
			JSONObject at_obj = JSONObject.fromObject(at); 
			if(StringUtils.isNotBlank(at_obj.getString("access_token"))){
				access_token = at_obj.getString("access_token");
				System.out.println("access_token ====== "+access_token);
				 //获取ticket
				params = new HashMap<String, String>();
				params.put("access_token",access_token);
				params.put("type","jsapi");
				httpPost = HttpXmlClient.postForm("https://api.weixin.qq.com/cgi-bin/ticket/getticket",params);
				String jt = httpClientUtil.sendHttpPost(httpPost);
				System.out.println(jt);
				JSONObject jt_obj = JSONObject.fromObject(jt); 
				if(StringUtils.isNotBlank(jt_obj.getString("ticket"))){
					jsapi_ticket = jt_obj.getString("ticket");
					System.out.println("jsapi_ticket ====== "+jsapi_ticket);
					 //获取签名signature
			        String str = "jsapi_ticket=" + jsapi_ticket +
			                "&noncestr=" + noncestr +
			                "&timestamp=" + timestamp +
			                "&url=" + url;
			        System.out.println("url========================"+str);
			        //sha1加密
			        signature = HttpXmlClient.SHA1(str);
			        
			        TraceWxToken wxToken = new TraceWxToken();
			        wxToken.setAccessToken(access_token);
			        wxToken.setCreateTime(new Date());
			        wxToken.setJsapiTicket(jsapi_ticket);
			        wxToken.setTimestamp(timestamp);
			        wxToken.setNoncestr(noncestr);
			        wxToken.setSignature(signature);
			        traceWxTokenService.save(wxToken);
				}else{
					resultBean = ResultUtil.error(ResultEnum.WX_GET_NULL.getCode(),ResultEnum.WX_GET_NULL.getMessage());
					return jsonMapper.toJson(resultBean);
				}
			}else{
				resultBean = ResultUtil.error(ResultEnum.WX_GET_NULL.getCode(),ResultEnum.WX_GET_NULL.getMessage());
				return jsonMapper.toJson(resultBean);
			}
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("appID", appID);
		map.put("noncestr", noncestr);
		map.put("timestamp", timestamp);
		map.put("signature", signature);
		System.out.println("signature ====== "+signature);
		String message = "上传成功";
		resultBean.setCode(0);
		resultBean.setMessage(message);
		resultBean.setBodyData(map);
		return jsonMapper.toJson(resultBean);
	}
	
	public float differentDaysByMillisecond(Date date1,Date date2){
		float days = (float) ((date2.getTime() - date1.getTime()) / (1000*3600.0));        
        return days;
	}
	
	/**
	 * 溯源精品查询
	 * @author wangyuewen
	 */
	@RequestMapping(value = "getDemoCompetitiveProduct",produces="application/json;charset=UTF-8", method = {RequestMethod.GET})
	@ResponseBody
	@ApiOperation(value = "溯源精品查询(手机端)", httpMethod = "GET", 
	notes = "溯源精品查询(手机端)",	consumes="application/x-www-form-urlencoded")
	public String getDemoCompetitiveProduct() {
		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(traceProductService.getCompetitiveProduct()));
	}
	
	/**
	 * 产品列表查询(手机端)
	 * @param request
	 * @param response
	 * @param productName 产品名称
	 * @param pageno
	 * @param pagesize
	 * @author wangyuewen
	 * @return
	 */
	@RequestMapping(value = "listDemoP",produces="application/json;charset=UTF-8", method = {RequestMethod.GET})
	@ResponseBody
	@ApiOperation(value = "产品列表查询(手机端)", httpMethod = "GET", 
	notes = "产品列表查询(手机端)",	consumes="application/x-www-form-urlencoded")
	public String listDemoP(HttpServletRequest request,HttpServletResponse response,
			@RequestParam(required = false) String productName,
			@RequestParam Integer pageno,
			@RequestParam Integer pagesize,
			@RequestParam String states) {
		response.setContentType("application/json; charset=UTF-8");
		int pageNo = pageno==null?Global.DEFAULT_PAGENO:pageno;
		int pageSize = pagesize==null?Global.DEFAULT_PAGESIZE:pagesize;
		Page<TraceProductP> page = new Page<TraceProductP>(pageNo,pageSize); 
		String userCode = request.getParameter("userCode");
		String officeId = "";
		if(userCode != null) {
			User user = userDao.findByLoginName(userCode);
			if(user != null && user.getCompany() != null) {
				officeId = user.getCompany().getId();
			}
			page = traceProductService.find(page, productName, officeId, states);
			List<TraceProductP> list = new ArrayList<TraceProductP>();
			for(int i=0;i<page.getList().size();i++){
				TraceProductP product = page.getList().get(i);
				product.setBatchCount(productBatchService.getBatchCount(officeId,product.getId(),states));
				product.setTraceCount(traceInfoService.getTraceCount(officeId,product.getId()));
				product.setLableCount(traceCodeService.getLabelCount(officeId,product.getId(),states));
				list.add(product);
			}
			page.setList(list);
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(page));
		}else {
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
	}
	
	@RequestMapping(value = "post/uploadPhoto", produces = "text/plain;charset=UTF-8", method = { RequestMethod.POST })
	@ResponseBody
	@ApiOperation(value = "拍照上传", httpMethod = "POST", notes = "拍照上传", consumes = "application/x-www-form-urlencoded")
	public String uploadPhoto(HttpServletRequest request, @RequestParam String len, @RequestParam String loginName) {
		String path = "";
		try {
			int l = Integer.parseInt(len);
			// 绝对路径
			String save_path = request.getSession().getServletContext().getRealPath("");
			// 服务器IP 端口拼接 http://127.0.0.1:8080/
			path = save_path.substring(0, save_path.lastIndexOf(File.separator))+ "/upload/photo/"+loginName+"/";
			File fiel = new File(path);
			if (!fiel.exists()) {
				fiel.mkdirs();// 如不存在路径则创建路�?
			}
			List<String> list = new ArrayList<String>();
			for(int i=0;i<l;i++){
				MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
				MultipartFile file = multipartRequest.getFile("photo");
				String fileName = request.getParameter("filename");
				String photoUrl = apiService.savePhoto(file, path,fileName,loginName);
				list.add(photoUrl);
			}
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(list));
		} catch (Exception e) {
			e.printStackTrace();
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.SYSTEM_ERROR.getCode(), ResultEnum.SYSTEM_ERROR.getMessage()));
		}
	}	
	
	@RequestMapping(value = "/updateCodeStatus",produces="application/json;charset=UTF-8", method = {RequestMethod.GET})
	@ResponseBody
	@ApiOperation(value = "更新溯源码打印状态", httpMethod = "GET", notes = "更新溯源码打印状态", consumes="application/x-www-form-urlencoded")
	public String updateCodeStatus(HttpServletRequest request,HttpServletResponse response,	@RequestParam String code) {
		try{
			traceCodeService.updateCodeStatus(code);
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(code));
		}catch(Exception e){
			e.printStackTrace();
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(""));
		}
	}
	
	@RequestMapping(value = "traceLableSpecificationList",produces="application/json;charset=UTF-8", method = {RequestMethod.GET})
	@ResponseBody
	@ApiOperation(value = "标签规格查询", httpMethod = "GET", notes = "标签规格查询",	consumes="application/x-www-form-urlencoded")
	public String traceLableSpecificationList(HttpServletRequest request,HttpServletResponse response,@RequestParam(required = false) String id) {
		response.setContentType("application/json; charset=UTF-8");
		List<TraceLableSpecification> traceLableSpecificationList = traceLableSpecificationService.findTraceLableSpecificationListByLabelId(id);
  		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(traceLableSpecificationList));
	}
	
	@RequestMapping(value = "getPrinterInfo",produces="application/json;charset=UTF-8", method = {RequestMethod.GET})
	@ResponseBody
	@ApiOperation(value = "打印机信息查询", httpMethod = "GET", notes = "打印机信息查询",	consumes="application/x-www-form-urlencoded")
	public String getPrinterInfo(HttpServletRequest request,HttpServletResponse response,@RequestParam(required = false) String id) {
		response.setContentType("application/json; charset=UTF-8");
		List<TraceLablePrinter> traceLablePrinterList = traceLablePrinterService.findTraceLablePrinterList();
  		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(traceLablePrinterList));
	}
	
	@RequestMapping(value = "getOffsetInfo",produces="application/json;charset=UTF-8", method = {RequestMethod.GET})
	@ResponseBody
	@ApiOperation(value = "获取偏移量信息", httpMethod = "GET", notes = "获取偏移量信息",	consumes="application/x-www-form-urlencoded")
	public String getOffsetInfo(HttpServletRequest request,HttpServletResponse response, @RequestParam String token, @RequestParam String printId) {
		response.setContentType("application/json; charset=UTF-8");
		User user = apiUserService.getUserByToken(token);
		TraceLableSelectPrinter traceLableSelectPrinter = traceLableSelectPrinterService.findTraceLableSelectPrinter(user.getOffice().getKuid(), printId);
  		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(traceLableSelectPrinter));
	}
	
	@RequestMapping(value = "getOffsetInfo_new",produces="application/json;charset=UTF-8", method = {RequestMethod.GET})
	@ResponseBody
	@ApiOperation(value = "获取偏移量信息", httpMethod = "GET", notes = "获取偏移量信息",	consumes="application/x-www-form-urlencoded")
	public String getOffsetInfo_new(HttpServletRequest request,HttpServletResponse response, @RequestParam String kuid, @RequestParam String printId) {
		response.setContentType("application/json; charset=UTF-8");
		TraceLableSelectPrinter traceLableSelectPrinter = traceLableSelectPrinterService.findTraceLableSelectPrinter(kuid, printId);
  		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(traceLableSelectPrinter));
	}
	
	@RequestMapping(value = "traceLableSpecification",produces="application/json;charset=UTF-8", method = {RequestMethod.GET})
	@ResponseBody
	@ApiOperation(value = "标签规格查询", httpMethod = "GET", notes = "标签规格查询",	consumes="application/x-www-form-urlencoded")
	public String traceLableSpecification(HttpServletRequest request,HttpServletResponse response,@RequestParam(required = false) String id) {
		response.setContentType("application/json; charset=UTF-8");
		TraceLableSpecification traceLableSpecification = traceLableSpecificationService.get(id);
  		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(traceLableSpecification));
	}
	
}
