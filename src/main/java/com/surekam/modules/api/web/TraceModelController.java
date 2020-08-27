package com.surekam.modules.api.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.surekam.common.config.Global;
import com.surekam.common.mapper.JsonMapper;
import com.surekam.common.persistence.Page;
import com.surekam.common.utils.ResultBean;
import com.surekam.common.utils.ResultEnum;
import com.surekam.common.utils.ResultUtil;
import com.surekam.common.utils.StringUtils;
import com.surekam.common.web.BaseController;
import com.surekam.modules.api.service.ApiUserService;
import com.surekam.modules.productbatch.entity.ProductBatch;
import com.surekam.modules.productbatch.service.ProductBatchService;
import com.surekam.modules.sys.entity.Office;
import com.surekam.modules.sys.entity.User;
import com.surekam.modules.sys.service.OfficeService;
import com.surekam.modules.sys.utils.DictUtils;
import com.surekam.modules.sys.utils.UserUtils;
import com.surekam.modules.tracemodel.entity.TraceModel;
import com.surekam.modules.tracemodel.entity.TraceModelData;
import com.surekam.modules.tracemodel.entity.TraceModelDataGroup;
import com.surekam.modules.tracemodel.service.TraceModelService;
import com.surekam.modules.traceproduct.entity.TraceProduct;
import com.surekam.modules.traceproduct.service.TraceProductService;
import com.surekam.modules.traceproperty.dao.TracePropertyNewDao;
import com.surekam.modules.traceproperty.entity.TraceProperty;
import com.surekam.modules.traceproperty.entity.TracePropertyData;
import com.surekam.modules.traceproperty.entity.TracePropertyNew;
import com.surekam.modules.traceproperty.service.TracePropertyService;
import com.surekam.modules.traceresumetheme.entity.TraceResumeTheme;
import com.surekam.modules.traceresumetheme.service.TraceResumeThemeService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * 溯源模块配置管理Controller
 * 
 * @author law
 * @version 2018-09-07
 */

@Api(value="溯源模块配置接口Controller", description="溯源模块配置的相关数据接口")
@Controller
@RequestMapping(value = "api/traceModel")
public class TraceModelController extends BaseController {

	@Autowired
	private TraceModelService traceModelService;

	@Autowired
	private TraceProductService traceProductService;

	@Autowired
	private OfficeService officeService;

	@Autowired
	private TraceResumeThemeService traceResumeThemeService;

	@Autowired
	private TracePropertyService tracePropertyService;

	@Autowired
	private TracePropertyNewDao tracePropertyNewDao;

	@Autowired
	private CommonApiController commonApiController;

	@Autowired
	private ProductBatchService productBatchService;

	@Autowired
	private ApiUserService apiUserService;

	@ModelAttribute
	public TraceModel get(@RequestParam(required = false) String id) {
		if (StringUtils.isNotBlank(id)) {
			return traceModelService.get(id);
		} else {
			return new TraceModel();
		}
	}

