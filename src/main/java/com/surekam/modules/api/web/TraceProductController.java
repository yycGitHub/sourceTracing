package com.surekam.modules.api.web;

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
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.surekam.common.config.Global;
import com.surekam.common.mapper.JsonMapper;
import com.surekam.common.persistence.Page;
import com.surekam.common.utils.ResultEnum;
import com.surekam.common.utils.ResultUtil;
import com.surekam.common.utils.StringUtils;
import com.surekam.common.utils.UniqueCodeUtil;
import com.surekam.common.web.BaseController;
import com.surekam.modules.api.service.ApiUserService;
import com.surekam.modules.productbatch.service.ProductBatchService;
import com.surekam.modules.sys.entity.CommonUser;
import com.surekam.modules.sys.entity.Office;
import com.surekam.modules.sys.entity.User;
import com.surekam.modules.sys.service.OfficeService;
import com.surekam.modules.sys.service.SystemService;
import com.surekam.modules.sys.utils.DictUtils;
import com.surekam.modules.sys.utils.UserUtils;
import com.surekam.modules.trace.TraceInfo.service.TraceInfoService;
import com.surekam.modules.tracecode.service.TraceCodeService;
import com.surekam.modules.traceproduct.entity.TraceProduct;
import com.surekam.modules.traceproduct.entity.TraceProductP;
import com.surekam.modules.traceproduct.service.TraceProductService;
import com.surekam.modules.traceresumetheme.entity.TraceResumeTheme;
import com.surekam.modules.traceresumetheme.service.TraceResumeThemeService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * 溯源产品管理接口
 * 
 * @author ligm
 * @version 2018-08-21
 */

@Api(value="溯源产品管理接口Controller", description="溯源产品管理的相关数据接口")
@RequestMapping(value = "api/traceProduct")
@Controller
public class TraceProductController extends BaseController {

	@Autowired
	private TraceProductService traceProductService;

	@Autowired
	private TraceResumeThemeService traceResumeThemeService;

	@Autowired
	private OfficeService officeService;

	@Autowired
	private SystemService systemService;

	@Autowired
	private ProductBatchService productBatchService;

	@Autowired
	private TraceCodeService traceCodeService;

	@Autowired
	private ApiUserService apiUserService;

	@Autowired
	private TraceInfoService traceInfoService;

	@ModelAttribute
	public TraceProduct get(@RequestParam(required = false) String id) {
		if (StringUtils.isNotBlank(id)) {
			return traceProductService.get(id);
		} else {
			return new TraceProduct();
		}
	}

