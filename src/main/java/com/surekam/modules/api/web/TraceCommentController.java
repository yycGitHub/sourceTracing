package com.surekam.modules.api.web;

import java.text.ParseException;
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
import com.surekam.common.persistence.Page;
import com.surekam.common.utils.ResultEnum;
import com.surekam.common.utils.ResultUtil;
import com.surekam.common.utils.StringUtils;
import com.surekam.common.web.BaseController;
import com.surekam.modules.api.service.ApiUserService;
import com.surekam.modules.sys.entity.User;
import com.surekam.modules.sys.service.OfficeService;
import com.surekam.modules.tracecomment.entity.TraceComment;
import com.surekam.modules.tracecomment.service.TraceCommentService;
import com.surekam.modules.tracecommentreply.entity.TraceCommentReply;
import com.surekam.modules.tracecommentreply.service.TraceCommentReplyService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * 评论管理
 * @author xy
 *
 */
@Api(value="溯源评论管理接口Controller", description="溯源评论管理的相关数据接口")
@Controller
@RequestMapping(value = "api/comment")
public class TraceCommentController extends BaseController{

	
	
	@Autowired
	private TraceCommentService traceCommentService;
	
	@Autowired
	private ApiUserService apiUserService;
	
	@Autowired
	private TraceCommentReplyService traceCommentReplyService;
	
	@Autowired
	private OfficeService officeService;
	
