package com.surekam.modules.api.web;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.surekam.common.utils.StringUtils;
import com.surekam.common.mapper.JsonMapper;
import com.surekam.common.utils.ResultBean;
import com.surekam.common.utils.ResultEnum;
import com.surekam.common.utils.ResultUtil;
import com.surekam.common.web.BaseController;
import com.surekam.modules.sys.dao.UserDao;
import com.surekam.modules.sys.entity.CommonUser;
import com.surekam.modules.trace.TraceLableApply.entity.TraceLableApply;
import com.surekam.modules.trace.TraceLableApply.service.TraceLableApplyService;
import com.surekam.modules.tracelableaudit.entity.TraceLableAudit;
import com.surekam.modules.tracelableaudit.service.TraceLableAuditService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 标签审核
 * @author 
 * xy
 */

@Api(value="溯源标签审核接口Controller", description="溯源标签审核的相关数据接口")
@Controller
@RequestMapping(value = "api/traceLableAudit")
public class TraceLableAuditController extends BaseController{
	
	@Autowired
	private TraceLableAuditService traceLableAuditService;
	
	@Autowired
	private TraceLableApplyService traceLableApplyService;
	
	@Autowired
	private UserDao userDao;
	
	
	@ModelAttribute
	public TraceLableAudit get(@RequestParam(required=false) String id) {
		if (StringUtils.isNotBlank(id)){
			return traceLableAuditService.get(id);
		}else{
			return new TraceLableAudit();
		}
	}
	
	
	/**
	 * 审核新增
	 * auditCode      审核编号 
	 * applyId 		    标签id
	 * auditExplain   审核说明
	 * auditResult    审核结果 0 拒绝   1 通过
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "lableAuditSave",produces="text/plain;charset=UTF-8", method = {RequestMethod.POST})
	@ResponseBody
	@ApiOperation(value = "标签审核新增", httpMethod = "POST", notes = "标签审核新增",	consumes="application/x-www-form-urlencoded")
	public String lableAuditSave(HttpServletRequest request,
			@RequestParam  String auditCode,
			@RequestParam  String applyId,
			@RequestParam  String auditExplain,
			@RequestParam  String auditResult) {
		String token = request.getHeader("X-Token");
		CommonUser user = userDao.findByToken(token);
		TraceLableAudit audit = new TraceLableAudit();
		audit.setAuditCode(auditCode);
		audit.setApplyId(applyId);
		audit.setAuditExplain(auditExplain);
		audit.setAuditResult(auditResult);
		audit.setCreatTime(new Date());
		audit.setCreatUserid(user.getId());
		
		ResultBean<TraceLableAudit> resultBean = new ResultBean<TraceLableAudit>();
		try{
			traceLableAuditService.save(audit);
			TraceLableApply traceLableApply = new TraceLableApply();
			traceLableApply = traceLableApplyService.get(applyId);
			traceLableApply.setAuditFlag(auditResult);
			traceLableApply.setUpdateTime(new Date());
			traceLableApply.setUpdateUserid(user.getId());
			traceLableApplyService.save(traceLableApply);
			resultBean = ResultUtil.success();
		}catch(Exception e){
			e.printStackTrace();
			resultBean = ResultUtil.error(ResultEnum.SYSTEM_ERROR.getCode(),ResultEnum.SYSTEM_ERROR.getMessage());
		}
		return JsonMapper.nonDefaultMapper().toJson(resultBean);
		
	}

}
