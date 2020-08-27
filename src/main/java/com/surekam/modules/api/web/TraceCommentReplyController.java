package com.surekam.modules.api.web;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.surekam.common.mapper.JsonMapper;
import com.surekam.common.utils.ResultEnum;
import com.surekam.common.utils.ResultUtil;
import com.surekam.common.utils.StringUtils;
import com.surekam.modules.api.service.ApiUserService;
import com.surekam.modules.sys.entity.User;
import com.surekam.modules.tracecommentreply.entity.TraceCommentReply;
import com.surekam.modules.tracecommentreply.service.TraceCommentReplyService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * 评论回复管理
 * @author xy
 *
 */
@Api(value="溯源评论回复管理接口Controller", description="溯源评论回复管理的相关数据接口")
@Controller
@RequestMapping(value = "api/commentReply")
public class TraceCommentReplyController {
	
	@Autowired
	private TraceCommentReplyService traceCommentReplyService;
	
	@Autowired
	private ApiUserService apiUserService;
	
	
	/**
	  * 保存评论回复
	 * @return
	 * @author xy
	 */
	@RequestMapping(value = "saveCommentReply",produces="text/plain;charset=UTF-8", method = {RequestMethod.POST})
	@ResponseBody
	@ApiOperation(value = "保存评论回复", httpMethod = "POST", notes = "保存评论回复",	consumes="application/json")
	public String saveCommentReply(HttpServletRequest request,@RequestBody @ApiParam(name="保存评论回复",value="传入json格式",required=true) TraceCommentReply commentReply) {
		String token = request.getHeader("X-Token");
		if(StringUtils.isNotBlank(token)){
			User user = apiUserService.getUserByToken(token);
			commentReply.setCreatUserid(user.getId());
			commentReply.setCreatTime(new Date());
			traceCommentReplyService.save(commentReply);
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(commentReply));
		} else {
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
	}

}