	/**
	 * 根据企业编号获取对应的模块数据 分页 add by huangrd 2018-11-23
	 * 
	 * @param corpCode
	 *            企业编号
	 * @param pageno
	 *            第几页
	 * @param pagesize
	 *            每页条数 默认为系统配置的值 测试地址：
	 *            http://localhost:8080/sureserve-admin/api/traceModel/list?corpCode=1&pageno=1&pagesize=20
	 * @return
	 */
	@RequestMapping(value = "modelList", produces = "application/json;charset=UTF-8", method = { RequestMethod.GET })
	@ResponseBody
	@ApiOperation(value = "模块列表查询", httpMethod = "GET", notes = "模块列表查询", consumes = "application/x-www-form-urlencoded")
	public String list(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(required = false) String modelName, @RequestParam(required = false) String modelCode,
			@RequestParam(required = false) String status, @RequestParam(required = false) String productId,
			@RequestParam Integer pageno, @RequestParam Integer pagesize) {
		response.setContentType("application/json; charset=UTF-8");
		int pageNo = pageno == null ? Global.DEFAULT_PAGENO : pageno;
		int pageSize = pagesize == null ? Global.DEFAULT_PAGESIZE : pagesize;
		Page<TraceModel> page = new Page<TraceModel>(pageNo, pageSize);
		String token = request.getHeader("X-Token");
		if (StringUtils.isBlank(token)) {
			return JsonMapper.nonDefaultMapper().toJson(
					ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
		String corpCode = "";
		if (token != null) {
			User user = apiUserService.getUserByToken(token);
			// 超级管理默认查询所有企业，其他默认查询本企业模块
			if (!user.isAdmin()) {
				corpCode = user.getCompany().getId();
			}
		}
		page = traceModelService.find(page, modelName, modelCode, status, productId, corpCode);

		List<TraceModel> list = new ArrayList<TraceModel>();

		for (int i = 0; i < page.getList().size(); i++) {
			TraceModel model = page.getList().get(i);
			List<TraceProperty> traceProperty = tracePropertyService.findByPropertyId(model.getId());
			model.setTracePropertyList(traceProperty);
			// 字典转义
			model.setShowTypeName(DictUtils.getDictLabel(model.getModelShowType(), "PRODUCT_SHOW_TYPE", ""));
			User user = UserUtils.getUserById(model.getCreatUserid());
			model.setCreatUserName(user.getName());

			Office office = officeService.get(model.getOfficeId());
			model.setCorpName(office.getName());
			TraceProduct product = traceProductService.get(model.getProductId());
			if (product != null) {
				model.setProductName(product.getProductName());
			}
			list.add(model);
		}
		page.setList(list);
		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(page));
	}

	/**
	 * 模块新增 add by huangrd 2018-11-26 测试地址：
	 * http://localhost:8080/sureserve-admin/api/traceModel/addSave productId 产品编号
	 * modelName 模块名称 sort 排序号
	 * 
	 * @return
	 */
	@RequestMapping(value = "addSave", produces = "text/plain;charset=UTF-8", method = { RequestMethod.POST })
	@ResponseBody
	@ApiOperation(value = "模块新增保存", httpMethod = "POST", notes = "模块新增保存", consumes = "application/json")
	public String addSave(HttpServletRequest request,
			@RequestBody @ApiParam(name = "模块对象", value = "传入json格式", required = true) TraceModel traceModel) {
		String token = request.getHeader("X-Token");
		User user = apiUserService.getUserByToken(token);
		traceModel.setOfficeId(user.getCompany().getId());
		String modelCode = commonApiController.getCode(TraceModel.SY_MODEL_CODE);
		traceModel.setModelCode(modelCode);
		traceModel.setStatus(TraceModel.OPEN);

		if (StringUtils.isBlank(token)) {
			return JsonMapper.nonDefaultMapper().toJson(
					ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
		traceModel.setCreatUserid(user.getId());
		traceModel.setUserid(user.getId());
		// 判断该产品该模块是否已存在
		TraceModel tempModel = traceModelService.find(traceModel.getModelName(), traceModel.getProductId());
		if (tempModel != null) {
			return JsonMapper.nonDefaultMapper().toJson(
					ResultUtil.error(ResultEnum.MODEL_NAME_EXIST.getCode(), ResultEnum.MODEL_NAME_EXIST.getMessage()));
		}
		ResultBean<TraceModel> resultBean = new ResultBean<TraceModel>();
		try {
			traceModelService.save(traceModel);
			resultBean = ResultUtil.success();
		} catch (Exception e) {
			e.printStackTrace();
			resultBean = ResultUtil.error(ResultEnum.SYSTEM_ERROR.getCode(), ResultEnum.SYSTEM_ERROR.getMessage());
		}
		return JsonMapper.nonDefaultMapper().toJson(resultBean);
	}

	/**
	 * 模块修改 add by huangrd 2018-11-27 测试地址：
	 * http://localhost:8080/sureserve-admin/api/traceModel/updateSave productId
	 * 产品编号 modelName 模块名称 sort 排序号
	 * 
	 * @return
	 */
	@RequestMapping(value = "updateSave", produces = "text/plain;charset=UTF-8", method = { RequestMethod.POST })
	@ResponseBody
	@ApiOperation(value = "模块修改保存", httpMethod = "POST", notes = "模块修改保存", consumes = "application/json")
	public String updateSave(HttpServletRequest request,
			@RequestBody @ApiParam(name = "模块对象", value = "传入json格式", required = true) TraceModel traceModel) {
		String token = request.getHeader("X-Token");
		TraceModel model = traceModelService.get(traceModel.getId());
		if (traceModel.getTracePropertyList() != null) {
			model.setTracePropertyList(traceModel.getTracePropertyList());
		}
		if (traceModel.getProductId() != null) {
			model.setProductId(traceModel.getProductId());
		}
		if (traceModel.getModelName() != null) {
			model.setModelName(traceModel.getModelName());
		}
		if (traceModel.getSort() != null) {
			model.setSort(traceModel.getSort());
		}

		if (StringUtils.isBlank(token)) {
			return JsonMapper.nonDefaultMapper().toJson(
					ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
		User user = apiUserService.getUserByToken(token);
		model.setUserid(user.getId());
		model.setUpdateTime(new Date());
		model.setUpdateUserid(user.getId());

		ResultBean<TraceModel> resultBean = new ResultBean<TraceModel>();
		try {
			traceModelService.update(model);
			resultBean = ResultUtil.success();
		} catch (Exception e) {
			e.printStackTrace();
			resultBean = ResultUtil.error(ResultEnum.SYSTEM_ERROR.getCode(), ResultEnum.SYSTEM_ERROR.getMessage());
		}
		return JsonMapper.nonDefaultMapper().toJson(resultBean);

	}

	/**
	 * 启用溯源模块 add by huangrd 2018-11-23 测试地址：
	 * http://localhost:8080/sureserve-admin/api/traceModel/enable id 主题id
	 * 
	 * @return
	 */
	@RequestMapping(value = "modelEnable", produces = "application/json;charset=UTF-8", method = { RequestMethod.POST })
	@ResponseBody
	@ApiOperation(value = "模块启用", httpMethod = "POST", notes = "模块启用", consumes = "application/x-www-form-urlencoded")
	public String enable(HttpServletRequest request, @RequestParam String id) {
		traceModelService.enable(id);
		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success());

	}

	/**
	 * 停用溯源模块 add by huangrd 2018-11-23 测试地址：
	 * http://localhost:8080/sureserve-admin/api/traceModel/disable id 主题id
	 * 
	 * @return
	 */
	@RequestMapping(value = "modelDisable", produces = "application/json;charset=UTF-8", method = {
			RequestMethod.POST })
	@ResponseBody
	@ApiOperation(value = "模块停用", httpMethod = "POST", notes = "模块停用", consumes = "application/x-www-form-urlencoded")
	public String disable(HttpServletRequest request, @RequestParam String id) {
		traceModelService.disable(id);
		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success());

	}

	/**
	 * 删除模块 add by liw 2018-08-30 测试地址：
	 * http://localhost:8080/sureserve-admin/api/traceModel/modelDelete id 模块id
	 * 
	 * @return
	 */
	@RequestMapping(value = "modelDelete", produces = "application/json;charset=UTF-8", method = { RequestMethod.POST })
	@ResponseBody
	@ApiOperation(value = "模块删除", httpMethod = "POST", notes = "模块删除", consumes = "application/x-www-form-urlencoded")
	public String modelDelete(HttpServletRequest request, @RequestParam String id) {
		traceModelService.delete(id);
		/*
		 * if(StringUtils.isNotBlank(resultFlag) && "1".equals(resultFlag)) { return
		 * JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.
		 * PRODUCT_LABEL_PRINTED.getCode(), "使用的模块不能删除")); }
		 */
		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success());

	}

	/**
	 * 获取属性 add by huangrd 2018-11-26 测试地址：
	 * http://localhost:8080/sureserve-admin/api/traceModel/getTracePropertyData
	 * 
	 * @return
	 */
	@RequestMapping(value = "getTracePropertyData", produces = "application/json;charset=UTF-8", method = {
			RequestMethod.GET })
	@ResponseBody
	@ApiOperation(value = "获取属性", httpMethod = "GET", notes = "获取属性", consumes = "application/x-www-form-urlencoded")
	public String getTracePropertyData(HttpServletRequest request) {
		String corpCode = "";
		String token = request.getHeader("X-Token");
		if (StringUtils.isBlank(token)) {
			return JsonMapper.nonDefaultMapper().toJson(
					ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
		User user = apiUserService.getUserByToken(token);
		corpCode = user.getCompany().getId();

		List<TraceProperty> list = tracePropertyService.findTraceProperty(corpCode);
		List<Map<String, Object>> mapList = Lists.newArrayList();
		for (int i = 0; i < list.size(); i++) {
			TraceProperty e = list.get(i);
			Map<String, Object> map = Maps.newHashMap();
			map.put("id", e.getId());
			// map.put("pId", e.getParent() != null ? e.getParent().getId() : 0);
			map.put("name", e.getPropertyNameZh());
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
	 * 根据当前企业获取产品下拉列表
	 * 
	 * @author huangrd 2018/11/27
	 * @return
	 */
	@RequestMapping(value = "getProductData", produces = "application/json;charset=UTF-8", method = {
			RequestMethod.GET })
	@ResponseBody
	@ApiOperation(value = "获取产品", httpMethod = "GET", notes = "获取产品", consumes = "application/x-www-form-urlencoded")
	public String getProductData(HttpServletRequest request) {
		String token = request.getHeader("X-Token");
		String corpCode = "";
		if (StringUtils.isBlank(token)) {
			return JsonMapper.nonDefaultMapper().toJson(
					ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
		if (token != null) {
			User user = apiUserService.getUserByToken(token);
			// 超级管理默认查询所有企业，其他默认查询本企业产品
			if (!user.isAdmin()) {
				corpCode = user.getCompany().getId();
			}
		}
		List<TraceProduct> list = traceProductService.findAllProducts(corpCode);
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

	/**
	 * 获取新增批次初始化数据 add by ligm 2018-09-29
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "getInforForBatchAddPre", produces = "application/json;charset=UTF-8", method = {
			RequestMethod.GET })
	@ResponseBody
	@ApiOperation(value = "根据产品ID获取新增批次初始化数据", httpMethod = "GET", notes = "根据产品ID获取新增批次初始化数据，包括产品、主题、模块、属性", consumes = "application/x-www-form-urlencoded")
	public String getInforForBatchAddPre(HttpServletRequest request, @RequestParam String id) {
		if (StringUtils.isNotBlank(id)) {
			// 新增批次取最新批次数据
			int pageNo = 1;
			int pageSize = 10;
			Page<ProductBatch> page = new Page<ProductBatch>(pageNo, pageSize);
			page = productBatchService.find(page, null, null, null, null, id);
			if (page != null && page.getList() != null && page.getList().size() > 0) {
				return getBatchInformationForNew(request, page.getList().get(0).getId());
			}

			TraceProduct traceProduct = traceProductService.get(id);
			String imgUrl = Global.getConfig("sy_img_url");
			// 都采用相对地址
			/*
			 * if(StringUtils.isNotBlank(traceProduct.getProductPic())){
			 * traceProduct.setProductPicUrl(imgUrl+traceProduct.getProductPic()); }else{
			 * traceProduct.setProductPicUrl(""); }
			 */
			Office office = officeService.get(traceProduct.getOfficeId());
			traceProduct.setCorpName(office.getName());

			// 主题名称
			TraceResumeTheme traceResumeTheme = traceResumeThemeService.get(traceProduct.getThemeId());
			traceProduct.setThemeName(traceResumeTheme.getThemeName());
			traceProduct.setThemeId(traceResumeTheme.getId());
			traceProduct.setTheme(traceResumeTheme);

			// 模块
			List<TraceModel> traceModelList = new ArrayList<TraceModel>();
			if (StringUtils.isNotBlank(traceResumeTheme.getTemplet_product_id())) {
				traceModelList = traceModelService.findTraceModelsByProductId(traceResumeTheme.getTemplet_product_id());
			} else {
				traceModelList = traceModelService.findTraceModelsByProductId(traceProduct.getId());
			}

			for (Iterator<TraceModel> iterator = traceModelList.iterator(); iterator.hasNext();) {
				TraceModel traceModel = (TraceModel) iterator.next();
				// 获取模块对应属性
				List<TraceProperty> propertyList = tracePropertyService.findPropertyListByModelId(traceModel.getId());
				traceModel.setTracePropertyList(propertyList);
				traceModel.setStates1(traceModel.getStates());
			}
			traceProduct.setTraceModelList(traceModelList);

			// 作为初始化界面表单的提示语
			ProductBatch batch = new ProductBatch();
			batch.setBatchCode("保存批次时系统自动分配");
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(traceProduct));
		} else {
			return JsonMapper.nonDefaultMapper().toJson(
					ResultUtil.error(ResultEnum.DATA_NOT_EXIST.getCode(), ResultEnum.DATA_NOT_EXIST.getMessage()));
		}
	}

	/**
	 * 获取批次所有数据，包括所有模块 以及模块对应的属性和属性值信息 但不包括生长记录属性值数据 此数据量大，需分页 单独查询 add by ligm
	 * 2018-10-15
	 * 
	 * @param batchId
	 * @return
	 */
	@RequestMapping(value = "getBatchInformation", produces = "application/json;charset=UTF-8", method = {
			RequestMethod.GET })
	@ResponseBody
	@ApiOperation(value = "获取批次某个模块数据信息和对应的属性及属性值信息", httpMethod = "GET", notes = "获取某个批次所有模块数据信息和对应的属性及属性值信息，生长记录的属性值数据不包括在内", consumes = "application/x-www-form-urlencoded")
	public String getBatchInformation(HttpServletRequest request, @RequestParam String batchId) {
		if (StringUtils.isNotBlank(batchId)) {
			ProductBatch batch = productBatchService.get(batchId);
			// 把所有数据 均封装至product里，方便手机端操作同样的对象及属性
			TraceProduct traceProduct = traceProductService.get(batch.getProductId());
			traceProduct.setProductBatch(batch);

			List<TraceModel> traceModelList = new ArrayList<TraceModel>();// 模块
			// 批次对应所有模块数据
			List<TraceModelData> traceModelDataList = traceModelService.findTraceModelDatas(null, batchId, "2");
			// 查找生长记录模块
			TraceModelData modelDataGrow = traceModelService.getTraceModelDataGrowByBatchId(batchId);
			for (Iterator<TraceModelData> iterator = traceModelDataList.iterator(); iterator.hasNext();) {
				TraceModelData traceModelData = (TraceModelData) iterator.next();
				TraceModel traceModel = new TraceModel();
				traceModel.setId(traceModelData.getModelId());
				traceModel.setOfficeId(traceModelData.getOfficeId());
				traceModel.setProductId(batch.getProductId());
				traceModel.setModelName(traceModelData.getModelName());
				traceModel.setSort(traceModelData.getSort());
				traceModel.setModelDataId(traceModelData.getId());

				if (null != modelDataGrow) {
					// 生长记录数据 跳过不处理
					if (modelDataGrow.getId() == traceModelData.getId()) {
						traceModel.setModelShowType("2");
						traceModelList.add(traceModel);
						continue;
					}
				}

				List<TraceProperty> tracePropertyList = new ArrayList<TraceProperty>();// 模块对应属性
				// 其他模块数据 读取属性和属性值 封装到属性对象中
				List<TracePropertyData> propertyDataList = tracePropertyService
						.findPropertyDataListByModelDataId(traceModelData.getId());
				for (Iterator<TracePropertyData> iterator2 = propertyDataList.iterator(); iterator2.hasNext();) {
					TracePropertyData tracePropertyData = (TracePropertyData) iterator2.next();
					TraceProperty property = tracePropertyService.get(tracePropertyData.getPropertyId());
					property.setPropertyValue(tracePropertyData.getPropertyValue());
					tracePropertyList.add(property);
				}
				tracePropertyService.ListSortArrayList(tracePropertyList);
				traceModel.setTracePropertyList(tracePropertyList);
				traceModelList.add(traceModel);
			}

			tracePropertyService.ListSortArrayListTraceModel(traceModelList);
			traceProduct.setTraceModelList(traceModelList);
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(traceProduct));
		} else {
			return JsonMapper.nonDefaultMapper().toJson(
					ResultUtil.error(ResultEnum.DATA_NOT_EXIST.getCode(), ResultEnum.DATA_NOT_EXIST.getMessage()));
		}
	}

	/**
	 * 获取批次所有数据以及未添加的模块数据，不包括生长记录属性值数据 add by ligm 2018-10-15
	 * 
	 * @param batchId
	 * @return
	 */
	@RequestMapping(value = "getBatchInformationForNew", produces = "application/json;charset=UTF-8", method = {
			RequestMethod.GET })
	@ResponseBody
	@ApiOperation(value = "获取批次所有数据以及未添加的模块数据，不包括生长记录属性值数据", httpMethod = "GET", notes = "获取批次所有数据以及未添加的模块数据，不包括生长记录属性值数据，为配置新的模块关联做准备", consumes = "application/x-www-form-urlencoded")
	public String getBatchInformationForNew(HttpServletRequest request, @RequestParam String batchId) {
		if (StringUtils.isNotBlank(batchId)) {
			ProductBatch batch = productBatchService.get(batchId);
			// 把所有数据 均封装至product里，方便手机端操作同样的对象及属性
			TraceProduct traceProduct = traceProductService.get(batch.getProductId());
			traceProduct.setProductBatch(batch);
			// 批次对应所有模块数据
			List<TraceModelData> traceModelDataList = traceModelService.findTraceModelDatas(null, batchId, "2");
			if (null != traceModelDataList && 0 != traceModelDataList.size()) {
				// 批次对应产品对应的所有模块 再将modelData封装进去 如果TraceModel的modelDataId有值
				// 说明是此批次已经配置的模块，否则为可新增的模块
				List<TraceModel> traceModelList = traceModelService
						.findProductTraceModelsByOneModelId(traceModelDataList.get(0).getModelId());
				Map<String, TraceModelData> traceModelDataMap = new HashMap<String, TraceModelData>();
				for (Iterator<TraceModelData> iterator = traceModelDataList.iterator(); iterator.hasNext();) {
					TraceModelData traceModelData = (TraceModelData) iterator.next();
					traceModelDataMap.put(traceModelData.getModelId(), traceModelData);
				}
				for (Iterator<TraceModel> iterator = traceModelList.iterator(); iterator.hasNext();) {
					TraceModel traceModel = (TraceModel) iterator.next();
					TraceModelData traceModelData = traceModelDataMap.get(traceModel.getId());
					if (null != traceModelData) {
						traceModel.setSort(traceModelData.getSort());
						traceModel.setModelDataId(traceModelData.getId());
						// 检查是否为生长日记模块 是则不带入属性值 否则 根据propertyDataList填充tracePropertyList
						if (StringUtils.isNotBlank(traceModel.getModelShowType())
								&& "2".equals(traceModel.getModelShowType())) {
							continue;
						}

						List<TracePropertyNew> tracePropertyList = new ArrayList<TracePropertyNew>();// 模块对应属性
						// 其他模块数据 读取属性和属性值 封装到属性对象中
						List<TracePropertyData> propertyDataList = tracePropertyService
								.findPropertyDataListByModelDataId(traceModelData.getId());
						if (propertyDataList.size() > 0) {
							for (Iterator<TracePropertyData> iterator2 = propertyDataList.iterator(); iterator2
									.hasNext();) {
								TracePropertyData tracePropertyData = (TracePropertyData) iterator2.next();
								TracePropertyNew property = tracePropertyNewDao.get(tracePropertyData.getPropertyId());
								if (property != null) {
									property.setPropertyValue(tracePropertyData.getPropertyValue());
									tracePropertyList.add(property);
								}
							}
							tracePropertyService.ListSortArrayList1(tracePropertyList);
							traceModel.setTracePropertyNewList(tracePropertyList);
						} else {
							// 没有配置的模块 获取模块对应的tracePropertyList
							List<TraceProperty> tracePropertyLists = tracePropertyService
									.findPropertyListByModelId(traceModel.getId());// 模块对应属性
							traceModel.setTracePropertyList(tracePropertyLists);
						}
					} else {
						// 没有配置的模块 获取模块对应的tracePropertyList
						List<TraceProperty> tracePropertyList = tracePropertyService
								.findPropertyListByModelId(traceModel.getId());// 模块对应属性
						traceModel.setTracePropertyList(tracePropertyList);
					}
				}
				tracePropertyService.ListSortArrayListTraceModelWithNew(traceModelList);

				for (int i = 0; i < traceModelDataList.size(); i++) {
					TraceModelData traceModelData = traceModelDataList.get(i);
					for (int j = 0; j < traceModelList.size(); j++) {
						TraceModel traceModel = traceModelList.get(j);
						if (traceModelData.getModelId().equals(traceModel.getId())) {
							traceModel.setStates1(traceModelData.getStates());
							break;
						}
					}
				}
				traceProduct.setTraceModelList(traceModelList);
				return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(traceProduct));
			} else {
				return JsonMapper.nonDefaultMapper()
						.toJson(ResultUtil.error(ResultEnum.PRODUCT_MODEL_NOT_EXIST_ERROR.getCode(),
								ResultEnum.PRODUCT_MODEL_NOT_EXIST_ERROR.getMessage()));
			}
		} else {
			return JsonMapper.nonDefaultMapper().toJson(
					ResultUtil.error(ResultEnum.DATA_NOT_EXIST.getCode(), ResultEnum.DATA_NOT_EXIST.getMessage()));
		}
	}

	/**
	 * 根据批次ID获取生长记录模块数据信息和对应的属性信息，用于新增生长记录结构定义 add by ligm 2018-10-16
	 * 
	 * @param batchId
	 * @return
	 */
	@RequestMapping(value = "getGrowModelPropertiesByBatchIdPre", produces = "application/json;charset=UTF-8", method = {
			RequestMethod.GET })
	@ResponseBody
	@ApiOperation(value = "根据批次ID获取生长记录模块数据信息和对应的属性信息，用于新增生长记录结构定义", httpMethod = "GET", notes = "根据批次ID获取生长记录模块数据信息和对应的属性信息，用于新增生长记录结构定义", consumes = "application/x-www-form-urlencoded")
	public String getGrowModelPropertiesByBatchIdPre(HttpServletRequest request, @RequestParam String batchId) {
		if (StringUtils.isNotBlank(batchId)) {
			ProductBatch batch = productBatchService.get(batchId);
			// 把所有数据 均封装至product里，方便手机端操作同样的对象及属性
			TraceProduct traceProduct = traceProductService.get(batch.getProductId());
			traceProduct.setProductBatch(batch);

			List<TraceModel> traceModelList = new ArrayList<TraceModel>();// 模块
			// 查找生长记录模块
			TraceModelData modelDataGrow = traceModelService.getTraceModelDataGrowByBatchId(batchId);
			if (null != modelDataGrow) {
				TraceModel traceModel = new TraceModel();
				traceModel.setId(modelDataGrow.getModelId());
				traceModel.setOfficeId(modelDataGrow.getOfficeId());
				traceModel.setProductId(batch.getProductId());
				traceModel.setModelName(modelDataGrow.getModelName());
				traceModel.setSort(modelDataGrow.getSort());
				traceModel.setModelDataId(modelDataGrow.getId());

				// 模块对应属性
				List<TraceProperty> propertyList = tracePropertyService
						.findPropertyListByModelId(modelDataGrow.getModelId());
				traceModel.setTracePropertyList(propertyList);
				traceModelList.add(traceModel);
			}
			traceProduct.setTraceModelList(traceModelList);
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(traceProduct));
		} else {
			return JsonMapper.nonDefaultMapper().toJson(
					ResultUtil.error(ResultEnum.DATA_NOT_EXIST.getCode(), ResultEnum.DATA_NOT_EXIST.getMessage()));
		}
	}

	/**
	 * 保存及修改批次所有模块、模块属性数据 add by ligm 2018-10-11 测试地址：
	 * http://localhost:8080/sureserve-admin/api/traceModel/saveBatchWithModelsAndProperties
	 * corpCode 企业号 productCode 产品编号
	 * 
	 * @return
	 */
	@RequestMapping(value = "saveBatchWithModelsAndProperties", produces = "text/plain;charset=UTF-8", method = {
			RequestMethod.POST })
	@ResponseBody
	@ApiOperation(value = "保存及修改批次所有模块、模块属性数据", httpMethod = "POST", notes = "保存及修改批次所有模块、模块属性数据,保存前会清除历史数据，但不会影响生长记录数据", consumes = "application/json")
	public String saveBatchWithModelsAndProperties(HttpServletRequest request,
			@RequestBody @ApiParam(name = "溯源产品对象", value = "传入json格式", required = true) TraceProduct traceProduct) {
		String token = request.getHeader("X-Token");
		if (StringUtils.isBlank(token)) {
			return JsonMapper.nonDefaultMapper().toJson(
					ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
		User user = apiUserService.getUserByToken(token);
		ProductBatch batch = null;
		try {
			batch = productBatchService.saveBatchWithModelsAndProperties(traceProduct, user);
		} catch (Exception e) {
			e.printStackTrace();
			return JsonMapper.nonDefaultMapper()
					.toJson(ResultUtil.error(ResultEnum.SYSTEM_ERROR.getCode(), ResultEnum.SYSTEM_ERROR.getMessage()));
		}
		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(batch));
	}

	/**
	 * 根据批次ID获取生长记录模块数据信息 以及生长记录的分页数据 即属性值 add by ligm 2018-10-15
	 * 
	 * @param batchId
	 * @return
	 */
	@RequestMapping(value = "getModelGrowPropertiesInformation", produces = "application/json;charset=UTF-8", method = {
			RequestMethod.GET })
	@ResponseBody
	@ApiOperation(value = "根据批次ID获取生长记录模块数据信息 以及生长记录的分页数据 即属性值", httpMethod = "GET", notes = "根据批次ID获取生长记录模块数据信息 以及生长记录的分页数据 即属性值", consumes = "application/x-www-form-urlencoded")
	public String getModelGrowPropertiesInformation(HttpServletRequest request, 
			@RequestParam String batchId, 
			@RequestParam(required = false) String type, 
			@RequestParam Integer pageno,
			@RequestParam Integer pagesize
			) {
		int pageNo = pageno == null ? Global.DEFAULT_PAGENO : pageno;
		int pageSize = pagesize == null ? Global.DEFAULT_PAGESIZE : pagesize;
		Page<TraceModelDataGroup> page = new Page<TraceModelDataGroup>(pageNo, pageSize);
		if(StringUtils.isBlank(type)) {
			if (StringUtils.isNotBlank(batchId)) {
				ProductBatch batch = productBatchService.get(batchId);
				// 查找生长记录模块
				TraceModelData modelDataGrow = traceModelService.getTraceModelDataGrowByBatchId(batchId);
				if (null != modelDataGrow) {
					// 读取属性和属性值 封装到属性对象中
					page = traceModelService.find(page, modelDataGrow.getId());
					List<TraceModelDataGroup> modelDataGroupList = page.getList();
					List<TraceModelDataGroup> modelDataGroupListAft = new ArrayList<TraceModelDataGroup>();
					for (Iterator<TraceModelDataGroup> iterator = modelDataGroupList.iterator(); iterator.hasNext();) {
						TraceModelDataGroup traceModelDataGroup = (TraceModelDataGroup) iterator.next();

						List<TraceProperty> tracePropertyList = new ArrayList<TraceProperty>();// 模块对应属性 包含值
						List<TracePropertyData> propertyDataList = tracePropertyService.findPagePropertyDataListByModelDataGroupId(traceModelDataGroup.getId());
						for (Iterator<TracePropertyData> iterator2 = propertyDataList.iterator(); iterator2.hasNext();) {
							TracePropertyData tracePropertyData = (TracePropertyData) iterator2.next();
							TraceProperty property = tracePropertyService.get(tracePropertyData.getPropertyId());
							property = tracePropertyService.evict(property);
							if (property != null) {
								property.setPropertyValue(tracePropertyData.getPropertyValue());
								// 可能需要返回删除或修改对应某条生长记录值
								property.setPropertyDataId(tracePropertyData.getId());
								tracePropertyList.add(property);
							}
						}
						if(!batch.getSysId().equals("1") && !batch.getSysId().equals("2")){
							tracePropertyService.ListSortArrayList(tracePropertyList);
						}
						traceModelDataGroup.setTracePropertyList(tracePropertyList);
						modelDataGroupListAft.add(traceModelDataGroup);
					}
					page.setList(modelDataGroupListAft);
				}
				return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(page));
			} else {
				return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.DATA_NOT_EXIST.getCode(), ResultEnum.DATA_NOT_EXIST.getMessage()));
			}
		} else {
			if (StringUtils.isNotBlank(batchId)) {
				ProductBatch batch = productBatchService.get(batchId);
				// 查找生长记录模块
				TraceModelData modelDataGrow = traceModelService.getTraceModelDataGrowByBatchId(batchId);
				if (null != modelDataGrow) {
					// 读取属性和属性值 封装到属性对象中
					page = traceModelService.find(page, modelDataGrow.getId());
					List<TraceModelDataGroup> modelDataGroupList = page.getList();
					List<TraceModelDataGroup> modelDataGroupListAft = new ArrayList<TraceModelDataGroup>();
					for (Iterator<TraceModelDataGroup> iterator = modelDataGroupList.iterator(); iterator.hasNext();) {
						TraceModelDataGroup traceModelDataGroup = (TraceModelDataGroup) iterator.next();
						
						List<TraceProperty> tracePropertyList = new ArrayList<TraceProperty>();// 模块对应属性 包含值
						List<TracePropertyData> propertyDataList = tracePropertyService.findPagePropertyDataListByModelDataGroupIdReqDel(traceModelDataGroup.getId());
						for (Iterator<TracePropertyData> iterator2 = propertyDataList.iterator(); iterator2.hasNext();) {
							TracePropertyData tracePropertyData = (TracePropertyData) iterator2.next();
							TraceProperty property = tracePropertyService.get(tracePropertyData.getPropertyId());
							property = tracePropertyService.evict(property);
							if (property != null) {
								property.setPropertyValue(tracePropertyData.getPropertyValue());
								// 可能需要返回删除或修改对应某条生长记录值
								property.setPropertyDataId(tracePropertyData.getId());
								tracePropertyList.add(property);
							}
						}
						if(!batch.getSysId().equals("1") && !batch.getSysId().equals("2")){
							tracePropertyService.ListSortArrayList(tracePropertyList);
						}
						traceModelDataGroup.setTracePropertyList(tracePropertyList);
						modelDataGroupListAft.add(traceModelDataGroup);
					}
					page.setList(modelDataGroupListAft);
				}
				return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(page));
			} else {
				return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.DATA_NOT_EXIST.getCode(), ResultEnum.DATA_NOT_EXIST.getMessage()));
			}
		}
	}

	/**
	 * 根据批次ID保存生长记录数据 add by ligm 2018-10-15 corpCode 企业号 productCode 产品编号
	 * 
	 * @return
	 */
	@RequestMapping(value = "savePropertyDataWithBatchId", produces = "text/plain;charset=UTF-8", method = {
			RequestMethod.POST })
	@ResponseBody
	@ApiOperation(value = "保存生长记录数据", httpMethod = "POST", notes = "保存生长记录数据,数据封装在TraceModelDataGroup中，存在多条traceProperty", consumes = "application/json")
	public String savePropertyDataWithBatchId(HttpServletRequest request,
			@RequestBody @ApiParam(name = "生长记录数据对象", value = "传入json格式", required = true) TraceModelDataGroup traceModelDataGroup) {
		String token = request.getHeader("X-Token");
		if (StringUtils.isBlank(token)) {
			return JsonMapper.nonDefaultMapper().toJson(
					ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
		User user = apiUserService.getUserByToken(token);
		try {
			traceModelService.savePropertyDataWithTraceModelDataGroup(traceModelDataGroup, user);
		} catch (Exception e) {
			e.printStackTrace();
			return JsonMapper.nonDefaultMapper()
					.toJson(ResultUtil.error(ResultEnum.SYSTEM_ERROR.getCode(), ResultEnum.SYSTEM_ERROR.getMessage()));
		}
		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(traceModelDataGroup));
	}

	/**
	 * 删除模块数据 add by ligm 2018-10-24 modelDataId 模块数据主键
	 * 
	 * @return
	 */
	@RequestMapping(value = "deleteModelData", produces = "application/json;charset=UTF-8", method = {
			RequestMethod.POST })
	@ResponseBody
	@ApiOperation(value = "删除模块数据", httpMethod = "POST", notes = "删除模块数据，包括对应的模块属性数据", consumes = "application/x-www-form-urlencoded")
	public String deleteModelData(HttpServletRequest request, @RequestParam String modelDataId) {
		String token = request.getHeader("X-Token");
		User user = apiUserService.getUserByToken(token);
		String resultFlag = traceModelService.deleteModelDataWithPropertyDatas(modelDataId, user);
		// 返回1 表名存在批次数据 不能删除此产品
		if (StringUtils.isNotBlank(resultFlag) && "1".equals(resultFlag)) {
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.PRODUCT_LABEL_EXIST.getCode(),
					ResultEnum.PRODUCT_LABEL_EXIST.getMessage()));
		}

		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success());
	}

	/**
	 * 删除某组生长日记数据 add by ligm 2018-10-24 groupId 生长日记分组主键
	 * 
	 * @return
	 */
	@RequestMapping(value = "deleteModelData2", produces = "application/json;charset=UTF-8", method = {
			RequestMethod.POST })
	@ResponseBody
	@ApiOperation(value = "删除某组生长日记数据", httpMethod = "POST", notes = "删除删除某组生长日记数据，包括分组数据及property_data数据", consumes = "application/x-www-form-urlencoded")
	public String deletePropertyDataByGroupId(HttpServletRequest request, @RequestParam String groupId) {
		String token = request.getHeader("X-Token");
		User user = apiUserService.getUserByToken(token);
		traceModelService.deletePropertyDataByGroupId(groupId, user);
		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success());
	}

	/**
	 * 获取某组生长日记数据 add by ligm 2018-10-24 groupId 生长日记分组主键
	 * 
	 * @return
	 */
	@RequestMapping(value = "getPropertyDataByGroupId", produces = "application/json;charset=UTF-8", method = {
			RequestMethod.GET })
	@ResponseBody
	@ApiOperation(value = "获取某组生长日记数据", httpMethod = "GET", notes = "获取某组生长日记数据，包括分组数据及property_data数据", consumes = "application/x-www-form-urlencoded")
	public String getPropertyDataByGroupId(HttpServletRequest request, @RequestParam String groupId) {
		String token = request.getHeader("X-Token");
		if (StringUtils.isBlank(token)) {
			return JsonMapper.nonDefaultMapper().toJson(
					ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
		List<TracePropertyData> List = traceModelService.getPropertyDataByGroupId(groupId);
		if (List == null) {
			return JsonMapper.nonDefaultMapper().toJson(
					ResultUtil.error(ResultEnum.DATA_NOT_EXIST.getCode(), ResultEnum.DATA_NOT_EXIST.getMessage()));
		}
		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(List));
	}

	/**
	 * 保存模块排序数据 add by ligm 2018-10-24
	 * 
	 * @return
	 */
	@RequestMapping(value = "saveModelsWithSort", produces = "text/plain;charset=UTF-8", method = {
			RequestMethod.POST })
	@ResponseBody
	@ApiOperation(value = "保存模块排序数据", httpMethod = "POST", notes = "保存模块排序数据", consumes = "application/json")
	public String saveModelsWithSort(HttpServletRequest request,
			@RequestBody @ApiParam(name = "溯源产品对象", value = "传入json格式", required = true) TraceProduct traceProduct) {
		String token = request.getHeader("X-Token");
		if (StringUtils.isBlank(token)) {
			return JsonMapper.nonDefaultMapper().toJson(
					ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
		User user = apiUserService.getUserByToken(token);
		String result = traceModelService.saveModelsWithSort(traceProduct, user);
		if (StringUtils.isNotBlank(result) && "1".equals(result)) {
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.PRODUCT_MODEL_DATA_ERROR.getCode(),
					ResultEnum.PRODUCT_MODEL_DATA_ERROR.getMessage()));
		}
		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success());
	}

	/**
	 * 获取模块(下拉列表)
	 * 
	 * @return
	 */
	@RequestMapping(value = "getModelData", produces = "application/json;charset=UTF-8", method = {
			RequestMethod.POST })
	@ResponseBody
	@ApiOperation(value = "获取模块", httpMethod = "POST", notes = "获取模块", consumes = "application/x-www-form-urlencoded")
	public String getModelData(HttpServletRequest request, @RequestParam(required = false) String officeId) {
		String token = request.getHeader("X-Token");
		if (StringUtils.isBlank(token)) {
			return JsonMapper.nonDefaultMapper().toJson(
					ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
		User user = apiUserService.getUserByToken(token);
		String corpCode = "";
		// 超级管理默认查询所有企业，其他默认查询本企业
		if (!user.isAdmin()) {
			corpCode = user.getCompany().getId();
		}
		if (StringUtils.isNotBlank(officeId)) {
			corpCode = officeId;
		}
		List<TraceModel> list = traceModelService.findModelByOfficeId(corpCode);
		List<Map<String, Object>> mapList = Lists.newArrayList();
		for (int i = 0; i < list.size(); i++) {
			TraceModel m = list.get(i);
			Map<String, Object> map = Maps.newHashMap();
			map.put("id", m.getId());
			// map.put("pId", e.getParent() != null ? e.getParent().getId() : 0);
			map.put("name", m.getModelName());
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

	@RequiresPermissions("tracemodel:traceModel:view")
	@RequestMapping(value = { "list", "" })
	public String list(TraceModel traceModel, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<TraceModel> page = traceModelService.find(new Page<TraceModel>(request, response), traceModel);
		model.addAttribute("page", page);
		return "modules/" + "tracemodel/traceModelList";
	}

	@RequiresPermissions("tracemodel:traceModel:view")
	@RequestMapping(value = "form")
	public String form(TraceModel traceModel, Model model) {
		model.addAttribute("traceModel", traceModel);
		return "modules/" + "tracemodel/traceModelForm";
	}

	@RequiresPermissions("tracemodel:traceModel:view")
	@RequestMapping(value = "information")
	public String information(TraceModel traceModel, Model model) {
		model.addAttribute("traceModel", traceModel);
		return "modules/" + "tracemodel/traceModelInformation";
	}

	@RequiresPermissions("tracemodel:traceModel:edit")
	@RequestMapping(value = "save")
	public String save(TraceModel traceModel, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, traceModel)) {
			return form(traceModel, model);
		}
		traceModelService.save(traceModel);
		addMessage(redirectAttributes, "保存溯源模块配置管理成功");
		return "redirect:" + Global.getAdminPath() + "/tracemodel/traceModel/?repage";
	}

	@RequiresPermissions("tracemodel:traceModel:edit")
	@RequestMapping(value = "delete")
	public String delete(String id, RedirectAttributes redirectAttributes) {
		traceModelService.delete(id);
		addMessage(redirectAttributes, "删除溯源模块配置管理成功");
		return "redirect:" + Global.getAdminPath() + "/tracemodel/traceModel/?repage";
	}

}
