package com.surekam.modules.api.web;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.joda.time.LocalDate;
import org.json.JSONObject;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.surekam.common.config.Global;
import com.surekam.common.mapper.JsonMapper;
import com.surekam.common.persistence.Page;
import com.surekam.common.utils.ChineseCharToEnUtil;
import com.surekam.common.utils.DateUtils;
import com.surekam.common.utils.MapKeyComparator;
import com.surekam.common.utils.ResultEnum;
import com.surekam.common.utils.ResultUtil;
import com.surekam.common.utils.StringUtils;
import com.surekam.common.utils.UniqueCodeUtil;
import com.surekam.common.web.BaseController;
import com.surekam.modules.api.service.ApiUserService;
import com.surekam.modules.api.utils.AccessData;
import com.surekam.modules.api.utils.ApiUtil;
import com.surekam.modules.productbatch.entity.ProductBatch;
import com.surekam.modules.productbatch.service.ProductBatchService;
import com.surekam.modules.sys.entity.Office;
import com.surekam.modules.sys.entity.User;
import com.surekam.modules.sys.service.OfficeService;
import com.surekam.modules.trace.TraceDayInfo.entity.TraceInfoReport;
import com.surekam.modules.trace.TraceInfo.service.TraceInfoService;
import com.surekam.modules.trace.TraceLableApply.service.TraceLableApplyService;
import com.surekam.modules.tracecode.entity.TraceCode;
import com.surekam.modules.tracecode.service.TraceCodeService;
import com.surekam.modules.tracecomment.entity.TraceComment;
import com.surekam.modules.tracecomment.service.TraceCommentService;
import com.surekam.modules.tracecommentreply.entity.TraceCommentReply;
import com.surekam.modules.tracecommentreply.service.TraceCommentReplyService;
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
import com.surekam.modules.traceverifycode.entity.TraceVerifyCode;
import com.surekam.modules.traceverifycode.service.TraceVerifyCodeService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import net.sf.json.JSONArray;

@Api(value="溯源首页接口Controller", description="溯源首页的相关数据接口")
@Controller
@RequestMapping(value = "api/pcIndex")
public class PCIndexController extends BaseController {

	@Autowired
	private TraceProductService traceProductService;

	@Autowired
	private ProductBatchService productBatchService;

	@Autowired
	private TraceCodeService traceCodeService;

	@Autowired
	private OfficeService officeService;

	@Autowired
	private ApiUserService apiUserService;

	@Autowired
	private TraceModelService traceModelService;

	@Autowired
	private TracePropertyService tracePropertyService;

	@Autowired
	private TraceResumeThemeService traceResumeThemeService;

	@Autowired
	private TraceLableApplyService traceLableApplyService;

	@Autowired
	private TracePropertyNewDao tracePropertyNewDao;

	@Autowired
	private TraceCommentReplyService traceCommentReplyService;

	@Autowired
	private TraceCommentService traceCommentService;

	@Autowired
	private TraceInfoService traceInfoService;

	@Autowired
	private TraceVerifyCodeService traceVerifyCodeService;
	
