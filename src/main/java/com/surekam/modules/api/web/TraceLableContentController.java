package com.surekam.modules.api.web;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.surekam.common.mapper.JsonMapper;
import com.surekam.common.utils.ResultEnum;
import com.surekam.common.utils.ResultUtil;
import com.surekam.common.utils.StringUtils;
import com.surekam.common.web.BaseController;
import com.surekam.modules.api.service.ApiUserService;
import com.surekam.modules.sys.entity.User;
import com.surekam.modules.trace.TraceLableContent.entity.TraceLableContent;
import com.surekam.modules.trace.TraceLableContent.service.TraceLableContentService;
import com.surekam.modules.trace.tracelableprinter.service.TraceLablePrinterService;
import com.surekam.modules.trace.tracelableselectprinter.service.TraceLableSelectPrinterService;
import com.surekam.modules.trace.tracelablespecification.service.TraceLableSpecificationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 标签内容接口
 * @author xy
 * @version 2018-11-22
 */

@Api(value="溯源标签内容接口Controller", description="溯源标签内容的相关数据接口")
@Controller
@RequestMapping(value = "api/lableContent")
public class TraceLableContentController extends BaseController{
	
	@Autowired
	private TraceLableContentService traceLableContentService;
	
	@Autowired
	private ApiUserService apiUserService;
	
	@Autowired
	private TraceLableSpecificationService traceLableSpecificationService;
	
	@Autowired
	private TraceLablePrinterService traceLablePrinterService;
	
	@Autowired
	private TraceLableSelectPrinterService traceLableSelectPrinterService;
	
	@ModelAttribute
	public TraceLableContent get(@RequestParam(required=false) String id) {
		if (StringUtils.isNotBlank(id)){
			return traceLableContentService.get(id);
		}else{
			return new TraceLableContent();
		}
	}
	
	
	@RequestMapping(value = "traceLableContentList",produces="application/json;charset=UTF-8", method = {RequestMethod.GET})
	@ResponseBody
	@ApiOperation(value = "标签内容查询", httpMethod = "GET", notes = "标签内容查询",	consumes="application/x-www-form-urlencoded")
	public String TraceLableContentList(HttpServletRequest request,HttpServletResponse response,@RequestParam(required = false) String id) {
		response.setContentType("application/json; charset=UTF-8");
		List<TraceLableContent> list = traceLableContentService.find(id);
  		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(list));
	}
	
	/**
	 * @author liwei
	 * saveLableContent
	 * @param request
	 * @param traceProduct
	 * @return
	 */
	@RequestMapping(value = "saveLableContent",produces="text/plain;charset=UTF-8", method = {RequestMethod.GET})
	@ResponseBody
	@ApiOperation(value = "保存标签内容", httpMethod = "GET", 
	notes = "保存标签内容",	consumes="application/json")
	public String saveLableContent(HttpServletRequest request,@RequestParam String applyId,@RequestParam String[] content) {
		String token = request.getHeader("X-Token");
		if(StringUtils.isNotBlank(token)){
			User user = apiUserService.getUserByToken(token);
			try {
				traceLableContentService.save(applyId,content,user);
			} catch (Exception e) {
				return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.SYSTEM_ERROR.getCode(), ResultEnum.SYSTEM_ERROR.getMessage()));
			}
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success());
		} else {
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
	}

}
