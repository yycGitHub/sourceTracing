/**
 * Copyright &copy; 2012-2013 <a href="https://github.com/sureserve/surekam">surekam</a> All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.surekam.modules.sys.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;
import com.surekam.common.persistence.BaseDao;
import com.surekam.common.persistence.Parameter;
import com.surekam.modules.sys.entity.CommonUser;
import com.surekam.modules.sys.entity.LoginToken;
import com.surekam.modules.sys.entity.Menu;
import com.surekam.modules.sys.entity.Office;
import com.surekam.modules.sys.entity.Role;
import com.surekam.modules.sys.entity.User;
import com.surekam.modules.sys.entity.vo.UserVo;

/**
 * 用户DAO接口
 * 
 * @author sureserve
 * @version 2013-8-23
 */
@Repository
public class UserDao extends BaseDao<User> {

	@Autowired
	private LoginTokenDao loginTokenDao;

	@Autowired
	private UserRoleDao userRoleDao;

	@Autowired
	private RoleDao roleDao;

	@Autowired
	private MenuDao menuDao;
	@Autowired
	private OfficeDao officeDao;

	public List<User> findAllList() {
		return find("from User where delFlag=:p1 order by id", new Parameter(User.DEL_FLAG_NORMAL));
	}

	public User findByLoginName(String loginName) {
		return getByHql("from User where loginName = :p1 and delFlag = :p2",
				new Parameter(loginName, User.DEL_FLAG_NORMAL));
	}

	public User findByOpenId(String openId) {
		return getByHql("from User where openId = :p1 and delFlag = :p2", new Parameter(openId, User.DEL_FLAG_NORMAL));
	}

	public User getUserByLoginCode(String loginCode) {
		return getByHql("from User where loginCode = :p1 and delFlag = :p2",
				new Parameter(loginCode, User.DEL_FLAG_NORMAL));
	}

	public int updatePasswordById(String newPassword, String id) {
		return update("update User set password=:p1 where id = :p2", new Parameter(newPassword, id));
	}

	public int updateLoginInfo(String loginIp, Date loginDate, String id) {
		return update("update User set loginIp=:p1, loginDate=:p2 where id = :p3",
				new Parameter(loginIp, loginDate, id));
	}

	public User findByUnionId(String unionId) {
		return getByHql("from User where unionId = :p1 and delFlag = :p2",
				new Parameter(unionId, User.DEL_FLAG_NORMAL));
	}

	public CommonUser findByToken(String token) {
		LoginToken loginToken = loginTokenDao.findByToken(token);
		if (StringUtils.isNotBlank(loginToken.getUserId())) {
			User user = get(loginToken.getUserId());
			CommonUser cuser = new CommonUser();
			if (user.getOffice() != null) {
				Office office = officeDao.get(user.getOffice().getId());
				if (office.getId().equals("1") || StringUtils.isBlank(office.getName())) {
					cuser.setOfficeName("农科院信息所公共溯源平台");
				} else {
					cuser.setOfficeName(office.getName());
				}
				cuser.setOfficeLog(office.getOfficeLogo());
			}
			cuser.setId(user.getId());
			cuser.setLoginName(user.getLoginName());
			cuser.setName(user.getName());
			cuser.setPhone(user.getPhone());
			List<String> roleNameList = new ArrayList<String>();
			List<String> roleIdList = userRoleDao.findByUserId(loginToken.getUserId());
			for (int i = 0; i < roleIdList.size(); i++) {
				Role role = roleDao.get(roleIdList.get(i).toString());
				roleNameList.add(role.getName());
			}
			cuser.setRoleIdList(roleIdList);
			cuser.setRoleNameList(roleNameList);
//			List<Menu> menuList = menuDao.findAllList(roleIdList);
//			cuser.setMenuList(menuList);

			return cuser;
		} else {
			return new CommonUser();
		}
	}

	public List<User> findUserByIds(String[] ids, User user) {
		StringBuffer sql = new StringBuffer();
		sql.append("from User where delFlag=:p1 and id in (:p2) ");
		if (StringUtils.isNotBlank(user.getLoginName())) {
			sql.append(" and loginName like " + "'%" + user.getLoginName() + "%'");
		}
		if (StringUtils.isNotBlank(user.getName())) {
			sql.append(" and name like " + "'%" + user.getName() + "%'");
		}
		if (user.getOffice() != null && StringUtils.isNotBlank(user.getOffice().getId())) {
			sql.append(" and office.id = '" + user.getOffice().getId() + "'");
		}
		sql.append(" order by id");

		return find(sql.toString(), new Parameter(User.DEL_FLAG_NORMAL, ids));
	}

	public List<User> findUserListByCondition(User user) {
		StringBuffer sql = new StringBuffer();
		sql.append("from User where delFlag=:p1");

		if (StringUtils.isNotBlank(user.getLoginName())) {
			sql.append(" and loginName like " + "'%" + user.getLoginName() + "%'");
		}
		if (StringUtils.isNotBlank(user.getName())) {
			sql.append(" and name like " + "'%" + user.getName() + "%'");
		}
		if (user.getOffice() != null && StringUtils.isNotBlank(user.getOffice().getId())) {
			sql.append(" and office.id = '" + user.getOffice().getId() + "'");
		}
		sql.append(" order by id");
		Parameter param = new Parameter(User.DEL_FLAG_NORMAL);

		return find(sql.toString(), param);
	}

	public User findByOfficeId(String officeId,String roleId) {
		String sql = "select a.* from sys_user a inner join sys_user_role b on a.id = b.user_id inner join sys_role c on b.role_id = c.id where a.del_flag = '0' and c.del_flag = '0' and a.office_id =:p1 and c.id =:p2 ";
		List<User> User = findBySql(sql, new Parameter(officeId, roleId), User.class);
		if (!User.isEmpty()) {
			return User.get(0);
		} else {
			return null;
		}
	}
	
	public Map findUserByToken(String token){
		String sql = "select a.id, a.name from sys_user a, sys_login_token b where a.del_flag='0' and b.user_id=a.id and b.token=:p1";
		List<Map> list = findBySql(sql, new Parameter(token), Map.class);
		if (!list.isEmpty()) {
			return list.get(0);
		} else {
			return null;
		}
	}

}
