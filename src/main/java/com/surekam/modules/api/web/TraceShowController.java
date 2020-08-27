package com.surekam.modules.api.web;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.client.ClientProtocolException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.surekam.common.config.Global;
import com.surekam.common.mapper.JsonMapper;
import com.surekam.common.persistence.Page;
import com.surekam.common.utils.DateUtils;
import com.surekam.common.utils.NetworkUtil;
import com.surekam.common.utils.ResultEnum;
import com.surekam.common.utils.ResultUtil;
import com.surekam.common.utils.StringUtils;
import com.surekam.common.web.BaseController;
import com.surekam.modules.api.entity.WXUser;
import com.surekam.modules.api.service.WXUserService;
import com.surekam.modules.api.utils.WXAuthUtil;
import com.surekam.modules.api.utils.tet;
import com.surekam.modules.productbatch.entity.ProductBatch;
import com.surekam.modules.productbatch.service.ProductBatchService;
import com.surekam.modules.sys.service.OfficeService;
import com.surekam.modules.trace.TraceDayInfo.entity.TraceDayInfo;
import com.surekam.modules.trace.TraceDayInfo.service.TraceDayInfoService;
import com.surekam.modules.trace.TraceInfo.entity.TraceInfo;
import com.surekam.modules.trace.TraceInfo.service.TraceInfoService;
import com.surekam.modules.tracecode.entity.TraceCode;
import com.surekam.modules.tracecode.service.TraceCodeService;
import com.surekam.modules.tracecomment.entity.TraceComment;
import com.surekam.modules.tracecomment.service.TraceCommentService;
import com.surekam.modules.tracecommentreply.entity.TraceCommentReply;
import com.surekam.modules.tracecommentreply.service.TraceCommentReplyService;
import com.surekam.modules.traceproduct.entity.MainModelData;
import com.surekam.modules.traceproduct.entity.TracePackageData;
import com.surekam.modules.traceproduct.entity.TraceProduct;
import com.surekam.modules.traceproduct.entity.TraceRootBean;
import com.surekam.modules.traceproduct.service.TraceProductService;
import com.surekam.modules.traceproduct.service.TraceShowService;
import com.surekam.modules.traceresumetheme.entity.TraceResumeTheme;
import com.surekam.modules.traceresumetheme.service.TraceResumeThemeService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import net.sf.json.JSONObject;

/**
 * 溯源展示接口
 * 
 * @author liuyi
 * @version 2018-09-20
 */

@Api(value="溯源手机端展示接口Controller", description="溯源手机端展示的相关数据接口")
@Controller
@RequestMapping(value = "api/traceShow")
public class TraceShowController extends BaseController {

	@Autowired
	private TraceProductService traceProductService;

	@Autowired
	private TraceResumeThemeService traceResumeThemeService;

	@Autowired
	private TraceCodeService traceCodeService;

	@Autowired
	private ProductBatchService productBatchService;

	@Autowired
	private TraceShowService traceShowService;

	@Autowired
	private TraceInfoService traceInfoService;

	@Autowired
	private TraceDayInfoService traceDayInfoService;

	@Autowired
	private OfficeService officeService;

	@Autowired
	private TraceCommentService traceCommentService;

	@Autowired
	private WXUserService wxUserService;

	@Autowired
	private TraceCommentReplyService traceCommentReplyService;

	@ModelAttribute
	public TraceProduct get(@RequestParam(required = false) String id) {
		if (StringUtils.isNotBlank(id)) {
			return traceProductService.get(id);
		} else {
			return new TraceProduct();
		}
	}

