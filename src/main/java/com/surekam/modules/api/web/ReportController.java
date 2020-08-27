package com.surekam.modules.api.web;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.surekam.common.mapper.JsonMapper;
import com.surekam.common.utils.ResultUtil;
import com.surekam.common.web.BaseController;
import com.surekam.modules.sys.entity.User;
import com.surekam.modules.sys.service.OfficeService;
import com.surekam.modules.trace.TraceInfo.service.TraceInfoService;
import com.surekam.modules.trace.TraceLableApply.service.TraceLableApplyService;
import com.surekam.modules.tracecomment.service.TraceCommentService;
import com.surekam.modules.traceproduct.service.TraceProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import net.sf.json.JSONArray;

/**
 * 报表接口Controller
 * @author liwei
 * @version 2019-05-08
 */
@Api(value="报表接口Controller", description="报表的相关数据接口")
@RequestMapping(value = "api/report")
@Controller
public class ReportController extends BaseController {
	@Autowired
	private TraceInfoService traceInfoService;
	
	@Autowired
	private OfficeService officeService;
	
	@Autowired
	private TraceCommentService traceCommentService;
	
	@Autowired
	private TraceProductService traceProductService;
	
	@Autowired
	private TraceLableApplyService traceLableApplyService;
	
	/**
	 * 获取企业数、产品数、标签数、扫码数
	 * 
	 * @return
	 */
	@RequestMapping(value = "getTraceInfoCount", produces = "application/json;charset=UTF-8", method = { RequestMethod.GET })
	@ResponseBody
	@ApiOperation(value = "获取企业数、产品数、标签数、扫码数", httpMethod = "GET", notes = "获取企业数、产品数、标签数、扫码数", consumes = "application/x-www-form-urlencoded")
	public String getTraceInfoCount(HttpServletRequest request, 
			@ApiParam(name="code", value="平台账户", required = true) @RequestParam(required = true, defaultValue = "nanxian") String code) {
		Map<String,Object> map = new HashMap<String,Object>();
		
		String id ="2";
		if(code.equals("wangcheng")) {
			id = "a2bfcffa042646a98360ef86343de977";
		}else if(code.equals("tulufan")) {
			id = "433620addfd144028f9d1afaabfe8299";
		}else if(code.equals("yuanxiang")) {
			id = "2";
		}else if(code.equals("nanxian")) {
			id = "1";
		}
		
		String smcs = traceInfoService.findTraceCount("", "", id)+"";
		map.put("scanCount", smcs);
		String bqs = traceLableApplyService.findBySql(id);
		map.put("labelCount", bqs);
		//String cps = traceProductService.findAllProducts(id).size()+"";
		String cps = traceProductService.findAllProductsTwo(id).size()+"";
		map.put("productCount", cps);
		//String gss = officeService.findAll().size()+"";
		String gss = officeService.findAllTwo(id).size()+"";
		map.put("companyCount", gss);
		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(map));
	}
	
	/**
	 * 溯源趋势分析
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "getTraceTrendAnalysis", produces = "application/json;charset=UTF-8", method = { RequestMethod.GET })
	@ApiOperation(value = "溯源趋势分析", httpMethod = "GET", notes = "溯源趋势分析", consumes = "application/x-www-form-urlencoded")
	public String getTraceTrendAnalysis(HttpServletRequest request, 
			@ApiParam(name="code", value="平台账户", required = true) @RequestParam(required = true, defaultValue = "nanxian") String code) {
		Map<String,Object> map = new HashMap<String,Object>();
		//User user = apiUserService.getUserByToken("e47c8eca-dc65-46e1-997e-98dcd9146f12");
		
		String id ="2";
		if(code.equals("wangcheng")) {
			id = "a2bfcffa042646a98360ef86343de977";
		}else if(code.equals("tulufan")) {
			id = "433620addfd144028f9d1afaabfe8299";
		}else if(code.equals("yuanxiang")) {
			id = "2";
		}else if(code.equals("nanxian")) {
			id = "1";
		}
		
		//Map<String,Object> map2 =  getData2("",user,"");
		Map<String,Object> map2 =  getData3(id);
		map.put("map2", map2);
		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(map));
	}
	/**
	 *  传入code（wangcheng或者tulufan）id
	 * @param officeId
	 * @return
	 */
	public Map<String,Object> getData3(String officeId){
		Map<String,Object> map = new HashMap<String,Object>();
		List<Object> list =  traceInfoService.findMonthsTraceInfo3(officeId);
		List<Object> list2 =  traceCommentService.findMonthscComment3(officeId);
		JSONArray json3 = getHalfYearMonth();
		JSONArray  json = new JSONArray();
		JSONArray  json2 = new JSONArray();
		long saomamax =0;
		long commentmax =0;
		for(int i = 0;i < json3.size();i++) {
			int m =0;
			for(m = 0;m < list.size();m++) {
				Object[] objArr =(Object[]) list.get(m);
				if(json3.get(i).equals(objArr[0].toString())) {
					json.add(objArr[1].toString());
					if(saomamax <  Long.parseLong(objArr[1].toString())) {
						saomamax = Long.parseLong(objArr[1].toString());
					}
					break;
				}
			}
			if(m==list.size()) {
				json.add("0");
			}
			  
		}
		for(int i = 0;i < json3.size();i++) {
			int m =0;
			for(m = 0;m < list2.size();m++) {
				Object[] objArr =(Object[]) list2.get(m);
				if(json3.get(i).equals(objArr[0].toString())) {
					json2.add(objArr[1].toString());
					if(commentmax <  Long.parseLong(objArr[1].toString())) {
						commentmax = Long.parseLong(objArr[1].toString());
					}
					break;
				}
			}
			if(m==list2.size()) {
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
	
	//月份统计扫码留言图表数据
	public Map<String,Object> getData2(String productId,User user,String officeId){
		Map<String,Object> map = new HashMap<String,Object>();
		List<Object> list =  traceInfoService.findMonthsTraceInfo(productId,user,officeId);
		List<Object> list2 =  traceCommentService.findMonthscComment(productId,user,officeId);
		JSONArray json3 = getHalfYearMonth();
		JSONArray  json = new JSONArray();
		JSONArray  json2 = new JSONArray();
		long saomamax =0;
		long commentmax =0;
		for(int i = 0;i < json3.size();i++) {
			int m =0;
			for(m = 0;m < list.size();m++) {
				Object[] objArr =(Object[]) list.get(m);
				if(json3.get(i).equals(objArr[0].toString())) {
					json.add(objArr[1].toString());
					if(saomamax <  Long.parseLong(objArr[1].toString())) {
						saomamax = Long.parseLong(objArr[1].toString());
					}
					break;
				}
			}
			if(m==list.size()) {
				json.add("0");
			}
			  
		}
		for(int i = 0;i < json3.size();i++) {
			int m =0;
			for(m = 0;m < list2.size();m++) {
				Object[] objArr =(Object[]) list2.get(m);
				if(json3.get(i).equals(objArr[0].toString())) {
					json2.add(objArr[1].toString());
					if(commentmax <  Long.parseLong(objArr[1].toString())) {
						commentmax = Long.parseLong(objArr[1].toString());
					}
					break;
				}
			}
			if(m==list2.size()) {
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
		JSONArray  json = new JSONArray();
		LocalDate today = LocalDate.now();
        for(long i = 1L;i <= 12L; i++){
            LocalDate localDate = today.minusMonths((int) i-1);
            String ss = localDate.toString().substring(0,7);
            json.add(ss);
        }
		return json;
	}
	
	/**
	 * 获取企业对应的标签数
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "getCompanyLabelCount", produces = "application/json;charset=UTF-8", method = { RequestMethod.GET })
	@ApiOperation(value="获取企业对应的标签数", httpMethod = "GET", notes="获取企业对应的标签数", consumes = "application/x-www-form-urlencoded")
	public String getCompanyLabelCount(HttpServletRequest request, 
			@ApiParam(name="code", value="平台账户", required = true) @RequestParam(required = true, defaultValue = "nanxian") String code) {
		String id ="2";
		if(code.equals("wangcheng")) {
			id = "a2bfcffa042646a98360ef86343de977";
		}else if(code.equals("tulufan")) {
			id = "433620addfd144028f9d1afaabfe8299";
		}else if(code.equals("yuanxiang")) {
			id = "2";
		}else if(code.equals("country")) {
			id = "3e48ede7e64d499195186fcdd086a854";       
		}else if(code.equals("nanxian")) {
			id = "1";
		}
		List<Map<String,Object>> list = traceLableApplyService.getCompanyLabelCount(id);
		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(list));
	}
	
}
