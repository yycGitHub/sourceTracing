package com.surekam.modules.api.web.sys;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.google.common.collect.Lists;
import com.surekam.common.config.Global;
import com.surekam.common.mapper.JsonMapper;
import com.surekam.common.persistence.Page;
import com.surekam.common.utils.ResultBean;
import com.surekam.common.utils.ResultEnum;
import com.surekam.common.utils.ResultUtil;
import com.surekam.common.web.BaseController;
import com.surekam.modules.api.entity.UpdataUserVO;
import com.surekam.modules.api.entity.UserQuery;
import com.surekam.modules.sys.entity.Office;
import com.surekam.modules.sys.entity.Role;
import com.surekam.modules.sys.entity.User;
import com.surekam.modules.sys.entity.vo.UserVo;
import com.surekam.modules.sys.service.ApiSystemService;
import com.surekam.modules.sys.service.SystemService;

@Api(value="溯源系统框架-用户管理接口Controller", description="溯源系统框架-用户管理的相关数据接口")
@Controller
@RequestMapping(value = "api/user/")
public class UserApiController extends BaseController {

	@Autowired
	private ApiSystemService apiSystemService;

	@RequestMapping(value = "list", produces = "application/json;charset=UTF-8")
	@ResponseBody
	@ApiOperation(value = "用户列表查询", notes = "用户列表查询", consumes = "application/json")
	public String list(HttpServletRequest request, HttpServletResponse response,
			@RequestBody UserQuery userQuery) {
		response.setContentType("application/json; charset=UTF-8");
		JsonMapper jsonMapper = JsonMapper.getInstance();
		ResultBean<Object> resultBean = new ResultBean<Object>();
		try {
			int pageNo = userQuery.getPageno() == null ? Global.DEFAULT_PAGENO : userQuery.getPageno();
			int pageSize = userQuery.getPagesize() == null ? Global.DEFAULT_PAGESIZE : userQuery.getPagesize();
			String token = request.getHeader("X-Token");
			User currentUser = apiSystemService.findUserByToken(token);
			User user = new User();
			user.setLoginName(userQuery.getLoginName());
			user.setName(userQuery.getName());
			if (StringUtils.isBlank(userQuery.getCompanyId())) {
				userQuery.setCompanyId(currentUser.getCompany().getId());
			}
			user.setCompany(new Office(userQuery.getCompanyId()));
			user.setOffice(new Office(userQuery.getOfficeId()));
			user.setDelFlag(userQuery.getDelFlag());
			Page<User> page = new Page<User>(pageNo, pageSize);
			page.setOrderBy(userQuery.getOrderFields());
			page = apiSystemService.findUser(page, user, currentUser);
			Page<UserVo> pageVo = new Page<UserVo>(pageNo, pageSize);
			pageVo.setCount(page.getCount());
			if (page.getList() != null && page.getList().size() > 0) {
				List<User> list = page.getList();
				List<UserVo> listVo = new ArrayList<UserVo>();
				for (int i = 0; i < list.size(); i++) {
					UserVo userVo = new UserVo();
					User userTemp = list.get(i);
					BeanUtils.copyProperties(userTemp, userVo, new String[] { "password" });
					if (userTemp.getCompany() != null) {
						userVo.setCompanyId(userTemp.getCompany().getId());
					}
					if (userTemp.getOffice() != null) {
						userVo.setOfficeId(userTemp.getOffice().getId());
					}
					listVo.add(userVo);
				}
				pageVo.setList(listVo);
			}
			return jsonMapper.toJson(ResultUtil.success(pageVo));
		} catch (Exception e) {
			logger.error("用户列表查询错误：" + e);
			e.printStackTrace();
			resultBean = ResultUtil.error(ResultEnum.SYSTEM_ERROR.getCode(), ResultEnum.SYSTEM_ERROR.getMessage());
			return jsonMapper.toJson(resultBean);
		}

	}