	/**
	 * 根据企业编号获取对应的产品数据 分页 add by ligm 2018-08-21
	 * 
	 * @param corpCode
	 *            企业编号
	 * @param pageno
	 *            第几页
	 * @param pagesize
	 *            每页条数 默认为系统配置的值 测试地址：
	 *            http://localhost:8080/sureserve-admin/api/traceProduct/list?corpCode=1&pageno=1&pagesize=20
	 * @return
	 */
	@RequestMapping(value = "list", produces = "application/json;charset=UTF-8", method = { RequestMethod.GET })
	@ResponseBody
	@ApiOperation(value = "产品列表查询", httpMethod = "GET", notes = "产品列表查询", consumes = "application/x-www-form-urlencoded")
	public String list(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(required = false) String productName, @RequestParam(required = false) String productCode,
			@RequestParam(required = false) String status, @RequestParam(required = false) String officeId, @RequestParam(required = false) String officeName,
			@RequestParam Integer pageno, @RequestParam Integer pagesize) {
		response.setContentType("application/json; charset=UTF-8");
		int pageNo = pageno == null ? Global.DEFAULT_PAGENO : pageno;
		int pageSize = pagesize == null ? Global.DEFAULT_PAGESIZE : pagesize;
		Page<TraceProduct> page = new Page<TraceProduct>(pageNo, pageSize);
		String token = request.getHeader("X-Token");
		if (StringUtils.isBlank(token)) {
			return JsonMapper.nonDefaultMapper().toJson(
					ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
		if (token != null) {
			User user = apiUserService.getUserByToken(token);
			// 超级管理默认查询所有企业，其他默认查询本企业产品
			if (!user.isAdmin() && StringUtils.isBlank(officeId)) {
				officeId = user.getCompany().getId();
			}
		}
		page = traceProductService.find(page, productName, productCode, status, officeId, officeName);

		List<TraceProduct> list = new ArrayList<TraceProduct>();
		for (int i = 0; i < page.getList().size(); i++) {
			TraceProduct product = page.getList().get(i);
			// 字典转义
			product.setLableAuditFlagName(
					DictUtils.getDictLabel(product.getLableAuditFlag(), "PRODUCT_LABEL_AUDIT", ""));
			product.setShowTypeName(DictUtils.getDictLabel(product.getShowType(), "PRODUCT_SHOW_TYPE", ""));
			product.setStatusName(DictUtils.getDictLabel(product.getStatus(), "PRODUCT_IS_USED", ""));
			User user = UserUtils.getUserById(product.getCreatUserid());
			if (user != null) {
				product.setCreatUserName(user.getName());
			}
			// 主题名称
			if (StringUtils.isNotBlank(product.getThemeId())) {
				TraceResumeTheme traceResumeTheme = traceResumeThemeService.get(product.getThemeId());
				if (traceResumeTheme != null) {
					product.setThemeName(traceResumeTheme.getThemeName());
				}
			}

			Office office = officeService.get(product.getOfficeId());
			product.setCorpName(office.getName());

			String imgUrl = Global.getConfig("sy_img_url");
			if (StringUtils.isNotBlank(product.getProductPic())) {
				product.setProductPicUrl(imgUrl + product.getProductPic());
			} else {
				product.setProductPicUrl("");
			}
			list.add(product);
		}
		page.setList(list);
		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(page));
	}

	/**
	 * 产品列表查询(手机端)
	 * 
	 * @param request
	 * @param response
	 * @param productName
	 *            产品名称
	 * @param pageno
	 * @param pagesize
	 * @author wangyuewen
	 * @return
	 */
	@RequestMapping(value = "listP", produces = "application/json;charset=UTF-8", method = { RequestMethod.GET })
	@ResponseBody
	@ApiOperation(value = "产品列表查询(手机端)", httpMethod = "GET", notes = "产品列表查询(手机端)", consumes = "application/x-www-form-urlencoded")
	public String listP(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(required = false) String productName, @RequestParam Integer pageno,
			@RequestParam Integer pagesize, @RequestParam String states) {
		response.setContentType("application/json; charset=UTF-8");
		int pageNo = pageno == null ? Global.DEFAULT_PAGENO : pageno;
		int pageSize = pagesize == null ? Global.DEFAULT_PAGESIZE : pagesize;
		Page<TraceProductP> page = new Page<TraceProductP>(pageNo, pageSize);
		String token = request.getHeader("X-Token");
		String officeId = "";
		if (token != null) {
			User user = apiUserService.getUserByToken(token);
			if (user != null && user.getCompany() != null) {
				officeId = user.getCompany().getId();
			}
			page = traceProductService.find(page, productName, officeId, states);
			List<TraceProductP> list = new ArrayList<TraceProductP>();
			for (int i = 0; i < page.getList().size(); i++) {
				TraceProductP product = page.getList().get(i);
				product.setBatchCount(productBatchService.getBatchCount(officeId, product.getId(), states));
				product.setTraceCount(traceInfoService.getTraceCount(officeId, product.getId()));
				product.setLableCount(traceCodeService.getLabelCount(officeId, product.getId(), states));
				list.add(product);
			}
			page.setList(list);
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(page));
		} else {
			return JsonMapper.nonDefaultMapper().toJson(
					ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
	}

	/**
	 * 回收站产品列表查询(PC端)
	 * 
	 * @param request
	 * @param response
	 * @param productName
	 *            产品名称
	 * @author xy
	 * @return --2019-01-10 15:20
	 */
	@RequestMapping(value = "listP2", produces = "application/json;charset=UTF-8", method = { RequestMethod.GET })
	@ResponseBody
	@ApiOperation(value = "产品列表查询", httpMethod = "GET", notes = "产品列表查询", consumes = "application/x-www-form-urlencoded")
	public String listP2(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(required = false) String productName, @RequestParam(required = false) String corpCode,
			@RequestParam Integer pageno, @RequestParam Integer pagesize, @RequestParam String states) {
		response.setContentType("application/json; charset=UTF-8");

		Map<String, Object> map = new HashMap<String, Object>();
		int pageNo = pageno == null ? Global.DEFAULT_PAGENO : pageno;
		int pageSize = pagesize == null ? Global.DEFAULT_PAGESIZE : pagesize;
		Page<TraceProductP> page = new Page<TraceProductP>(pageNo, pageSize);
		String token = request.getHeader("X-Token");
		if (token != null) {
			User user = apiUserService.getUserByToken(token);
			List<String> findChildrenOffice = new ArrayList<String>();
			if (!StringUtils.isNotBlank(corpCode)) {
				findChildrenOffice = officeService.findChildrenOffice(user.getCompany().getId());
			}
			String admin = "";
			// roleType 1: 非管路员 0 ：则管路员
			if (!user.isAdmin()) {
				admin = user.getCompany().getId();
				map.put("roleType", "0");
			} else {
				map.put("roleType", "0");
			}
			page = traceProductService.PC_find(page, productName, corpCode, states, findChildrenOffice, admin, user);
			List<TraceProductP> list = new ArrayList<TraceProductP>();
			for (int i = 0; i < page.getList().size(); i++) {
				TraceProductP product = page.getList().get(i);
				product.setBatchCount(productBatchService.getBatchCount(corpCode, product.getId(), states));
				product.setTraceCount(traceInfoService.getTraceCount(corpCode, product.getId()));
				product.setLableCount(traceCodeService.getLabelCount(corpCode, product.getId(), states));

				// 获取到公司名称
				if (!"".equals(product.getOfficeId()) && product.getOfficeId() != null) {
					product.setCorpName(officeService.get(product.getOfficeId()).getName());
				}
				list.add(product);
			}
			page.setList(list);
			map.put("page", page);
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(map));
		} else {
			return JsonMapper.nonDefaultMapper().toJson(
					ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
	}

	/**
	 * 回收站产品(PC端)导出，不分页
	 * 
	 * @param request
	 * @param response
	 * @param productName
	 *            产品名称
	 * @author xy
	 * @return --2019-03-18 16:20
	 */
	@RequestMapping(value = "listExport", produces = "application/json;charset=UTF-8", method = { RequestMethod.GET })
	@ResponseBody
	@ApiOperation(value = "回收站产品(PC端)导出，不分页", httpMethod = "GET", notes = "回收站产品(PC端)导出，不分页", consumes = "application/x-www-form-urlencoded")
	public String listExport(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(required = false) String productName, @RequestParam(required = false) String corpCode,
			@RequestParam Integer pageno, @RequestParam Integer pagesize, @RequestParam String states) {
		response.setContentType("application/json; charset=UTF-8");

		Map<String, Object> map = new HashMap<String, Object>();
		int pageNo = pageno == null ? Global.DEFAULT_PAGENO : pageno;
		int pageSize = pagesize == null ? Global.DEFAULT_PAGESIZE : pagesize;
		Page<TraceProductP> page = new Page<TraceProductP>(pageNo, pageSize);
		String token = request.getHeader("X-Token");
		if (token != null) {
			User user = apiUserService.getUserByToken(token);
			List<String> findChildrenOffice = new ArrayList<String>();
			if (!StringUtils.isNotBlank(corpCode)) {
				findChildrenOffice = officeService.findChildrenOffice(user.getCompany().getId());
			}
			String admin = "";
			// roleType 1: 非管路员 0 ：则管路员
			if (!user.isAdmin()) {
				admin = user.getCompany().getId();
				map.put("roleType", "0");
			} else {
				map.put("roleType", "0");
			}
			List<TraceProductP> list2 = traceProductService.findListExport(productName, corpCode, states,
					findChildrenOffice, admin, user);
			page.setList(list2);
			List<TraceProductP> list = new ArrayList<TraceProductP>();
			for (int i = 0; i < page.getList().size(); i++) {
				TraceProductP product = page.getList().get(i);
				product.setBatchCount(productBatchService.getBatchCount(corpCode, product.getId(), states));
				product.setTraceCount(traceInfoService.getTraceCount(corpCode, product.getId()));
				product.setLableCount(traceCodeService.getLabelCount(corpCode, product.getId(), states));

				// 获取到公司名称
				if (!"".equals(product.getOfficeId()) && product.getOfficeId() != null) {
					Office office = officeService.get(product.getOfficeId());
					if (office == null) {
						product.setCorpName("");
					} else {
						product.setCorpName(office.getName());
					}
				}
				list.add(product);
			}
			page.setList(list);
			map.put("page", page);
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(map));
		} else {
			return JsonMapper.nonDefaultMapper().toJson(
					ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
	}

	/**
	 * 溯源精品查询
	 * 
	 * @author wangyuewen
	 */
	@RequestMapping(value = "getCompetitiveProduct", produces = "application/json;charset=UTF-8", method = {
			RequestMethod.GET })
	@ResponseBody
	@ApiOperation(value = "溯源精品查询(手机端)", httpMethod = "GET", notes = "溯源精品查询(手机端)", consumes = "application/x-www-form-urlencoded")
	public String getCompetitiveProduct() {
		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(traceProductService.getCompetitiveProduct()));
	}

	/**
	 * 获取某个产品信息 add by ligm 2018-08-21
	 * 
	 * @param id
	 *            测试地址：
	 *            http://localhost:8080/sureserve-admin/api/traceProduct/getInformation?id=1
	 * @return
	 */
	@RequestMapping(value = "getInformation", produces = "application/json;charset=UTF-8", method = {
			RequestMethod.GET })
	@ResponseBody
	@ApiOperation(value = "产品详情", httpMethod = "GET", notes = "产品详情", consumes = "application/x-www-form-urlencoded")
	public String getInformation(HttpServletRequest request, @RequestParam String id) {
		if (StringUtils.isNotBlank(id)) {
			TraceProduct traceProduct = traceProductService.get(id);
			if (StringUtils.isNotBlank(traceProduct.getProductPic())) {
				traceProduct.setProductPicUrl(traceProduct.getProductPic());
			} else {
				traceProduct.setProductPicUrl("");
			}
			Office office = officeService.get(traceProduct.getOfficeId());
			traceProduct.setCorpName(office.getName());
			// 主题名称
			if (traceProduct.getThemeId() != null && !traceProduct.getThemeId().equals("")) {
				TraceResumeTheme traceResumeTheme = traceResumeThemeService.get(traceProduct.getThemeId());
				traceProduct.setThemeName(traceResumeTheme.getThemeName());
			}

			List<Object> bqList = traceCodeService.getBqsl(id);
			Object[] obj = (Object[]) bqList.get(0);
			traceProduct.setSqs(obj[0] == null ? "0" : obj[0].toString());
			traceProduct.setYjhs(obj[1] == null ? "0" : obj[1].toString());
			traceProduct.setWjhs(obj[2] == null ? "0" : obj[2].toString());

			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(traceProduct));
		} else {
			return JsonMapper.nonDefaultMapper().toJson(
					ResultUtil.error(ResultEnum.DATA_NOT_EXIST.getCode(), ResultEnum.DATA_NOT_EXIST.getMessage()));
		}
	}

	/**
	 * 产品新增 add by ligm 2018-08-21 测试地址：
	 * http://localhost:8080/sureserve-admin/api/traceProduct/addSave corpCode 企业号
	 * productCode 产品编号 productName 产品名称 productTitle 标题 showType 是否显示 themeCode
	 * 主题编号 lableAuditFlag 标签审核标识 productPic 图片路径
	 * 
	 * @return
	 */
	@RequestMapping(value = "addSave", produces = "text/plain;charset=UTF-8", method = { RequestMethod.POST })
	@ResponseBody
	@ApiOperation(value = "产品新增保存", httpMethod = "POST", notes = "产品新增保存", consumes = "application/json")
	public String addSave(HttpServletRequest request,
			@RequestBody @ApiParam(name = "溯源产品对象", value = "传入json格式", required = true) TraceProduct traceProduct) {
		String token = request.getHeader("X-Token");
		// 分配产品的唯一编码
		User user = apiUserService.getUserByToken(token);
		traceProduct.setOfficeId(user.getCompany().getId());
		Office office = officeService.get(user.getCompany().getId());
		String productCode = UniqueCodeUtil.getUniqueCode("TraceProduct", "productCode", office.getCode());
		traceProduct.setProductCode(office.getCode() + "-" + UniqueCodeUtil.PRODUCT_PRE + "-" + productCode);

		traceProduct.setStatus(TraceProduct.OPEN);
		if (StringUtils.isBlank(token)) {
			return JsonMapper.nonDefaultMapper().toJson(
					ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
		traceProduct.setCreatUserid(user.getId());
		TraceProduct tempProduct = traceProductService.find(traceProduct, user.getCompany().getId());
		if (tempProduct != null) {
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.PRODUCT_NAME_EXIST.getCode(),
					ResultEnum.PRODUCT_NAME_EXIST.getMessage()));
		}
		try {
			traceProductService.save(traceProduct);
		} catch (Exception e) {
			e.printStackTrace();
			return JsonMapper.nonDefaultMapper()
					.toJson(ResultUtil.error(ResultEnum.SYSTEM_ERROR.getCode(), ResultEnum.SYSTEM_ERROR.getMessage()));
		}
		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(traceProduct));
	}

	/**
	 * 产品修改 add by ligm 2018-08-21 测试地址：
	 * http://localhost:8080/sureserve-admin/api/traceProduct/updateSave id 产品ID
	 * corpCode 企业号 productCode 产品编号 productName 产品名称 productTitle 标题 showType 是否显示
	 * themeCode 主题编号 lableAuditFlag 标签审核标识 file 文件
	 * 
	 * @return
	 */
	@RequestMapping(value = "updateSave", produces = "text/plain;charset=UTF-8", method = { RequestMethod.POST })
	@ResponseBody
	@ApiOperation(value = "产品修改保存", httpMethod = "POST", notes = "产品修改保存", consumes = "application/json")
	public String updateSave(HttpServletRequest request,
			@RequestBody @ApiParam(name = "溯源产品对象", value = "传入json格式", required = true) TraceProduct traceProduct) {
		String token = request.getHeader("X-Token");
		TraceProduct product = traceProductService.get(traceProduct.getId());
		if (traceProduct.getProductName() != null) {
			product.setProductName(traceProduct.getProductName());
		}
		if (traceProduct.getProductDiscription() != null) {
			product.setProductDiscription(traceProduct.getProductDiscription());
		}
		if (traceProduct.getThemeId() != null) {
			product.setThemeId(traceProduct.getThemeId());
		}
		if (traceProduct.getLableAuditFlag() != null) {
			product.setLableAuditFlag(traceProduct.getLableAuditFlag());
		}
		if (traceProduct.getProductPic() != null) {
			product.setProductPic(traceProduct.getProductPic());
		}
		if (StringUtils.isBlank(token)) {
			return JsonMapper.nonDefaultMapper().toJson(
					ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
		User user = apiUserService.getUserByToken(token);
		product.setUpdateTime(new Date());
		product.setUpdateUserid(user.getId());

		try {
			traceProductService.save(product);
		} catch (Exception e) {
			e.printStackTrace();
			return JsonMapper.nonDefaultMapper()
					.toJson(ResultUtil.error(ResultEnum.SYSTEM_ERROR.getCode(), ResultEnum.SYSTEM_ERROR.getMessage()));
		}
		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(product));
	}

	/**
	 * 启用溯源产品 add by liw 2018-08-21 测试地址：
	 * http://localhost:8080/sureserve-admin/api/traceProduct/enable post 传入参数为以下：
	 * id 产品id
	 * 
	 * @return
	 */
	@RequestMapping(value = "enable", produces = "application/json;charset=UTF-8", method = { RequestMethod.POST })
	@ResponseBody
	@ApiOperation(value = "产品启用", httpMethod = "POST", notes = "产品启用", consumes = "application/x-www-form-urlencoded")
	public String enable(HttpServletRequest request, @RequestParam String id) {
		traceProductService.enable(id);
		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success());
	}

	/**
	 * 停用溯源产品 add by liw 2018-08-21 测试地址：
	 * http://localhost:8080/sureserve-admin/api/traceProduct/disable post 传入参数为以下：
	 * id 产品id
	 * 
	 * @return
	 */
	@RequestMapping(value = "disable", produces = "application/json;charset=UTF-8", method = { RequestMethod.POST })
	@ResponseBody
	@ApiOperation(value = "产品停用", httpMethod = "POST", notes = "产品停用", consumes = "application/x-www-form-urlencoded")
	public String disable(HttpServletRequest request, @RequestParam String id) {
		traceProductService.disable(id);
		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success());
	}

