package com.surekam.modules.Interface.web;

import java.math.BigDecimal;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.surekam.common.config.Global;
import com.surekam.common.mapper.JsonMapper;
import com.surekam.common.utils.ResultEnum;
import com.surekam.common.utils.ResultUtil;
import com.surekam.common.utils.StringUtils;
import com.surekam.common.web.BaseController;
import com.surekam.modules.productbatch.entity.ProductBatch;
import com.surekam.modules.productbatch.service.ProductBatchService;
import com.surekam.modules.sys.entity.Office;
import com.surekam.modules.sys.entity.User;
import com.surekam.modules.sys.service.OfficeService;
import com.surekam.modules.sys.utils.TracingSourceCode;
import com.surekam.modules.trace.TraceLableContent.entity.TraceLableContent;
import com.surekam.modules.trace.TraceLableElement.entity.TraceLableElement;
import com.surekam.modules.trace.TraceLableElement.service.TraceLableElementService;
import com.surekam.modules.trace.TraceLableTemplate.entity.TraceLableTemplate;
import com.surekam.modules.trace.TraceLableTemplate.service.TraceLableTemplateService;
import com.surekam.modules.trace.entity.Content;
import com.surekam.modules.trace.entity.Element;
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
import com.surekam.modules.traceproduct.entity.TraceProduct;
import com.surekam.modules.traceproduct.service.TraceProductService;
import com.surekam.modules.traceresumetheme.entity.TraceResumeTheme;
import com.surekam.modules.traceresumetheme.service.TraceResumeThemeService;
import com.surekam.modules.tracewccode.service.TraceWcCodeService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import net.sf.json.JSONObject;


@Controller
@RequestMapping(value = "api/code")
public class TraceCodeAPIController extends BaseController{
	
	@Autowired
	private OfficeService officeService;
	@Autowired
	private TraceCodeService traceCodeService;
	
	@Autowired
	private TraceProductService traceProductService;
	
	@Autowired
	private ProductBatchService productBatchService;
	
	@Autowired
	private TraceWcCodeService traceWcCodeService;
	
	@Autowired
	private TraceLableElementService traceLableElementService;
	
	@Autowired
	private TraceResumeThemeService traceResumeThemeService;
	
	@Autowired
	private TraceLableTemplateService traceLableTemplateService;
	
	@Autowired
	private TraceLableSpecificationService traceLableSpecificationService;
	
	@Autowired
	private TraceLablePrinterService traceLablePrinterService;
	
	@Autowired
	private TraceLableSelectPrinterService traceLableSelectPrinterService;
	
	@Autowired
	private TraceLableElementNewService traceLableElementNewService;
	
