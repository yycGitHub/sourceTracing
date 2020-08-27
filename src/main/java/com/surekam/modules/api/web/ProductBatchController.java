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
import com.surekam.common.utils.UniqueCodeUtil;
import com.surekam.common.web.BaseController;
import com.surekam.modules.api.service.ApiUserService;
import com.surekam.modules.api.utils.ApiUtil;
import com.surekam.modules.productbatch.entity.ProductBatch;
import com.surekam.modules.productbatch.service.ProductBatchService;
import com.surekam.modules.sys.dao.UserDao;
import com.surekam.modules.sys.entity.CommonUser;
import com.surekam.modules.sys.entity.Office;
import com.surekam.modules.sys.entity.User;
import com.surekam.modules.sys.service.OfficeService;
import com.surekam.modules.sys.utils.DictUtils;
import com.surekam.modules.sys.utils.UserUtils;
import com.surekam.modules.tracecode.entity.TraceCode;
import com.surekam.modules.tracecode.service.TraceCodeService;
import com.surekam.modules.tracemodel.entity.TraceModelDataGroup;
import com.surekam.modules.traceproduct.entity.TraceProduct;
import com.surekam.modules.traceproduct.entity.TraceRootBean;
import com.surekam.modules.traceproduct.service.TraceProductService;
import com.surekam.modules.traceproduct.service.TraceShowService;
import com.surekam.modules.traceproductaudit.entity.SavaAuditReq;
import com.surekam.modules.traceproductaudit.entity.TraceProductAudit;
import com.surekam.modules.traceproductaudit.service.TraceProductAuditService;
import com.surekam.modules.traceproductauditnew.entity.TraceProductAuditNew;
import com.surekam.modules.traceproductauditnew.service.TraceProductAuditNewService;
import com.surekam.modules.traceresumetheme.entity.TraceResumeTheme;
import com.surekam.modules.traceresumetheme.service.TraceResumeThemeService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * 产品批次接口
 * 
 * @author liuyi
 * @version 2018-09-04
 */
@Api(value="溯源批次管理接口Controller", description="溯源批次管理的相关数据接口")
@Controller
@RequestMapping(value = "api/productBatch")
public class ProductBatchController extends BaseController {

	@Autowired
	private ProductBatchService productBatchService;

	@Autowired
	private TraceResumeThemeService traceResumeThemeService;

	@Autowired
	private TraceProductService traceProductService;

	@Autowired
	private OfficeService officeService;

	@Autowired
	private CommonApiController commonApiController;

	@Autowired
	private TraceShowService traceShowService;

	@Autowired
	private UserDao userDao;

	@Autowired
	private ApiUserService apiUserService;

	@Autowired
	private TraceCodeService traceCodeService;
	
	@Autowired
	private TraceProductAuditService traceProductAuditService;
	
	@Autowired
	private TraceProductAuditNewService traceProductAuditNewService;

	@ModelAttribute
	public ProductBatch get(@RequestParam(required = false) String id) {
		if (StringUtils.isNotBlank(id)) {
			return productBatchService.get(id);
		} else {
			return new ProductBatch();
		}
	}