	@RequestMapping(value = "getUserInfo", produces = "application/json;charset=UTF-8", method = { RequestMethod.POST })
	@ResponseBody
	@ApiOperation(value = "获取用户详细信息", httpMethod = "POST", notes = "获取用户详细信息", consumes = "application/json")
	public String getUserInfo(HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = false) String userId) {
		response.setContentType("application/json; charset=UTF-8");
		JsonMapper jsonMapper = JsonMapper.getInstance();
		ResultBean<Object> resultBean = new ResultBean<Object>();
		try {
			User user = apiSystemService.getUser(userId);
			// 判断显示的用户是否在授权范围内
			String officeId = user.getOffice().getId();
			String token = request.getHeader("X-Token");
			User currentUser = apiSystemService.findUserByToken(token);
			if (!currentUser.isAdmin()) {
				String dataScope = apiSystemService.getDataScope(currentUser);
				if (dataScope.indexOf("office.id=") != -1) {
					String AuthorizedOfficeId = dataScope.substring(
							dataScope.indexOf("office.id=") + 10,
							dataScope.indexOf(" or"));
					if (!AuthorizedOfficeId.equalsIgnoreCase(officeId)) {
						resultBean = ResultUtil.error(
								ResultEnum.SYSTEM_NO_AUTH_USER.getCode(),
								ResultEnum.SYSTEM_NO_AUTH_USER.getMessage());
						return jsonMapper.toJson(resultBean);
					}
				}
			}
			UserVo userVo = new UserVo();
			BeanUtils.copyProperties(user, userVo);
			userVo.setCompanyId(user.getCompany().getId());
			userVo.setOfficeId(user.getOffice().getId());
			return jsonMapper.toJson(ResultUtil.success(userVo));
		} catch (Exception e) {
			logger.error("获取用户详细信息错误：" + e);
			resultBean = ResultUtil.error(ResultEnum.SYSTEM_ERROR.getCode(),
					ResultEnum.SYSTEM_ERROR.getMessage());
			return jsonMapper.toJson(resultBean);
		}
	}

	@RequestMapping(value = "save", produces = "application/json;charset=UTF-8", method = { RequestMethod.POST })
	@ResponseBody
	@ApiOperation(value = "保存用户信息", httpMethod = "POST", notes = "保存用户信息", consumes = "application/json")
	public String save(HttpServletRequest request,
			HttpServletResponse response, @RequestBody UserVo userVo) {
		response.setContentType("application/json; charset=UTF-8");
		JsonMapper jsonMapper = JsonMapper.getInstance();
		ResultBean<UserVo> resultBean = new ResultBean<UserVo>();
		try {
			User user = new User();
			String token = request.getHeader("X-Token");
			User currentUser = apiSystemService.findUserByToken(token);
			BeanUtils.copyProperties(userVo, user, new String[] { "id",
					"loginIp", "loginDate", "createDate", "updateDate",
					"delFlag" });
			user.setCompany(new Office(userVo.getCompanyId()));
			user.setOffice(new Office(userVo.getCompanyId()));
			// 登录后台时密码必须6位数，所以新增时加上这个判断
			if (userVo.getPassword().length() < 6) {
				resultBean = ResultUtil.error(
						ResultEnum.SYSTEM_USER_PASSWORD_LENGTH.getCode(),
						ResultEnum.SYSTEM_USER_PASSWORD_LENGTH.getMessage());
				return jsonMapper.toJson(resultBean);
			}
			user.setPassword(SystemService.entryptPassword(userVo.getPassword()));
			if (StringUtils.isNotBlank(userVo.getUserImg())) {
				user.setUserImg(userVo.getUserImg());
			}
			if (userVo.getRoleListIds().size() == 0) {
				// 如果没有选择角色，用户登录不上后台
				resultBean = ResultUtil.error(
						ResultEnum.SYSTEM_USER_ROLE.getCode(),
						ResultEnum.SYSTEM_USER_ROLE.getMessage());
				return jsonMapper.toJson(resultBean);
			}
			// 筛选掉不在授权范围内的角色，避免被非法授权
			List<String> roleIds = userVo.getRoleListIds();
			List<Role> roleList = Lists.newArrayList();
			if (roleIds != null) {
				for (Role r : apiSystemService.findAllRole(currentUser)) {
					if (roleIds.contains(r.getId())) {
						roleList.add(r);
					}
				}
			}
			user.setRoleList(roleList);

			user.setCreateBy(currentUser);
			if (apiSystemService.existUserName(user)) {
				resultBean = ResultUtil.error(
						ResultEnum.SYSTEM_USER_EXIST.getCode(),
						ResultEnum.SYSTEM_USER_EXIST.getMessage());
				return jsonMapper.toJson(resultBean);
			}
			apiSystemService.saveUser(user);
			return jsonMapper.toJson(ResultUtil.success());
		} catch (Exception e) {
			logger.error("保存用户信息错误：" + e);
			resultBean = ResultUtil.error(ResultEnum.SYSTEM_ERROR.getCode(),
					ResultEnum.SYSTEM_ERROR.getMessage());
			return jsonMapper.toJson(resultBean);
		}
	}

	
	@RequestMapping(value = "update", produces = "application/json;charset=UTF-8", method = {RequestMethod.POST})
	@ResponseBody
	@ApiOperation(value = "更新用户信息", httpMethod = "POST", notes = "更新用户信息", consumes = "application/x-www-form-urlencoded")
	public String update(HttpServletRequest request,HttpServletResponse response,@RequestBody UpdataUserVO uservo) {
		//response.setContentType("application/json; charset=UTF-8");
		JsonMapper jsonMapper = JsonMapper.getInstance();
		ResultBean<UserVo> resultBean = new ResultBean<UserVo>();
		try {
			User user = new User();
			String token = request.getHeader("X-Token");
			User currentUser = apiSystemService.findUserByToken(token);
			if (StringUtils.isNotBlank(uservo.getId()) && StringUtils.isNotBlank(uservo.getCompanyId())&& StringUtils.isNotBlank(uservo.getOfficeId())) {
				user = apiSystemService.getUser(uservo.getId());
				user.setName(uservo.getName());
				if (StringUtils.isNotBlank(uservo.getPassword())) {
					if (uservo.getPassword().length() < 6) {
						resultBean = ResultUtil.error(ResultEnum.SYSTEM_USER_PASSWORD_LENGTH.getCode(),ResultEnum.SYSTEM_USER_PASSWORD_LENGTH.getMessage());
						return jsonMapper.toJson(resultBean);
					}
					user.setPassword(SystemService.entryptPassword(uservo.getPassword()));
				}
				if (StringUtils.isNotBlank(uservo.getUserImg())) {
					user.setUserImg(uservo.getUserImg());
				}
				user.setCompany(new Office(uservo.getCompanyId()));
				user.setOffice(new Office(uservo.getCompanyId()));
			} else {
				logger.error("更新用户信息错误：参数不正确");
				resultBean = ResultUtil.error(ResultEnum.SYSTEM_ERROR.getCode(),ResultEnum.SYSTEM_ERROR.getMessage());
				return jsonMapper.toJson(resultBean);
			}
			// 筛选掉不在授权范围内的角色，避免被非法授权
			List<Role> roleList = Lists.newArrayList();
			if (uservo.getRoleListIds() != null) {
				for (Role r : apiSystemService.findAllRole(currentUser)) {
					for (String rid : uservo.getRoleListIds()) {
						if (r.getId().equals(rid)) {
							roleList.add(r);
						}
					}
				}
			}
			if (roleList.size() == 0) {
				// 如果没有选择角色，用户登录不上后台
				resultBean = ResultUtil.error(ResultEnum.SYSTEM_USER_ROLE.getCode(),ResultEnum.SYSTEM_USER_ROLE.getMessage());
				return jsonMapper.toJson(resultBean);
			}
			user.setRoleList(roleList);
			apiSystemService.saveUser(user);
			return jsonMapper.toJson(ResultUtil.success());
		} catch (Exception e) {
			logger.error("更新用户信息错误：" + e);
			e.printStackTrace();
			resultBean = ResultUtil.error(ResultEnum.SYSTEM_ERROR.getCode(),ResultEnum.SYSTEM_ERROR.getMessage());
			return jsonMapper.toJson(resultBean);
		}
	}

	/**
	 * 恢复
	 * 
	 * @return
	 */
	@RequestMapping(value = "enable", produces = "application/json;charset=UTF-8", method = { RequestMethod.POST })
	@ResponseBody
	@ApiOperation(value = "用户恢复", httpMethod = "POST", notes = "用户恢复", consumes = "application/x-www-form-urlencoded")
	public String enable(HttpServletRequest request, @RequestParam String id) {
		apiSystemService.userChangeState(id, User.DEL_FLAG_NORMAL);
		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success());
	}

	/**
	 * 删除
	 * 
	 * @return
	 */
	@RequestMapping(value = "delete", produces = "application/json;charset=UTF-8", method = { RequestMethod.POST })
	@ResponseBody
	@ApiOperation(value = "用户删除", httpMethod = "POST", notes = "用户删除", consumes = "application/x-www-form-urlencoded")
	public String delete(HttpServletRequest request, @RequestParam String id) {
		apiSystemService.userChangeState(id, User.DEL_FLAG_DELETE);
		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success());
	}
}
