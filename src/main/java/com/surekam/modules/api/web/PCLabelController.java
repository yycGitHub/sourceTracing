package com.surekam.modules.api.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.surekam.common.mapper.JsonMapper;
import com.surekam.common.persistence.Page;
import com.surekam.common.utils.ResultEnum;
import com.surekam.common.utils.ResultUtil;
import com.surekam.common.utils.StringUtils;
import com.surekam.common.web.BaseController;
import com.surekam.modules.TraceDeliveryAddress.entity.TraceDeliveryAddress;
import com.surekam.modules.TraceDeliveryAddress.service.TraceDeliveryAddressService;
import com.surekam.modules.api.service.ApiUserService;
import com.surekam.modules.api.utils.ApiUtil;
import com.surekam.modules.productbatch.entity.ProductBatch;
import com.surekam.modules.productbatch.service.ProductBatchService;
import com.surekam.modules.sys.entity.Office;
import com.surekam.modules.sys.entity.User;
import com.surekam.modules.sys.service.OfficeService;
import com.surekam.modules.trace.TraceLableApply.entity.TraceLableApply;
import com.surekam.modules.trace.TraceLableApply.service.TraceLableApplyService;
import com.surekam.modules.trace.TraceLableTemplate.entity.TraceLableTemplate;
import com.surekam.modules.trace.TraceLableTemplate.service.TraceLableTemplateService;
import com.surekam.modules.tracecode.entity.CodeDataVO;
import com.surekam.modules.tracecode.service.TraceCodeService;
import com.surekam.modules.tracelablediscount.entity.DiscountPriceVO;
import com.surekam.modules.tracelablediscount.service.TraceLableDiscountService;
import com.surekam.modules.traceproduct.entity.MainModelData;
import com.surekam.modules.traceproduct.entity.ModelData;
import com.surekam.modules.traceproduct.entity.Rows;
import com.surekam.modules.traceproduct.entity.TraceData;
import com.surekam.modules.traceproduct.entity.TraceProduct;
import com.surekam.modules.traceproduct.entity.TraceRootBean;
import com.surekam.modules.traceproduct.service.TraceProductService;
import com.surekam.modules.traceproduct.service.TraceShowService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Api(value="溯源标签管理接口Controller", description="溯源标签管理的相关数据接口")
@Controller
@RequestMapping(value = "api/pcLabel")
public class PCLabelController extends BaseController {

	@Autowired
	private TraceProductService traceProductService;

	@Autowired
	private ProductBatchService productBatchService;

	@Autowired
	private TraceCodeService traceCodeService;

	@Autowired
	private ApiUserService apiUserService;

	@Autowired
	private TraceLableTemplateService traceLableTemplateService;

	@Autowired
	private TraceDeliveryAddressService traceDeliveryAddressService;

	@Autowired
	private TraceLableDiscountService traceLableDiscountService;

	@Autowired
	private TraceLableApplyService traceLableApplyService;

	@Autowired
	private TraceShowService traceShowService;

	@Autowired
	private OfficeService officeService;