	/***************************************************************
	 * 打印
	 **********************************************************************/
	/**
	 * @description 跳转到首页；
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "index")
	public String index(String token, Model model, HttpServletRequest request) {
		model.addAttribute("token", token);
		List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
		if (token != null) {
			User user = apiUserService.getUserByToken(token);
			// 超级管理默认查询所有企业，其他默认查询本企业
			if (user.getId().equals("1")) {
				model.addAttribute("xs", 1);
				List<Office> officeList = officeService.findAllThree(user);
				for (int i = 0; i < officeList.size(); i++) {
					Map<String, Object> map = new HashMap<String, Object>();
					Office office = officeList.get(i);
					office.setSelected("1");
					if (!office.getId().equals("1")) {
						String name = office.getName();
						String szm = ChineseCharToEnUtil.getFirstLetter(name);
						if (szm.length() > 0) {
							szm = szm.substring(0, 1).toUpperCase();
							map.put(szm, office);
							listMap.add(map);
						}
					}
				}

				Map<String, Object> rmap = new HashMap<String, Object>();
				List<Office> rlist = new ArrayList<Office>();
				Office office = new Office();
				office.setId("");
				office.setName("所有公司");
				office.setSelected("2");
				rlist.add(office);
				rmap.put("A", rlist);

				for (Map<String, Object> map : listMap) {
					Iterator it = map.entrySet().iterator();
					Entry entry = (Entry) it.next();
					String key = (String) entry.getKey();
					Office value = (Office) entry.getValue();
					if (rmap.containsKey(key)) {
						List<Office> slist = (List<Office>) rmap.get(key);
						slist.add(value);
						rmap.put(key, slist);
					} else {
						rlist = new ArrayList<Office>();
						rlist.add(value);
						rmap.put(key, rlist);
					}
				}
				rmap = sortMapByKey(rmap);
				model.addAttribute("map", rmap);
			} else {
				model.addAttribute("xs", 0);
				List<Office> officeList = officeService.findChildrenOfficeList(user.getCompany().getId());
				for (int i = 0; i < officeList.size(); i++) {
					Map<String, Object> map = new HashMap<String, Object>();
					Office office = officeList.get(i);
					if(office.getId().equals(user.getCompany().getId())){
						office.setSelected("2");
					}else{
						office.setSelected("1");
					}
					String name = office.getName();
					String szm = ChineseCharToEnUtil.getFirstLetter(name);
					if (szm.length() > 0) {
						szm = szm.substring(0, 1).toUpperCase();
						map.put(szm, office);
						listMap.add(map);
					}
				}

				Map<String, Object> rmap = new HashMap<String, Object>();
				List<Office> rlist = new ArrayList<Office>();
				for (Map<String, Object> map : listMap) {
					Iterator it = map.entrySet().iterator();
					Entry entry = (Entry) it.next();
					String key = (String) entry.getKey();
					Office value = (Office) entry.getValue();
					if (rmap.containsKey(key)) {
						List<Office> slist = (List<Office>) rmap.get(key);
						slist.add(value);
						rmap.put(key, slist);
					} else {
						rlist = new ArrayList<Office>();
						rlist.add(value);
						rmap.put(key, rlist);
					}
				}
				rmap = sortMapByKey(rmap);
				model.addAttribute("map", rmap);
			}
		}
		return "modules/" + "index/pc_index";
	}

	/**
	 * @description 跳转到展示页面；
	 * @return
	 */
	@RequestMapping(value = "main")
	public String main(String token, String officeId, Model model, HttpServletRequest request) {
		String num = "";
		if (token != null) {
			User user = apiUserService.getUserByToken(token);
			String corpCode = "";
			if (StringUtils.isNotEmpty(officeId)) {
				corpCode = officeId;
			} else {
				// 超级管理默认查询所有企业，其他默认查询本企业批次
				if (!user.isAdmin()) {
					corpCode = user.getCompany().getId();
				}
			}

			num = traceLableApplyService.findBySql(corpCode);
			model.addAttribute("num", num);

			TraceInfoReport traceInfoReport = new TraceInfoReport();
			// 本周
			traceInfoReport
					.setWeekTraceCount(traceInfoService.findTraceCount(DateUtils.getBeginDayOfWeek(), "", corpCode));
			// 本月
			traceInfoReport
					.setMonthTraceCount(traceInfoService.findTraceCount(DateUtils.getBeginDayOfMonth(), "", corpCode));
			// 总扫码次数
			traceInfoReport.setTraceCount(traceInfoService.findTraceCount("", "", corpCode));
			model.addAttribute("traceInfoReport", traceInfoReport);
			List<TraceProduct> productList = new ArrayList<TraceProduct>();
			productList = traceProductService.findAllProducts(corpCode);
			long allcount = 0;
			long highcount = 0;
			long lowcount = 0;
			allcount = traceCommentService.commentAllCount(corpCode);
			highcount = traceCommentService.commentHighCount(corpCode);
			lowcount = traceCommentService.commentLowCount(corpCode);
			String productId = "";
			Map<String, Object> map2 = getData2(productId, user, corpCode);
			model.addAttribute("map", getData(productId, user, corpCode));// 全国扫码图表数据
			model.addAttribute("map2", map2);// 扫码留言图表数据
			model.addAttribute("productList", productList);
			model.addAttribute("allcount", allcount);
			model.addAttribute("highcount", highcount);
			model.addAttribute("lowcount", lowcount);
			model.addAttribute("token", token);
			model.addAttribute("officeId", officeId);
			return "modules/" + "index/pc_main";
		} else {
			return JsonMapper.nonDefaultMapper().toJson(
					ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
	}

	/**
	 * 获取企业列表
	 * 
	 * @param token
	 * @return
	 */
	@RequestMapping(value = "getCompanyList", produces = "application/json;charset=UTF-8", method = {
			RequestMethod.GET })
	@ResponseBody
	@ApiOperation(value = "获取企业列表", httpMethod = "GET", notes = "获取企业列表", consumes = "application/x-www-form-urlencoded")
	public String getCompanyList(String token) {
		Map<String, Object> rmap = new HashMap<String, Object>();
		if (StringUtils.isBlank(token)) {
			return JsonMapper.nonDefaultMapper().toJson(
					ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
		} else {
			try {
				List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
				User user = apiUserService.getUserByToken(token);
				List<Map<String, Object>> officeList = officeService.findChildrenOfficeList1(user.getCompany().getId());
				for (int i = 0; i < officeList.size(); i++) {
					Map<String, Object> map = new HashMap<String, Object>();
					Map<String, Object> m = officeList.get(i);
					String name = (String) m.get("NAME");
					String szm = ChineseCharToEnUtil.getFirstLetter(name);
					if (szm.length() > 0) {
						szm = szm.substring(0, 1).toUpperCase();
						map.put(szm, m);
						listMap.add(map);
					}
				}

				List<Map<String, Object>> rlist = new ArrayList<Map<String, Object>>();
				for (Map<String, Object> map : listMap) {
					Iterator it = map.entrySet().iterator();
					Entry entry = (Entry) it.next();
					String key = (String) entry.getKey();
					Map<String, Object> value = (Map<String, Object>) entry.getValue();
					if (rmap.containsKey(key)) {
						List<Map<String, Object>> slist = (List<Map<String, Object>>) rmap.get(key);
						slist.add(value);
						rmap.put(key, slist);
					} else {
						rlist = new ArrayList<Map<String, Object>>();
						rlist.add(value);
						rmap.put(key, rlist);
					}
				}
				return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(rmap));
			} catch (Exception e) {
				e.printStackTrace();
				return JsonMapper.nonDefaultMapper().toJson(
						ResultUtil.error(ResultEnum.SYSTEM_ERROR.getCode(), ResultEnum.SYSTEM_ERROR.getMessage()));
			}
		}
	}

	/**
	 * 获取本周、本月扫码次数
	 * 
	 * @param token
	 * @return
	 */
	@RequestMapping(value = "getScanCount", produces = "application/json;charset=UTF-8", method = { RequestMethod.GET })
	@ResponseBody
	@ApiOperation(value = "获取本周、本月扫码次数", httpMethod = "GET", notes = "获取本周、本月扫码次数", consumes = "application/x-www-form-urlencoded")
	public String getScanCount(String token, String officeId) {
		Map<String, Object> rmap = new HashMap<String, Object>();
		if (StringUtils.isBlank(token)) {
			return JsonMapper.nonDefaultMapper().toJson(
					ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
		} else {
			try {
				Map<String, Object> map = new HashMap<String, Object>();
				User user = apiUserService.getUserByToken(token);
				String corpCode = "";
				if (StringUtils.isNotEmpty(officeId)) {
					corpCode = officeId;
				} else {
					// 超级管理默认查询所有企业，其他默认查询本企业批次
					if (!user.isAdmin()) {
						corpCode = user.getCompany().getId();
					}
				}

				String num = traceLableApplyService.findBySql(corpCode);
				map.put("num", num);

				TraceInfoReport traceInfoReport = new TraceInfoReport();
				// 本周
				traceInfoReport.setWeekTraceCount(
						traceInfoService.findTraceCount(DateUtils.getBeginDayOfWeek(), "", corpCode));
				// 本月
				traceInfoReport.setMonthTraceCount(
						traceInfoService.findTraceCount(DateUtils.getBeginDayOfMonth(), "", corpCode));
				// 总扫码次数
				traceInfoReport.setTraceCount(traceInfoService.findTraceCount("", "", corpCode));
				map.put("traceInfoReport", traceInfoReport);
				List<TraceProduct> productList = new ArrayList<TraceProduct>();
				productList = traceProductService.findAllProducts(corpCode);
				long allcount = 0;
				long highcount = 0;
				long lowcount = 0;
				allcount = traceCommentService.commentAllCount(corpCode);
				highcount = traceCommentService.commentHighCount(corpCode);
				lowcount = traceCommentService.commentLowCount(corpCode);
				String productId = "";
				Map<String, Object> map2 = getData2(productId, user, officeId);
				map.put("map", getData(productId, user, officeId));
				map.put("map2", map2);
				map.put("productList", productList);
				map.put("allcount", allcount);
				map.put("highcount", highcount);
				map.put("lowcount", lowcount);
				map.put("token", token);
				map.put("officeId", corpCode);
				return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(map));
			} catch (Exception e) {
				e.printStackTrace();
				return JsonMapper.nonDefaultMapper().toJson(
						ResultUtil.error(ResultEnum.SYSTEM_ERROR.getCode(), ResultEnum.SYSTEM_ERROR.getMessage()));
			}
		}
	}

	// 月份统计扫码留言图表数据
	public Map<String, Object> getData2(String productId, User user, String officeId) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<Object> list = traceInfoService.findMonthsTraceInfo(productId, user, officeId);
		List<Object> list2 = traceCommentService.findMonthscComment(productId, user, officeId);
		JSONArray json3 = getHalfYearMonth();
		JSONArray json = new JSONArray();
		JSONArray json2 = new JSONArray();
		long saomamax = 0;
		long commentmax = 0;
		for (int i = 0; i < json3.size(); i++) {
			int m = 0;
			for (m = 0; m < list.size(); m++) {
				Object[] objArr = (Object[]) list.get(m);
				if (json3.get(i).equals(objArr[0].toString())) {
					json.add(objArr[1].toString());
					if (saomamax < Long.parseLong(objArr[1].toString())) {
						saomamax = Long.parseLong(objArr[1].toString());
					}
					break;
				}
			}
			if (m == list.size()) {
				json.add("0");
			}

		}
		for (int i = 0; i < json3.size(); i++) {
			int m = 0;
			for (m = 0; m < list2.size(); m++) {
				Object[] objArr = (Object[]) list2.get(m);
				if (json3.get(i).equals(objArr[0].toString())) {
					json2.add(objArr[1].toString());
					if (commentmax < Long.parseLong(objArr[1].toString())) {
						commentmax = Long.parseLong(objArr[1].toString());
					}
					break;
				}
			}
			if (m == list2.size()) {
				json2.add("0");
			}

		}
		Collections.reverse(json3);
		Collections.reverse(json2);
		Collections.reverse(json);
		map.put("month", json3);
		map.put("comment", json2);
		map.put("saoma", json);
		map.put("saomamax", saomamax);
		map.put("commentmax", commentmax);
		return map;
	}

	// 获得最近12个月的月份
	public JSONArray getHalfYearMonth() {
		JSONArray json = new JSONArray();
		LocalDate today = LocalDate.now();
		for (long i = 1L; i <= 12L; i++) {
			LocalDate localDate = today.minusMonths((int) i - 1);
			String ss = localDate.toString().substring(0, 7);
			json.add(ss);
		}
		return json;
	}

	// 全国扫码图表数据
	public Map<String, Object> getData(String productId, User user, String officeId) {
		String[] proviceArray = { "北京", "天津", "上海", "重庆", "河北", "河南", "云南", "辽宁", "黑龙江", "湖南", "安徽", "山东", "新疆", "江苏",
				"浙江", "江西", "湖北", "广西", "甘肃", "山西", "内蒙古", "陕西", "吉林", "福建", "贵州", "广东", "青海", "西藏", "四川", "宁夏", "海南",
				"台湾", "香港", "澳门" };
		List<Integer> stringlist = new ArrayList<Integer>();
		List<JSONObject> cf = new ArrayList<JSONObject>();
		Map<String, Object> map = new HashMap<String, Object>();
		List<Object> list = traceInfoService.findAllTraceInfo(productId, user, officeId);
		if (list != null) {
			String pro = "";
			for (int j = 0; j < proviceArray.length; j++) {
				pro = proviceArray[j];
				int i = 0;
				for (i = 0; i < list.size(); i++) {
					Object[] objArr = (Object[]) list.get(i);
					String provice = objArr[0] == null ? "" : objArr[0].toString();
					String sl = objArr[1].toString();
					if (provice.contains("北京")) {
						provice = "北京";
					} else if (provice.contains("天津")) {
						provice = "天津";
					} else if (provice.contains("上海")) {
						provice = "上海";
					} else if (provice.contains("重庆")) {
						provice = "重庆";
					} else if (provice.contains("河北")) {
						provice = "河北";
					} else if (provice.contains("河南")) {
						provice = "河南";
					} else if (provice.contains("云南")) {
						provice = "云南";
					} else if (provice.contains("辽宁")) {
						provice = "辽宁";
					} else if (provice.contains("黑龙江")) {
						provice = "黑龙江";
					} else if (provice.contains("湖南")) {
						provice = "湖南";
					} else if (provice.contains("安徽")) {
						provice = "安徽";
					} else if (provice.contains("山东")) {
						provice = "山东";
					} else if (provice.contains("新疆")) {
						provice = "新疆";
					} else if (provice.contains("江苏")) {
						provice = "江苏";
					} else if (provice.contains("浙江")) {
						provice = "浙江";
					} else if (provice.contains("江西")) {
						provice = "江西";
					} else if (provice.contains("湖北")) {
						provice = "湖北";
					} else if (provice.contains("广西")) {
						provice = "广西";
					} else if (provice.contains("甘肃")) {
						provice = "甘肃";
					} else if (provice.contains("山西")) {
						provice = "山西";
					} else if (provice.contains("内蒙古")) {
						provice = "内蒙古";
					} else if (provice.contains("陕西")) {
						provice = "陕西";
					} else if (provice.contains("吉林")) {
						provice = "吉林";
					} else if (provice.contains("福建")) {
						provice = "福建";
					} else if (provice.contains("贵州")) {
						provice = "贵州";
					} else if (provice.contains("广东")) {
						provice = "广东";
					} else if (provice.contains("青海")) {
						provice = "青海";
					} else if (provice.contains("西藏")) {
						provice = "西藏";
					} else if (provice.contains("四川")) {
						provice = "四川";
					} else if (provice.contains("宁夏")) {
						provice = "宁夏";
					} else if (provice.contains("海南")) {
						provice = "海南";
					} else if (provice.contains("台湾")) {
						provice = "台湾";
					} else if (provice.contains("香港")) {
						provice = "香港";
					} else if (provice.contains("澳门")) {
						provice = "澳门";
					}
					if (pro.equals(provice)) {
						JSONObject jObject = new JSONObject();
						jObject.put("name", provice);
						jObject.put("value", Integer.parseInt(sl));
						cf.add(jObject);
						stringlist.add(Integer.parseInt(sl));
						break;
					}
				}
				if (i == list.size()) {
					JSONObject jObject = new JSONObject();
					jObject.put("name", pro);
					jObject.put("value", 0);
					cf.add(jObject);
				}
			}
			String cfData = StringUtils.join(cf, ",");
			map.put("data", cfData);
			if (stringlist.size() > 0) {
				map.put("maxNum", Collections.max(stringlist));
			} else {
				map.put("maxNum", 100);
			}
		}
		return map;
	}

	/**
	 * @description 跳转到评论审核页；
	 * @return
	 */
	@RequestMapping(value = "commentAudit")
	public String commentAudit(String token, String commentId, Model model, HttpServletRequest request) {
		model.addAttribute("token", token);
		model.addAttribute("commentId", commentId);
		return "modules/" + "index/pc_commentAudit";
	}

	/**
	 * @description 跳转到评论回复页；
	 * @return
	 */
	@RequestMapping(value = "commentReply")
	public String commentReply(String token, String commentId, Model model, HttpServletRequest request) {
		model.addAttribute("token", token);
		model.addAttribute("commentId", commentId);
		return "modules/" + "index/pc_commentReply";
	}

	/**
	 * @description 获取产品列表数据；
	 * @return
	 */
	@RequestMapping(value = "/productList", produces = "application/json;charset=UTF-8", method = { RequestMethod.GET })
	@ResponseBody
	@ApiOperation(value = "查询产品列表", httpMethod = "GET", notes = "查询产品列表", consumes = "application/x-www-form-urlencoded")
	public String productList(String token, String pageno, String pagesize, Model model, HttpServletRequest request) {
		if (StringUtils.isBlank(token)) {
			return JsonMapper.nonDefaultMapper().toJson(
					ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
		} else {
			int pageNo = pageno == null ? Global.DEFAULT_PAGENO : Integer.parseInt(pageno);
			int pageSize = pagesize == null ? Global.DEFAULT_PAGESIZE : Integer.parseInt(pagesize);
			String corpCode = "";
			User user = apiUserService.getUserByToken(token);
			// 超级管理默认查询所有企业，其他默认查询本企业产品
			if (!user.isAdmin()) {
				corpCode = user.getCompany().getId();
			}
			Page<TraceProduct> page = new Page<TraceProduct>(pageNo, pageSize);
			page = traceProductService.find(page, "", "", "1", corpCode, user);
			long count = 0;
			count = traceProductService.findCount("", "", "1", corpCode, user);
			List<TraceProduct> list = new ArrayList<TraceProduct>();
			for (int i = 0; i < page.getList().size(); i++) {
				TraceProduct product = page.getList().get(i);
				product.setCjrq(DateUtils.formatDate(product.getCreatTime(), "yyyy-MM-dd"));

				List<Object> bqList = traceCodeService.newGetBqsl(product.getId());
				int Sqs = 0;
				int Yjhs = 0;
				int Wjhs = 0;
				for (Object object : bqList) {
					Object[] obj = (Object[]) object;
					Sqs += Integer.parseInt(obj[0] == null ? "0" : obj[0].toString());
					Yjhs += Integer.parseInt(obj[1] == null ? "0" : obj[1].toString());
					Wjhs += Integer.parseInt(obj[2] == null ? "0" : obj[2].toString());
				}
				product.setSqs(String.valueOf(Sqs));
				product.setYjhs(String.valueOf(Yjhs));
				product.setWjhs(String.valueOf(Wjhs));
				product.setBatchCount(productBatchService.newGetBatchCount(product.getOfficeId(), product.getId(), "A", user));
				String imgUrl = Global.getConfig("sy_img_url");
				if (StringUtils.isNotBlank(product.getProductPic())) {
					if(!product.getProductPic().contains("http")){
						product.setProductPicUrl(imgUrl + product.getProductPic());
					}else{
						product.setProductPicUrl(product.getProductPic());
					}
				} else {
					product.setProductPicUrl("");
				}
				list.add(product);
			}
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("list", list);
			map.put("count", count);
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(map));
		}
	}
	
	/**
	 * @description 获取产品列表数据；
	 * @return
	 */
	@RequestMapping(value = "/productList1", produces = "application/json;charset=UTF-8", method = { RequestMethod.GET })
	@ResponseBody
	@ApiOperation(value = "查询产品列表(新)", httpMethod = "GET", notes = "查询产品列表(新)", consumes = "application/x-www-form-urlencoded")
	public String productList1(String token, String pageno, String pagesize, Model model, HttpServletRequest request) {
		if (StringUtils.isBlank(token)) {
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
		} else {
			int pageNo = pageno == null ? Global.DEFAULT_PAGENO : Integer.parseInt(pageno);
			int pageSize = pagesize == null ? Global.DEFAULT_PAGESIZE : Integer.parseInt(pagesize);
			String corpCode = "";
			User user = apiUserService.getUserByToken(token);
			// 超级管理默认查询所有企业，其他默认查询本企业产品
			if (!user.isAdmin()) {
				corpCode = user.getCompany().getId();
			}
			Page<TraceProduct> page = new Page<TraceProduct>(pageNo, pageSize);
			page = traceProductService.find(page, "", "", "1", corpCode, user);
			long count = 0;
			count = traceProductService.findCount("", "", "1", corpCode, user);
			List<TraceProduct> list = new ArrayList<TraceProduct>();
			for (int i = 0; i < page.getList().size(); i++) {
				TraceProduct product = page.getList().get(i);
				product.setCjrq(DateUtils.formatDate(product.getCreatTime(), "yyyy-MM-dd"));
				product.setAuditFlag(traceLableApplyService.getAuditFlagByProductId(product.getId()));

				List<Object> bqList = traceCodeService.newGetBqsl1(product.getId());
				int Sqs = 0;
				int Dsh = 0;
				int Yjhs = 0;
				int Wjhs = 0;
				for (Object object : bqList) {
					Object[] obj = (Object[]) object;
					Sqs += Integer.parseInt(obj[0] == null ? "0" : obj[0].toString());
					Dsh += Integer.parseInt(obj[1] == null ? "0" : obj[1].toString());
					Yjhs += Integer.parseInt(obj[2] == null ? "0" : obj[2].toString());
					Wjhs += Integer.parseInt(obj[3] == null ? "0" : obj[3].toString());
				}
				product.setSqs(String.valueOf(Sqs));
				product.setDsh(String.valueOf(Dsh));
				product.setYjhs(String.valueOf(Yjhs));
				product.setWjhs(String.valueOf(Wjhs));
				product.setBatchCount(productBatchService.newGetBatchCount(product.getOfficeId(), product.getId(), "A", user));
				String imgUrl = Global.getConfig("sy_img_url");
				if (StringUtils.isNotBlank(product.getProductPic())) {
					if(!product.getProductPic().contains("http")){
						product.setProductPicUrl(imgUrl + product.getProductPic());
					}else{
						product.setProductPicUrl(product.getProductPic());
					}
				} else {
					product.setProductPicUrl("");
				}
				list.add(product);
			}
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("list", list);
			map.put("count", count);
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(map));
		}
	}

