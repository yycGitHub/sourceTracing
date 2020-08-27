package com.surekam.modules.api.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
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
import com.surekam.modules.productbatch.service.ProductBatchService;
import com.surekam.modules.sys.entity.CommonUser;
import com.surekam.modules.sys.entity.User;
import com.surekam.modules.sys.service.OfficeService;
import com.surekam.modules.sys.service.SystemService;
import com.surekam.modules.trace.TraceLableApply.entity.TraceLableApply;
import com.surekam.modules.trace.TraceLableApply.service.TraceLableApplyService;
import com.surekam.modules.trace.TraceLableContent.entity.TraceLableContent;
import com.surekam.modules.trace.TraceLableContent.service.TraceLableContentService;
import com.surekam.modules.trace.TraceLableTemplate.entity.TraceLableTemplate;
import com.surekam.modules.trace.TraceLableTemplate.service.TraceLableTemplateService;
import com.surekam.modules.traceproduct.service.TraceProductService;
import com.surekam.modules.traceproductaudit.entity.SavaAuditReq;
import com.surekam.modules.traceproductaudit.service.TraceProductAuditService;
import com.surekam.modules.traceproductauditnew.entity.TraceProductAuditNew;

/**
 * @author wangyuewen 标签管理
 */

@Api(value="溯源标签申请管理接口Controller", description="溯源标签申请管理的相关数据接口")
@Controller
@RequestMapping(value = "api/traceLableApply")
public class TraceLableApplyController extends BaseController {

	@Autowired
	private ApiUserService apiUserService;

	@Autowired
	private TraceLableTemplateService traceLableTemplateService;

	@Autowired
	private TraceLableApplyService traceLableApplyService;

	@Autowired
	private SystemService systemService;

	@Autowired
	private ProductBatchService productBatchService;

	@Autowired
	private TraceProductService traceProductService;

	@Autowired
	private TraceLableContentService traceLableContentService;

	@Autowired
	private OfficeService officeService;
	
	@Autowired
	private TraceProductAuditService traceProductAuditService;