	/**
	 * 根据企业编号获取对应的主题数据 分页 add by liuyi 2018-09-04
	 * 
	 * @param corpCode
	 *            企业编号
	 * @param productId
	 *            产品ID 可查询对应产品的批次
	 * @param pageno
	 *            第几页
	 * @param pagesize
	 *            每页条数 默认为系统配置的值 测试地址：
	 *            http://localhost:8080/sureserve-admin/api/productBatch/productBatchList?corpCode=1&pageno=1&pagesize=20
	 * @return
	 */
	@RequestMapping(value = "productBatchList", produces = "application/json;charset=UTF-8", method = {
			RequestMethod.GET })
	@ResponseBody
	@ApiOperation(value = "批次列表查询", httpMethod = "GET", notes = "批次列表查询", consumes = "application/x-www-form-urlencoded")
	public String productBatchList(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(required = false) String batchCode, @RequestParam(required = false) String batchName,
			@RequestParam(required = false) String isEnd, @RequestParam(required = false) String corpCode,
			@RequestParam(required = false) String productId, @RequestParam Integer pageno,
			@RequestParam Integer pagesize) {
		response.setContentType("application/json; charset=UTF-8");
		String token = request.getHeader("X-Token");
		if (StringUtils.isBlank(token)) {
			return JsonMapper.nonDefaultMapper().toJson(
					ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
		if (token != null) {
			User user = apiUserService.getUserByToken(token);
			// 超级管理默认查询所有企业，其他默认查询本企业批次
			if (StringUtils.isEmpty(corpCode)) {
				if (!user.isAdmin()) {
					corpCode = user.getCompany().getId();
				}
			}
		}
		int pageNo = pageno == null ? Global.DEFAULT_PAGENO : pageno;
		int pageSize = pagesize == null ? Global.DEFAULT_PAGESIZE : pagesize;
		Page<ProductBatch> page = new Page<ProductBatch>(pageNo, pageSize);
		page = productBatchService.find(page, batchName, batchCode, isEnd, corpCode, productId);

		// 返回没有信息提示
		// if(0 == page.getCount()){
		// return
		// JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.DATA_COUNT_ZERO.getCode(),
		// ResultEnum.DATA_COUNT_ZERO.getMessage()));
		// }else{
		List<ProductBatch> list = new ArrayList<ProductBatch>();
		for (int i = 0; i < page.getList().size(); i++) {
			ProductBatch batch = page.getList().get(i);
			// 字典转义
			batch.setIsEndName(DictUtils.getDictLabel(batch.getIsEnd(), "is_end", ""));
			User user = UserUtils.getUserById(batch.getCreatUserid());
			if (user != null && user.getName() != null) {
				batch.setCreatUserName(user.getName());
			}

			Office office = officeService.get(batch.getOfficeId());
			batch.setCorpName(office.getName());

			TraceProduct traceProduct = traceProductService.get(batch.getProductId());
			batch.setProductName(traceProduct == null ? null : traceProduct.getProductName());

			if (batch.getThemeId() != null && !batch.getThemeId().equals("")) {
				TraceResumeTheme theme = traceResumeThemeService.get(batch.getThemeId());
				batch.setThemeName(theme == null ? null : theme.getThemeName());
			}

			// 获取激活码
			List<Integer> jhdList = traceCodeService.getBqjhm(productId, batch.getId());
			String qjjhm = ApiUtil.getQjjhm(jhdList);
			batch.setJhm(qjjhm);

			// 获取已绑定标签数
			List<Integer> jhsList = traceCodeService.getYbdbqs(productId, batch.getId());
			String jhs = jhsList.get(0) + "";
			batch.setJhs(jhs);

			// 获取批次相关属性数据 这些属性为定义模板时的固定属性
			try {
				TraceRootBean traceRootBean = traceShowService.findTraceDataByBatchId(batch.getId());
				batch.setTraceRootBean(traceRootBean);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			list.add(batch);
		}
		page.setList(list);
		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(page));
		// }
	}

	@RequestMapping(value = "addBatch", produces = "application/json;charset=UTF-8", method = { RequestMethod.POST })
	@ResponseBody
	@ApiOperation(value = "新增批次", httpMethod = "POST", notes = "新增批次", consumes = "application/json")
	public String addBatch(HttpServletRequest request, HttpServletResponse response,
			@RequestBody ProductBatch productBatch) {
		String token = request.getHeader("X-Token");
		if (StringUtils.isBlank(token)) {
			// token = "0021fa19-5f42-4d8f-921b-3dee9236c74a";
			return JsonMapper.nonDefaultMapper().toJson(
					ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
		// 分配唯一批次码
		User user = apiUserService.getUserByToken(token);
		Office office = officeService.get(user.getCompany().getId());
		String productCode = UniqueCodeUtil.getUniqueCode("ProductBatch", "batchCode", office.getCode());
		productBatch.setBatchCode(office.getCode() + "-" + UniqueCodeUtil.BATCH_PRE + "-" + productCode);
		productBatch.setOfficeId(productBatch.getOfficeId());
		productBatch.setThemeId(productBatch.getThemeId());
		productBatch.setBatchName(productBatch.getBatchName());
		productBatch.setCreatUserid(user.getId());
		productBatch.setCreatTime(new Date());
		productBatch.setIsEnd("0");
		productBatch.setStates("A");
		productBatch.setSysId("0");
		productBatch.setAuditStatus("1");
		try {
			productBatchService.save(productBatch);
		} catch (Exception e) {
			e.printStackTrace();
			return JsonMapper.nonDefaultMapper()
					.toJson(ResultUtil.error(ResultEnum.SYSTEM_ERROR.getCode(), ResultEnum.SYSTEM_ERROR.getMessage()));
		}
		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(productBatch));

	}

	@RequestMapping(value = "updateProductBatch", produces = "application/json;charset=UTF-8", method = {
			RequestMethod.POST })
	@ResponseBody
	@ApiOperation(value = "批次编辑", httpMethod = "POST", notes = "批次编辑", consumes = "application/json")
	public String updateProductBatch(HttpServletRequest request, HttpServletResponse response,
			@RequestBody ProductBatch productBatch) {
		String token = request.getHeader("X-Token");
		if (StringUtils.isBlank(token)) {
			return JsonMapper.nonDefaultMapper().toJson(
					ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
		User user = apiUserService.getUserByToken(token);
		productBatch.setUpdateTime(new Date());
		productBatch.setUpdateUserid(user.getId());
		productBatch.setStates("U");

		productBatch.setOfficeId(productBatch.getOfficeId());
		productBatch.setThemeId(productBatch.getThemeId());
		productBatch.setProductId(productBatch.getProductId());
		productBatch.setSysId("0");
		productBatch.setAuditStatus("1");
		try {
			productBatchService.save(productBatch);
		} catch (Exception e) {
			e.printStackTrace();
			return JsonMapper.nonDefaultMapper()
					.toJson(ResultUtil.error(ResultEnum.SYSTEM_ERROR.getCode(), ResultEnum.SYSTEM_ERROR.getMessage()));
		}

		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(productBatch));

	}

	/**
	 * 获取某个主题信息 add by ligm 2018-08-21
	 * 
	 * @param id
	 *            测试地址：
	 *            http://localhost:8080/sureserve-admin/api/traceResumeTheme/getThemeInformation?id=1
	 * @return
	 */
	@RequestMapping(value = "getThemeInformation", produces = "application/json;charset=UTF-8", method = {
			RequestMethod.GET })
	@ResponseBody
	@ApiOperation(value = "主题详情", httpMethod = "GET", notes = "主题详情", consumes = "application/x-www-form-urlencoded")
	public String getThemeInformation(HttpServletRequest request, @RequestParam String id) {
		if (StringUtils.isNotBlank(id)) {
			TraceResumeTheme theme = traceResumeThemeService.get(id);
			String imgUrl = Global.getConfig("sy_img_url");
			if (StringUtils.isNotBlank(theme.getPageUrl())) {
				theme.setPageUrl(imgUrl + theme.getPageUrl());
			} else {
				theme.setPageUrl("");
			}
			Office office = officeService.get(theme.getOfficeId());
			theme.setCorpName(office.getName());

			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(theme));
		} else {
			return JsonMapper.nonDefaultMapper().toJson(
					ResultUtil.error(ResultEnum.DATA_NOT_EXIST.getCode(), ResultEnum.DATA_NOT_EXIST.getMessage()));
		}
	}

	/**
	 * 主题新增 add by liw 2018-08-29 测试地址：
	 * http://localhost:8080/sureserve-admin/api/traceResumeTheme/addThemeSave
	 * corpCode 企业号 themeCode 主题编号 themeName 主题名称 themeType 主题类型 pageUrl 主题页面路径
	 * 
	 * @return
	 */
	@RequestMapping(value = "addThemeSave", produces = "text/plain;charset=UTF-8", method = { RequestMethod.POST })
	@ResponseBody
	@ApiOperation(value = "主题新增保存", httpMethod = "POST", notes = "主题新增保存", consumes = "application/x-www-form-urlencoded")
	public String addThemeSave(HttpServletRequest request, @RequestParam String corpCode,
			@RequestParam String themeName, @RequestParam String themeType,
			@RequestParam(required = false) String pageUrl) {
		String token = request.getParameter("token");
		TraceResumeTheme theme = new TraceResumeTheme();
		theme.setOfficeId(corpCode);
		String themeCode = commonApiController.getCode(TraceResumeTheme.SY_THEME_CODE);
		theme.setThemeCode(themeCode);
		theme.setThemeName(themeName);
		theme.setThemeType(themeType);
		theme.setPageUrl(pageUrl);
		theme.setStatus(TraceProduct.OPEN);
		if (StringUtils.isBlank(token)) {
			token = "0021fa19-5f42-4d8f-921b-3dee9236c74a";
		}
		CommonUser user = userDao.findByToken(token);
		theme.setCreatUserid(user.getId());

		ResultBean<TraceResumeTheme> resultBean = new ResultBean<TraceResumeTheme>();
		try {
			traceResumeThemeService.save(theme);
			resultBean = ResultUtil.success();
		} catch (Exception e) {
			e.printStackTrace();
			resultBean = ResultUtil.error(ResultEnum.SYSTEM_ERROR.getCode(), ResultEnum.SYSTEM_ERROR.getMessage());
		}
		return JsonMapper.nonDefaultMapper().toJson(resultBean);
	}

	/*
	 * 主题修改 add by liw 2018-08-29 测试地址：
	 * http://localhost:8080/sureserve-admin/api/traceResumeTheme/updateThemeSave id
	 * 产品ID corpCode 企业号 themeCode 主题编号 themeName 主题名称 themeType 主题类型 pageUrl 主题页面路径
	 * file 文件
	 * 
	 * @return
	 */
	@RequestMapping(value = "updateThemeSave", produces = "application/json;charset=UTF-8", method = {
			RequestMethod.POST })
	@ResponseBody
	@ApiOperation(value = "主题修改保存", httpMethod = "POST", notes = "主题修改保存", consumes = "application/x-www-form-urlencoded")
	public String updateThemeSave(HttpServletRequest request, @RequestParam String id, @RequestParam String corpCode,
			@RequestParam String themeName, @RequestParam String themeType,
			@RequestParam(required = false) String pageUrl) {
		String token = request.getParameter("token");
		TraceResumeTheme theme = traceResumeThemeService.get(id);
		theme.setOfficeId(corpCode);
		theme.setThemeName(themeName);
		theme.setThemeType(themeType);
		theme.setPageUrl(pageUrl);
		if (StringUtils.isBlank(token)) {
			token = "0021fa19-5f42-4d8f-921b-3dee9236c74a";
		}
		CommonUser user = userDao.findByToken(token);
		theme.setUpdateTime(new Date());
		theme.setUpdateUserid(user.getId());

		ResultBean<TraceResumeTheme> resultBean = new ResultBean<TraceResumeTheme>();
		try {
			traceResumeThemeService.save(theme);
			resultBean = ResultUtil.success();
		} catch (Exception e) {
			e.printStackTrace();
			resultBean = ResultUtil.error(ResultEnum.SYSTEM_ERROR.getCode(), ResultEnum.SYSTEM_ERROR.getMessage());
		}
		return JsonMapper.nonDefaultMapper().toJson(resultBean);
	}

	/**
	 * 结束产品批次 add by liuyi 2018-09-05 测试地址：
	 * http://127.0.0.1:8087/sureserve-admin/api/productBatch/batchDisable id 批次id
	 * 
	 * @return
	 */
	@RequestMapping(value = "batchDisable", produces = "application/json;charset=UTF-8", method = {
			RequestMethod.POST })
	@ResponseBody
	@ApiOperation(value = "结束产品批次", httpMethod = "POST", notes = "结束产品批次", consumes = "application/x-www-form-urlencoded")
	public String batchDisable(HttpServletRequest request, @RequestParam String id) {
		productBatchService.disable(id);
		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success());
	}

	/**
	 * 恢复产品批次
	 * 
	 * @param request
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "batchRecovery", produces = "application/json;charset=UTF-8", method = {
			RequestMethod.POST })
	@ResponseBody
	@ApiOperation(value = "恢复产品批次", httpMethod = "POST", notes = "恢复产品批次", consumes = "application/x-www-form-urlencoded")
	public String batchRecovery(HttpServletRequest request, @RequestParam String id) {
		productBatchService.Recovery(id);
		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success());
	}

	/**
	 * 删除批次 add by liuyi 2018-09-05 测试地址：
	 * http://127.0.0.1:8087/sureserve-admin/api/productBatch/batchDelete id 主题id
	 * 
	 * @return
	 */
	@RequestMapping(value = "batchDelete", produces = "application/json;charset=UTF-8", method = { RequestMethod.POST })
	@ResponseBody
	@ApiOperation(value = "批次删除", httpMethod = "POST", notes = "批次删除", consumes = "application/x-www-form-urlencoded")
	public String batchDelete(HttpServletRequest request, @RequestParam String id) {
		productBatchService.delete(id);
		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success());
	}

	/**
	 * 批次删除，没有已打印标签的数据才能删除 add by ligm 2018-10-22 batchId 批次id
	 * 
	 * @return
	 */
	@RequestMapping(value = "deleteBatch", produces = "application/json;charset=UTF-8", method = { RequestMethod.POST })
	@ResponseBody
	@ApiOperation(value = "逻辑删除批次及批次的模块数据、模块属性数据和未打印的标签数据", httpMethod = "POST", notes = "逻辑删除批次及批次的模块数据和模块属性数据，包括未绑定的标签相关数据", consumes = "application/x-www-form-urlencoded")
	public String deleteBatch(HttpServletRequest request, @RequestParam String batchId) {
		String token = request.getHeader("X-Token");
		User user = apiUserService.getUserByToken(token);
		String resultFlag = productBatchService.deleteBatchWithModelDatas(batchId, user);
		// 返回1 表名存在批次数据 不能删除此产品
		if (StringUtils.isNotBlank(resultFlag) && "1".equals(resultFlag)) {
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.PRODUCT_LABEL_EXIST.getCode(), ResultEnum.PRODUCT_LABEL_EXIST.getMessage()));
		}

		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success());
	}

	/**
	 * 文件上传 add by liw 2018-08-30 测试地址：
	 * http://localhost:8080/sureserve-admin/api/traceResumeTheme/themeFileUpload
	 * file 文件
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "themeFileUpload", produces = "text/plain;charset=UTF-8", method = { RequestMethod.POST })
	@ResponseBody
	@ApiOperation(value = "文件上传", httpMethod = "POST", notes = "文件上传", consumes = "application/x-www-form-urlencoded")
	public String themeFileUpload(HttpServletRequest request, @RequestParam MultipartFile file) throws Exception {
		String path = "";
		String path1 = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmsssss");
		String time = sdf.format(new Date());
		if (file != null && !file.getOriginalFilename().isEmpty()) {
			// 绝对路径
			String save_path = request.getSession().getServletContext().getRealPath("");
			// 服务器IP 端口拼接 http://127.0.0.1:8080/
			path = save_path.substring(0, save_path.lastIndexOf(File.separator)) + "/upload/theme_model/" + time + "/";
			File fiel = new File(path);
			if (!fiel.exists()) {
				fiel.mkdirs();// 如不存在路径则创建路径
			}
			path1 = "/upload/theme_model/" + time + "/";
		}

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		ResultBean<List<Map<String, Object>>> resultBean = new ResultBean<List<Map<String, Object>>>();
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			String picUrl = traceResumeThemeService.saveAttach(file, path);
			map.put("relative_url", picUrl);
			String imgUrl = Global.getConfig("sy_img_url");
			map.put("absolute_url", imgUrl + picUrl);
			list.add(map);
			resultBean = ResultUtil.success(list);
		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage().contains("只支持zip和rar格式的压缩包")) {
				resultBean = ResultUtil.error(ResultEnum.ATTACH_TYPE_ERROR.getCode(),
						ResultEnum.ATTACH_TYPE_ERROR.getMessage());
			} else {
				resultBean = ResultUtil.error(ResultEnum.SYSTEM_ERROR.getCode(), ResultEnum.SYSTEM_ERROR.getMessage());
			}
		}
		return JsonMapper.nonDefaultMapper().toJson(resultBean);
	}

	/**
	 * @author liwei 获取批次号列表
	 * @return
	 */
	@RequestMapping(value = "getPchList", produces = "application/json;charset=UTF-8", method = { RequestMethod.GET })
	@ResponseBody
	@ApiOperation(value = "获取批次号列表", httpMethod = "GET", notes = "获取批次号列表", consumes = "application/x-www-form-urlencoded")
	public String getPchList(HttpServletRequest request, @RequestParam String productId) {
		String token = request.getHeader("X-Token");
		if (token != null) {
			List<ProductBatch> list = productBatchService.findBatchListByProductId(productId);
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(list));
		} else {
			return JsonMapper.nonDefaultMapper().toJson(
					ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
	}

	/**
	 * 数据审核刘表查询接口
	 */
	@RequestMapping(value = "getDataAudit", produces = "application/json;charset=UTF-8", method = { RequestMethod.POST })
	@ResponseBody
	@ApiOperation(value = "数据审核刘表", httpMethod = "POST", notes = "数据审核刘表", consumes = "application/x-www-form-urlencoded")
	public String getDataAudit(HttpServletRequest request, @RequestParam Integer pageno, @RequestParam Integer pagesize, @RequestParam String type) {
		String token = request.getHeader("X-Token");
		if (StringUtils.isBlank(token)) {
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
		User user = apiUserService.getUserByToken(token);
		Page<Map<String, String>> page = productBatchService.getDataAudit(pageno, pagesize, user, type);
		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(page));
	}
	
	/**
	 * 数据审核刘表查询接口
	 */
	@RequestMapping(value = "getDataAuditNew", produces = "application/json;charset=UTF-8", method = { RequestMethod.POST })
	@ResponseBody
	@ApiOperation(value = "数据审核列表", httpMethod = "POST", notes = "数据审核列表", consumes = "application/x-www-form-urlencoded")
	public String getDataAuditNew(HttpServletRequest request, @RequestParam Integer pageno, @RequestParam Integer pagesize, @RequestParam String type) {
		String token = request.getHeader("X-Token");
		if (StringUtils.isBlank(token)) {
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
		User user = apiUserService.getUserByToken(token);
		Page<Map<String, String>> page = productBatchService.getDataAuditNew(pageno, pagesize, user, type);
		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(page));
	}

	/**
	 * 根据批次ID获取生长记录模块数据信息
	 * 
	 * @param batchId
	 *            批次Id
	 * @return
	 */
	@RequestMapping(value = "getModelGrowPropertiesInformation", produces = "application/json;charset=UTF-8", method = { RequestMethod.POST })
	@ResponseBody
	@ApiOperation(value = "根据批次ID获取生长记录模块数据信息 ", httpMethod = "POST", notes = "根据批次ID获取生长记录模块数据信息", consumes = "application/x-www-form-urlencoded")
	public String getModelGrowPropertiesInformation(HttpServletRequest request, @RequestParam String batchId) {
		String token = request.getHeader("X-Token");
		if (StringUtils.isBlank(token)) {
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
		List<TraceModelDataGroup> list = productBatchService.getModelGrowPropertiesInformation(batchId);
		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(list));
	}

	@RequestMapping(value = "getTraceShowPreview", produces = "application/json;charset=UTF-8", method = { RequestMethod.POST })
	@ResponseBody
	@ApiOperation(value = "溯源预览", httpMethod = "POST", notes = "溯源预览", consumes = "application/x-www-form-urlencoded")
	public String getTraceShowPreview(HttpServletRequest request, @RequestParam String batchId) throws Exception {
		String token = request.getHeader("X-Token");
		if (StringUtils.isBlank(token)) {
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
		String officeId = "";
		String productId = "";
		String templetProductId = "";
		String themeId = "";
		String skinId = "1";
		int count = 0;
		// 溯源产品
		ProductBatch productBatch = productBatchService.get(batchId);
		if (productBatch != null) {
			productId = productBatch.getProductId();
			// themeId = productBatch.getThemeId();
			templetProductId = productBatch.getProductId();
			officeId = productBatch.getOfficeId();
		}
		TraceCode traceCode = traceCodeService.getFirstTraceCode(batchId);
		if (traceCode != null) {
			officeId = traceCode.getOfficeId();
		}

		TraceProduct traceProduct = traceProductService.get(productId);
		if (traceProduct != null) {
			themeId = traceProduct.getThemeId();
			// 主题
			if (StringUtils.isBlank(themeId)) {
				themeId = "039db9f681744daf90d1114fc86604de";
			}
			TraceResumeTheme traceResumeTheme = traceResumeThemeService.get(themeId);
			skinId = traceResumeTheme.getSkinId();
		}
		count++;
		// 属性和属性数据
		TraceRootBean traceRootBean = new TraceRootBean();
		try {
			traceRootBean = traceShowService.findTraceData(templetProductId, batchId, count);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (traceCode != null) {
			traceCode.setTraceCount(count);
			traceCodeService.save(traceCode);
		}
		TraceProduct product = traceProductService.get(templetProductId);
		product.setCorpName(officeService.get(officeId).getName());
		traceRootBean.getMainModelData().setProduct(product);
		if (traceCode != null) {
			traceRootBean.getMainModelData().setIdentityCode(traceCode.getTraceCode());
		} else {
			traceRootBean.getMainModelData().setIdentityCode("*********");
		}
		traceRootBean.getMainModelData().setSkinId(skinId);
		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(traceRootBean));
	}
	
	/**
	 * 数据审核刘表查询接口
	 */
	@RequestMapping(value = "savaAudit", produces = "application/json;charset=UTF-8")
	@ResponseBody
	@ApiOperation(value = "保存审核记录", notes = "保存审核记录", consumes = "application/json")
	public String savaAudit(HttpServletRequest request, 
			@RequestBody @ApiParam(name = "保存审核记录", value = "传入json格式", required = true) SavaAuditReq req) {
		String token = request.getHeader("X-Token");
		if (StringUtils.isBlank(token)) {
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
		User user = apiUserService.getUserByToken(token);
		ResultBean<String> savaAudit = traceProductAuditService.savaAudit(user, req);
		return JsonMapper.nonDefaultMapper().toJson(savaAudit);
	}
	
	/**
	 * 保存审核记录
	 */
	@RequestMapping(value = "savaAuditNew", produces = "application/json;charset=UTF-8")
	@ResponseBody
	@ApiOperation(value = "保存审核记录", notes = "保存审核记录", consumes = "application/json")
	public String savaAuditNew(HttpServletRequest request, 
			@RequestBody @ApiParam(name = "保存审核记录", value = "传入json格式", required = true) SavaAuditReq req) {
		String token = request.getHeader("X-Token");
		if (StringUtils.isBlank(token)) {
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
		User user = apiUserService.getUserByToken(token);
		ResultBean<String> savaAudit = traceProductAuditNewService.savaAuditNew(user, req);
		return JsonMapper.nonDefaultMapper().toJson(savaAudit);
	}
	
	@ResponseBody
	@RequestMapping(value = "findByAuditId")
	@ApiOperation(value = "获取审核记录", httpMethod = "POST", notes = "获取审核记录", consumes = "application/x-www-form-urlencoded")
	public String findByAuditId(HttpServletRequest request, @RequestParam String batchCode) {
		String token = request.getHeader("X-Token");
		if (StringUtils.isBlank(token)) {
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
		List<TraceProductAudit> findByAuditId = traceProductAuditService.findByAuditId(batchCode);
		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(findByAuditId));
	}
	
	@ResponseBody
	@RequestMapping(value = "findByAuditIdNew")
	@ApiOperation(value = "获取审核记录", httpMethod = "POST", notes = "获取审核记录", consumes = "application/x-www-form-urlencoded")
	public String findByAuditIdNew(HttpServletRequest request, @RequestParam String batchCode) {
		String token = request.getHeader("X-Token");
		if (StringUtils.isBlank(token)) {
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
		List<TraceProductAuditNew> findByAuditId = traceProductAuditService.findByAuditIdNew(batchCode);
		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(findByAuditId));
	}
	
}