	/**
	 * @description 跳转到申请标签页面；
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "applyPage")
	public String applyPage(String token, String productId, Model model, HttpServletRequest request) throws Exception {
		int pageNo = 1;
		int pageSize = 20;
		Page<TraceLableTemplate> page = new Page<TraceLableTemplate>(pageNo, pageSize);
		Page<TraceDeliveryAddress> page2 = new Page<TraceDeliveryAddress>(pageNo, pageSize);
		if (token != null) {
			TraceProduct traceProduct = new TraceProduct();
			traceProduct = traceProductService.get(productId);
			model.addAttribute("traceProduct", traceProduct);

			List<ProductBatch> list = productBatchService.newFindBatchListByProductId(productId);// 获取批次号列表和审核过的批次号
			model.addAttribute("ProductBatchList", list);

			User user = apiUserService.getUserByToken(token);
			TraceLableTemplate labelTemplate = new TraceLableTemplate();
			labelTemplate.setOfficeId(user.getCompany().getId());
			page = traceLableTemplateService.find(page, labelTemplate);
			model.addAttribute("TraceLableTemplateList", page.getList());

			String admin = "";
			// roleType 1: 非管路员 0 ：则管路员
			if (!user.isAdmin()) {
				admin = user.getCompany().getId();
			}

			TraceDeliveryAddress traceDeliveryAddress2 = new TraceDeliveryAddress();
			traceDeliveryAddress2.setCreatUserid(user.getId());
			List<String> findChildrenOffice = new ArrayList<String>();
			findChildrenOffice = officeService.findChildrenOffice(user.getCompany().getId());
			List<TraceDeliveryAddress> listadd = traceDeliveryAddressService.findListExport(traceDeliveryAddress2,
					findChildrenOffice, "", admin);
			if (null != listadd && 0 != listadd.size()) {
				for (Iterator<TraceDeliveryAddress> iterator = listadd.iterator(); iterator.hasNext();) {
					TraceDeliveryAddress deliveryAddress = (TraceDeliveryAddress) iterator.next();
					if (!deliveryAddress.getOfficeId().equals("") && deliveryAddress.getOfficeId() != null) {
						Office office = officeService.get(deliveryAddress.getOfficeId());
						deliveryAddress.setOfficeName(office.getName());
					} else {
						deliveryAddress.setOfficeName("");
					}
					StringBuffer str = new StringBuffer();
					if (StringUtils.isNotBlank(deliveryAddress.getReceiver())) {
						str.append(deliveryAddress.getReceiver() + '\u0009');
					}
					if (StringUtils.isNotBlank(deliveryAddress.getPhoneNum())) {
						str.append(deliveryAddress.getPhoneNum() + '\u0009');
					}
					if (StringUtils.isNotBlank(deliveryAddress.getProvince())) {
						str.append(deliveryAddress.getProvince());
					}
					if (StringUtils.isNotBlank(deliveryAddress.getCity())) {
						str.append(deliveryAddress.getCity());
					}
					if (StringUtils.isNotBlank(deliveryAddress.getArea())) {
						str.append(deliveryAddress.getArea());
					}
					if (StringUtils.isNotBlank(deliveryAddress.getStreet())) {
						str.append(deliveryAddress.getStreet());
					}
					if (StringUtils.isNotBlank(deliveryAddress.getDetailAddress())) {
						str.append(deliveryAddress.getDetailAddress());
					}
					deliveryAddress.setAddressMosaicing(str.toString());
				}
			}
			model.addAttribute("TraceDeliveryAddressList", listadd);

			// 溯源产品
			ProductBatch productBatch = productBatchService.getProductBatchByProductId(productId);
			if (productBatch != null) {
				// 属性和属性数据
				TraceRootBean traceRootBean = traceShowService.findTraceData(productId, productBatch.getId(), 1);
				TraceProduct product = traceProductService.get(productId);
				if (traceRootBean != null && product != null) {
					if (traceRootBean.getMainModelData() != null) {
						traceRootBean.getMainModelData().setProduct(product);
					} else {
						traceRootBean.setMainModelData(new MainModelData());
						traceRootBean.getMainModelData().setProduct(product);
					}
				}
				List<ModelData> listm = traceRootBean.getModelData();
				String plantGround = "";
				String farmerName = "";
				String phoneNum = "";
				String qualityGuaranteePeriod = "";
				qualityGuaranteePeriod = traceRootBean.getMainModelData().getQualityGuaranteePeriod();
				if (listm.size() > 0) {
					for (int i = 0; i < listm.size(); i++) {
						List<Rows> rows = listm.get(i).getRows();
						List<TraceData> propertyList = rows.get(0).getTraceData();
						if (propertyList.size() > 0) {
							for (int j = 0; j < propertyList.size(); j++) {
								if (propertyList.get(j).getFieldName() != null) {// 判断是否有属性数据
									if (propertyList.get(j).getFieldName().equals("产地")) {
										plantGround = propertyList.get(j).getFieldData(); // 产地
									}
									if (propertyList.get(j).getFieldName().equals("负责人")) {
										farmerName = propertyList.get(j).getFieldData(); // 负责人
									}
									if (propertyList.get(j).getFieldName().indexOf("电话") > -1) {
										phoneNum = propertyList.get(j).getFieldData(); // 电话号码
									}
								}
							}
						}
					}
				}
				model.addAttribute("plantGround", plantGround);
				model.addAttribute("farmerName", farmerName);
				model.addAttribute("phoneNum", phoneNum);
				model.addAttribute("qualityGuaranteePeriod", qualityGuaranteePeriod);
				model.addAttribute("traceRootBean",
						JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(traceRootBean)));
			}
		} else {
			return JsonMapper.nonDefaultMapper().toJson(
					ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
		model.addAttribute("token", token);
		model.addAttribute("productId", productId);
		model.addAttribute("TraceLableApply", new TraceLableApply());
		return "modules/" + "index/pc_applyLabel";
	}
	
	/**
	 * @description 跳转到申请标签页面；
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "applyLablePage")
	public String applyLablePage(String token, String productId, Model model, HttpServletRequest request){
		try{
			if (token != null) {
				//标签信息
				List<TraceLableTemplate> llist = traceLableTemplateService.find("");
				model.addAttribute("lableList", llist);
			} else {
				return JsonMapper.nonDefaultMapper().toJson(
						ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
			}
			model.addAttribute("productId", productId);
			model.addAttribute("token", token);
			return "modules/" + "index/pc_label_apply";
		}catch(Exception e){
			e.printStackTrace();
			return JsonMapper.nonDefaultMapper().toJson(
					ResultUtil.error(ResultEnum.INTERNAL_ERROR.getCode(), ResultEnum.INTERNAL_ERROR.getMessage()));
		}
	}
	
	/**
	 * @description 跳转到申请标签页面；
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "applyLablePage1")
	public String applyLablePage1(String token, String batchId, String productId, Model model, HttpServletRequest request){
		try{
			if (token != null) {
				User user = apiUserService.getUserByToken(token);
				//标签信息
				List<TraceLableTemplate> llist = traceLableTemplateService.find("");
				model.addAttribute("lableList", llist);
				
				model.addAttribute("parentOfficeName", officeService.getParentOfficeName(user));
				
				//获取地址信息
				TraceDeliveryAddress traceDeliveryAddress2 = new TraceDeliveryAddress();
				traceDeliveryAddress2.setCreatUserid(user.getId());
				
				TraceProduct traceProduct = traceProductService.get(productId);
				List<TraceDeliveryAddress> listadd = traceDeliveryAddressService.findTraceDeliveryAddressByProductId(traceDeliveryAddress2, traceProduct.getOfficeId());
				if (null != listadd && 0 != listadd.size()) {
					for (Iterator<TraceDeliveryAddress> iterator = listadd.iterator(); iterator.hasNext();) {
						TraceDeliveryAddress deliveryAddress = (TraceDeliveryAddress) iterator.next();
						if (!deliveryAddress.getOfficeId().equals("") && deliveryAddress.getOfficeId() != null) {
							Office office = officeService.get(deliveryAddress.getOfficeId());
							deliveryAddress.setOfficeName(office.getName());
						} else {
							deliveryAddress.setOfficeName("");
						}
						StringBuffer str = new StringBuffer();
						if (StringUtils.isNotBlank(deliveryAddress.getReceiver())) {
							str.append(deliveryAddress.getReceiver() + " ");
						}
						if (StringUtils.isNotBlank(deliveryAddress.getPhoneNum())) {
							str.append(deliveryAddress.getPhoneNum() + " ");
						}
						if (StringUtils.isNotBlank(deliveryAddress.getProvince())) {
							str.append(deliveryAddress.getProvince());
						}
						if (StringUtils.isNotBlank(deliveryAddress.getCity())) {
							str.append(deliveryAddress.getCity());
						}
						if (StringUtils.isNotBlank(deliveryAddress.getArea())) {
							str.append(deliveryAddress.getArea());
						}
						if (StringUtils.isNotBlank(deliveryAddress.getStreet())) {
							str.append(deliveryAddress.getStreet());
						}
						if (StringUtils.isNotBlank(deliveryAddress.getDetailAddress())) {
							str.append(deliveryAddress.getDetailAddress());
						}
						deliveryAddress.setAddressMosaicing(str.toString());
					}
				}
				model.addAttribute("addressList", listadd);
			} else {
				return JsonMapper.nonDefaultMapper().toJson(
						ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
			}
			model.addAttribute("batchId", batchId);
			model.addAttribute("productId", productId);
			model.addAttribute("token", token);
			return "modules/" + "index/pc_label_apply1";
		}catch(Exception e){
			e.printStackTrace();
			return JsonMapper.nonDefaultMapper().toJson(
					ResultUtil.error(ResultEnum.INTERNAL_ERROR.getCode(), ResultEnum.INTERNAL_ERROR.getMessage()));
		}
	}
	
	/**
	 * @description 获取对应标签的数据；
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "getLabelInformation", produces = "application/json;charset=UTF-8", method = {RequestMethod.POST })
	@ResponseBody
	@ApiOperation(value = "获取对应标签的数据", httpMethod = "POST", notes = "获取对应标签的数据", consumes = "application/x-www-form-urlencoded")
	public String getLabelInformation(String token, String productId, String lableId, Model model, HttpServletRequest request){
		try{
			if (token != null) {
				Map<String, Object> map = new HashMap<String, Object>();
				//当前用户信息
				User user = apiUserService.getUserByToken(token);
				
				//产品信息
				TraceProduct traceProduct = traceProductService.get(productId);
				map.put("traceProduct", traceProduct);
				
				//批次信息
				List<ProductBatch> plist = productBatchService.newFindBatchListByProductId(productId);// 获取批次号列表和审核过的批次号
				map.put("batchList", plist);
				
				//标签信息
				TraceLableTemplate traceLableTemplate = traceLableTemplateService.get(lableId);
				map.put("traceLableTemplate", traceLableTemplate);
	
				String admin = "";
				// roleType 1: 非管路员 0 ：则管路员
				if (!user.isAdmin()) {
					admin = user.getCompany().getId();
				}
				//获取地址信息
				TraceDeliveryAddress traceDeliveryAddress2 = new TraceDeliveryAddress();
				traceDeliveryAddress2.setCreatUserid(user.getId());
				List<String> findChildrenOffice = officeService.findChildrenOffice(user.getCompany().getId());
				List<TraceDeliveryAddress> listadd = traceDeliveryAddressService.findListExport(traceDeliveryAddress2, findChildrenOffice, "", admin);
				if (null != listadd && 0 != listadd.size()) {
					for (Iterator<TraceDeliveryAddress> iterator = listadd.iterator(); iterator.hasNext();) {
						TraceDeliveryAddress deliveryAddress = (TraceDeliveryAddress) iterator.next();
						if (!deliveryAddress.getOfficeId().equals("") && deliveryAddress.getOfficeId() != null) {
							Office office = officeService.get(deliveryAddress.getOfficeId());
							deliveryAddress.setOfficeName(office.getName());
						} else {
							deliveryAddress.setOfficeName("");
						}
						StringBuffer str = new StringBuffer();
						if (StringUtils.isNotBlank(deliveryAddress.getReceiver())) {
							str.append(deliveryAddress.getReceiver() + '\u0009');
						}
						if (StringUtils.isNotBlank(deliveryAddress.getPhoneNum())) {
							str.append(deliveryAddress.getPhoneNum() + '\u0009');
						}
						if (StringUtils.isNotBlank(deliveryAddress.getProvince())) {
							str.append(deliveryAddress.getProvince());
						}
						if (StringUtils.isNotBlank(deliveryAddress.getCity())) {
							str.append(deliveryAddress.getCity());
						}
						if (StringUtils.isNotBlank(deliveryAddress.getArea())) {
							str.append(deliveryAddress.getArea());
						}
						if (StringUtils.isNotBlank(deliveryAddress.getStreet())) {
							str.append(deliveryAddress.getStreet());
						}
						if (StringUtils.isNotBlank(deliveryAddress.getDetailAddress())) {
							str.append(deliveryAddress.getDetailAddress());
						}
						deliveryAddress.setAddressMosaicing(str.toString());
					}
				}
				map.put("addressList", listadd);
	
				// 溯源产品
				ProductBatch productBatch = productBatchService.getProductBatchByProductId(productId);
				if (productBatch != null) {
					// 属性和属性数据
					TraceRootBean traceRootBean = traceShowService.findTraceData(productId, productBatch.getId(), 1);
					TraceProduct product = traceProductService.get(productId);
					if (traceRootBean != null && product != null) {
						if (traceRootBean.getMainModelData() != null) {
							traceRootBean.getMainModelData().setProduct(product);
						} else {
							traceRootBean.setMainModelData(new MainModelData());
							traceRootBean.getMainModelData().setProduct(product);
						}
					}
					List<ModelData> listm = traceRootBean.getModelData();
					String plantGround = "";
					String farmerName = "";
					String phoneNum = "";
					String bzq = traceRootBean.getMainModelData().getQualityGuaranteePeriod();
					if (listm.size() > 0) {
						for (int i = 0; i < listm.size(); i++) {
							List<Rows> rows = listm.get(i).getRows();
							List<TraceData> propertyList = rows.get(0).getTraceData();
							if (propertyList!=null && propertyList.size() > 0) {
								for (int j = 0; j < propertyList.size(); j++) {
									if (propertyList.get(j).getFieldName() != null) {// 判断是否有属性数据
										if (propertyList.get(j).getFieldName().equals("产地")) {
											plantGround = propertyList.get(j).getFieldData(); // 产地
										}
										if (propertyList.get(j).getFieldName().equals("负责人") || propertyList.get(j).getFieldName().equals("经办人")) {
											farmerName = propertyList.get(j).getFieldData(); // 负责人
										}
										if (propertyList.get(j).getFieldName().indexOf("电话") > -1 || propertyList.get(j).getFieldName().indexOf("联系方式") > -1) {
											phoneNum = propertyList.get(j).getFieldData(); // 电话号码
										}
									}
								}
							}
						}
					}
					map.put("productName", traceProduct.getProductName());
					map.put("plantGround", plantGround);
					map.put("farmerName", farmerName);
					map.put("phoneNum", phoneNum);
					map.put("qualityGuaranteePeriod", bzq);
				}
				return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(map));
			} else {
				return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
			}
		}catch(Exception e){
			e.printStackTrace();
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.INTERNAL_ERROR.getCode(), ResultEnum.INTERNAL_ERROR.getMessage()));
		}
	}
	
	/**
	 * @description 获取对应标签的数据；
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "getLabelInformation1", produces = "application/json;charset=UTF-8", method = {RequestMethod.POST })
	@ResponseBody
	@ApiOperation(value = "获取对应标签的数据", httpMethod = "POST", notes = "获取对应标签的数据", consumes = "application/x-www-form-urlencoded")
	public String getLabelInformation1(String token, String batchId, String productId, String lableId, Model model, HttpServletRequest request){
		try{
			if (token != null) {
				Map<String, Object> map = new HashMap<String, Object>();
				//当前用户信息
				User user = apiUserService.getUserByToken(token);
				
				//产品信息
				TraceProduct traceProduct = traceProductService.get(productId);
				map.put("traceProduct", traceProduct);
				
				//批次信息
				ProductBatch batch = productBatchService.get(batchId);// 获取批次号列表和审核过的批次号
				map.put("batch", batch);
				
				//标签信息
				TraceLableTemplate traceLableTemplate = traceLableTemplateService.get(lableId);
				map.put("traceLableTemplate", traceLableTemplate);
	
				//获取地址信息
				TraceDeliveryAddress traceDeliveryAddress2 = new TraceDeliveryAddress();
				traceDeliveryAddress2.setCreatUserid(user.getId());
				List<TraceDeliveryAddress> listadd = traceDeliveryAddressService.findTraceDeliveryAddressByProductId(traceDeliveryAddress2, traceProduct.getOfficeId());
				if (null != listadd && 0 != listadd.size()) {
					for (Iterator<TraceDeliveryAddress> iterator = listadd.iterator(); iterator.hasNext();) {
						TraceDeliveryAddress deliveryAddress = (TraceDeliveryAddress) iterator.next();
						if (!deliveryAddress.getOfficeId().equals("") && deliveryAddress.getOfficeId() != null) {
							Office office = officeService.get(deliveryAddress.getOfficeId());
							deliveryAddress.setOfficeName(office.getName());
						} else {
							deliveryAddress.setOfficeName("");
						}
						StringBuffer str = new StringBuffer();
						if (StringUtils.isNotBlank(deliveryAddress.getReceiver())) {
							str.append(deliveryAddress.getReceiver() + " ");
						}
						if (StringUtils.isNotBlank(deliveryAddress.getPhoneNum())) {
							str.append(deliveryAddress.getPhoneNum() + " ");
						}
						if (StringUtils.isNotBlank(deliveryAddress.getProvince())) {
							str.append(deliveryAddress.getProvince());
						}
						if (StringUtils.isNotBlank(deliveryAddress.getCity())) {
							str.append(deliveryAddress.getCity());
						}
						if (StringUtils.isNotBlank(deliveryAddress.getArea())) {
							str.append(deliveryAddress.getArea());
						}
						if (StringUtils.isNotBlank(deliveryAddress.getStreet())) {
							str.append(deliveryAddress.getStreet());
						}
						if (StringUtils.isNotBlank(deliveryAddress.getDetailAddress())) {
							str.append(deliveryAddress.getDetailAddress());
						}
						deliveryAddress.setAddressMosaicing(str.toString());
					}
				}
				
				map.put("addressList", listadd);
	
				// 溯源产品
				ProductBatch productBatch = productBatchService.getProductBatchByProductId(productId);
				if (productBatch != null) {
					// 属性和属性数据
					TraceRootBean traceRootBean = traceShowService.findTraceData(productId, productBatch.getId(), 1);
					TraceProduct product = traceProductService.get(productId);
					if (traceRootBean != null && product != null) {
						if (traceRootBean.getMainModelData() != null) {
							traceRootBean.getMainModelData().setProduct(product);
						} else {
							traceRootBean.setMainModelData(new MainModelData());
							traceRootBean.getMainModelData().setProduct(product);
						}
					}
					List<ModelData> listm = traceRootBean.getModelData();
					String plantGround = "";
					String farmerName = "";
					String phoneNum = "";
					String bzq = traceRootBean.getMainModelData().getQualityGuaranteePeriod();
					if (listm.size() > 0) {
						for (int i = 0; i < listm.size(); i++) {
							List<Rows> rows = listm.get(i).getRows();
							List<TraceData> propertyList = rows.get(0).getTraceData();
							if (propertyList!=null && propertyList.size() > 0) {
								for (int j = 0; j < propertyList.size(); j++) {
									if (propertyList.get(j).getFieldName() != null) {// 判断是否有属性数据
										if (propertyList.get(j).getFieldName().equals("产地")) {
											plantGround = propertyList.get(j).getFieldData(); // 产地
										}
										if (propertyList.get(j).getFieldName().equals("负责人") || propertyList.get(j).getFieldName().equals("经办人")) {
											farmerName = propertyList.get(j).getFieldData(); // 负责人
										}
										if (propertyList.get(j).getFieldName().indexOf("电话") > -1 || propertyList.get(j).getFieldName().indexOf("联系方式") > -1) {
											phoneNum = propertyList.get(j).getFieldData(); // 电话号码
										}
									}
								}
							}
						}
					}
					map.put("productName", traceProduct.getProductName());
					map.put("plantGround", plantGround);
					map.put("farmerName", farmerName);
					map.put("phoneNum", phoneNum);
					map.put("qualityGuaranteePeriod", bzq);
				}
				return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(map));
			} else {
				return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
			}
		}catch(Exception e){
			e.printStackTrace();
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.INTERNAL_ERROR.getCode(), ResultEnum.INTERNAL_ERROR.getMessage()));
		}
	}

	/**
	 * @description 跳转到新增地址页面；
	 * @return
	 */
	@RequestMapping(value = "newAddress")
	public String newAddress(String token, String productId, Model model, HttpServletRequest request) {
		if (StringUtils.isBlank(token)) {
			return JsonMapper.nonDefaultMapper().toJson(
					ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
		model.addAttribute("token", token);
		TraceProduct traceProduct = traceProductService.get(productId);
		Office office = officeService.get(traceProduct.getOfficeId());
		TraceDeliveryAddress traceDeliveryAddress = traceDeliveryAddressService.getAddressByOffice(office);
		model.addAttribute("address", traceDeliveryAddress);
		return "modules/" + "index/pc_newAddress";
	}

	/**
	 * PC端首页申请标签窗口保存并使用地址
	 * 
	 * @param request
	 * @param traceProduct
	 * @return
	 * @author xy
	 */
	@RequestMapping(value = "PC_saveAddress", produces = "application/json;charset=UTF-8", method = {RequestMethod.GET })
	@ResponseBody
	@ApiOperation(value = "PC端首页申请标签窗口保存并使用地址", httpMethod = "GET", notes = "PC端首页申请标签窗口保存并使用地址", consumes = "application/x-www-form-urlencoded")
	public String PC_saveAddress(HttpServletRequest request, @RequestParam(required = false) String token,
			TraceDeliveryAddress address) {
		if (StringUtils.isNotBlank(token)) {
			User user = apiUserService.getUserByToken(token);
			address.setCreatUserid(user.getId());
			traceDeliveryAddressService.save(address);
			address = traceDeliveryAddressService.getAddress(user.getId());
			StringBuffer str = new StringBuffer();
			if (StringUtils.isNotBlank(address.getReceiver())) {
				str.append(address.getReceiver() + '\u0009');
			}
			if (StringUtils.isNotBlank(address.getPhoneNum())) {
				str.append(address.getPhoneNum() + '\u0009');
			}
			if (StringUtils.isNotBlank(address.getProvince())) {
				str.append(address.getProvince());
			}
			if (StringUtils.isNotBlank(address.getCity())) {
				str.append(address.getCity());
			}
			if (StringUtils.isNotBlank(address.getArea())) {
				str.append(address.getArea());
			}
			if (StringUtils.isNotBlank(address.getStreet())) {
				str.append(address.getStreet());
			}
			if (StringUtils.isNotBlank(address.getDetailAddress())) {
				str.append(address.getDetailAddress());
			}
			address.setAddressMosaicing(str.toString());// 在后台拼接地址返回给前台
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(address));
		} else {
			return JsonMapper.nonDefaultMapper().toJson(
					ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
	}

	/**
	 * PC端新首页申请标签计算价格
	 * 
	 * @param request
	 * @param response
	 * @param token
	 * @param lableTemplateId
	 * @param applyNum
	 * @return
	 */
	@RequestMapping(value = "traceLableCalculation", produces = "application/json;charset=UTF-8", method = {
			RequestMethod.GET })
	@ResponseBody
	@ApiOperation(value = "PC端新首页申请标签计算价格", httpMethod = "GET", notes = "PC端新首页申请标签计算价格", consumes = "application/x-www-form-urlencoded")
	public String TraceLableCalculation(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(required = false) String lableTemplateId,
			@RequestParam(required = false) String applyNum) {
		response.setContentType("application/json; charset=UTF-8");
		DiscountPriceVO discountPriceVO = new DiscountPriceVO();
		discountPriceVO = traceLableDiscountService.calculatedTotalPrice(lableTemplateId, applyNum);
		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(discountPriceVO));
	}

	/**
	 * @author wangyuewen 保存标签申请
	 * @param request
	 * @param traceProduct
	 * @return
	 */
	@RequestMapping(value = "saveLableApply", produces = "application/json;charset=UTF-8", method = {
			RequestMethod.POST })
	@ResponseBody
	@ApiOperation(value = "保存标签申请", httpMethod = "POST", notes = "保存标签申请", consumes = "application/json")
	public String saveLableApply(HttpServletRequest request, @RequestParam(required = false) String token,
			@RequestBody @ApiParam(name = "申请标签对象", value = "传入json格式", required = true) TraceLableApply apply) {
		if (StringUtils.isNotBlank(token)) {
			User user = apiUserService.getUserByToken(token);
			apply.setCreatUserid(user.getId());
			try {
				if (StringUtils.isNotBlank(apply.getBatchId())) {
					apply.setFlag("0");
				} else {
					apply.setFlag("1");
				}
				TraceProduct traceProduct = traceProductService.get(apply.getTraceProductId());// 获取产品的企业id
				traceLableApplyService.save(apply, traceProduct.getOfficeId(), user);
			} catch (Exception e) {
				return JsonMapper.nonDefaultMapper().toJson(
						ResultUtil.error(ResultEnum.SYSTEM_ERROR.getCode(), ResultEnum.SYSTEM_ERROR.getMessage()));
			}
			System.out.println(JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(apply)));
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(apply));
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
	@RequestMapping(value = "saveLableApply1", produces = "application/json;charset=UTF-8", method = {
			RequestMethod.POST })
	@ResponseBody
	@ApiOperation(value = "保存标签申请", httpMethod = "POST", notes = "保存标签申请", consumes = "application/json")
	public String saveLableApply1(HttpServletRequest request, @RequestParam(required = false) String token,
			@RequestBody @ApiParam(name = "申请标签对象", value = "传入json格式", required = true) TraceLableApply apply) {
		if (StringUtils.isNotBlank(token)) {
			User user = apiUserService.getUserByToken(token);
			apply.setCreatUserid(user.getId());
			try {
				if (StringUtils.isNotBlank(apply.getBatchId())) {
					apply.setFlag("0");
				} else {
					apply.setFlag("1");
				}
				TraceProduct traceProduct = traceProductService.get(apply.getTraceProductId());// 获取产品的企业id
				traceLableApplyService.save(apply, traceProduct.getOfficeId(),user);
			} catch (Exception e) {
				return JsonMapper.nonDefaultMapper().toJson(
						ResultUtil.error(ResultEnum.SYSTEM_ERROR.getCode(), ResultEnum.SYSTEM_ERROR.getMessage()));
			}
			System.out.println(JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(apply)));
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(apply));
		} else {
			return JsonMapper.nonDefaultMapper().toJson(
					ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
	}

	/**
	 * @description 跳转到标签绑定页面；
	 * @return
	 */
	@RequestMapping(value = "bindLabel")
	public String bindLabel(String token, Model model, String batchId, String productId, HttpServletRequest request) {

		String qjjhm = "";
		if (token != null) {
			// 获取该批次的标签段
			List<Integer> jhdList = traceCodeService.getBqjhm(productId, batchId);
			qjjhm = ApiUtil.getQjjhm(jhdList);
			model.addAttribute("qjjhm", qjjhm);

			// 获取产品未激活溯源码信息
			List<CodeDataVO> dataList = traceCodeService.getTraceCodeByProductId(productId);
			Collections.sort(dataList, new Comparator<CodeDataVO>() {
				public int compare(CodeDataVO arg0, CodeDataVO arg1) {
					return arg0.getMin().compareTo(arg1.getMin());
				}
			});
			model.addAttribute("dataList", JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(dataList)));
		} else {
			return JsonMapper.nonDefaultMapper().toJson(
					ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
		model.addAttribute("token", token);
		model.addAttribute("batchId", batchId);
		model.addAttribute("productId", productId);
		return "modules/" + "index/pc_bindLabel";
	}

	/**
	 * PC端手动绑定标签
	 * 
	 * @param response
	 * @param batchId
	 * @param min
	 * @param max
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/activationTraceCode", produces = "application/json;charset=UTF-8", method = RequestMethod.GET)
	@ApiOperation(value = "PC端手动绑定标签", httpMethod = "GET", notes = "PC端手动绑定标签", consumes = "application/x-www-form-urlencoded")
	public String activationTraceCode(HttpServletResponse response, @RequestParam(required = false) String token,
			@RequestParam(required = false) String batchId, @RequestParam(required = false) String productId,
			@RequestParam(required = false) String paramString) {
		response.setContentType("application/json; charset=UTF-8");

		if (token != null) {
			try {
				if (StringUtils.isNotBlank(paramString)) {
					String bq = traceCodeService.existSameBq(batchId, productId, paramString);
					if (StringUtils.isNotEmpty(bq)) {
						String[] arr = bq.split("_");
						if (arr[0].equals("1")) {
							return JsonMapper.nonDefaultMapper()
									.toJson(ResultUtil.error(ResultEnum.THEME_LABEL_NOTEXIST.getCode(),
											arr[1] + ResultEnum.THEME_LABEL_NOTEXIST.getMessage()));
						} else {
							return JsonMapper.nonDefaultMapper()
									.toJson(ResultUtil.error(ResultEnum.THEME_LABEL_EXIST.getCode(),
											arr[1] + ResultEnum.THEME_LABEL_EXIST.getMessage()));
						}
					} else {
						traceCodeService.activationTraceCode(batchId, productId, paramString);
					}
				} else {
					return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.THEME_LABEL_NULL.getCode(),
							ResultEnum.THEME_LABEL_NULL.getMessage()));
				}
			} catch (Exception e) {
				return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.ACT_TRACE_CODE_ERROR.getCode(),
						ResultEnum.ACT_TRACE_CODE_ERROR.getMessage()));
			}
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success());
		} else {
			return JsonMapper.nonDefaultMapper().toJson(
					ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
	}

	/**
	 * 批次删除，没有已打印标签的数据才能删除 2019-01-28 batchId 批次id
	 * 
	 * @return
	 */
	@RequestMapping(value = "deleteBatch", produces = "application/json;charset=UTF-8", method = { RequestMethod.GET })
	@ResponseBody
	@ApiOperation(value = "逻辑删除批次及批次的模块数据、模块属性数据和未打印的标签数据", httpMethod = "GET", notes = "逻辑删除批次及批次的模块数据和模块属性数据，包括未绑定的标签相关数据", consumes = "application/x-www-form-urlencoded")
	public String deleteBatch(HttpServletRequest request, @RequestParam String batchId, @RequestParam String token) {

		if (token != null) {
			User user = apiUserService.getUserByToken(token);
			String resultFlag = productBatchService.deleteBatchWithModelDatas(batchId, user);
			// 返回1 表名存在批次数据 不能删除此产品
			if (StringUtils.isNotBlank(resultFlag) && "1".equals(resultFlag)) {
				return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.PRODUCT_LABEL_EXIST.getCode(),
						ResultEnum.PRODUCT_LABEL_EXIST.getMessage()));
			}
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success());
		} else {
			return JsonMapper.nonDefaultMapper().toJson(
					ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
	}

	/**
	 * 申请标签（保存）
	 * 
	 * @param id,token
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(value = "/lableSave", produces = "application/json;charset=UTF-8", method = { RequestMethod.POST })
	@ResponseBody
	@ApiOperation(value = "申请标签（保存）", httpMethod = "POST", notes = "申请标签（保存）", consumes = "application/x-www-form-urlencoded")
	public String lableSave(String id, String token, String batchId, String isNew, String ifxz, String[] result,
			String[] modelArr, String[] propertyArr, String[] mkArr, String[] rjArr, String[] rjresult, Model model,
			RedirectAttributes redirectAttributes, String modelId) {
		if (StringUtils.isBlank(token)) {
			return JsonMapper.nonDefaultMapper().toJson(
					ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
		Map map = apiUserService.findUserByToken(token);
		TraceProduct traceProduct = traceProductService.get(id);
		ProductBatch batch = null;
		try {
			batch = productBatchService.saveBatchModelProperties(traceProduct, batchId, isNew, ifxz, map, result,
					modelArr, propertyArr, mkArr, rjArr, rjresult, modelId);
		} catch (Exception e) {
			e.printStackTrace();
			return JsonMapper.nonDefaultMapper()
					.toJson(ResultUtil.error(ResultEnum.SYSTEM_ERROR.getCode(), ResultEnum.SYSTEM_ERROR.getMessage()));
		}
		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(batch));
	}

}