	/**
	 * 产品删除，没有已打印标签的批次数据也能删除 add by ligm 2018-08-21 productId 产品id
	 * 
	 * @return
	 */
	@RequestMapping(value = "deleteProduct", produces = "application/json;charset=UTF-8", method = {
			RequestMethod.POST })
	@ResponseBody
	@ApiOperation(value = "产品删除，没有已打印标签的批次数据也能删除", httpMethod = "POST", notes = "产品删除，没有已打印标签的批次数据也能删除，连带删除产品对应的model modelProperty数据", consumes = "application/x-www-form-urlencoded")
	public String deleteProduct(HttpServletRequest request, @RequestParam String productId) {
		String token = request.getHeader("X-Token");
		User user = apiUserService.getUserByToken(token);
		String resultFlag = traceProductService.deleteProductWithModelsAndProperties(productId, user);
		// 返回1 表名存在批次数据 不能删除此产品
		if (StringUtils.isNotBlank(resultFlag) && "1".equals(resultFlag)) {
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.PRODUCT_BATCH_EXIST.getCode(),
					ResultEnum.PRODUCT_BATCH_EXIST.getMessage()));
		}

		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success());
	}

	/**
	 * 产品恢复 add by liw 2018-12-19 productId 产品id
	 * 
	 * @return
	 */
	@RequestMapping(value = "recoverProduct", produces = "application/json;charset=UTF-8", method = {
			RequestMethod.POST })
	@ResponseBody
	@ApiOperation(value = "产品恢复", httpMethod = "POST", notes = "产品恢复", consumes = "application/x-www-form-urlencoded")
	public String recoverProduct(HttpServletRequest request, @RequestParam String productId) {
		String token = request.getHeader("X-Token");
		if (StringUtils.isNotBlank(token)) {
			User user = apiUserService.getUserByToken(token);
			traceProductService.recoverProduct(productId, user);
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success());
		} else {
			return JsonMapper.nonDefaultMapper().toJson(
					ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
	}

	/**
	 * 复制产品、主题、模块、模块属性数据 add by ligm 2018-09-28 测试地址：
	 * http://localhost:8080/sureserve-admin/api/traceProduct/copyProduct post
	 * 传入参数为以下： productId 产品id officeId 复制到目标公司的主键
	 * 
	 * @return
	 */
	@RequestMapping(value = "copyProduct", produces = "application/json;charset=UTF-8", method = { RequestMethod.POST })
	@ResponseBody
	@ApiOperation(value = "复制产品、主题、模块、模块属性数据", httpMethod = "POST", notes = "根据产品主键复制产品、主题、模块、模块属性数据", consumes = "application/x-www-form-urlencoded")
	public String copyProduct(HttpServletRequest request, @RequestParam String productId,
			@RequestParam String officeId) {
		String token = request.getHeader("X-Token");
		if (StringUtils.isBlank(token)) {
			return JsonMapper.nonDefaultMapper().toJson(
					ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
		CommonUser user = systemService.findByToken(token);
		traceProductService.copyProductData(productId, officeId, user.getId());
		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success());
	}

	/**
	 * 获取主题(下拉列表) add by liw 2018-08-24 测试地址：
	 * http://localhost:8080/sureserve-admin/api/traceProduct/getTHemeData
	 * 
	 * @return
	 */
	@RequestMapping(value = "getTHemeData", produces = "application/json;charset=UTF-8", method = { RequestMethod.GET })
	@ResponseBody
	@ApiOperation(value = "获取主题", httpMethod = "GET", notes = "获取主题", consumes = "application/x-www-form-urlencoded")
	public String getTHemeData(HttpServletRequest request, @RequestParam(required = false) String officeId) {
		List<TraceResumeTheme> list = traceResumeThemeService.findAllThemes(officeId);
		// 返回没有信息提示
		if (0 == list.size()) {
			return JsonMapper.nonDefaultMapper().toJson(
					ResultUtil.error(ResultEnum.DATA_COUNT_ZERO.getCode(), ResultEnum.DATA_COUNT_ZERO.getMessage()));
		} else {
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(list));
		}
	}

	/**
	 * 根据企业id获取产品(下拉列表) add by liuyi 2018-09-05 测试地址：
	 * http://localhost:8080/sureserve-admin/api/traceProduct/getProductByOfficeId
	 * 
	 * @return
	 */
	@RequestMapping(value = "getProductByOfficeId", produces = "application/json;charset=UTF-8", method = {
			RequestMethod.GET })
	@ResponseBody
	@ApiOperation(value = "获取产品", httpMethod = "GET", notes = "获取产品", consumes = "application/x-www-form-urlencoded")
	public String getProductByOfficeId(HttpServletRequest request, @RequestParam(required = false) String officeId) {
		List<TraceProduct> list = traceProductService.findAllProducts(officeId);
		// 返回没有信息提示
		if (0 == list.size()) {
			return JsonMapper.nonDefaultMapper().toJson(
					ResultUtil.error(ResultEnum.DATA_COUNT_ZERO.getCode(), ResultEnum.DATA_COUNT_ZERO.getMessage()));
		} else {
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(list));
		}
	}

	/**
	 * 获取企业(下拉列表) add by liw 2018-08-24 测试地址：
	 * http://localhost:8080/sureserve-admin/api/traceProduct/getCompanyData
	 * 
	 * @return
	 */
	@RequestMapping(value = "getCompanyData", produces = "application/json;charset=UTF-8", method = {
			RequestMethod.GET })
	@ResponseBody
	@ApiOperation(value = "获取企业", httpMethod = "GET", notes = "获取企业", consumes = "application/x-www-form-urlencoded")
	public String getCompanyData(HttpServletRequest request) {
		String token = request.getHeader("X-Token");
		if (StringUtils.isBlank(token)) {
			return JsonMapper.nonDefaultMapper().toJson(
					ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
		User user = apiUserService.getUserByToken(token);
		List<Office> list = officeService.findOfficesByUser(user);
		List<Map<String, Object>> mapList = Lists.newArrayList();
		for (int i = 0; i < list.size(); i++) {
			Office e = list.get(i);
			Map<String, Object> map = Maps.newHashMap();
			map.put("id", e.getId());
			// map.put("pId", e.getParent() != null ? e.getParent().getId() : 0);
			map.put("name", e.getName());
			mapList.add(map);
		}
		// 返回没有信息提示
		if (0 == mapList.size()) {
			return JsonMapper.nonDefaultMapper().toJson(
					ResultUtil.error(ResultEnum.DATA_COUNT_ZERO.getCode(), ResultEnum.DATA_COUNT_ZERO.getMessage()));
		} else {
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(mapList));
		}
	}

	/**
	 * 获取产品下拉列表)
	 * 
	 * @author wangyuewen 2018/11/21
	 * @return
	 */
	@RequestMapping(value = "getProductData", produces = "application/json;charset=UTF-8", method = {
			RequestMethod.GET })
	@ResponseBody
	@ApiOperation(value = "获取产品", httpMethod = "GET", notes = "获取产品", consumes = "application/x-www-form-urlencoded")
	public String getProductData(HttpServletRequest request, @RequestParam(required = true) String officeId) {
		String token = request.getHeader("X-Token");
		if (StringUtils.isBlank(token)) {
			return JsonMapper.nonDefaultMapper().toJson(
					ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
		if (token != null) {
			User user = apiUserService.getUserByToken(token);
			if (!user.getId().equals("1") && !user.getCompany().getId().equals(officeId)) {
				return JsonMapper.nonDefaultMapper().toJson(
						ResultUtil.error(ResultEnum.BAD_REQ_PARAM.getCode(), ResultEnum.BAD_REQ_PARAM.getMessage()));
			}
		}
		List<TraceProduct> list = traceProductService.findAllProducts(officeId);
		List<Map<String, Object>> mapList = Lists.newArrayList();
		for (int i = 0; i < list.size(); i++) {
			TraceProduct e = list.get(i);
			Map<String, Object> map = Maps.newHashMap();
			map.put("id", e.getId());
			// map.put("pId", e.getParent() != null ? e.getParent().getId() : 0);
			map.put("name", e.getProductName());
			mapList.add(map);
		}
		// 返回没有信息提示
		if (0 == mapList.size()) {
			return JsonMapper.nonDefaultMapper().toJson(
					ResultUtil.error(ResultEnum.DATA_COUNT_ZERO.getCode(), ResultEnum.DATA_COUNT_ZERO.getMessage()));
		} else {
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(mapList));
		}
	}

}