	// 获取公司对应产品列表
	@RequestMapping(value = "getProductList", produces = "application/json;charset=UTF-8", method = {RequestMethod.GET })
	@ResponseBody
	@ApiOperation(value = "查询公司对应产品列表", httpMethod = "GET", notes = "查询公司对应产品列表", consumes = "application/x-www-form-urlencoded")
	public String getProductList(HttpServletRequest request, String token, String officeId, String pageno,
			String pagesize) {
		int pageNo = pageno == null ? Global.DEFAULT_PAGENO : Integer.parseInt(pageno);
		int pageSize = pagesize == null ? Global.DEFAULT_PAGESIZE : Integer.parseInt(pagesize);
		Page<TraceProduct> page = new Page<TraceProduct>(pageNo, pageSize);
		List<TraceProduct> list = new ArrayList<TraceProduct>();
		page = traceProductService.find(page, "", "", "1", officeId, "");
		long count = 0;
		count = traceProductService.findCount("", "", "1", officeId);
		if (page.getList() != null && page.getList().size() > 0) {
			for (int i = 0; i < page.getList().size(); i++) {
				TraceProduct product = page.getList().get(i);
				product.setCjrq(DateUtils.formatDate(product.getCreatTime(), "yyyy-MM-dd"));

				List<Object> bqList = traceCodeService.getBqsl(product.getId());
				Object[] obj = (Object[]) bqList.get(0);
				product.setSqs(obj[0] == null ? "0" : obj[0].toString());
				product.setYjhs(obj[1] == null ? "0" : obj[1].toString());
				product.setWjhs(obj[2] == null ? "0" : obj[2].toString());
				product.setBatchCount(productBatchService.getBatchCount(product.getOfficeId(), product.getId(), "A"));
				String imgUrl = Global.getConfig("sy_img_url");
				if (StringUtils.isNotBlank(product.getProductPic())) {
					if(!product.getProductPic().contains("http")){
						product.setProductPicUrl(imgUrl + product.getProductPic());
					}else{
						product.setProductPicUrl(product.getProductPic());
					}
				} else {
					product.setProductPicUrl("");
				}
				list.add(product);
			}
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("list", list);
			map.put("count", count);
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(map));
		} else {
			return JsonMapper.nonDefaultMapper()
					.toJson(ResultUtil.error(ResultEnum.SYSTEM_ERROR.getCode(), ResultEnum.SYSTEM_ERROR.getMessage()));
		}

	}
	
	// 获取公司对应产品列表
	@RequestMapping(value = "getProductList1", produces = "application/json;charset=UTF-8", method = {RequestMethod.GET })
	@ResponseBody
	@ApiOperation(value = "查询公司对应产品列表(新)", httpMethod = "GET", notes = "查询公司对应产品列表(新)", consumes = "application/x-www-form-urlencoded")
	public String getProductList1(HttpServletRequest request, String token, String officeId, String pageno,	String pagesize) {
		int pageNo = pageno == null ? Global.DEFAULT_PAGENO : Integer.parseInt(pageno);
		int pageSize = pagesize == null ? Global.DEFAULT_PAGESIZE : Integer.parseInt(pagesize);
		Page<TraceProduct> page = new Page<TraceProduct>(pageNo, pageSize);
		List<TraceProduct> list = new ArrayList<TraceProduct>();
		page = traceProductService.find(page, "", "", "1", officeId, "");
		long count = 0;
		count = traceProductService.findCount("", "", "1", officeId);
		if (page.getList() != null && page.getList().size() > 0) {
			for (int i = 0; i < page.getList().size(); i++) {
				TraceProduct product = page.getList().get(i);
				product.setCjrq(DateUtils.formatDate(product.getCreatTime(), "yyyy-MM-dd"));
				product.setAuditFlag(traceLableApplyService.getAuditFlagByProductId(product.getId()));
				List<Object> bqList = traceCodeService.newGetBqsl1(product.getId());
				int Sqs = 0;
				int Dsh = 0;
				int Yjhs = 0;
				int Wjhs = 0;
				for (Object object : bqList) {
					Object[] obj = (Object[]) object;
					Sqs += Integer.parseInt(obj[0] == null ? "0" : obj[0].toString());
					Dsh += Integer.parseInt(obj[1] == null ? "0" : obj[1].toString());
					Yjhs += Integer.parseInt(obj[2] == null ? "0" : obj[2].toString());
					Wjhs += Integer.parseInt(obj[3] == null ? "0" : obj[3].toString());
				}
				product.setSqs(String.valueOf(Sqs));
				product.setDsh(String.valueOf(Dsh));
				product.setYjhs(String.valueOf(Yjhs));
				product.setWjhs(String.valueOf(Wjhs));
				product.setBatchCount(productBatchService.getBatchCount(product.getOfficeId(), product.getId(), "A"));
				String imgUrl = Global.getConfig("sy_img_url");
				if (StringUtils.isNotBlank(product.getProductPic())) {
					if(!product.getProductPic().contains("http")){
						product.setProductPicUrl(imgUrl + product.getProductPic());
					}else{
						product.setProductPicUrl(product.getProductPic());
					}
				} else {
					product.setProductPicUrl("");
				}
				list.add(product);
			}
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("list", list);
			map.put("count", count);
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(map));
		} else {
			return JsonMapper.nonDefaultMapper()
					.toJson(ResultUtil.error(ResultEnum.SYSTEM_ERROR.getCode(), ResultEnum.SYSTEM_ERROR.getMessage()));
		}

	}

	/**
	 * @description 查询批次列表；
	 * @return
	 */
	@RequestMapping(value = "/batchList", produces = "application/json;charset=UTF-8", method = { RequestMethod.GET })
	@ResponseBody
	@ApiOperation(value = "查询批次列表", httpMethod = "GET", notes = "查询批次列表", consumes = "application/x-www-form-urlencoded")
	public String batchList(String token, String productId, Model model, HttpServletRequest request) {
		if (StringUtils.isBlank(token)) {
			return JsonMapper.nonDefaultMapper().toJson(
					ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
		String corpCode = "";
		User user = apiUserService.getUserByToken(token);
		// 超级管理默认查询所有企业，其他默认查询本企业批次
		if (StringUtils.isEmpty(corpCode)) {
			if (!user.isAdmin()) {
				corpCode = user.getCompany().getId();
			}
		}
		Page<ProductBatch> page = new Page<ProductBatch>(1, 10, -1);
		page = productBatchService.find(page, "", "", "", corpCode, productId, user);

		List<ProductBatch> list = new ArrayList<ProductBatch>();
		for (int i = 0; i < page.getList().size(); i++) {
			ProductBatch batch = page.getList().get(i);
			batch.setCjrq(DateUtils.formatDate(batch.getCreatTime(), "yyyy-MM-dd"));
			// 获取激活码
			List<Integer> jhdList = traceCodeService.getBqjhm(productId, batch.getId());
			String qjjhm = ApiUtil.getQjjhm(jhdList);
			batch.setJhm(qjjhm);

			// 获取已绑定标签数
			List<Integer> jhsList = traceCodeService.getYbdbqs(productId, batch.getId());
			String jhs = jhsList.get(0) + "";
			batch.setJhs(jhs);

			list.add(batch);
		}
		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(list));
	}
	
	/**
	 * @description 查询批次列表 不根据当前用户来查；
	 * @return
	 */
	@RequestMapping(value = "/getBatchList", produces = "application/json;charset=UTF-8", method = { RequestMethod.GET })
	@ResponseBody
	@ApiOperation(value = "查询批次列表", httpMethod = "GET", notes = "查询批次列表", consumes = "application/x-www-form-urlencoded")
	public String getBatchList(String token, String productId, Model model, HttpServletRequest request) {
		if (StringUtils.isBlank(token)) {
			return JsonMapper.nonDefaultMapper().toJson(
					ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
		User user = apiUserService.getUserByToken(token);
		Page<ProductBatch> page = new Page<ProductBatch>(1, 10, -1);
		page = productBatchService.find(page, "", "", "", "", productId, user);

		List<ProductBatch> list = new ArrayList<ProductBatch>();
		for (int i = 0; i < page.getList().size(); i++) {
			ProductBatch batch = page.getList().get(i);
			batch.setCjrq(DateUtils.formatDate(batch.getCreatTime(), "yyyy-MM-dd"));
			// 获取激活码
			List<Integer> jhdList = traceCodeService.getBqjhm(productId, batch.getId());
			String qjjhm = ApiUtil.getQjjhm(jhdList);
			batch.setJhm(qjjhm);

			// 获取已绑定标签数
			List<Integer> jhsList = traceCodeService.getYbdbqs(productId, batch.getId());
			String jhs = jhsList.get(0) + "";
			batch.setJhs(jhs);

			list.add(batch);
		}
		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(list));
	}
	
	/**
	 * @description 查询批次列表 不根据当前用户来查；
	 * @return
	 */
	@RequestMapping(value = "/getBatchList1", produces = "application/json;charset=UTF-8", method = { RequestMethod.GET })
	@ResponseBody
	@ApiOperation(value = "查询批次列表", httpMethod = "GET", notes = "查询批次列表", consumes = "application/x-www-form-urlencoded")
	public String getBatchList1(String token, String productId, Model model, HttpServletRequest request) {
		if (StringUtils.isBlank(token)) {
			return JsonMapper.nonDefaultMapper().toJson(
					ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
		User user = apiUserService.getUserByToken(token);
		Page<ProductBatch> page = new Page<ProductBatch>(1, 10, -1);
		page = productBatchService.find(page, "", "", "", "", productId, user);

		List<ProductBatch> list = new ArrayList<ProductBatch>();
		for (int i = 0; i < page.getList().size(); i++) {
			ProductBatch batch = page.getList().get(i);
			batch.setCjrq(DateUtils.formatDate(batch.getCreatTime(), "yyyy-MM-dd"));
			batch.setAuditFlag(traceLableApplyService.getAuditFlagByBatchId(batch.getId()));
			
			List<Object> bqList = traceCodeService.newGetBqsl2(batch.getId());
			int Dsh = 0;
			int Yjhs = 0;
			int Wjhs = 0;
			for (Object object : bqList) {
				Object[] obj = (Object[]) object;
				Dsh += Integer.parseInt(obj[1] == null ? "0" : obj[1].toString());
				Yjhs += Integer.parseInt(obj[2] == null ? "0" : obj[2].toString());
				Wjhs += Integer.parseInt(obj[3] == null ? "0" : obj[3].toString());
			}
			batch.setDsh(String.valueOf(Dsh));
			batch.setYjhs(String.valueOf(Yjhs));
			batch.setWjhs(String.valueOf(Wjhs));
			
			// 获取激活码
//			List<Integer> jhdList = traceCodeService.getBqjhm(productId, batch.getId());
//			String qjjhm = ApiUtil.getQjjhm(jhdList);
//			batch.setJhm(qjjhm);

			// 获取已绑定标签数
//			List<Integer> jhsList = traceCodeService.getYbdbqs(productId, batch.getId());
//			String jhs = jhsList.get(0) + "";
//			batch.setJhs(jhs);

			list.add(batch);
		}
		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(list));
	}

	/**
	 * @description 获取主题列表数据；
	 * @return
	 */
	@RequestMapping(value = "/resumeThemeList", produces = "application/json;charset=UTF-8", method = {
			RequestMethod.GET })
	@ResponseBody
	@ApiOperation(value = "查询主题列表", httpMethod = "GET", notes = "查询主题列表", consumes = "application/x-www-form-urlencoded")
	public String resumeThemeList(String token, Model model, HttpServletRequest request) {
		if (StringUtils.isBlank(token)) {
			return JsonMapper.nonDefaultMapper().toJson(
					ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
		} else {
			Page<TraceResumeTheme> page = new Page<TraceResumeTheme>(1, 10, -1);
			page = traceResumeThemeService.find(page, "", "", "", "", "");
			List<TraceResumeTheme> list = new ArrayList<TraceResumeTheme>();
			for (int i = 0; i < page.getList().size(); i++) {
				TraceResumeTheme traceResumeTheme = page.getList().get(i);

				// String imgUrl = Global.getConfig("sy_img_url");
				if (StringUtils.isNotBlank(traceResumeTheme.getThemeImg())) {
					traceResumeTheme.setThemeImgUrl(traceResumeTheme.getThemeImg());
				} else {
					traceResumeTheme.setThemeImgUrl("");
				}
				list.add(traceResumeTheme);
			}
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(list));
		}
	}

	/**
	 * 上传图片
	 * 
	 * @param request
	 * @param file
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(value = "uploadImage")
	@ResponseBody
	public String uploadImage(MultipartHttpServletRequest request, String sxid, RedirectAttributes redirectAttributes) {
		String path = "";
		String lj = "";
		String fjData = "";
		if (StringUtils.isBlank(sxid)) {
			sxid = "";
		}
		MultipartFile file = (MultipartFile) request.getFile("file" + sxid);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		SimpleDateFormat sdfdate = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		try {
			if (!file.getOriginalFilename().isEmpty()) {
				// 绝对路径
				String save_path = request.getSession().getServletContext().getRealPath("");
				lj = "/upload/trace/" + sdf.format(new Date()) + "/";
				path = save_path.substring(0, save_path.lastIndexOf(File.separator)) + lj;
				File fiel = new File(path);
				if (!fiel.exists()) {
					fiel.mkdirs();// 如不存在路径则创建路径
				}

				String time = sdfdate.format(new Date());
				String name = file.getOriginalFilename();
				String extName = name.substring(name.lastIndexOf("."));
				// 生成文件名
				String new_name = time + extName;
				// 转存文件
				file.transferTo(new File(path + new_name));

				fjData = lj + new_name;
				return "success|" + fjData;
			} else {
				return "error";
			}

		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}
	}

	// ---------------------------------------------------产品管理----------------------------------------------------------------//

	/**
	 * @description 跳转到新增产品页面；
	 * @return
	 */
	@RequestMapping(value = "addProductPage")
	public String addProductPage(String token, String officeId, Model model, HttpServletRequest request) {
		model.addAttribute("token", token);
		model.addAttribute("product", new TraceProduct());

		Page<TraceResumeTheme> page = new Page<TraceResumeTheme>(1, 10, -1);
		page = traceResumeThemeService.find(page, "", "", "", "", "");
		List<TraceResumeTheme> list = new ArrayList<TraceResumeTheme>();
		for (int i = 0; i < page.getList().size(); i++) {
			TraceResumeTheme traceResumeTheme = page.getList().get(i);
			// String imgUrl = Global.getConfig("sy_img_url");
			if (StringUtils.isNotBlank(traceResumeTheme.getThemeImg())) {
				traceResumeTheme.setThemeImgUrl(traceResumeTheme.getThemeImg());
			} else {
				traceResumeTheme.setThemeImgUrl("");
			}
			list.add(traceResumeTheme);
		}

		// 查询当前用户下的分产品信息
		User user = apiUserService.getUserByToken(token);
		List<Office> findAll = null;
		if (user.isAdmin()) {
			findAll = officeService.findAllThree(user);
		} else {
			findAll = officeService.findChildrenOfficeList(user.getCompany().getId());
		}
		model.addAttribute("themeList", list);
		model.addAttribute("officeList", findAll);
		model.addAttribute("officeId", officeId);
		return "modules/" + "index/pc_product_add_new";
	}

	/**
	 * @description 跳转到修改产品页面；
	 * @return
	 */
	@RequestMapping(value = "editProductPage")
	public String editProductPage(String token, String productId, Model model, HttpServletRequest request) {
		String imgUrl = Global.getConfig("sy_img_url");
		model.addAttribute("token", token);
		TraceProduct traceProduct = traceProductService.get(productId);
		if (StringUtils.isNotBlank(traceProduct.getProductPic())) {
			if(!traceProduct.getProductPic().contains("http")){
				traceProduct.setProductPicUrl(imgUrl + traceProduct.getProductPic());
			}else{
				traceProduct.setProductPicUrl(traceProduct.getProductPic());
			}
		} else {
			traceProduct.setProductPicUrl("");
		}
		model.addAttribute("product", traceProduct);

		Page<TraceResumeTheme> page = new Page<TraceResumeTheme>(1, 10, -1);
		page = traceResumeThemeService.find(page, "", "", "", "", "");
		List<TraceResumeTheme> list = new ArrayList<TraceResumeTheme>();
		for (int i = 0; i < page.getList().size(); i++) {
			TraceResumeTheme traceResumeTheme = page.getList().get(i);

			if (StringUtils.isNotBlank(traceResumeTheme.getThemeImg())) {
				traceResumeTheme.setThemeImgUrl(traceResumeTheme.getThemeImg());
			} else {
				traceResumeTheme.setThemeImgUrl("");
			}
			list.add(traceResumeTheme);
		}

		// 查询当前用户下的分产品信息
		User user = apiUserService.getUserByToken(token);
		List<Office> findAll = null;
		if (user.isAdmin()) {
			findAll = officeService.findAllThree(user);
		} else {
			findAll = officeService.findChildrenOfficeList(user.getCompany().getId());
		}
		model.addAttribute("themeList", list);
		model.addAttribute("officeList", findAll);
		model.addAttribute("officeId", traceProduct.getOfficeId());
		return "modules/" + "index/pc_product_edit";
	}

	/**
	 * 产品新增（第一次保存）
	 * 
	 * @param redirectAttributes
	 * @param traceProduct
	 * @param productId
	 *            产品ID 新增为空 修改不为空
	 * @param officeId
	 *            通过下拉框选择公司得到公司ID
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/firstSave", produces = "application/json;charset=UTF-8", method = { RequestMethod.GET })
	@ResponseBody
	@ApiOperation(value = "产品新增（第一次保存）", httpMethod = "GET", notes = "产品新增（第一次保存）", consumes = "application/x-www-form-urlencoded")
	public String firstSave(RedirectAttributes redirectAttributes, TraceProduct traceProduct, String productId,
			String officeId, Model model) {
		if (StringUtils.isBlank(traceProduct.getToken())) {
			return JsonMapper.nonDefaultMapper().toJson(
					ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
		} else {
			try {
				if ("".equals(officeId) || officeId == null) {
					return JsonMapper.nonDefaultMapper().toJson(
							ResultUtil.error(ResultEnum.SYSTEM_ERROR.getCode(), ResultEnum.SYSTEM_ERROR.getMessage()));
				}
				User user = apiUserService.getUserByToken(traceProduct.getToken());
				if (StringUtils.isNotBlank(productId)) {
					TraceProduct product = traceProductService.get(productId);
					product.setOfficeId(officeId);
					product.setProductName(traceProduct.getProductName());
					product.setProductTitle(traceProduct.getProductName());
					product.setProductPic(traceProduct.getProductPic());
					product.setProductDiscription(StringEscapeUtils.unescapeHtml(traceProduct.getProductDiscription()));
					product.setThemeId(traceProduct.getThemeId());
					product.setUpdateUserid(user.getId());
					product.setUpdateTime(new Date());
					TraceProduct tempProduct = traceProductService.find(product, officeId);
					if (tempProduct != null) {
						return JsonMapper.nonDefaultMapper()
								.toJson(ResultUtil.error(ResultEnum.OTHER_PRODUCT_NAME_EXIST.getCode(),
										ResultEnum.OTHER_PRODUCT_NAME_EXIST.getMessage()));
					} else {
						traceProductService.save(product);
					}
				} else {
					Office office = officeService.get(officeId);
					// 分配产品的唯一编码
					String productCode = UniqueCodeUtil.getUniqueCode("TraceProduct", "productCode", office.getCode());
					traceProduct
							.setProductCode(office.getCode() + "-" + UniqueCodeUtil.PRODUCT_PRE + "-" + productCode);
					traceProduct.setProductTitle(traceProduct.getProductName());
					traceProduct.setStatus(TraceProduct.OPEN);
					traceProduct.setCreatUserid(user.getId());
					traceProduct.setOfficeId(officeId);
					traceProduct.setProductDiscription(
							StringEscapeUtils.unescapeHtml(traceProduct.getProductDiscription()));
					TraceProduct tempProduct = traceProductService.find(traceProduct, officeId);
					if (tempProduct != null) {
						return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(
								ResultEnum.PRODUCT_NAME_EXIST.getCode(), ResultEnum.PRODUCT_NAME_EXIST.getMessage()));
					} else {
						traceProductService.save(traceProduct);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				return JsonMapper.nonDefaultMapper().toJson(
						ResultUtil.error(ResultEnum.SYSTEM_ERROR.getCode(), ResultEnum.SYSTEM_ERROR.getMessage()));
			}
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(traceProduct));
		}
	}

	/**
	 * 产品新增（第二次保存）
	 * 
	 * @param traceProduct
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(value = "/secordSave", produces = "application/json;charset=UTF-8", method = { RequestMethod.GET })
	@ResponseBody
	@ApiOperation(value = "产品新增（第二次保存）", httpMethod = "GET", notes = "产品新增（第二次保存）", consumes = "application/x-www-form-urlencoded")
	public String secordSave(String id, String token, String themeId, Model model,
			RedirectAttributes redirectAttributes) {
		if (StringUtils.isBlank(token)) {
			return JsonMapper.nonDefaultMapper().toJson(
					ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
		} else {
			User user = apiUserService.getUserByToken(token);
			TraceProduct traceProduct = traceProductService.get(id);
			traceProduct.setThemeId(themeId);
			traceProduct.setUpdateUserid(user.getId());
			traceProduct.setUpdateTime(new Date());
			try {
				traceProductService.save(traceProduct);
			} catch (Exception e) {
				e.printStackTrace();
				return JsonMapper.nonDefaultMapper().toJson(
						ResultUtil.error(ResultEnum.SYSTEM_ERROR.getCode(), ResultEnum.SYSTEM_ERROR.getMessage()));
			}
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(traceProduct));
		}
	}

	/**
	 * 产品新增（第三次保存）
	 * 
	 * @param id,token,result,modelArr,propertyArr,mkArr,rjArr,rjresult
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(value = "/thirdSave", produces = "application/json;charset=UTF-8", method = { RequestMethod.POST })
	@ResponseBody
	@ApiOperation(value = "产品新增（第三次保存）", httpMethod = "POST", notes = "产品新增（第三次保存）", consumes = "application/x-www-form-urlencoded")
	public String thirdSave(String id, String token, String batchId, String isNew, String ifxz, String[] result,
			String[] modelArr, String[] propertyArr, String[] mkArr, String[] rjArr, String[] rjresult, Model model,
			RedirectAttributes redirectAttributes, String modelId) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
		if (StringUtils.isBlank(token)) {
			return JsonMapper.nonDefaultMapper().toJson(
					ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
		System.out.println("111        "+sdf.format(new Date()));
		Map map = apiUserService.findUserByToken(token);
		System.out.println("222        "+sdf.format(new Date()));
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

	/**
	 * 获取新增批次初始化数据 add by liw 2019-01-18
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "getBatchAddPre", produces = "application/json;charset=UTF-8", method = {
			RequestMethod.GET })
	@ResponseBody
	@ApiOperation(value = "根据产品ID获取新增批次初始化数据", httpMethod = "GET", notes = "根据产品ID获取新增批次初始化数据，包括产品、主题、模块、属性", consumes = "application/x-www-form-urlencoded")
	public String getBatchAddPre(HttpServletRequest request, @RequestParam String productId) {
		if (StringUtils.isNotBlank(productId)) {
			TraceProduct traceProduct = traceProductService.get(productId);
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
				if (!traceModel.getModelName().equals("用户评价")) {
					traceModel.setTracePropertyList(propertyList);
				}
			}
			traceProduct.setTraceModelList(traceModelList);

			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(traceProduct));
		} else {
			return JsonMapper.nonDefaultMapper().toJson(
					ResultUtil.error(ResultEnum.DATA_NOT_EXIST.getCode(), ResultEnum.DATA_NOT_EXIST.getMessage()));
		}
	}

	/**
	 * 产品删除
	 * 
	 * @param id，token
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(value = "/deleteProduct", produces = "application/json;charset=UTF-8", method = {
			RequestMethod.POST })
	@ResponseBody
	@ApiOperation(value = "产品删除", httpMethod = "POST", notes = "产品删除", consumes = "application/x-www-form-urlencoded")
	public String deleteProduct(String id, String token, Model model, RedirectAttributes redirectAttributes) {
		if (StringUtils.isBlank(token)) {
			return JsonMapper.nonDefaultMapper().toJson(
					ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
		try {
			User user = apiUserService.getUserByToken(token);
			String resultFlag = traceProductService.deleteProductWithModelsAndProperties(id, user);
			// 返回1 表名存在批次数据 不能删除此产品
			if (StringUtils.isNotBlank(resultFlag) && "1".equals(resultFlag)) {
				return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.PRODUCT_BATCH_EXIST.getCode(),
						ResultEnum.PRODUCT_BATCH_EXIST.getMessage()));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return JsonMapper.nonDefaultMapper()
					.toJson(ResultUtil.error(ResultEnum.SYSTEM_ERROR.getCode(), ResultEnum.SYSTEM_ERROR.getMessage()));
		}
		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success());
	}

	// ---------------------------------------------------批次管理----------------------------------------------------------------//
	/**
	 * @description 跳转到产品批次编辑页面； token, batchId, isNew(1-新增，2-修改)
	 * @return
	 */
	@RequestMapping(value = "editBatchPage")
	public String editBatchPage(String token, String isNew, String batchId, String productId, Model model,
			HttpServletRequest request) {
		model.addAttribute("token", token);
		model.addAttribute("isNew", isNew);
		if (isNew.equals("2") && StringUtils.isNotBlank(batchId)) {
			ProductBatch productBatch = productBatchService.get(batchId);
			model.addAttribute("productBatch", productBatch);
		} else {
			model.addAttribute("productBatch", new ProductBatch());
		}

		String imgUrl = Global.getConfig("sy_img_url");
		TraceProduct traceProduct = traceProductService.get(productId);
		if (StringUtils.isNotBlank(traceProduct.getProductPic())) {
			if(!traceProduct.getProductPic().contains("http")){
				traceProduct.setProductPicUrl(imgUrl + traceProduct.getProductPic());
			}else{
				traceProduct.setProductPicUrl(traceProduct.getProductPic());
			}
		} else {
			traceProduct.setProductPicUrl("");
		}
		model.addAttribute("product", traceProduct);
		return "modules/" + "index/pc_batch_edit_new";
	}

	/**
	 * @description 跳转到产品批次新增页面； token, batchId, isNew(1-新增，2-修改)
	 * @return
	 */
	@RequestMapping(value = "addBatchPage")
	public String addBatchPage(String token, String isNew, String batchId, String productId, Model model,
			HttpServletRequest request) {
		model.addAttribute("token", token);
		model.addAttribute("isNew", isNew);
		if (isNew.equals("2") && StringUtils.isNotBlank(batchId)) {
			ProductBatch productBatch = productBatchService.get(batchId);
			model.addAttribute("productBatch", productBatch);
		} else {
			model.addAttribute("productBatch", new ProductBatch());
		}

		String imgUrl = Global.getConfig("sy_img_url");
		TraceProduct traceProduct = traceProductService.get(productId);
		if (StringUtils.isNotBlank(traceProduct.getProductPic())) {
			if(!traceProduct.getProductPic().contains("http")){
				traceProduct.setProductPicUrl(imgUrl + traceProduct.getProductPic());
			}else{
				traceProduct.setProductPicUrl(traceProduct.getProductPic());
			}
		} else {
			traceProduct.setProductPicUrl("");
		}
		model.addAttribute("product", traceProduct);
		return "modules/" + "index/pc_batch_add_new";
	}

	// TODO 新增批次编辑
	/**
	 * 新增批次编辑 获取批次所有数据以及未添加的模块数据，不包括生长记录属性值数据 add by liw 2019-01-28
	 * 
	 * @param productId,batchId,isNew
	 * @return
	 */
	@RequestMapping(value = "getBatchInformation", produces = "application/json;charset=UTF-8", method = {
			RequestMethod.GET })
	@ResponseBody
	@ApiOperation(value = "获取批次所有数据以及未添加的模块数据，不包括生长记录属性值数据", httpMethod = "GET", notes = "获取批次所有数据以及未添加的模块数据，不包括生长记录属性值数据，为配置新的模块关联做准备", consumes = "application/x-www-form-urlencoded")
	public String getBatchInformation(HttpServletRequest request, String productId, String batchId, String isNew) {
		ProductBatch batch = null;
		if (StringUtils.isNotBlank(batchId) && isNew.equals("2")) {
			batch = productBatchService.get(batchId);
		}
		if (StringUtils.isBlank(batchId) && isNew.equals("1")) {
			batch = productBatchService.getProductBatchByProductId(productId);
		}
		/*
		 * else{ batch = productBatchService.getProductBatchByProductId(productId);
		 * if(batch == null) { batch =
		 * productBatchService.get("a1ef5259fa524051962a86d78d88db7e"); } }
		 */
		// 把所有数据 均封装至product里，方便操作同样的对象及属性
		TraceProduct traceProduct = traceProductService.get(productId);
		List<TraceModel> traceModelList = new ArrayList<TraceModel>();
		if (batch != null) {
			traceProduct.setProductBatch(batch);
			// 批次对应所有模块数据
			List<TraceModelData> traceModelDataList = traceModelService.findTraceModelDatas(null, batch.getId(), "1");
			if (null != traceModelDataList && 0 != traceModelDataList.size()) {
				for (Iterator<TraceModelData> iterator = traceModelDataList.iterator(); iterator.hasNext();) {
					TraceModelData traceModelData = (TraceModelData) iterator.next();
					TraceModel traceModel = traceModelService.get(traceModelData.getModelId());
					traceModel.setStates(traceModelData.getStates());
					List<TracePropertyNew> tracePropertyList = new ArrayList<TracePropertyNew>();// 模块对应属性
					// 生长日记模块
					if (StringUtils.isNotBlank(traceModel.getModelShowType()) && "2".equals(traceModel.getModelShowType())) {
						TraceModelData modelDataGrow = traceModelService.getTraceModelDataGrowByBatchId_new(batch.getId(), traceModelData.getModelId());
						if (null != modelDataGrow) {
							// 读取属性和属性值 封装到属性对象中
							List<TraceModelDataGroup> modelDataGroupList = traceModelService.findTraceModelDataGroupByModelDataId(modelDataGrow.getId());
							for (Iterator<TraceModelDataGroup> iterator1 = modelDataGroupList.iterator(); iterator1.hasNext();) {
								TraceModelDataGroup traceModelDataGroup = (TraceModelDataGroup) iterator1.next();
								List<TracePropertyData> propertyDataList = tracePropertyService.findPagePropertyDataListByModelDataGroupId(traceModelDataGroup.getId());
								TracePropertyNew tracePropertyNew = new TracePropertyNew();
								TracePropertyNew property = new TracePropertyNew();
								List<TracePropertyNew> tracePropertyList2 = new ArrayList<TracePropertyNew>();
								for (Iterator<TracePropertyData> iterator2 = propertyDataList.iterator(); iterator2.hasNext();) {
									TracePropertyData tracePropertyData = (TracePropertyData) iterator2.next();
									property = tracePropertyNewDao.get(tracePropertyData.getPropertyId());
									property = tracePropertyService.evict(property);
									if (StringUtils.isNotBlank(tracePropertyData.getPropertyValue())) {
										property.setPropertyValue(tracePropertyData.getPropertyValue());
									} else {
										property.setPropertyValue("");
									}
									tracePropertyList2.add(property);
								}
								tracePropertyNew.setList(tracePropertyList2);
								tracePropertyList.add(tracePropertyNew);
							}

						}
					} else {
						// 其他模块数据 读取属性和属性值 封装到属性对象中
						List<TracePropertyData> propertyDataList = tracePropertyService
								.findPropertyDataListByModelDataId(traceModelData.getId());
						for (Iterator<TracePropertyData> iterator2 = propertyDataList.iterator(); iterator2
								.hasNext();) {
							TracePropertyData tracePropertyData = (TracePropertyData) iterator2.next();
							TracePropertyNew property = tracePropertyNewDao.get(tracePropertyData.getPropertyId());
							if (StringUtils.isNotBlank(tracePropertyData.getPropertyValue())) {
								property.setPropertyValue(tracePropertyData.getPropertyValue());
							} else {
								property.setPropertyValue("");
							}
							tracePropertyList.add(property);
						}
					}
					if (!traceModel.getModelName().equals("用户评价")) {
						traceModel.setTracePropertyNewList(tracePropertyList);
					}
					traceModelList.add(traceModel);
				}
				traceProduct.setTraceModelList(traceModelList);
				return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(traceProduct));
			} else {
				return JsonMapper.nonDefaultMapper()
						.toJson(ResultUtil.error(ResultEnum.PRODUCT_MODEL_NOT_EXIST_ERROR.getCode(),
								ResultEnum.PRODUCT_MODEL_NOT_EXIST_ERROR.getMessage()));
			}
		} else {
			// 模块
			TraceResumeTheme traceResumeTheme = traceResumeThemeService.get(traceProduct.getThemeId());
			if (StringUtils.isNotBlank(traceResumeTheme.getTemplet_product_id())) {
				traceModelList = traceModelService.findTraceModelsByProductId(traceResumeTheme.getTemplet_product_id());
			} else {
				traceModelList = traceModelService.findTraceModelsByProductId("ddc96d3f55474fdb85ecd6706fe45379");
			}
			for (Iterator<TraceModel> iterator = traceModelList.iterator(); iterator.hasNext();) {
				TraceModel traceModel = (TraceModel) iterator.next();
				// 获取模块对应属性
				List<TraceProperty> propertyList = tracePropertyService.findPropertyListByModelId(traceModel.getId());
				if (!traceModel.getModelName().equals("用户评价")) {
					traceModel.setTracePropertyList(propertyList);
				}
			}
			traceProduct.setTraceModelList(traceModelList);
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(traceProduct));
		}
	}

	// TODO 新增产品录入批次数据
	/**
	 * 新增产品录入批次数据 获取批次所有数据以及未添加的模块数据，不包括生长记录属性值数据 add by liw 2019-01-28
	 * 
	 * @param productId,batchId,isNew
	 * @return
	 */
	@RequestMapping(value = "getBatchInformationNew", produces = "application/json;charset=UTF-8", method = {
			RequestMethod.GET })
	@ResponseBody
	@ApiOperation(value = "获取批次所有数据以及未添加的模块数据，不包括生长记录属性值数据", httpMethod = "GET", notes = "获取批次所有数据以及未添加的模块数据，不包括生长记录属性值数据，为配置新的模块关联做准备", consumes = "application/x-www-form-urlencoded")
	public String getBatchInformationNew(HttpServletRequest request, String productId, String batchId, String isNew) {
		ProductBatch batch = null;
		if (StringUtils.isNotBlank(batchId) && isNew.equals("2")) {
			batch = productBatchService.get(batchId);
		}
		if (StringUtils.isBlank(batchId) && isNew.equals("1")) {
			batch = productBatchService.getProductBatchByProductId(productId);
		}
		// 把所有数据 均封装至product里，方便操作同样的对象及属性
		TraceProduct traceProduct = traceProductService.get(productId);
		List<TraceModel> traceModelList = new ArrayList<TraceModel>();
		if (batch != null) {
			traceProduct.setProductBatch(batch);
			// 批次对应所有模块数据
			List<TraceModelData> traceModelDataList = traceModelService.findTraceModelDatas(null, batch.getId(), "1");
			if (null != traceModelDataList && 0 != traceModelDataList.size()) {
				for (Iterator<TraceModelData> iterator = traceModelDataList.iterator(); iterator.hasNext();) {
					TraceModelData traceModelData = (TraceModelData) iterator.next();
					TraceModel traceModel = traceModelService.get(traceModelData.getModelId());
					traceModel.setStates(traceModelData.getStates());
					List<TracePropertyNew> tracePropertyList = new ArrayList<TracePropertyNew>();// 模块对应属性
					// 生长日记模块
					if (StringUtils.isNotBlank(traceModel.getModelShowType())
							&& "2".equals(traceModel.getModelShowType())) {
						TraceModelData modelDataGrow = traceModelService.getTraceModelDataGrowByBatchId(batch.getId());
						if (null != modelDataGrow) {
							// 读取属性和属性值 封装到属性对象中
							List<TraceModelDataGroup> modelDataGroupList = traceModelService
									.find(modelDataGrow.getId());
							for (Iterator<TraceModelDataGroup> iterator1 = modelDataGroupList.iterator(); iterator1
									.hasNext();) {
								TraceModelDataGroup traceModelDataGroup = (TraceModelDataGroup) iterator1.next();
								List<TracePropertyData> propertyDataList = tracePropertyService
										.findPagePropertyDataListByModelDataGroupId(traceModelDataGroup.getId());
								TracePropertyNew property = new TracePropertyNew();
								for (Iterator<TracePropertyData> iterator2 = propertyDataList.iterator(); iterator2
										.hasNext();) {
									TracePropertyData tracePropertyData = (TracePropertyData) iterator2.next();
									property = tracePropertyNewDao.get(tracePropertyData.getPropertyId());
									property = tracePropertyService.evict(property);
									if (StringUtils.isNotBlank(tracePropertyData.getPropertyValue())) {
										property.setPropertyValue(tracePropertyData.getPropertyValue());
									} else {
										property.setPropertyValue("");
									}
									tracePropertyList.add(property);
								}

							}
						}
					} else {
						// 其他模块数据 读取属性和属性值 封装到属性对象中
						List<TracePropertyData> propertyDataList = tracePropertyService
								.findPropertyDataListByModelDataId(traceModelData.getId());
						for (Iterator<TracePropertyData> iterator2 = propertyDataList.iterator(); iterator2
								.hasNext();) {
							TracePropertyData tracePropertyData = (TracePropertyData) iterator2.next();
							TracePropertyNew property = tracePropertyNewDao.get(tracePropertyData.getPropertyId());
							if (StringUtils.isNotBlank(tracePropertyData.getPropertyValue())) {
								property.setPropertyValue(tracePropertyData.getPropertyValue());
							} else {
								property.setPropertyValue("");
							}
							tracePropertyList.add(property);
						}
					}
					if (!traceModel.getModelName().equals("用户评价")) {
						traceModel.setTracePropertyNewList(tracePropertyList);
					}
					traceModelList.add(traceModel);
				}
				traceProduct.setTraceModelList(traceModelList);
				return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(traceProduct));
			} else {
				return JsonMapper.nonDefaultMapper()
						.toJson(ResultUtil.error(ResultEnum.PRODUCT_MODEL_NOT_EXIST_ERROR.getCode(),
								ResultEnum.PRODUCT_MODEL_NOT_EXIST_ERROR.getMessage()));
			}
		} else {
			// 模块
			TraceResumeTheme traceResumeTheme = traceResumeThemeService.get(traceProduct.getThemeId());
			if (StringUtils.isNotBlank(traceResumeTheme.getTemplet_product_id())) {
				traceModelList = traceModelService.findTraceModelsByProductId(traceResumeTheme.getTemplet_product_id());
			} else {
				traceModelList = traceModelService.findTraceModelsByProductId("ddc96d3f55474fdb85ecd6706fe45379");
			}
			for (Iterator<TraceModel> iterator = traceModelList.iterator(); iterator.hasNext();) {
				TraceModel traceModel = (TraceModel) iterator.next();
				// 获取模块对应属性
				List<TraceProperty> propertyList = tracePropertyService.findPropertyListByModelId(traceModel.getId());
				if (!traceModel.getModelName().equals("用户评价")) {
					traceModel.setTracePropertyList(propertyList);
				}
			}
			traceProduct.setTraceModelList(traceModelList);
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(traceProduct));
		}
	}

	/**
	 * 查询评论列表
	 * 
	 * @param request
	 * @param response
	 * @param productName
	 * @param batchCode
	 * @param pageno
	 * @param pagesize
	 * @return
	 * @throws ParseException
	 */
	@RequestMapping(value = "/commentlist", produces = "application/json;charset=UTF-8", method = { RequestMethod.GET })
	@ResponseBody
	@ApiOperation(value = "查询评论列表", httpMethod = "GET", notes = "查询评论列表", consumes = "application/x-www-form-urlencoded")
	public String commentlist(HttpServletRequest request, HttpServletResponse response, Model model, String token,
			String officeId, Integer pageNo, Integer pageSize) throws ParseException {
		response.setContentType("application/json; charset=UTF-8");
		int pageNo3 = pageNo == null ? Global.DEFAULT_PAGENO : pageNo;
		int pageSize3 = pageSize == null ? Global.DEFAULT_PAGESIZE : pageSize;
		String corpCode = "";
		Page<TraceComment> page = new Page<TraceComment>(pageNo3, pageSize3);
		if (StringUtils.isBlank(token)) {
			return JsonMapper.nonDefaultMapper().toJson(
					ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
		User user = apiUserService.getUserByToken(token);
		if (StringUtils.isNotEmpty(officeId)) {
			corpCode = officeId;
		} else {
			// 超级管理默认查询所有企业，其他默认查询本企业批次
			if (!user.isAdmin()) {
				corpCode = user.getCompany().getId();
			}
		}
		page = traceCommentService.find2(page, corpCode, user);
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
			List<String> commentPicUrlList = new ArrayList<String>();
			String commentPic = comment.getCommentPic();
			if(StringUtils.isNotBlank(commentPic)){
				String commentPics[] = commentPic.split(",");
				for (int j = 0; j < commentPics.length; j++) {
					commentPicUrlList.add(imgUrl + commentPics[j]);
					comment.setCommentPicUrlList(commentPicUrlList);
				}
			}else{
				comment.setCommentPicUrlList(commentPicUrlList);
			}
			list.add(comment);
		}
		page.setList(list);
		if (list.size() > 0) {
			model.addAttribute("totalQuantity", page.getList().size());
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(page));
		} else {
			return JsonMapper.nonDefaultMapper().toJson(
					ResultUtil.error(ResultEnum.DATA_COUNT_ZERO.getCode(), ResultEnum.DATA_COUNT_ZERO.getMessage()));
		}

	}

	/**
	 * 查询好评高分评论列表
	 * 
	 * @param request
	 * @param response
	 * @param productName
	 * @param batchCode
	 * @param pageno
	 * @param pagesize
	 * @return
	 * @throws ParseException
	 */
	@RequestMapping(value = "/commentHighScorelist", produces = "application/json;charset=UTF-8", method = {
			RequestMethod.GET })
	@ResponseBody
	@ApiOperation(value = "查询评论列表", httpMethod = "GET", notes = "查询评论列表", consumes = "application/x-www-form-urlencoded")
	public String commentHighScorelist(HttpServletRequest request, HttpServletResponse response, String token,
			String officeId, Integer pageNo, Integer pageSize) throws ParseException {
		response.setContentType("application/json; charset=UTF-8");
		int pageNo3 = pageNo == null ? Global.DEFAULT_PAGENO : pageNo;
		int pageSize3 = pageSize == null ? Global.DEFAULT_PAGESIZE : pageSize;
		String corpCode = "";
		Page<TraceComment> page = new Page<TraceComment>(pageNo3, pageSize3);
		if (StringUtils.isBlank(token)) {
			return JsonMapper.nonDefaultMapper().toJson(
					ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
		User user = apiUserService.getUserByToken(token);
		if (token != null) {
			if (StringUtils.isNotEmpty(officeId)) {
				corpCode = officeId;
			} else {
				// 超级管理默认查询所有企业，其他默认查询本企业批次
				if (!user.isAdmin()) {
					corpCode = user.getCompany().getId();
				}
			}
		}
		page = traceCommentService.findhigh(page, corpCode, user);
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
			List<String> commentPicUrlList = new ArrayList<String>();
			String commentPic = comment.getCommentPic();
			if(StringUtils.isNotBlank(commentPic)){
				String commentPics[] = commentPic.split(",");
				for (int j = 0; j < commentPics.length; j++) {
					commentPicUrlList.add(imgUrl + commentPics[j]);
					comment.setCommentPicUrlList(commentPicUrlList);
				}
			}else{
				comment.setCommentPicUrlList(commentPicUrlList);
			}
			list.add(comment);
		}
		page.setList(list);
		if (list.size() > 0) {
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(page));
		} else {
			return JsonMapper.nonDefaultMapper().toJson(
					ResultUtil.error(ResultEnum.DATA_COUNT_ZERO.getCode(), ResultEnum.DATA_COUNT_ZERO.getMessage()));
		}
	}

	/**
	 * 查询低分差评评论列表
	 * 
	 * @param request
	 * @param response
	 * @param productName
	 * @param batchCode
	 * @param pageno
	 * @param pagesize
	 * @return
	 * @throws ParseException
	 */
	@RequestMapping(value = "/commentLowScorelist", produces = "application/json;charset=UTF-8", method = {
			RequestMethod.GET })
	@ResponseBody
	@ApiOperation(value = "查询评论列表", httpMethod = "GET", notes = "查询评论列表", consumes = "application/x-www-form-urlencoded")
	public String commentLowScorelist(HttpServletRequest request, HttpServletResponse response, String token,
			String officeId, Integer pageNo, Integer pageSize) throws ParseException {
		response.setContentType("application/json; charset=UTF-8");
		int pageNo3 = pageNo == null ? Global.DEFAULT_PAGENO : pageNo;
		int pageSize3 = pageSize == null ? Global.DEFAULT_PAGESIZE : pageSize;
		String corpCode = "";
		Page<TraceComment> page = new Page<TraceComment>(pageNo3, pageSize3);
		if (StringUtils.isBlank(token)) {
			return JsonMapper.nonDefaultMapper().toJson(
					ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
		User user = apiUserService.getUserByToken(token);
		if (token != null) {
			if (StringUtils.isNotEmpty(officeId)) {
				corpCode = officeId;
			} else {
				// 超级管理默认查询所有企业，其他默认查询本企业批次
				if (!user.isAdmin()) {
					corpCode = user.getCompany().getId();
				}
			}
		}
		page = traceCommentService.findlow(page, corpCode, user);
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
			List<String> commentPicUrlList = new ArrayList<String>();
			String commentPic = comment.getCommentPic();
			if(StringUtils.isNotBlank(commentPic)){
				String commentPics[] = commentPic.split(",");
				for (int j = 0; j < commentPics.length; j++) {
					commentPicUrlList.add(imgUrl + commentPics[j]);
					comment.setCommentPicUrlList(commentPicUrlList);
				}
			}else{
				comment.setCommentPicUrlList(commentPicUrlList);
			}
			list.add(comment);
		}
		page.setList(list);
		if (list.size() > 0) {
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(page));
		} else {
			return JsonMapper.nonDefaultMapper().toJson(
					ResultUtil.error(ResultEnum.DATA_COUNT_ZERO.getCode(), ResultEnum.DATA_COUNT_ZERO.getMessage()));
		}
	}

	/**
	 * 评论审核 xy
	 * 
	 * @return
	 */
	@RequestMapping(value = "pc_auditSave", produces = "application/json;charset=UTF-8", method = { RequestMethod.GET })
	@ResponseBody
	@ApiOperation(value = "评论审核保存", httpMethod = "GET", notes = "评论审核保存", consumes = "application/x-www-form-urlencoded")
	public String auditSave(HttpServletRequest request, String token, String commentId, String auditFlag) {
		TraceComment comment = traceCommentService.get(commentId);
		if (token != null) {
			User user = apiUserService.getUserByToken(token);
			try {
				comment.setAuditFlag(auditFlag);
				Date date = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				comment.setAuditDate(sdf.format(date));
				comment.setAuditUserid(user.getId());
				comment.setUpdateTime(date);
				comment.setUpdateUserid(user.getId());
				traceCommentService.save(comment);
			} catch (Exception e) {
				e.printStackTrace();
				return JsonMapper.nonDefaultMapper().toJson(
						ResultUtil.error(ResultEnum.SYSTEM_ERROR.getCode(), ResultEnum.SYSTEM_ERROR.getMessage()));
			}
		}
		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(comment));
	}

	/**
	 * 保存评论回复
	 * 
	 * @return
	 * @author xy
	 */
	@RequestMapping(value = "pc_saveCommentReply", produces = "application/json;charset=UTF-8", method = {
			RequestMethod.GET })
	@ResponseBody
	@ApiOperation(value = "保存评论回复", httpMethod = "GET", notes = "保存评论回复", consumes = "application/x-www-form-urlencoded")
	public String saveCommentReply(HttpServletRequest request, String token, String commentId, String replyContent) {
		if (StringUtils.isNotBlank(token)) {
			User user = apiUserService.getUserByToken(token);
			TraceCommentReply commentreply = new TraceCommentReply();
			commentreply.setCommentId(commentId);
			commentreply.setReplyContent(replyContent);
			commentreply.setCreatUserid(user.getId());
			commentreply.setCreatTime(new Date());
			traceCommentReplyService.save(commentreply);
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(commentreply));
		} else {
			return JsonMapper.nonDefaultMapper().toJson(
					ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
	}

	/**
	 * 获取全国地理位置数据
	 * 
	 * @return
	 * @author xy
	 */
	@RequestMapping(value = "getchina", produces = "application/json;charset=UTF-8", method = { RequestMethod.GET })
	@ResponseBody
	@ApiOperation(value = "获取全国地理位置数据", httpMethod = "GET", notes = "获取全国地理位置数据", consumes = "application/x-www-form-urlencoded")
	public String getchina(HttpServletRequest request, String token, String productId, String officeId) {
		if (StringUtils.isNotBlank(token)) {
			User user = apiUserService.getUserByToken(token);
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(getData(productId, user, officeId)));
		} else {
			return JsonMapper.nonDefaultMapper().toJson(
					ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
	}

	/**
	 * 获取新疆地理位置数据
	 * 
	 * @return
	 * @author xy
	 */
	@RequestMapping(value = "getxinjiang", produces = "application/json;charset=UTF-8", method = { RequestMethod.GET })
	@ResponseBody
	@ApiOperation(value = "获取新疆地理位置数据", httpMethod = "GET", notes = "获取新疆地理位置数据", consumes = "application/x-www-form-urlencoded")
	public String getxinjiang(HttpServletRequest request, String token, String productId, String officeId) {
		if (StringUtils.isNotBlank(token)) {
			User user = apiUserService.getUserByToken(token);
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(getxinjiangData(productId, user, officeId)));
		} else {
			return JsonMapper.nonDefaultMapper().toJson(
					ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
	}

	// 新疆扫码图表数据
	public Map<String, Object> getxinjiangData(String productId, User user, String officeId) {
		String[] proviceArray = { "哈密市", "吐鲁番市", "乌鲁木齐市", "昌吉回族自治州", "阿勒泰地区", "克拉玛依市", "塔城地区", "博尔塔拉蒙古自治州", "伊犁哈萨克自治州",
				"阿克苏地区", "和田地区", "阿拉尔市", "克孜勒苏柯尔克孜自治州", "巴音郭楞蒙古自治州", "喀什地区", "图木舒克市", "昆玉市", "北屯市", "铁门关市", "双河市",
				"石河子市", "五家渠市", "可克达拉市" };
		List<Integer> stringlist = new ArrayList<Integer>();
		List<JSONObject> cf = new ArrayList<JSONObject>();
		Map<String, Object> map = new HashMap<String, Object>();
		List<Object> list = traceInfoService.findAllTraceInfo(productId, user, officeId);
		if (list != null) {
			String pro = "";
			for (int j = 0; j < proviceArray.length; j++) {
				pro = proviceArray[j];
				int i = 0;
				for (i = 0; i < list.size(); i++) {
					Object[] objArr = (Object[]) list.get(i);
					String provice = objArr[0] == null ? "" : objArr[0].toString();
					String sl = objArr[1].toString();
					if (provice.contains("哈密")) {
						provice = "哈密市";
					} else if (provice.contains("吐鲁番")) {
						provice = "吐鲁番市";
					} else if (provice.contains("乌鲁木齐")) {
						provice = "乌鲁木齐市";
					} else if (provice.contains("昌吉")) {
						provice = "昌吉回族自治州";
					} else if (provice.contains("阿勒泰")) {
						provice = "阿勒泰地区";
					} else if (provice.contains("克拉玛依")) {
						provice = "克拉玛依市";
					} else if (provice.contains("塔城")) {
						provice = "塔城地区";
					} else if (provice.contains("博尔塔拉")) {
						provice = "博尔塔拉蒙古自治州";
					} else if (provice.contains("伊犁")) {
						provice = "伊犁哈萨克自治州";
					} else if (provice.contains("阿克苏")) {
						provice = "阿克苏地区";
					} else if (provice.contains("和田")) {
						provice = "和田地区";
					} else if (provice.contains("阿拉尔")) {
						provice = "阿拉尔市";
					} else if (provice.contains("克孜勒苏柯尔克孜")) {
						provice = "克孜勒苏柯尔克孜自治州";
					} else if (provice.contains("巴音郭楞")) {
						provice = "巴音郭楞蒙古自治州";
					} else if (provice.contains("喀什")) {
						provice = "喀什地区";
					} else if (provice.contains("图木舒克")) {
						provice = "图木舒克市";
					} else if (provice.contains("昆玉")) {
						provice = "昆玉市";
					} else if (provice.contains("北屯")) {
						provice = "北屯市";
					} else if (provice.contains("铁门关")) {
						provice = "铁门关市";
					} else if (provice.contains("双河")) {
						provice = "双河市";
					} else if (provice.contains("石河子")) {
						provice = "石河子市";
					} else if (provice.contains("五家渠")) {
						provice = "五家渠市";
					} else if (provice.contains("可克达拉")) {
						provice = "可克达拉市";
					}
					if (pro.equals(provice)) {
						JSONObject jObject = new JSONObject();
						jObject.put("name", provice);
						jObject.put("value", Integer.parseInt(sl));
						cf.add(jObject);
						stringlist.add(Integer.parseInt(sl));
						break;
					}
				}
				if (i == list.size()) {
					JSONObject jObject = new JSONObject();
					jObject.put("name", pro);
					jObject.put("value", 0);
					cf.add(jObject);
				}
			}
			String cfData = StringUtils.join(cf, ",");
			map.put("data", cfData);
			if (stringlist.size() > 0) {
				map.put("maxNum", Collections.max(stringlist));
			} else {
				map.put("maxNum", 100);
			}
		}
		return map;
	}

	/**
	 * 获取柱形图数据
	 * 
	 * @return
	 * @author xy
	 */
	@RequestMapping(value = "getBarData", produces = "application/json;charset=UTF-8", method = { RequestMethod.GET })
	@ResponseBody
	@ApiOperation(value = "获取柱形图数据", httpMethod = "GET", notes = "获取柱形图数据", consumes = "application/x-www-form-urlencoded")
	public String getBarData(HttpServletRequest request, String token, String productId, String officeId) {
		if (StringUtils.isNotBlank(token)) {
			User user = apiUserService.getUserByToken(token);
			Map<String, Object> map = getData2(productId, user, officeId);
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(map));
		} else {
			return JsonMapper.nonDefaultMapper().toJson(
					ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
	}

	/**
	 * @description 跳转到电话页面；
	 * @return
	 */
	@RequestMapping(value = "telephone")
	public String telephone(HttpServletRequest request) {
		return "modules/" + "common/tel";
	}

	/**
	 * @description 跳转地图图表页面 type == xinjiang 为新疆 type == china 为全国
	 * @return
	 */
	@RequestMapping(value = "region")
	public String region(String token, String productId, String type, String officeId, Model model,
			HttpServletRequest request) {
		if (token != null) {
			model.addAttribute("token", token);
			User user = apiUserService.getUserByToken(token);
			String corpCode = "";
			if (StringUtils.isNotBlank(officeId)) {
				corpCode = officeId;
			} else {
				// 超级管理默认查询所有企业，其他默认查询本企业批次
				if (!user.isAdmin()) {
					corpCode = user.getCompany().getId();
				}
			}

			List<TraceProduct> productList = new ArrayList<TraceProduct>();
			productList = traceProductService.findAllProducts(corpCode);
			model.addAttribute("productList", productList);
			model.addAttribute("officeId", corpCode);

			if (type.equals("xinjiang")) {
				model.addAttribute("map", getxinjiangData(productId, user, officeId));// 新疆扫码图表数据
				return "modules/" + "index/pc_xinjiang";
			} else {
				model.addAttribute("map", getData(productId, user, officeId));// 全国扫码图表数据
				return "modules/" + "index/pc_china";
			}
		} else {
			return JsonMapper.nonDefaultMapper().toJson(
					ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
	}

	/**
	 * 使用 Map按key进行排序
	 * 
	 * @param map
	 * @return
	 */
	public Map<String, Object> sortMapByKey(Map<String, Object> map) {
		if (map == null || map.isEmpty()) {
			return null;
		}
		Map<String, Object> sortMap = new TreeMap<String, Object>(new MapKeyComparator());
		sortMap.putAll(map);
		return sortMap;
	}

	/**
	 * 接入加工农事数据（接口）
	 * 
	 * @param traceProduct
	 * @return
	 */
	@RequestMapping(value = "/accessDataInterface", produces = "application/json;charset=UTF-8", method = {
			RequestMethod.POST })
	@ResponseBody
	@ApiOperation(value = "接入加工农事数据", httpMethod = "POST", notes = "接入加工农事数据", consumes = "application/json")
	public String accessDataInterface(HttpServletRequest request,
			@RequestBody @ApiParam(name = "模块对象", value = "传入json格式", required = true) AccessData accessData) {
		try {
			if (StringUtils.isNotBlank(accessData.getOfficeId())) {
				Office office = officeService.getOfficeByKuid(accessData.getOfficeId());
				if (office != null) {
					User user = apiUserService.getUserByToken("00ebf9fe-ae61-4ffa-bc3e-49f8611cfde4");
					accessData.setOfficeId(office.getId());
					if ("1".equals(accessData.getSysId())) {// 农事系统，只接收溯源数据，没有标签数据
						TraceCode traceCode = traceCodeService.getEntityByCode(accessData.getTraceCode());
						if (traceCode == null) {
							saveTraceData(request, accessData, user, office);// 保存溯源信息
						}
					} else if ("2".equals(accessData.getSysId())) {// 加工系统，接收溯源数据和标签数据
						if ("0".equals(accessData.getIsBottomPacking())) {// 非底层包装（箱、盒）
							TraceCode traceCode = traceCodeService.getEntityByCode(accessData.getTraceCode());
							if (traceCode == null) {
								// ProductBatch batch =
								// productBatchService.getBatchByCode(accessData.getOfficeId(),accessData.getBatchCode(),user);
								saveLableData(accessData, user, new ProductBatch());// 保存标签信息
							}
						} else {// 底层包装（包）
							TraceCode traceCode = traceCodeService.getEntityByCode(accessData.getTraceCode());
							if (traceCode == null) {
								saveTraceData(request, accessData, user, office);// 保存溯源信息
							}
						}
					} else {
						return JsonMapper.nonDefaultMapper()
								.toJson(ResultUtil.error(ResultEnum.PRODUCTBACTH_SYSId_NOT_EXSIT.getCode(),
										ResultEnum.PRODUCTBACTH_SYSId_NOT_EXSIT.getMessage()));
					}
				} else {
					return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(
							ResultEnum.OFFICE_ID_NOT_EXSIT.getCode(), ResultEnum.OFFICE_ID_NOT_EXSIT.getMessage()));
				}
			} else {
				return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.OFFICE_ID_NOT_NULL.getCode(),
						ResultEnum.OFFICE_ID_NOT_NULL.getMessage()));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return JsonMapper.nonDefaultMapper()
					.toJson(ResultUtil.error(ResultEnum.SYSTEM_ERROR.getCode(), ResultEnum.SYSTEM_ERROR.getMessage()));
		}
		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success());
	}

	// 保存溯源数据
	public void saveTraceData(HttpServletRequest request, AccessData accessData, User user, Office office)
			throws Exception {
		accessData.getTraceProduct().setProductTitle(accessData.getTraceProduct().getProductName());
		accessData.getTraceProduct().setStatus(TraceProduct.OPEN);
		accessData.getTraceProduct().setOfficeId(accessData.getOfficeId());
		accessData.getTraceProduct().setShowType("1");
		accessData.getTraceProduct().setLableAuditFlag("1");
		accessData.getTraceProduct().setCreatUserid(user.getId());
		accessData.getTraceProduct().setSysId(accessData.getSysId());
		String productCode = UniqueCodeUtil.getUniqueCode("TraceProduct", "productCode", office.getCode());
		accessData.getTraceProduct().setProductCode(productCode);
		accessData.getTraceProduct().setProductDiscription(
				StringEscapeUtils.unescapeHtml(accessData.getTraceProduct().getProductDiscription()));
		String imgUrl = "";
		TraceProduct tempProduct = traceProductService.find(accessData.getTraceProduct(), accessData.getOfficeId());
		if (tempProduct != null) {
			tempProduct.setProductName(accessData.getTraceProduct().getProductName());
			tempProduct.setProductTitle(accessData.getTraceProduct().getProductName());
			tempProduct.setProductDiscription(accessData.getTraceProduct().getProductDiscription());
			tempProduct.setThemeId(accessData.getTraceProduct().getThemeId());
			if (StringUtils.isNotBlank(accessData.getTraceProduct().getProductPic())) {
				imgUrl = productBatchService.imageUpload(request, accessData.getTraceProduct().getProductPic());
			}
			tempProduct.setProductPic(imgUrl);
			tempProduct.setUpdateUserid(user.getId());
			tempProduct.setUpdateTime(new Date());
			tempProduct.setTraceModelList(accessData.getTraceProduct().getTraceModelList());
			traceProductService.save(tempProduct);
			productBatchService.saveBatchAndModel(request, tempProduct, accessData, user);
		} else {
			if (StringUtils.isNotBlank(accessData.getTraceProduct().getProductPic())) {
				imgUrl = productBatchService.imageUpload(request, accessData.getTraceProduct().getProductPic());
			}
			accessData.getTraceProduct().setProductPic(imgUrl);
			traceProductService.save(accessData.getTraceProduct());
			productBatchService.saveBatchAndModel(request, accessData.getTraceProduct(), accessData, user);
		}
	}

	// 保存标签数据
	public void saveLableData(AccessData accessData, User user, ProductBatch batch) throws Exception {
		TraceCode traceCode = new TraceCode();
		traceCode.setTraceCode(accessData.getTraceCode());
		traceCode.setSerialNumber(Integer.parseInt(accessData.getTraceCode().substring(14, 24)));
		traceCode.setPackType(accessData.getPackType());
		traceCode.setPackTypeName(accessData.getPackTypeName());
		traceCode.setSysId(accessData.getSysId());
		traceCode.setApplyId("");
		traceCode.setOfficeId(accessData.getOfficeId());
		traceCode.setBatchId(batch.getId());
		traceCode.setTraceCode("0");
		traceCode.setStatus("2");
		traceCode.setActivationDate(new Date());
		traceCode.setActivator(user.getId());
		traceCodeService.save(traceCode);
		productBatchService.saveCodeRelationship(accessData, traceCode, batch);
	}

	@RequestMapping(value = "/verifyCode", method = RequestMethod.GET)
	@ResponseBody
	public String getVerifyCode(HttpServletResponse response, String phone) {
		try {
			BufferedImage image = traceVerifyCodeService.createVerifyCode(phone);
			OutputStream out = response.getOutputStream();
			ImageIO.write(image, "JPEG", out);
			out.flush();
			out.close();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return "获取失败";
		}
	}

	@RequestMapping(value = "/compareCode", method = RequestMethod.GET)
	@ResponseBody
	public String compareCode(HttpServletResponse response, String code, String phone) {
		try {
			TraceVerifyCode traceVerifyCode = traceVerifyCodeService.findTraceVerifyCodeByPhone(phone);
			if (StringUtils.isNotBlank(traceVerifyCode.getAnswer())) {// 农事系统，只接收溯源数据，没有标签数据
				if (!traceVerifyCode.getAnswer().equals(code.trim())) {
					return JsonMapper.nonDefaultMapper()
							.toJson(ResultUtil.error(ResultEnum.VERIFYCODE_CODE_ANSWER_WRONG.getCode(),
									ResultEnum.VERIFYCODE_CODE_ANSWER_WRONG.getMessage()));
				}
			} else {
				return JsonMapper.nonDefaultMapper()
						.toJson(ResultUtil.error(ResultEnum.VERIFYCODE_CODE_NOT_NULL.getCode(),
								ResultEnum.VERIFYCODE_CODE_NOT_NULL.getMessage()));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return JsonMapper.nonDefaultMapper()
					.toJson(ResultUtil.error(ResultEnum.SYSTEM_ERROR.getCode(), ResultEnum.SYSTEM_ERROR.getMessage()));
		}
		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success());
	}

}