	/**
	 * 查询评论列表
	 * @param request
	 * @param response
	 * @param productName
	 * @param batchCode
	 * @param pageno
	 * @param pagesize
	 * @return
	 * @throws ParseException 
	 */
	@RequestMapping(value = "/list",produces="application/json;charset=UTF-8", method = {RequestMethod.GET})
	@ResponseBody
	@ApiOperation(value = "查询评论列表", httpMethod = "GET", notes = "查询评论列表",	consumes="application/x-www-form-urlencoded")
	public String listP(HttpServletRequest request,HttpServletResponse response,
			@RequestParam(required = false) String corpCode,
			@RequestParam(required = false) String productName,
			@RequestParam(required = false) String startTime,
			@RequestParam(required = false) String endTime,
			@RequestParam Integer pageno,
			@RequestParam Integer pagesize) throws ParseException {
		response.setContentType("application/json; charset=UTF-8");
		Map<String, Object> map = new HashMap<String, Object>();
		int pageNo = pageno==null?Global.DEFAULT_PAGENO:pageno;
		int pageSize = pagesize==null?Global.DEFAULT_PAGESIZE:pagesize;
		Page<TraceComment> page = new Page<TraceComment>(pageNo,pageSize); 
		String token = request.getHeader("X-Token");
		if(StringUtils.isBlank(token)){
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(),ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
		String admin = "";
		List<String> findChildrenOffice = new ArrayList<String>();
		User user = apiUserService.getUserByToken(token);
		if(!StringUtils.isNotBlank(corpCode)) {
			findChildrenOffice = officeService.findChildrenOffice(user.getCompany().getId());
		}
		
		//超级管理默认查询所有企业，其他默认查询本企业批次
		// roleType 1: 非管路员 0 ：则管路员
		if (!user.isAdmin()) {
			admin = user.getCompany().getId();
			map.put("roleType", "0");
		} else {
			map.put("roleType", "0");
		}
		page = traceCommentService.find3(page, admin,productName,startTime,endTime,corpCode,findChildrenOffice,user);
		List<TraceComment> list = new ArrayList<TraceComment>();
		int pageNo2 = 1;
		int pageSize2 = 10;
		for (int i = 0; i < page.getList().size(); i++) {
			TraceComment comment = page.getList().get(i);
			// 获取到公司名称
			if (!"".equals(comment.getOfficeId()) && comment.getOfficeId() != null) {
				comment.setCorpName(officeService.get(comment.getOfficeId()).getName());
			}
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
				commentPicUrlList.add(imgUrl + commentPics[j]);
				comment.setCommentPicUrlList(commentPicUrlList);
			}
			list.add(comment);
		}
        page.setList(list);
        map.put("page", page);
		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(map));
	}
	
	/**
	 * 查询评论导出数据，不分页
	 * @param request
	 * @param response
	 * @param productName
	 * @param batchCode
	 * @param pageno
	 * @param pagesize
	 * @return
	 * @throws ParseException 
	 */
	@RequestMapping(value = "/listExport",produces="application/json;charset=UTF-8", method = {RequestMethod.GET})
	@ResponseBody
	@ApiOperation(value = "查询评论导出数据，不分页", httpMethod = "GET", notes = "查询评论导出数据，不分页",	consumes="application/x-www-form-urlencoded")
	public String listExport(HttpServletRequest request,HttpServletResponse response,
			@RequestParam(required = false) String corpCode,
			@RequestParam(required = false) String productName,
			@RequestParam(required = false) String startTime,
			@RequestParam(required = false) String endTime,
			@RequestParam Integer pageno,
			@RequestParam Integer pagesize) throws ParseException {
		response.setContentType("application/json; charset=UTF-8");
		Map<String, Object> map = new HashMap<String, Object>();
		int pageNo = pageno==null?Global.DEFAULT_PAGENO:pageno;
		int pageSize = pagesize==null?Global.DEFAULT_PAGESIZE:pagesize;
		Page<TraceComment> page = new Page<TraceComment>(pageNo,pageSize); 
		String token = request.getHeader("X-Token");
		if(StringUtils.isBlank(token)){
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(),ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
		String admin = "";
		List<String> findChildrenOffice = new ArrayList<String>();
		User user = apiUserService.getUserByToken(token);
		if(!StringUtils.isNotBlank(corpCode)) {
			findChildrenOffice = officeService.findChildrenOffice(user.getCompany().getId());
		}
		//超级管理默认查询所有企业，其他默认查询本企业批次
		// roleType 1: 非管路员 0 ：则管路员
		if (!user.isAdmin()) {
			admin = user.getCompany().getId();
			map.put("roleType", "1");
		} else {
			map.put("roleType", "0");
		}
		List<TraceComment> list5 = traceCommentService.findListExport(admin,productName,startTime,endTime,corpCode,findChildrenOffice,user);
		page.setList(list5);
		List<TraceComment> list = new ArrayList<TraceComment>();
		int pageNo2 = 1;
		int pageSize2 = 10;
		for (int i = 0; i < page.getList().size(); i++) {
			TraceComment comment = page.getList().get(i);
			// 获取到公司名称
			if (!"".equals(comment.getOfficeId()) && comment.getOfficeId() != null) {
				comment.setCorpName(officeService.get(comment.getOfficeId()).getName());
			}
			Page<TraceCommentReply> page2 = new Page<TraceCommentReply>(pageNo2, pageSize2);
			TraceCommentReply traceCommentReply = new TraceCommentReply();
			page2 = traceCommentReplyService.find(page2, comment.getId());
			List<TraceCommentReply> list2 = page2.getList();
			if (list2.size() > 0) {
				traceCommentReply = list2.get(0);
				comment.setReplyContent(traceCommentReply.getReplyContent());
				comment.setReplyCreatTime(traceCommentReply.getCreatTime());
			}else {
				comment.setReplyContent("");
			}
			String imgUrl = Global.getConfig("sy_img_url");
			String commentPic = comment.getCommentPic();
			List<String> commentPicUrlList = new ArrayList<String>();
			String commentPics[] = commentPic.split(",");
			for (int j = 0; j < commentPics.length; j++) {
				commentPicUrlList.add(imgUrl + commentPics[j]);
				comment.setCommentPicUrlList(commentPicUrlList);
			}
			list.add(comment);
		}
        page.setList(list);
        map.put("page", page);
		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(map));
	}
	
	/**
	  *评论审核
	 * xy
	 * @return
	 */
	@RequestMapping(value = "auditSave",produces = "text/plain;charset=UTF-8",method = {RequestMethod.POST})
	@ResponseBody
	@ApiOperation(value = "评论审核保存",httpMethod = "POST",notes = "评论审核保存",consumes = "application/json")
	public String auditSave(HttpServletRequest request,
			@RequestBody @ApiParam(name = "评论对象",value = "传入json格式",required = true) TraceComment traceComment) {
		String token = request.getHeader("X-Token");
		TraceComment comment = traceCommentService.get(traceComment.getId());
		if(token != null) {
			User user = apiUserService.getUserByToken(token);
			try {
				comment.setAuditFlag(traceComment.getAuditFlag());
				Date date = new Date();
		        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				comment.setAuditDate(sdf.format(date));
				comment.setAuditUserid(user.getId());
				comment.setUpdateTime(date);
				comment.setUpdateUserid(user.getId());
				traceCommentService.save(comment);
			} catch (Exception e) {
				e.printStackTrace();
				return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.SYSTEM_ERROR.getCode(),ResultEnum.SYSTEM_ERROR.getMessage()));
			}
		}
		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(comment));
	}
	
	
	/**
	  *评论逻辑删除
	 * xy
	 * @return
	 */
	@RequestMapping(value = "delSave",produces = "text/plain;charset=UTF-8",method = {RequestMethod.POST})
	@ResponseBody
	@ApiOperation(value = "评论逻辑删除",httpMethod = "POST",notes = "评论逻辑删除",consumes = "application/json")
	public String delSave(HttpServletRequest request,
			@RequestBody @ApiParam(name = "评论对象",value = "传入json格式",required = true) TraceComment traceComment) {
		String token = request.getHeader("X-Token");
		TraceComment comment = traceCommentService.get(traceComment.getId());
		if(token != null) {
			User user = apiUserService.getUserByToken(token);
			try {
				comment.setStates(TraceComment.STATE_FLAG_DEL);
				comment.setUpdateTime(new Date());
				comment.setUpdateUserid(user.getId());
				traceCommentService.save(comment);
			} catch (Exception e) {
				e.printStackTrace();
				return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.SYSTEM_ERROR.getCode(),ResultEnum.SYSTEM_ERROR.getMessage()));
			}
		}
		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(comment));
	}
}