	/**
	 * 查询标签申请列表 可加批次主键条件查询批次对应的标签列表
	 * 
	 * @author ligm
	 * @param request
	 * @param response
	 * @param pageno
	 * @param pagesize
	 * @return
	 */
	@RequestMapping(value = "getLabelApplyListByBatchId", produces = "application/json;charset=UTF-8", method = {
			RequestMethod.GET })
	@ResponseBody
	@ApiOperation(value = "查询标签申请列表 可加批次主键条件查询批次对应的标签列表", httpMethod = "GET", notes = "查询标签申请列表 可加批次主键条件查询批次对应的标签列表", consumes = "application/x-www-form-urlencoded")
	public String getLabelApplyListByBatchId(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(required = false) String batchName, @RequestParam(required = false) String productName,
			@RequestParam(defaultValue = "") String time, @RequestParam(required = false) String cancelFlag,
			@RequestParam(required = false) String printFlag, @RequestParam(required = false) String auditFlag,
			@RequestParam(required = false) String corpCode, @RequestParam Integer pageno,
			@RequestParam Integer pagesize) {
		response.setContentType("application/json; charset=UTF-8");
		Map<String, Object> map = new HashMap<String, Object>();
		int pageNo = pageno == null ? Global.DEFAULT_PAGENO : pageno;
		int pageSize = pagesize == null ? Global.DEFAULT_PAGESIZE : pagesize;
		Page<TraceLableApply> page = new Page<TraceLableApply>(pageNo, pageSize);
		String token = request.getHeader("X-Token");
		System.gc();
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
			page = traceLableApplyService.find(page, batchName, productName, time, cancelFlag, printFlag, auditFlag, corpCode, findChildrenOffice, admin, user);
			map.put("page", page);
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(map));
		} else {
			return JsonMapper.nonDefaultMapper().toJson(
					ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
	}
	
	/**
	 * 查询标签申请列表 可加批次主键条件查询批次对应的标签列表
	 * 
	 * @author liw
	 * @param request
	 * @param response
	 * @param pageno
	 * @param pagesize
	 * @return
	 */
	@RequestMapping(value = "getLabelApplyListNew", produces = "application/json;charset=UTF-8", method = {RequestMethod.GET })
	@ResponseBody
	@ApiOperation(value = "查询标签申请列表 可加批次主键条件查询批次对应的标签列表", httpMethod = "GET", notes = "查询标签申请列表 可加批次主键条件查询批次对应的标签列表", consumes = "application/x-www-form-urlencoded")
	public String getLabelApplyListNew(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(required = false) String batchCode, @RequestParam(required = false) String productName, 
			@RequestParam(defaultValue = "") String time, @RequestParam(required = false) String printFlag, @RequestParam(required = false) String type,
			@RequestParam(required = false) String corpCode, @RequestParam Integer pageno, @RequestParam Integer pagesize) {
		response.setContentType("application/json; charset=UTF-8");
		Map<String, Object> map = new HashMap<String, Object>();
		int pageNo = pageno == null ? Global.DEFAULT_PAGENO : pageno;
		int pageSize = pagesize == null ? Global.DEFAULT_PAGESIZE : pagesize;
		Page<TraceLableApply> page = new Page<TraceLableApply>(pageNo, pageSize);
		String token = request.getHeader("X-Token");
		System.gc();
		if (token != null) {
			User user = apiUserService.getUserByToken(token);
			String officeId = user.getCompany().getId();
			String parentIds = user.getCompany().getParentIds();
			List<String> findChildrenOffice = new ArrayList<String>();
			if (StringUtils.isBlank(corpCode)) {
				findChildrenOffice = officeService.findChildrenOffice(officeId);
			}
			page = traceLableApplyService.findTraceLableApplyList(page, batchCode, productName, time, printFlag, type, corpCode, findChildrenOffice, user);
			map.put("page", page);
			
			String flag = "0";
			if(user.isAdmin()){
				flag = "1";
			}else{
				if(parentIds.equals("0,1,")){
					flag = "1";
				}
			}
			map.put("flag", flag);
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(map));
		} else {
			return JsonMapper.nonDefaultMapper().toJson(
					ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
	}

	/**
	 * 查询标签申请列表导出数据，不分页
	 * 
	 * @author ligm
	 * @param request
	 * @param response
	 * @param pageno
	 * @param pagesize
	 * @return
	 */
	@RequestMapping(value = "getLabelApplyListExport", produces = "application/json;charset=UTF-8", method = {
			RequestMethod.GET })
	@ResponseBody
	@ApiOperation(value = "查询标签申请列表导出数据，不分页", httpMethod = "GET", notes = "查询标签申请列表导出数据，不分页", consumes = "application/x-www-form-urlencoded")
	public String getLabelApplyListExport(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(required = false) String batchName, @RequestParam(required = false) String productName,
			@RequestParam(defaultValue = "") String time, @RequestParam(required = false) String cancelFlag,
			@RequestParam(required = false) String printFlag, @RequestParam(required = false) String auditFlag,
			@RequestParam(required = false) String corpCode, @RequestParam Integer pageno,
			@RequestParam Integer pagesize) {
		response.setContentType("application/json; charset=UTF-8");
		int pageNo = pageno == null ? Global.DEFAULT_PAGENO : pageno;
		int pageSize = pagesize == null ? Global.DEFAULT_PAGESIZE : pagesize;
		Page<TraceLableApply> page = new Page<TraceLableApply>(pageNo, pageSize);
		List<TraceLableApply> list = new ArrayList<TraceLableApply>();
		String token = request.getHeader("X-Token");
		System.gc();
		if (token != null) {
			User user = apiUserService.getUserByToken(token);
			List<String> findChildrenOffice = new ArrayList<String>();
			if (!StringUtils.isNotBlank(corpCode)) {
				findChildrenOffice = officeService.findChildrenOffice(user.getCompany().getId());
			}
			String admin = "";
			// 超级管理默认查询所有企业，其他默认查询本企业批次
			if (!user.isAdmin()) {
				admin = user.getCompany().getId();
			}
			list = traceLableApplyService.listExport(batchName, productName, time, cancelFlag, printFlag, auditFlag,
					corpCode, findChildrenOffice, admin, user);
			page.setList(list);
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(page));
		} else {
			return JsonMapper.nonDefaultMapper().toJson(
					ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
	}

	/**
	 * 查询标签申请列表 可加批次主键条件查询批次对应的标签列表
	 * 
	 * @author ligm
	 * @param request
	 * @param response
	 * @param pageno
	 * @param pagesize
	 * @return
	 */
	@RequestMapping(value = "getLabelApplyList", produces = "application/json;charset=UTF-8", method = {
			RequestMethod.GET })
	@ResponseBody
	@ApiOperation(value = "查询标签申请列表 可加批次主键条件查询批次对应的标签列表", httpMethod = "GET", notes = "查询标签申请列表 可加批次主键条件查询批次对应的标签列表", consumes = "application/x-www-form-urlencoded")
	public String getLabelApplyList(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(required = false) String batchId, String productId, @RequestParam Integer pageno,
			@RequestParam Integer pagesize) {
		response.setContentType("application/json; charset=UTF-8");
		int pageNo = pageno == null ? Global.DEFAULT_PAGENO : pageno;
		int pageSize = pagesize == null ? Global.DEFAULT_PAGESIZE : pagesize;
		Page<TraceLableApply> page = new Page<TraceLableApply>(pageNo, pageSize);
		String token = request.getHeader("X-Token");
		if (token != null) {
			TraceLableApply label = new TraceLableApply();

			if (StringUtils.isNotBlank(productId)) {
				label.setTraceProductId(productId);
			}
			List<TraceLableApply> list = new ArrayList<TraceLableApply>();
			page = traceLableApplyService.find(page, label);
			for (int i = 0; i < page.getList().size(); i++) {
				TraceLableApply traceLableApply = page.getList().get(i);
				String bId = traceLableApply.getBatchId();
				if (StringUtils.isNotBlank(bId)) {
					traceLableApply.setBatchCode(productBatchService.get(bId).getBatchCode());
				}
				traceLableApply.setBqd(traceLableApplyService.getbqd(traceLableApply.getId()));
				list.add(traceLableApply);
			}
			page.setList(list);
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(page));
		} else {
			return JsonMapper.nonDefaultMapper().toJson(
					ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
	}

	/**
	 * 查询标签申请列表 可加批次主键条件查询批次对应的标签列表
	 * 
	 * @author ligm
	 * @param request
	 * @param response
	 * @param pageno
	 * @param pagesize
	 * @return
	 */
	@RequestMapping(value = "getAllLabelApplyList", produces = "application/json;charset=UTF-8", method = {
			RequestMethod.GET })
	@ResponseBody
	@ApiOperation(value = "查询当前用户的申请标签列表", httpMethod = "GET", notes = "查询当前用户的申请标签列表", consumes = "application/x-www-form-urlencoded")
	public String getAllLabelApplyList(HttpServletRequest request, HttpServletResponse response,
			@RequestParam Integer pageno, @RequestParam Integer pagesize) {
		response.setContentType("application/json; charset=UTF-8");
		int pageNo = pageno == null ? Global.DEFAULT_PAGENO : pageno;
		int pageSize = pagesize == null ? Global.DEFAULT_PAGESIZE : pagesize;
		Page<TraceLableApply> page = new Page<TraceLableApply>(pageNo, pageSize);
		String token = request.getHeader("X-Token");
		if (token != null) {
			CommonUser commonUser = systemService.findByToken(token);
			User user = systemService.getUserByLoginName(commonUser.getLoginName());
			TraceLableApply label = new TraceLableApply();
			label.setCreatUserid(user.getId());
			List<TraceLableApply> list = new ArrayList<TraceLableApply>();
			page = traceLableApplyService.find(page, label);
			for (int i = 0; i < page.getList().size(); i++) {
				TraceLableApply traceLableApply = page.getList().get(i);
				String bId = traceLableApply.getBatchId();
				if (StringUtils.isNotBlank(bId)) {
					traceLableApply.setBatchCode(productBatchService.get(bId).getBatchCode());
				}
				traceLableApply.setBqd(traceLableApplyService.getbqd(traceLableApply.getId()));
				traceLableApply
						.setProductName(traceProductService.get(traceLableApply.getTraceProductId()).getProductName());
				list.add(traceLableApply);
			}
			page.setList(list);
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(page));
		} else {
			return JsonMapper.nonDefaultMapper().toJson(
					ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
	}

	/**
	 * 查询标签申请列表 可加批次主键条件查询批次对应的标签列表
	 * 
	 * @author ligm
	 * @param request
	 * @param response
	 * @param pageno
	 * @param pagesize
	 * @return --2019-01-10 11:20 --xy
	 */
	@RequestMapping(value = "getAllLabelApplyList2", produces = "application/json;charset=UTF-8", method = {
			RequestMethod.GET })
	@ResponseBody
	@ApiOperation(value = "查询当前用户的申请标签列表,PC端不分页", httpMethod = "GET", notes = "查询当前用户的申请标签列表,PC端不分页", consumes = "application/x-www-form-urlencoded")
	public String getAllLabelApplyList2(HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		String token = request.getHeader("X-Token");
		if (token != null) {
			CommonUser commonUser = systemService.findByToken(token);
			User user = systemService.getUserByLoginName(commonUser.getLoginName());
			TraceLableApply label = new TraceLableApply();
			label.setCreatUserid(user.getId());
			List<TraceLableApply> list = new ArrayList<TraceLableApply>();
			list = traceLableApplyService.find(label);
			for (int i = 0; i < list.size(); i++) {
				TraceLableApply traceLableApply = list.get(i);
				String bId = traceLableApply.getBatchId();
				if (StringUtils.isNotBlank(bId)) {
					traceLableApply.setBatchCode(productBatchService.get(bId).getBatchCode());
				}
				traceLableApply.setContentList(traceLableContentService.find(traceLableApply.getId()));
				traceLableApply.setBqd(traceLableApplyService.getbqd(traceLableApply.getId()));
				traceLableApply
						.setProductName(traceProductService.get(traceLableApply.getTraceProductId()).getProductName());
			}
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(list));
		} else {
			return JsonMapper.nonDefaultMapper().toJson(
					ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
	}

	/**
	 * 查询模板列表
	 * 
	 * @author wangyuewen
	 * @param request
	 * @param response
	 * @param pageno
	 * @param pagesize
	 * @return
	 */
	@RequestMapping(value = "labelTemplateList", produces = "application/json;charset=UTF-8", method = {
			RequestMethod.GET })
	@ResponseBody
	@ApiOperation(value = "查询标签模板列表", httpMethod = "GET", notes = "查询标签模板列表", consumes = "application/x-www-form-urlencoded")
	public String labelTemplateList(HttpServletRequest request, HttpServletResponse response,
			@RequestParam Integer pageno, @RequestParam Integer pagesize) {
		response.setContentType("application/json; charset=UTF-8");
		int pageNo = pageno == null ? Global.DEFAULT_PAGENO : pageno;
		int pageSize = pagesize == null ? Global.DEFAULT_PAGESIZE : pagesize;
		Page<TraceLableTemplate> page = new Page<TraceLableTemplate>(pageNo, pageSize);
		String token = request.getHeader("X-Token");
		if (token != null) {
			User user = apiUserService.getUserByToken(token);
			TraceLableTemplate labelTemplate = new TraceLableTemplate();
			labelTemplate.setOfficeId(user.getCompany().getId());
			page = traceLableTemplateService.find(page, labelTemplate);
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(page));
		} else {
			return JsonMapper.nonDefaultMapper().toJson(
					ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
	}

	/**
	 * @author wangyuewen 保存标签申请
	 * @param request
	 * @param traceProduct
	 * @return
	 */
	@RequestMapping(value = "saveLableApply", produces = "text/plain;charset=UTF-8", method = { RequestMethod.POST })
	@ResponseBody
	@ApiOperation(value = "保存标签申请", httpMethod = "POST", notes = "保存标签申请", consumes = "application/json")
	public String saveLableApply(HttpServletRequest request,
			@RequestBody @ApiParam(name = "申请标签对象", value = "传入json格式", required = true) TraceLableApply apply) {
		String token = request.getHeader("X-Token");
		if (StringUtils.isNotBlank(token)) {
			User user = apiUserService.getUserByToken(token);
			apply.setCreatUserid(user.getId());
			try {
				if (StringUtils.isNotBlank(apply.getBatchId())) {
					apply.setFlag("0");
				} else {
					apply.setFlag("1");
				}
				traceLableApplyService.save(apply, user.getCompany().getId(), user);
			} catch (Exception e) {
				return JsonMapper.nonDefaultMapper().toJson(
						ResultUtil.error(ResultEnum.SYSTEM_ERROR.getCode(), ResultEnum.SYSTEM_ERROR.getMessage()));
			}
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(apply));
		} else {
			return JsonMapper.nonDefaultMapper().toJson(
					ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
	}

	/**
	 * @author wangyuewen 根据标签申请id生成溯源码
	 * @param request
	 * @param id
	 * @return
	 */
	/*
	 * @RequestMapping(value =
	 * "generateTraceCode",produces="application/json;charset=UTF-8", method =
	 * {RequestMethod.GET})
	 * 
	 * @ResponseBody
	 * 
	 * @ApiOperation(value = "根据标签申请id生成溯源码", httpMethod = "GET", notes =
	 * "根据标签申请id生成溯源码", consumes="application/x-www-form-urlencoded") public String
	 * generateTraceCode(HttpServletRequest request,
	 * 
	 * @RequestParam(required = true) String id,@RequestParam String batchId) {
	 * String token = request.getHeader("X-Token");
	 * if(StringUtils.isNotBlank(token)){ User user =
	 * apiUserService.getUserByToken(token); TraceLableApply apply = new
	 * TraceLableApply(); if(id != null && !id.equals("")) { apply.setId(id);
	 * apply.setCreatUserid(user.getId()); } apply =
	 * traceLableApplyService.get(apply); if(apply != null && apply.getApplyNum() !=
	 * null) { int num = Integer.valueOf(apply.getApplyNum()); if(num > 10000) {
	 * return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.
	 * APLLY_NUM_TOO_LGRGE.getCode(), ResultEnum.APLLY_NUM_TOO_LGRGE.getMessage()));
	 * } } try { if(apply != null) { if(StringUtils.isNotBlank(batchId)){
	 * apply.setFlag("0"); }else{ apply.setFlag("1"); } traceCodeService.save(apply,
	 * user.getCompany().getId()); } else { return
	 * JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.
	 * BAD_REQ_PARAM.getCode(), ResultEnum.BAD_REQ_PARAM.getMessage())); } } catch
	 * (Exception e) { return
	 * JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.SYSTEM_ERROR
	 * .getCode(), ResultEnum.SYSTEM_ERROR.getMessage())); } return
	 * JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(apply)); } else {
	 * return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.
	 * TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage())); } }
	 */
	/**
	 * 标签申请数据删除，没有已打印标签的数据才能删除 add by ligm 2018-10-31 batchId 批次id
	 * 
	 * @return
	 */
	@RequestMapping(value = "deleteLabelApply", produces = "application/json;charset=UTF-8", method = {
			RequestMethod.POST })
	@ResponseBody
	@ApiOperation(value = "标签申请数据删除，没有已打印标签的数据才能删除", httpMethod = "POST", notes = "标签申请数据删除，没有已打印标签的数据才能删除", consumes = "application/x-www-form-urlencoded")
	public String deleteLabelApply(HttpServletRequest request, @RequestParam String labelApplyId) {
		String token = request.getHeader("X-Token");
		User user = apiUserService.getUserByToken(token);
		String resultFlag = traceLableApplyService.deleteLabelApplyById(labelApplyId, user);
		// 返回1 表明标签已打印 不能删除此数据
		if (StringUtils.isNotBlank(resultFlag) && "1".equals(resultFlag)) {
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.PRODUCT_LABEL_PRINTED.getCode(),
					ResultEnum.PRODUCT_LABEL_PRINTED.getMessage()));
		}

		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success());
	}

	@RequestMapping(value = "printLabelApply", produces = "application/json;charset=UTF-8", method = {
			RequestMethod.POST })
	@ResponseBody
	@ApiOperation(value = "标签打印", httpMethod = "POST", notes = "标签打印", consumes = "application/x-www-form-urlencoded")
	public String printLabelApply(HttpServletRequest request, @RequestParam String labelApplyId) {
		String token = request.getHeader("X-Token");
		User user = apiUserService.getUserByToken(token);
		String resultFlag = traceLableApplyService.printLabelApply(labelApplyId, user);

		if (StringUtils.isNotBlank(resultFlag) && "1".equals(resultFlag)) {
			return JsonMapper.nonDefaultMapper()
					.toJson(ResultUtil.error(ResultEnum.PRODUCT_LABEL_PRINTED.getCode(), "标签审核未通过不能打印"));
		}
		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success());

	}

	@RequestMapping(value = "cancelLabelApply", produces = "application/json;charset=UTF-8", method = {
			RequestMethod.POST })
	@ResponseBody
	@ApiOperation(value = "标签作废，已打印的标签不能作废", httpMethod = "POST", notes = "标签作废，已打印的标签不能作废", consumes = "application/x-www-form-urlencoded")
	public String cancelLabelApply(HttpServletRequest request, @RequestParam String labelApplyId) {
		String token = request.getHeader("X-Token");
		User user = apiUserService.getUserByToken(token);
		String resultFlag = traceLableApplyService.cancelLabelApply(labelApplyId, user);
		if (StringUtils.isNotBlank(resultFlag) && "1".equals(resultFlag)) {
			return JsonMapper.nonDefaultMapper()
					.toJson(ResultUtil.error(ResultEnum.PRODUCT_LABEL_PRINTED.getCode(), "已打印的标签不能作废"));
		}
		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success());

	}

	@RequestMapping(value = "recoveryLabelApply", produces = "application/json;charset=UTF-8", method = {
			RequestMethod.POST })
	@ResponseBody
	@ApiOperation(value = "标签恢复", httpMethod = "POST", notes = "标签恢复", consumes = "application/x-www-form-urlencoded")
	public String recoveryLabelApply(HttpServletRequest request, @RequestParam String labelApplyId) {
		String token = request.getHeader("X-Token");
		User user = apiUserService.getUserByToken(token);
		String resultFlag = traceLableApplyService.recoveryLabelApply(labelApplyId, user);
		if (StringUtils.isNotBlank(resultFlag) && "1".equals(resultFlag)) {
			return JsonMapper.nonDefaultMapper()
					.toJson(ResultUtil.error(ResultEnum.PRODUCT_LABEL_PRINTED.getCode(), "恢复标签失败"));
		}
		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success());

	}
	
	@RequestMapping(value = "sumbitData", produces = "application/json;charset=UTF-8", method = {
			RequestMethod.GET })
	@ResponseBody
	@ApiOperation(value = "标签审核", httpMethod = "GET", notes = "标签审核", consumes = "application/x-www-form-urlencoded")
	public String sumbitData(HttpServletRequest request, @RequestParam String labelApplyId) {
		String token = request.getHeader("X-Token");
		User user = apiUserService.getUserByToken(token);
		String resultFlag = traceLableApplyService.sumbitData(labelApplyId, user);
		if (StringUtils.isNotBlank(resultFlag) && "1".equals(resultFlag)) {
			return JsonMapper.nonDefaultMapper()
					.toJson(ResultUtil.error(ResultEnum.PRODUCT_LABEL_PRINTED.getCode(), "标签审核失败"));
		}
		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success());
	}

	@RequestMapping(value = "auditLabelApply", produces = "application/json;charset=UTF-8", method = {
			RequestMethod.POST })
	@ResponseBody
	@ApiOperation(value = "标签审核", httpMethod = "POST", notes = "标签审核", consumes = "application/x-www-form-urlencoded")
	public String auditLabelApply(HttpServletRequest request, @RequestParam String labelApplyId,
			@RequestParam String auditFlag) {
		String token = request.getHeader("X-Token");
		User user = apiUserService.getUserByToken(token);
		String resultFlag = traceLableApplyService.auditLabelApply(labelApplyId, auditFlag, user);
		if (StringUtils.isNotBlank(resultFlag) && "1".equals(resultFlag)) {
			return JsonMapper.nonDefaultMapper()
					.toJson(ResultUtil.error(ResultEnum.PRODUCT_LABEL_PRINTED.getCode(), "标签审核失败"));
		}
		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success());
	}

	/**
	 * 查询标签申请列表 可加批次主键条件查询批次对应的标签列表
	 * 
	 * @author ligm
	 * @param request
	 * @param response
	 * @param pageno
	 * @param pagesize
	 * @return
	 */
	@RequestMapping(value = "getLabelListByApplyId", produces = "application/json;charset=UTF-8", method = {
			RequestMethod.GET })
	@ResponseBody
	@ApiOperation(value = "查询标签申请列表 可加批次主键条件查询批次对应的标签列表", httpMethod = "GET", notes = "查询标签申请列表 可加批次主键条件查询批次对应的标签列表", consumes = "application/x-www-form-urlencoded")
	public String getLabelListByApplyId(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(required = false) String applyId) {
		response.setContentType("application/json; charset=UTF-8");
		String token = request.getHeader("X-Token");
		if (token != null) {
			// List<TraceLableContent> resultList = new ArrayList<TraceLableContent>();;
			List<TraceLableContent> list = traceLableContentService.find(applyId);
			// for(int i=0;i<list.size();i++){
			// TraceLableContent traceLableContent = list.get(i);
			// traceLableContent.setSfbm(traceLableApplyService.getbqd(traceLableApply.getId()));
			// resultList.add(traceLableContent);
			// }
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(list));
		} else {
			return JsonMapper.nonDefaultMapper().toJson(
					ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
	}
	
	/**
	 * 保存审核记录
	 */
	@RequestMapping(value = "savaAuditInfo", produces = "application/json;charset=UTF-8")
	@ResponseBody
	@ApiOperation(value = "保存审核记录", notes = "保存审核记录", consumes = "application/json")
	public String savaAuditInfo(HttpServletRequest request, 
			@RequestBody @ApiParam(name = "保存审核记录", value = "传入json格式", required = true) SavaAuditReq req) {
		String token = request.getHeader("X-Token");
		if (StringUtils.isBlank(token)) {
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
		User user = apiUserService.getUserByToken(token);
		ResultBean<String> savaAudit = traceLableApplyService.savaAuditInfo(user, req);
		return JsonMapper.nonDefaultMapper().toJson(savaAudit);
	}
	
	@ResponseBody
	@RequestMapping(value = "findByAuditId")
	@ApiOperation(value = "获取审核记录", httpMethod = "POST", notes = "获取审核记录", consumes = "application/x-www-form-urlencoded")
	public String findByAuditIdNew(HttpServletRequest request, @RequestParam String id) {
		String token = request.getHeader("X-Token");
		if (StringUtils.isBlank(token)) {
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
		List<TraceProductAuditNew> findByAuditId = traceProductAuditService.findByAuditIdInfo(id);
		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(findByAuditId));
	}

}