	/**
	 * @param request
	 * @param response
	 * @param tCode
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "getTraceShow", produces = "application/json;charset=UTF-8", method = { RequestMethod.GET })
	@ResponseBody
	@ApiOperation(value = "溯源查询", httpMethod = "GET", notes = "溯源查询", consumes = "application/x-www-form-urlencoded")
	public String getTraceShow(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(required = true) String tCode, 
			@RequestParam(required = false) String type) {
		response.setContentType("application/json; charset=UTF-8");
		String officeId = "";
		String productId = "";
		String templateProductId = "";
		String batchId = "";
		String batchCode = "";
		String themeId = "";
		String skinId = "1";
		int count = 0;
		try {
			if (StringUtils.isBlank(type)) {
				// 溯源码
				TraceCode traceCodeInfo = traceCodeService.getEntityByCode(tCode);
				if (traceCodeInfo != null) {
					count = traceCodeInfo.getTraceCount();
					officeId = traceCodeInfo.getOfficeId();
					batchId = traceCodeInfo.getBatchId();
					if (StringUtils.isNotEmpty(batchId)) {
						// 批次
						ProductBatch productBatch = productBatchService.get(batchId);
						if (productBatch != null) {
							productId = productBatch.getProductId();
							batchCode = productBatch.getBatchCode();
							// themeId = productBatch.getThemeId();
						}
						// 溯源产品
						TraceProduct traceProduct = traceProductService.get(productId);
						if (traceProduct != null) {
							themeId = traceProduct.getThemeId();
							if (StringUtils.isBlank(themeId)) {
								themeId = "039db9f681744daf90d1114fc86604de";
							}
							if (StringUtils.isBlank(officeId)) {
								officeId = traceProduct.getOfficeId();
							}
						}
						// 主题
						TraceResumeTheme traceResumeTheme = traceResumeThemeService.get(themeId);
						if (traceResumeTheme != null) {
							templateProductId = traceResumeTheme.getTemplet_product_id();
							skinId = traceResumeTheme.getSkinId();
						}
						// 属性和属性数据
						count = traceInfoService.getCxcs(tCode) + 1;
						TraceRootBean traceRootBean = traceShowService.findTraceData(templateProductId, batchId, count);
						if (traceCodeInfo != null) {
							traceCodeInfo.setTraceCount(count);
							traceCodeService.save(traceCodeInfo);
						}
						TraceProduct product = traceProductService.get(productId);
						if (StringUtils.isNotBlank(officeId)) {
							product.setCorpName(officeService.get(officeId).getName());
						}
						traceRootBean.getMainModelData().setProduct(product);
						traceRootBean.getMainModelData().setIdentityCode(tCode);
						traceRootBean.getMainModelData().setSkinId(skinId);
						traceRootBean.setBatchId(batchId);
						traceRootBean.setBatchCode(batchCode);
						// String str =
						// JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(traceRootBean));

						// 记录扫码信息
						TraceInfo traceInfo = new TraceInfo();
						traceInfo.setTraceCode(tCode);
						traceInfo.setOfficeId(officeId);
						traceInfo.setProductId(productId);
						traceInfo.setBatchId(batchId);
						traceInfo.setDatetime(DateUtils.getDateTime());
						traceInfo.setIp(NetworkUtil.getIpAddress(request));
						traceInfoService.save(traceInfo);
						TraceDayInfo traceDayInfo = new TraceDayInfo();
						traceDayInfo.setOfficeId(officeId);
						traceDayInfo.setProductId(productId);
						traceDayInfo.setBatchId(batchId);
						traceDayInfo.setDate(DateUtils.getDate());
						traceDayInfo.setTraceCount(1);
						traceDayInfoService.updateOrSave(traceDayInfo);
						traceRootBean.setCodeId(traceInfo.getId());
						return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(traceRootBean));
					} else {
						return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.CODE_NULL.getCode(), ResultEnum.CODE_NULL.getMessage()));
					}
				} else {
					return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.CODE_NULL.getCode(), ResultEnum.CODE_NULL.getMessage()));
				}
			} else {
				// 溯源码
				TraceCode traceCodeInfo = traceCodeService.findByCodeAndStates(tCode);
				if (traceCodeInfo != null) {
					count = traceCodeInfo.getTraceCount();
					officeId = traceCodeInfo.getOfficeId();
					batchId = traceCodeInfo.getBatchId();
					if (StringUtils.isNotEmpty(batchId)) {
						// 批次
						ProductBatch productBatch = productBatchService.get(batchId);
						if (productBatch != null) {
							productId = productBatch.getProductId();
							batchCode = productBatch.getBatchCode();
							// themeId = productBatch.getThemeId();
						}
						// 溯源产品
						TraceProduct traceProduct = traceProductService.get(productId);
						if (traceProduct != null) {
							themeId = traceProduct.getThemeId();
							if (StringUtils.isBlank(themeId)) {
								themeId = "039db9f681744daf90d1114fc86604de";
							}
							if (StringUtils.isBlank(officeId)) {
								officeId = traceProduct.getOfficeId();
							}
						}
						// 主题
						TraceResumeTheme traceResumeTheme = traceResumeThemeService.get(themeId);
						if (traceResumeTheme != null) {
							templateProductId = traceResumeTheme.getTemplet_product_id();
							skinId = traceResumeTheme.getSkinId();
						}
						// 属性和属性数据
						count = traceInfoService.getCxcs(tCode) + 1;
						TraceRootBean traceRootBean = traceShowService.findDelTraceData(templateProductId, batchId, count);
						if (traceCodeInfo != null) {
							traceCodeInfo.setTraceCount(count);
							traceCodeService.save(traceCodeInfo);
						}
						TraceProduct product = traceProductService.get(productId);
						if (StringUtils.isNotBlank(officeId)) {
							product.setCorpName(officeService.get(officeId).getName());
						}
						if(traceRootBean.getMainModelData()!=null){
							traceRootBean.getMainModelData().setProduct(product);
							traceRootBean.getMainModelData().setIdentityCode(tCode);
							traceRootBean.getMainModelData().setSkinId(skinId);
						}
						traceRootBean.setBatchId(batchId);
						traceRootBean.setBatchCode(batchCode);
						// 记录扫码信息
						TraceInfo traceInfo = new TraceInfo();
						traceInfo.setTraceCode(tCode);
						traceInfo.setOfficeId(officeId);
						traceInfo.setProductId(productId);
						traceInfo.setBatchId(batchId);
						traceInfo.setDatetime(DateUtils.getDateTime());
						traceInfo.setIp(NetworkUtil.getIpAddress(request));
						traceInfoService.save(traceInfo);
						TraceDayInfo traceDayInfo = new TraceDayInfo();
						traceDayInfo.setOfficeId(officeId);
						traceDayInfo.setProductId(productId);
						traceDayInfo.setBatchId(batchId);
						traceDayInfo.setDate(DateUtils.getDate());
						traceDayInfo.setTraceCount(1);
						traceDayInfoService.updateOrSave(traceDayInfo);
						traceRootBean.setCodeId(traceInfo.getId());
						return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(traceRootBean));
					} else {
						return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.CODE_NULL.getCode(), ResultEnum.CODE_NULL.getMessage()));
					}
				} else {
					return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.CODE_NULL.getCode(), ResultEnum.CODE_NULL.getMessage()));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.SYSTEM_ERROR.getCode(), ResultEnum.SYSTEM_ERROR.getMessage()));
		}
	}

	/**
	 * 扫码信息，包装层级
	 * 
	 * @param request
	 * @param response
	 * @param tCode
	 * @return
	 */
	@RequestMapping(value = "getTraceShow2", produces = "application/json;charset=UTF-8", method = {
			RequestMethod.GET })
	@ResponseBody
	@ApiOperation(value = "溯源查询", httpMethod = "GET", notes = "溯源查询", consumes = "application/x-www-form-urlencoded")
	public String getTraceShow2(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(required = true) String tCode) {
		response.setContentType("application/json; charset=UTF-8");
		String officeId = "";
		String productId = "";
		String templateProductId = "";
		String batchId = "";
		String batchCode = "";
		String themeId = "";
		String skinId = "1";
		int count = 0;
		String packType = "";
		// TODO
		try {
			// 溯源码
			TraceCode traceCodeInfo = traceCodeService.getEntityByCode(tCode);
			if (traceCodeInfo != null) {
				packType = traceCodeInfo.getPackType();
				TracePackageData data = new TracePackageData();
				if (StringUtils.isNotBlank(packType)) {
					if (packType.equals("1")) {// packType==1最小包装类型
						count = traceCodeInfo.getTraceCount();
						officeId = traceCodeInfo.getOfficeId();
						batchId = traceCodeInfo.getBatchId();
						if (StringUtils.isNotEmpty(batchId)) {
							// 批次
							ProductBatch productBatch = productBatchService.get(batchId);
							if (productBatch != null) {
								productId = productBatch.getProductId();
								batchCode = productBatch.getBatchCode();
								// themeId = productBatch.getThemeId();
							}
							// 溯源产品
							TraceProduct traceProduct = traceProductService.get(productId);
							if (traceProduct != null) {
								themeId = traceProduct.getThemeId();
								if (StringUtils.isBlank(themeId)) {
									themeId = "039db9f681744daf90d1114fc86604de";
								}
								if (StringUtils.isBlank(officeId)) {
									officeId = traceProduct.getOfficeId();
								}
							}
							// 主题
							TraceResumeTheme traceResumeTheme = traceResumeThemeService.get(themeId);
							if (traceResumeTheme != null) {
								templateProductId = traceResumeTheme.getTemplet_product_id();
								skinId = traceResumeTheme.getSkinId();
							}
							// 属性和属性数据
							count = traceInfoService.getCxcs(tCode) + 1;
							TraceRootBean traceRootBean = traceShowService.findTraceData(templateProductId, batchId,
									count);
							if (traceCodeInfo != null) {
								traceCodeInfo.setTraceCount(count);
								traceCodeService.save(traceCodeInfo);
							}
							TraceProduct product = traceProductService.get(productId);
							if (StringUtils.isNotBlank(officeId)) {
								product.setCorpName(officeService.get(officeId).getName());
							}
							traceRootBean.getMainModelData().setProduct(product);
							traceRootBean.getMainModelData().setIdentityCode(tCode);
							traceRootBean.getMainModelData().setSkinId(skinId);
							traceRootBean.setBatchId(batchId);
							traceRootBean.setBatchCode(batchCode);
							// JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(traceRootBean));
							// 记录扫码信息
							TraceInfo traceInfo = new TraceInfo();
							traceInfo.setTraceCode(tCode);
							traceInfo.setOfficeId(officeId);
							traceInfo.setProductId(productId);
							traceInfo.setBatchId(batchId);
							traceInfo.setDatetime(DateUtils.getDateTime());
							traceInfo.setIp(NetworkUtil.getIpAddress(request));
							traceInfoService.save(traceInfo);
							TraceDayInfo traceDayInfo = new TraceDayInfo();
							traceDayInfo.setOfficeId(officeId);
							traceDayInfo.setProductId(productId);
							traceDayInfo.setBatchId(batchId);
							traceDayInfo.setDate(DateUtils.getDate());
							traceDayInfo.setTraceCount(1);
							traceDayInfoService.updateOrSave(traceDayInfo);
							traceRootBean.setCodeId(traceInfo.getId());
							data.setTraceRootBean(traceRootBean);
							data.setPackType(traceCodeInfo.getPackType());
							return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(data));
						} else {
							return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.CODE_NULL.getCode(),
									ResultEnum.CODE_NULL.getMessage()));
						}
					} else {// 非最小包装类型
						List<Integer> numList = new ArrayList<Integer>();
						List<TraceCode> list = traceCodeService.getTraceCodeList(traceCodeInfo.getId());
						data.setPackType(traceCodeInfo.getPackType());
						data.setPackTypeName(traceCodeInfo.getPackTypeName());
						data.setTraceCodeNumber(tCode);
						data.setQueryCount("10");
						if (list != null) {
							officeId = traceCodeInfo.getOfficeId();
							batchId = traceCodeInfo.getBatchId();
							if (StringUtils.isNotBlank(officeId)) {
								data.setOfficeId(officeId);
								data.setOfficeName(officeService.get(officeId).getName());
							}
							if (StringUtils.isNotEmpty(batchId)) {
								data.setBatchId(batchId);
								ProductBatch productBatch = productBatchService.get(batchId);
								if (productBatch != null) {
									data.setProductId(productBatch.getProductId());
									data.setBatchCode(productBatch.getBatchCode());
									// 溯源产品
									TraceProduct traceProduct = traceProductService.get(productBatch.getProductId());
									if (traceProduct != null) {
										themeId = traceProduct.getThemeId();
										if (StringUtils.isBlank(themeId)) {
											themeId = "039db9f681744daf90d1114fc86604de";
										}
									}
									TraceResumeTheme traceResumeTheme = traceResumeThemeService.get(themeId);
									if (traceResumeTheme != null) {
										templateProductId = traceResumeTheme.getTemplet_product_id();
									}
									data.setProductName(traceProduct.getProductName());
									count = traceInfoService.getCxcs(tCode) + 1;
									TraceRootBean traceRootBean = traceShowService.findTraceData(templateProductId,
											batchId, count);
									if (traceRootBean != null) {
										data.setProductDate(traceRootBean.getMainModelData().getDateOfManufacture());
										data.setShelfLife(traceRootBean.getMainModelData().getQualityGuaranteePeriod());
										Integer k = tet.extractionOfNumbers(
												traceRootBean.getMainModelData().getQualityGuaranteePeriod());
										numList.add(k);
									}
								}
							}
							List<TracePackageData> list2 = new ArrayList<TracePackageData>();
							for (Iterator<TraceCode> iterator = list.iterator(); iterator.hasNext();) {
								TraceCode traceCode = (TraceCode) iterator.next();
								TracePackageData data2 = new TracePackageData();
								data2.setPackType(traceCode.getPackType());
								data2.setPackTypeName(traceCode.getPackTypeName());
								data2.setTraceCodeNumber(traceCode.getTraceCode());
								String url = Global.getConfig("sy_img_url");
								data2.setPhoto(url + "upload/farming/images/20190509170025381.png");

								if (StringUtils.isNotEmpty(batchId)) {
									data2.setBatchId(batchId);
									ProductBatch productBatch = productBatchService.get(batchId);
									if (productBatch != null) {
										data2.setProductId(productBatch.getProductId());
										data2.setBatchCode(productBatch.getBatchCode());

									}
									// 溯源产品
									TraceProduct traceProduct = traceProductService.get(productBatch.getProductId());
									if (traceProduct != null) {
										themeId = traceProduct.getThemeId();
										if (StringUtils.isBlank(themeId)) {
											themeId = "039db9f681744daf90d1114fc86604de";
										}
									}
									TraceResumeTheme traceResumeTheme = traceResumeThemeService.get(themeId);
									if (traceResumeTheme != null) {
										templateProductId = traceResumeTheme.getTemplet_product_id();
									}
									data2.setProductName(traceProduct.getProductName());
									count = traceInfoService.getCxcs(tCode) + 1;
									TraceRootBean traceRootBean = traceShowService.findTraceData(templateProductId,
											batchId, count);
									if (traceRootBean != null) {
										data2.setProductDate(traceRootBean.getMainModelData().getDateOfManufacture());
										data2.setShelfLife(
												traceRootBean.getMainModelData().getQualityGuaranteePeriod());
										Integer k = tet.extractionOfNumbers(
												traceRootBean.getMainModelData().getQualityGuaranteePeriod());
										numList.add(k);
									}

								}
								list2.add(data2);
							}
							data.setList(list2);
						}
						Collections.sort(numList);
						Integer min = numList.get(0);
						Integer max = numList.get(numList.size() - 1);
						String d = "";
						if (min < max) {
							d = min.toString() + "-" + max.toString() + "天";
						} else if (min == max) {
							d = max.toString() + "天";
						}
						data.setShelfLife(d);
					}
				}
				return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(data));
			} else {
				return JsonMapper.nonDefaultMapper()
						.toJson(ResultUtil.error(ResultEnum.CODE_NULL.getCode(), ResultEnum.CODE_NULL.getMessage()));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return JsonMapper.nonDefaultMapper()
					.toJson(ResultUtil.error(ResultEnum.SYSTEM_ERROR.getCode(), ResultEnum.SYSTEM_ERROR.getMessage()));
		}

	}

	/**
	 * @param request
	 * @param response
	 * @param tCode
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "getTraceShowPreview", produces = "application/json;charset=UTF-8", method = { RequestMethod.GET })
	@ResponseBody
	@ApiOperation(value = "溯源预览", httpMethod = "GET", notes = "溯源预览", consumes = "application/x-www-form-urlencoded")
	public String getTraceShowPreview(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(required = true) String batchId, @RequestParam(required = false) String type)
			throws Exception {
		response.setContentType("application/json; charset=UTF-8");
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

		if (StringUtils.isBlank(type)) {
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
			TraceRootBean traceRootBean = traceShowService.findTraceData(productId, batchId, count);
			if (traceCode != null) {
				traceCode.setTraceCount(count);
				traceCodeService.save(traceCode);
			}
			traceRootBean.setAuditStatus(productBatch.getAuditStatus());
			TraceProduct product = traceProductService.get(productId);
			product.setCorpName(officeService.get(officeId).getName());
			traceRootBean.getMainModelData().setProduct(product);
			if (traceCode != null) {
				traceRootBean.getMainModelData().setIdentityCode(traceCode.getTraceCode());
			} else {
				traceRootBean.getMainModelData().setIdentityCode("*********");
			}
			traceRootBean.getMainModelData().setSkinId(skinId);
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(traceRootBean));
		} else {
			TraceCode traceCode = traceCodeService.getDelFirstTraceCode(batchId);
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
			TraceRootBean traceRootBean = traceShowService.findDelTraceData(templetProductId, batchId, count);
			if (traceCode != null) {
				traceCode.setTraceCount(count);
				traceCodeService.save(traceCode);
			}
			traceRootBean.setAuditStatus(productBatch.getAuditStatus());
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
	}

	/**
	 * @param request
	 * @param response
	 * @param tCode
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "getTraceShowPreview1", produces = "application/json;charset=UTF-8", method = {
			RequestMethod.GET })
	@ResponseBody
	@ApiOperation(value = "溯源预览", httpMethod = "GET", notes = "溯源预览", consumes = "application/x-www-form-urlencoded")
	public String getTraceShowPreview1(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(required = true) String productId) throws Exception {
		response.setContentType("application/json; charset=UTF-8");
		// 溯源产品
		ProductBatch productBatch = productBatchService.getProductBatchByProductId(productId);
		// 属性和属性数据
		TraceRootBean traceRootBean = new TraceRootBean();
		if (productBatch != null) {
			traceRootBean = traceShowService.findTraceData(productId, productBatch.getId(), 1);
			TraceProduct product = traceProductService.get(productId);
			if (traceRootBean != null && product != null) {
				if (traceRootBean.getMainModelData() != null) {
					traceRootBean.getMainModelData().setProduct(product);
				} else {
					traceRootBean.setMainModelData(new MainModelData());
					traceRootBean.getMainModelData().setProduct(product);
				}
			}
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(traceRootBean));
		} else {
			String productId2 = Global.getConfig("productId");
			ProductBatch productBatch2 = productBatchService.getProductBatchByProductId(productId2);
			traceRootBean = traceShowService.findTraceData(productId2, productBatch2.getId(), 1);
			TraceProduct product = traceProductService.get(productId2);
			if (traceRootBean != null && product != null) {
				if (traceRootBean.getMainModelData() != null) {
					traceRootBean.getMainModelData().setProduct(product);
				} else {
					traceRootBean.setMainModelData(new MainModelData());
					traceRootBean.getMainModelData().setProduct(product);
				}
			}
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(traceRootBean));
		}
	}

	/**
	 * @author wangyuewen
	 * @param request
	 * @param response
	 * @param batchId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "getTraceBatch", produces = "application/json;charset=UTF-8", method = {
			RequestMethod.GET })
	@ResponseBody
	@ApiOperation(value = "根据批次id查询批次信息", httpMethod = "GET", notes = "根据批次id查询批次信息", consumes = "application/x-www-form-urlencoded")
	public String getTraceBatch(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(required = true) String batchId) throws Exception {
		response.setContentType("application/json; charset=UTF-8");
		String productId = "";
		String themeId = "";
		ProductBatch productBatch = productBatchService.get(batchId);
		if (productBatch != null) {
			productId = productBatch.getProductId();
		}
		// 属性和属性数据
		TraceRootBean traceRootBean = traceShowService.findTraceData(productId, batchId, 0);
		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(traceRootBean));
	}

	/**
	 * 评论查询 分页 add by huangrd 2019-01-08
	 * 
	 * @param corpCode
	 *            企业编号
	 * @param pageno
	 *            第几页
	 * @param pagesize
	 *            每页条数 默认为系统配置的值 测试地址：
	 *            http://localhost:8080/sureserve-admin/api/traceComment/commentList?corpCode=1&pageno=1&pagesize=20
	 * @return
	 */
	@RequestMapping(value = "commentList", produces = "application/json;charset=UTF-8", method = { RequestMethod.GET })
	@ResponseBody
	@ApiOperation(value = "评论列表查询", httpMethod = "GET", notes = "评论列表查询", consumes = "application/x-www-form-urlencoded")
	public String commentList(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(required = false) String productId, @RequestParam(required = false) String openId,
			@RequestParam Integer pageno, @RequestParam Integer pagesize) {
		response.setContentType("application/json; charset=UTF-8");
		int pageNo = pageno == null ? Global.DEFAULT_PAGENO : pageno;
		int pageSize = pagesize == null ? Global.DEFAULT_PAGESIZE : pagesize;
		Page<TraceComment> page = new Page<TraceComment>(pageNo, pageSize);
		page = traceCommentService.find(page, productId, openId);

		List<TraceComment> list = new ArrayList<TraceComment>();
		int pageNo2 = 1;
		int pageSize2 = 10;
		for (int i = 0; i < page.getList().size(); i++) {
			TraceComment comment = page.getList().get(i);
			Page<TraceCommentReply> page2 = new Page<TraceCommentReply>(pageNo2, pageSize2);
			TraceCommentReply traceCommentReply = new TraceCommentReply();
			page2 = traceCommentReplyService.find(page2, comment.getId());
			List<TraceCommentReply> list2 = page2.getList();
			if (list2.size() > 0) {
				traceCommentReply = list2.get(0);
				comment.setReplyContent(traceCommentReply.getReplyContent());
				comment.setReplyCreatTime(traceCommentReply.getCreatTime());
			}
			String imgUrl = Global.getConfig("sy_img_url");
			String commentPic = comment.getCommentPic();
			List<String> commentPicUrlList = new ArrayList<String>();
			String commentPics[] = commentPic.split(",");
			for (int j = 0; j < commentPics.length; j++) {
				String commentPicImgUrl = commentPics[j];
				if (StringUtils.isNotBlank(commentPicImgUrl)) {
					commentPicUrlList.add(imgUrl + commentPicImgUrl);
				} else {
					commentPicUrlList.add("");
				}
				comment.setCommentPicUrlList(commentPicUrlList);
			}

			list.add(comment);
		}
		page.setList(list);
		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(page));
	}

	/**
	 * 评论查询 分页 add by huangrd 2019-01-08
	 * 
	 * @param corpCode
	 *            企业编号
	 * @param pageno
	 *            第几页
	 * @param pagesize
	 *            每页条数 默认为系统配置的值 测试地址：
	 *            http://localhost:8080/sureserve-admin/api/traceComment/commentList?corpCode=1&pageno=1&pagesize=20
	 * @return
	 */
	@RequestMapping(value = "list", produces = "application/json;charset=UTF-8", method = { RequestMethod.GET })
	@ResponseBody
	@ApiOperation(value = "溯源预览评论列表查询", httpMethod = "GET", notes = "溯源预览评论列表查询", consumes = "application/x-www-form-urlencoded")
	public String list(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(required = false) String productId, @RequestParam Integer pageno,
			@RequestParam Integer pagesize) {
		response.setContentType("application/json; charset=UTF-8");
		int pageNo = pageno == null ? Global.DEFAULT_PAGENO : pageno;
		int pageSize = pagesize == null ? Global.DEFAULT_PAGESIZE : pagesize;
		Page<TraceComment> page = new Page<TraceComment>(pageNo, pageSize);
		page = traceCommentService.find0(page, productId);

		List<TraceComment> list = new ArrayList<TraceComment>();
		int pageNo2 = 1;
		int pageSize2 = 10;
		for (int i = 0; i < page.getList().size(); i++) {
			TraceComment comment = page.getList().get(i);
			Page<TraceCommentReply> page2 = new Page<TraceCommentReply>(pageNo2, pageSize2);
			TraceCommentReply traceCommentReply = new TraceCommentReply();
			page2 = traceCommentReplyService.find(page2, comment.getId());
			List<TraceCommentReply> list2 = page2.getList();
			if (list2.size() > 0) {
				traceCommentReply = list2.get(0);
				comment.setReplyContent(traceCommentReply.getReplyContent());
				comment.setReplyCreatTime(traceCommentReply.getCreatTime());
			}
			String imgUrl = Global.getConfig("sy_img_url");
			String commentPic = comment.getCommentPic();
			List<String> commentPicUrlList = new ArrayList<String>();
			String commentPics[] = commentPic.split(",");
			for (int j = 0; j < commentPics.length; j++) {
				String commentPicImgUrl = commentPics[j];
				if (StringUtils.isNotBlank(commentPicImgUrl)) {
					commentPicUrlList.add(imgUrl + commentPicImgUrl);
				} else {
					commentPicUrlList.add("");
				}
				comment.setCommentPicUrlList(commentPicUrlList);
			}

			list.add(comment);
		}
		page.setList(list);
		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(page));
	}

	/**
	 * 评论新增 add by huangrd 2019-01-08 测试地址：
	 * http://localhost:8080/sureserve-admin/api/traceShow/addSave corpCode 企业号
	 * productID 产品编号 productName 产品名称 productPic 图片路径
	 * 
	 * @return
	 */
	@RequestMapping(value = "addSave", produces = "text/plain;charset=UTF-8", method = { RequestMethod.POST })
	@ResponseBody
	@ApiOperation(value = "评论新增保存", httpMethod = "POST", notes = "评论新增保存", consumes = "application/json")
	public String addSave(HttpServletRequest request,
			@RequestBody @ApiParam(name = "评论对象", value = "传入json格式", required = true) TraceComment traceComment) {
		traceComment.setAuditFlag("1");
		traceComment.setContentId("");
		traceComment.setStates("A");
		try {
			if (StringUtils.isBlank(traceComment.getNickname()) || traceComment.getNickname().equals("undefined")) {
				traceComment.setNickname("匿名用户");
			} else {
				String filterEmoji = tet.filterEmoji(traceComment.getNickname());
				traceComment.setNickname(filterEmoji);
			}
			if (StringUtils.isBlank(traceComment.getHeadImg()) || traceComment.getHeadImg().equals("undefined")) {
				String imgUrl = Global.getConfig("sy_img_url");
				traceComment.setHeadImg(imgUrl + "/upload/logoyx.png");
			}
			if (StringUtils.isBlank(traceComment.getOpenId()) || traceComment.getOpenId().equals("undefined")) {
				traceComment.setOpenId("");
			}
			traceCommentService.save(traceComment);
		} catch (Exception e) {
			e.printStackTrace();
			return JsonMapper.nonDefaultMapper()
					.toJson(ResultUtil.error(ResultEnum.SYSTEM_ERROR.getCode(), ResultEnum.SYSTEM_ERROR.getMessage()));
		}

		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(traceComment));

	}

	/**
	 * 获取微信用户信息
	 * 
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	@RequestMapping(value = "getWxUserInfo", produces = "application/json;charset=UTF-8", method = {
			RequestMethod.GET })
	@ResponseBody
	@ApiOperation(value = "获取微信用户信息", httpMethod = "GET", notes = "获取微信用户信息", consumes = "application/x-www-form-urlencoded")
	public String getWxUserInfo(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(required = true) String wxcode) throws ClientProtocolException, IOException {
		response.setContentType("application/json; charset=UTF-8");
		// 第二步：通过code换取网页授权access_token
		String APPID = Global.getConfig("appID");
		String APPSECRET = Global.getConfig("appSecret");
		String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + APPID + "&secret=" + APPSECRET
				+ "&code=" + wxcode + "&grant_type=authorization_code";
		System.out.println("授权地址-----------------------------" + url);
		JSONObject jsonObject = WXAuthUtil.doGetJson(url);
		String openid = jsonObject.getString("openid");
		System.out.println("用户信息1----------------------------" + openid);
		String access_token = jsonObject.getString("access_token");
		String refresh_token = jsonObject.getString("refresh_token");
		System.out.println("用户信息2----------------------------" + access_token);
		// 第五步验证access_token是否失效；展示都不需要
		String chickUrl = "https://api.weixin.qq.com/sns/auth?access_token=" + access_token + "&openid=" + openid;

		JSONObject chickuserInfo = WXAuthUtil.doGetJson(chickUrl);
		if (!"0".equals(chickuserInfo.getString("errcode"))) {
			// 第三步：刷新access_token（如果需要）-----暂时没有使用,参考文档https://mp.weixin.qq.com/wiki，
			String refreshTokenUrl = "https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=" + APPID
					+ "&grant_type=refresh_token&refresh_token=" + refresh_token;
			JSONObject refreshInfo = WXAuthUtil.doGetJson(refreshTokenUrl);
			access_token = refreshInfo.getString("access_token");
		}

		// 第四步：拉取用户信息(需scope为 snsapi_userinfo)
		String infoUrl = "https://api.weixin.qq.com/sns/userinfo?access_token=" + access_token + "&openid=" + openid
				+ "&lang=zh_CN";
		JSONObject userInfo = WXAuthUtil.doGetJson(infoUrl);
		WXUser wxuser = new WXUser();
		if (userInfo.has("openid")) {
			wxuser.setOpenId(userInfo.getString("openid"));
		}
		if (userInfo.has("nickname")) {
			if (StringUtils.isBlank(userInfo.getString("nickname"))
					|| userInfo.getString("nickname").equals("undefined")) {
				wxuser.setNickname("匿名用户");
			} else {
				String filterEmoji = tet.filterEmoji(userInfo.getString("nickname"));
				wxuser.setNickname(filterEmoji);
			}
		}
		if (userInfo.has("sex")) {
			wxuser.setSex(userInfo.getInt("sex"));
		}
		if (userInfo.has("province")) {
			wxuser.setProvince(userInfo.getString("province"));
		}
		if (userInfo.has("city")) {
			wxuser.setCity(userInfo.getString("city"));
		}
		if (userInfo.has("country")) {
			wxuser.setCountry(userInfo.getString("country"));
		}
		if (userInfo.has("headimgurl")) {
			if (StringUtils.isBlank(userInfo.getString("headimgurl"))
					|| userInfo.getString("headimgurl").equals("undefined")) {
				String imgUrl = Global.getConfig("sy_img_url");
				wxuser.setHeadImgUrl(imgUrl + "/upload/logoyx.png");
			}
			wxuser.setHeadImgUrl(userInfo.getString("headimgurl"));
		}
		if (userInfo.has("unionid")) {
			wxuser.setUnionId(userInfo.getString("unionid"));
		}
		try {
			if (!wxUserService.find(userInfo.getString("openid"))) {
				wxUserService.save(wxuser);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(wxuser));
	}

	/**
	 * 保存用户定位位置经纬度 add by huangrd 2019-02-20 测试地址：
	 * http://localhost:8080/sureserve-admin/api/traceShow/saveLocation openId
	 * 用户唯一标识号 latitude 纬度 longitude 经度
	 * 
	 * @return
	 */
	@RequestMapping(value = "saveLocation", produces = "text/plain;charset=UTF-8", method = { RequestMethod.GET })
	@ResponseBody
	@ApiOperation(value = "用户位置新增保存", httpMethod = "GET", notes = "用户位置新增保存", consumes = "application/json")
	public String saveLocation(HttpServletRequest request, @RequestParam(required = false) String id,
			@RequestParam(required = false) String latitude, @RequestParam(required = false) String longitude,
			@RequestParam(required = false) String openId) {
		if (StringUtils.isNotBlank(longitude) && StringUtils.isNotBlank(latitude)) {
			TraceInfo traceInfo = traceInfoService.get(id);
			BigDecimal lon = new BigDecimal(longitude).setScale(6, BigDecimal.ROUND_DOWN);
			BigDecimal lat = new BigDecimal(latitude).setScale(6, BigDecimal.ROUND_DOWN);
			traceInfo.setLongitude(lon.toString());
			traceInfo.setLatitude(lat.toString());
			traceInfo.setOpenId(openId);
			try {
				Map<String, Object> map = traceShowService.getAddress(lon.toString(), lat.toString());
				traceInfo.setProvince((String) map.get("province"));
				traceInfo.setCity((String) map.get("city"));
				traceInfo.setArea((String) map.get("area"));
				traceInfo.setAddress((String) map.get("address"));
				traceInfoService.save(traceInfo);
			} catch (Exception e) {
				e.printStackTrace();
				return JsonMapper.nonDefaultMapper().toJson(
						ResultUtil.error(ResultEnum.SYSTEM_ERROR.getCode(), ResultEnum.SYSTEM_ERROR.getMessage()));
			}
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(traceInfo));
		} else {
			return JsonMapper.nonDefaultMapper().toJson(
					ResultUtil.error(ResultEnum.OPERATION_FAILED.getCode(), ResultEnum.OPERATION_FAILED.getMessage()));
		}
	}

	/**
	 * @param request
	 * @param response
	 * @param tCode
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "getCode", produces = "application/json;charset=UTF-8", method = {
			RequestMethod.GET })
	@ResponseBody
	@ApiOperation(value = "获取溯源身份编码", httpMethod = "GET", notes = "获取溯源身份编码", consumes = "application/x-www-form-urlencoded")
	public String getCode(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(required = true) String batchId) throws Exception {
		TraceCode traceCode = traceCodeService.getDelFirstTraceCode(batchId);
		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(traceCode));
	}
}