	/**获取溯源码
	 * @param companyCode 公司编号（限3位数）
	 * @param baseCode 基地编号（限3位数）
	 * @param productCategoryCode 产品种类编码 （限1位数）
	 * @param packingCode 包装种类编码（限1位数）
	 * @param count 生成溯源码数量
	 * @return   00100120190612
	 */
	@ResponseBody
	@RequestMapping(value = "/getTraceCode")
	@ApiOperation(value = "获取溯源码", httpMethod = "POST", notes = "获取溯源码", consumes = "application/x-www-form-urlencoded")
	public String getTraceCode(String companyCode,String baseCode,String productCategoryCode, String packingCode, Integer count) {
		try{
			List<String> list = new ArrayList<String>();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String sjbh = sdf.format(new Date()).replaceAll("[[\\s-:punct:]]","");
			String ycount = traceWcCodeService.getMaxSerialNumber(companyCode, baseCode, sjbh, productCategoryCode, packingCode);
			Double f = Double.valueOf(ycount);
			int aa = (int)Math.ceil(f);
			for(int i = 0; i < count; i++) {
				String xlh = String.valueOf(i+aa+1);
				int  k = xlh.length();
				if(k <= 8) {
					for(int m = 0; m < 8-k;m++) {
						xlh = "0" + xlh;
					}
				}
				String code =TracingSourceCode.GeneratingTraceableCode(companyCode, baseCode, sjbh, productCategoryCode, packingCode, xlh);
				if(code.contains("_")){
					String[] str = code.split("_");
					return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(Integer.parseInt(str[0]), str[1]));
				}else{
					list.add(code);
				}
			}
			traceWcCodeService.save(companyCode, baseCode, sjbh, productCategoryCode, packingCode, count);
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(list));
		}catch(Exception e){
			e.printStackTrace();
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.SYSTEM_ERROR.getCode(), ResultEnum.SYSTEM_ERROR.getMessage()));
		}
	}
	
	/**
	 * 获取标签样式
	 * 
	 */
	@ResponseBody
	@RequestMapping(value = "/getLabelStyle")
	@ApiOperation(value = "获取标签样式", httpMethod = "GET", notes = "获取标签样式", consumes = "application/x-www-form-urlencoded")
	public String getLabelStyle() {
		try{
			List<TraceLableTemplate> list = new ArrayList<TraceLableTemplate>();
			List<TraceLableTemplate> lableTemplateList =  traceLableTemplateService.find("");
			for (TraceLableTemplate traceLableTemplate : lableTemplateList) {
				String id = traceLableTemplate.getId();
				if(id.equals("1") || id.equals("3")){
					List<TraceLableElement> elist = traceLableElementService.getTraceLableElementList();
					traceLableTemplate.setElementList(elist);
				}else{
					List<TraceLableElement> tlist = new ArrayList<TraceLableElement>();
					TraceLableElement element = new TraceLableElement();
					element.setId("11");
					element.setElementCode("productName");
					element.setElementName("产品名称");
					tlist.add(element);
					
					element = new TraceLableElement();
					element.setId("13");
					element.setElementCode("qualityGuaranteePeriod");
					element.setElementName("五常稻花香大米");
					tlist.add(element);
					
					element = new TraceLableElement();
					element.setId("14");
					element.setElementCode("plantGround");
					element.setElementName("保质期");
					tlist.add(element);
					
					element = new TraceLableElement();
					element.setId("15");
					element.setElementCode("farmerName");
					element.setElementName("365天");
					tlist.add(element);
					traceLableTemplate.setElementList(tlist);
				}
				list.add(traceLableTemplate);
			}
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(lableTemplateList));
		}catch(Exception e){
			e.printStackTrace();
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.SYSTEM_ERROR.getCode(), ResultEnum.SYSTEM_ERROR.getMessage()));
		}
	}
	
	/**
	 * 获取标签模板
	 * 
	 */
	@ResponseBody
	@RequestMapping(value = "/getLabelModel")
	@ApiOperation(value = "获取标签模板", httpMethod = "GET", notes = "获取标签模板", consumes = "application/x-www-form-urlencoded")
	public String getLabelModel() {
		try{
			List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
			Map<String,Object> map = new HashMap<String, Object>();
			map.put("type_id", "1");
			map.put("type_name", "左码右字(防水防油撕不破)");
			String url = Global.getConfig("sy_img_url");
			map.put("backgroundImgUrl", url+"upload/product_model/20190118161037354.png");
			List<Map<String,Object>> clist = traceLableElementService.getTraceLableElementList("1");
			map.put("content", clist);
			list.add(map);
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(list));
		}catch(Exception e){
			e.printStackTrace();
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.SYSTEM_ERROR.getCode(), ResultEnum.SYSTEM_ERROR.getMessage()));
		}
	}
	
	/**
	 * 获取主题
	 * 
	 * @return
	 */
	@RequestMapping(value = "getTraceResumeThemeList", produces = "application/json;charset=UTF-8", method = { RequestMethod.GET })
	@ResponseBody
	@ApiOperation(value = "获取主题", httpMethod = "GET", notes = "获取主题", consumes = "application/x-www-form-urlencoded")
	public String getTraceResumeThemeList(HttpServletRequest request) {
		try{
			List<TraceResumeTheme> themeList = traceResumeThemeService.findCommonAndOwnedThemes(""); 
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(themeList));
		}catch(Exception e){
			e.printStackTrace();
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.SYSTEM_ERROR.getCode(), ResultEnum.SYSTEM_ERROR.getMessage()));
		}
	}
	
	/**
	 * @param companyId 公司id
	 * @param baseCode 基地编号（限3位数）
	 * @param count 生成码数量（个）
	 * @param traceProduct 产品
	 * @return   00100120190612
	 */
	@ResponseBody
	@RequestMapping(value = "/saveTraceCode")
	@ApiOperation(value = "生成溯源码", httpMethod = "POST", notes = "生成溯源码", consumes = "application/x-www-form-urlencoded")
	public String saveTraceCode(String companyId,String baseCode,String cpzlbh, String bzzlbh, Integer count,String productBatchId,String applyId) {
		Office office = new Office();
		office = officeService.get(companyId);
		if(office !=null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String sjbh = sdf.format(new Date()).replaceAll("[[\\s-:punct:]]",""); 
			for(int i = 0; i < count; i++) {
				String xlh = String.valueOf(i);
				int  k = xlh.length();
				if(k <= 8) {
					for(int m = 0; m < 8-k;m++) {
						xlh = "0" + xlh;
					}
				}
				String code =TracingSourceCode.GeneratingTraceableCode(office.getOfficeCode(), baseCode, sjbh, cpzlbh, bzzlbh, xlh);
				//saveTraceCode(code,productBatchId,applyId,i+1,companyId,bzzlbh);
			}
		}else {
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.COMPANY_IS_NULL.getCode(),ResultEnum.COMPANY_IS_NULL.getMessage()));
		}
		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success());
		
	}
	/**
	 * 保存溯源码
	 * @param code
	 * @param productBatchId
	 * @param applyId
	 * @param serialNumber
	 * @param companyId
	 * @param bzzlbh
	 */
	public void saveTraceCode(String code,String productBatchId, String applyId,int serialNumber,String companyId,String bzzlbh) {
		TraceCode traceCode = new TraceCode();
		traceCode.setTraceCode(code);
		traceCode.setApplyId(applyId);
		traceCode.setBatchId(productBatchId);
		traceCode.setOfficeId(companyId);
		traceCode.setPackType(bzzlbh);
		traceCode.setStates("A");
		traceCode.setCreatTime(new Date());
		traceCodeService.save(traceCode);
	}
	
	/**
	 * 传参返回溯源码
	 * @param qybh
	 * @param jgscdbh
	 * @param sjbh
	 * @param cpzlbh
	 * @param bzzlbh
	 * @param xlh
	 * @return
	 */
	public String getCode(String qybh,String jgscdbh,String sjbh,String cpzlbh,String bzzlbh,String xlh) {
		String code =TracingSourceCode.GeneratingTraceableCode(qybh, jgscdbh, sjbh, cpzlbh, bzzlbh, xlh);
		return code;
	}
	/**
	 * 传入参数 D禁用,A激活
	 */
	@ResponseBody
	@RequestMapping(value = "/updateStates")
	@ApiOperation(value = "溯源码激活禁用", httpMethod = "POST", notes = "溯源码激活禁用", consumes = "application/x-www-form-urlencoded")
	public String SourceCodeActivationDisabled(HttpServletRequest request, HttpServletResponse response,String type,String [] code) {
		if(type.equals("A")) {
			for(int i = 0 ; i < code.length; i++) {
				traceCodeService.Activation(code[i]);
			}
		}else if(type.equals("D")) {
			for(int i = 0 ; i < code.length; i++) {
				traceCodeService.Disabled(code[i]);
			}
		}
		return type;
	}

	/**
	 * 保存产品信息
	 * @param traceProduct
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/saveProduct",produces = "text/plain;charset=UTF-8", method = { RequestMethod.POST })
	@ApiOperation(value = "保存产品信息", httpMethod = "POST", notes = "保存产品信息", consumes = "application/json")
	public String saveProduct(HttpServletRequest request, HttpServletResponse response,
			@RequestBody @ApiParam(name = "产品对象", value = "传入json格式", required = true) TraceProduct traceProduct) {
		try {
			traceProductService.saveProduct(traceProduct);
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.PRODUCT_SAVE_ERROR.getCode(),ResultEnum.PRODUCT_SAVE_ERROR.getMessage()));
		}
		
	}
	
	/**
	 * 保存批次信息
	 * @param traceProduct
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/saveProductBatch",produces = "text/plain;charset=UTF-8", method = { RequestMethod.POST })
	@ApiOperation(value = "保存批次信息", httpMethod = "POST", notes = "保存批次信息", consumes = "application/json")
	public String saveProductBatch(HttpServletRequest request, HttpServletResponse response,
			@RequestBody @ApiParam(name = "批次对象", value = "传入json格式", required = true) ProductBatch productBatch) {
		ProductBatch productBatch2 = productBatchService.get(productBatch.getId());
		if(productBatch2 != null) {
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.PRODUCTBACTH_IS_NOT_NULL.getCode(),ResultEnum.PRODUCTBACTH_IS_NOT_NULL.getMessage()));
		}else {
			productBatchService.save(productBatch);
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success());
		}
	}
	
	/**
	 * 封装打印数据
	 * @param request
	 * @param response
	 * @param productName
	 * @param batchCode
	 * @return
	 */
	@RequestMapping(value = "/printData",produces="application/json;charset=UTF-8", method = {RequestMethod.POST})
	@ResponseBody
	@ApiOperation(value = "查询溯源码列表", httpMethod = "POST", notes = "查询溯源码列表",	consumes="application/x-www-form-urlencoded")
	public String printData(HttpServletRequest request,HttpServletResponse response,
			@RequestParam String kuid,
			@RequestParam String labelId,
			@RequestParam String printId,
			@RequestParam String jzId,
			@RequestParam String xoffset,
			@RequestParam String yoffset,
			@RequestParam("codeList") List<String> codeList,
			@RequestParam("elementList") List<String> elementList) {
		Map<String,Object> data = new HashMap<String,Object>();
		TraceLableSelectPrinter traceLableSelectPrinter = traceLableSelectPrinterService.findTraceLableSelectPrinter(kuid, printId);
		if(traceLableSelectPrinter == null){
			traceLableSelectPrinter = new TraceLableSelectPrinter();
			traceLableSelectPrinter.setPrinterId(printId);
			traceLableSelectPrinter.setOfficeId(kuid);
			traceLableSelectPrinter.setXoffset(xoffset);
			traceLableSelectPrinter.setYoffset(yoffset);
			traceLableSelectPrinterService.save(traceLableSelectPrinter);
		}else{
			traceLableSelectPrinter.setXoffset(xoffset);
			traceLableSelectPrinter.setYoffset(yoffset);
			traceLableSelectPrinter.setUpdateTime(new Date());
			traceLableSelectPrinterService.save(traceLableSelectPrinter);
		}
		
		List<Content> contentList = new ArrayList<Content>();
		TraceLablePrinter traceLablePrinter = traceLablePrinterService.get(printId);
		traceLablePrinter.setXoffset(new BigDecimal(xoffset));
		traceLablePrinter.setYoffset(new BigDecimal(yoffset));
		data.put("printer", traceLablePrinter);
		TraceLableSpecification traceLableSpecification = traceLableSpecificationService.get(jzId);
		data.put("labelSpecification", traceLableSpecification);
		List<TraceLableElementNew> traceLableElementNewList = traceLableElementNewService.findByLabelId(labelId);
		data.put("labelElementList", traceLableElementNewList);
		
		for (String code : codeList) {
			Content content = new Content();
			if(code.contains("[") && code.contains("]")){
				code = code.substring(2,code.length()-2);
			}else if(code.contains("[")){
				code = code.substring(2,code.length()-1);
			}else if(code.contains("]")){
				code = code.substring(1,code.length()-2);
			}else{
				code = code.substring(1,code.length()-1);
			}
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
					for(int j=0;j<elementList.size();j++){
						String ele = elementList.get(j);
						String[] eArr = ele.split("_");
						if(eArr[0].equals(elementId)){
							element.setElementId(eArr[0]);
							element.setElementData(eArr[1]);
							elist.add(element);
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
}
