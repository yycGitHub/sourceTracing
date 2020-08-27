package com.surekam.modules.api.service;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.surekam.common.service.BaseService;
import com.surekam.common.utils.DateUtils;
import com.surekam.modules.sys.dao.LoginTokenDao;
import com.surekam.modules.sys.dao.UserDao;
import com.surekam.modules.sys.entity.LoginToken;
import com.surekam.modules.sys.entity.User;
import com.surekam.modules.sys.entity.vo.UserVo;
import com.surekam.modules.sys.service.SystemService;

/**
 * 接入方注册Service
 * 
 * @author lb
 * @version 2018-08-17
 */
@Component
@Transactional(readOnly = true)
public class ApiUserService extends BaseService {

	@Autowired
	private UserDao userDao;
	@Autowired
	private LoginTokenDao loginTokenDao;

	@Transactional(readOnly = false)
	public String login(String loginName, String pwd) {
		User user = userDao.findByLoginName(loginName);
		if (user != null) {
			String token = UUID.randomUUID().toString();
			String uuid = token.replace("-", "");
			LoginToken loginToken = new LoginToken();
			loginToken.setId(uuid);
			loginToken.setToken(token);
			loginToken.setUserName(loginName);
			loginToken.setFailTime(DateUtils.addMinutes(new Date(), 60 * 30 * 30 ));// 1080小时内免登录
			if (user != null && SystemService.validatePassword(pwd, user.getPassword())) {// 登录成功
				// 返回信息
				loginToken.setUserId(user.getId());
				//loginTokenDao.deleteLoginTokenByUserId(user.getId());
				loginTokenDao.save(loginToken);
				return token;
			} else {// 登录失败
				return "1";
			}
		} else {// 登录失败
			return "1";
		}
	}

	@Transactional(readOnly = false)
	public String skipLogin(String userId) {
		User user = userDao.get(userId);
		if (user != null) {
			String token = UUID.randomUUID().toString();
			String uuid = token.replace("-", "");
			LoginToken loginToken = new LoginToken();
			loginToken.setId(uuid);
			loginToken.setToken(token);
			loginToken.setUserName(user.getLoginName());
			loginToken.setFailTime(DateUtils.addMinutes(new Date(), 60 * 30 * 30));// 1080小时内免登录
			// 返回信息
			loginToken.setUserId(user.getId());
			// loginTokenDao.deleteLoginTokenByUserId(user.getId());
			loginTokenDao.save(loginToken);
			return token;
		} else {// 登录失败
			return "1";
		}
	}
	@Transactional(readOnly = false)
	public void saveToken(String loginName, String token) {
		User user = userDao.findByLoginName(loginName);
		String id = UUID.randomUUID().toString();
		String uuid = id.replace("-", "");
		LoginToken loginToken = new LoginToken();
		loginToken.setId(uuid);
		loginToken.setToken(token);
		loginToken.setUserName(loginName);
		loginToken.setFailTime(DateUtils.addMinutes(new Date(), 60 * 30 * 30 ));// 1080小时内免登录
		loginToken.setUserId(user.getId());
		loginTokenDao.flush();
		loginTokenDao.save(loginToken);
	}
	
	/**
	 * 根据token查询用户
	 * @param token
	 * @return
	 */
	public User getUserByToken(String token) {
		LoginToken loginToken = loginTokenDao.findByToken(token);
		if(StringUtils.isNotBlank(loginToken.getUserId())){
			return userDao.get(loginToken.getUserId());
		}else{
			return userDao.get("1");
		}
	}
	@Transactional(readOnly = false)
	public String casLogin(String loginName) {
		User user = userDao.findByLoginName(loginName);
		if (user != null) {
			String token = UUID.randomUUID().toString();
			String uuid = token.replace("-", "");
			LoginToken loginToken = new LoginToken();
			loginToken.setId(uuid);
			loginToken.setToken(token);
			loginToken.setUserName(user.getLoginName());
			loginToken.setFailTime(DateUtils.addMinutes(new Date(), 60 * 30 * 30));// 1080小时内免登录
			// 返回信息
			loginToken.setUserId(user.getId());
			// loginTokenDao.deleteLoginTokenByUserId(user.getId());
			loginTokenDao.save(loginToken);
			return token;
		} else {// 登录失败
			return "1";
		}
	}
	@Transactional(readOnly = false)
	public void destroyedUser(String agroToken) {
		loginTokenDao.deleteLoginTokenByToken(agroToken);
	}
	
	/**
	 * 根据token查询用户
	 * @param token
	 * @return
	 */
	public Map findUserByToken(String token) {
		return userDao.findUserByToken(token);
	}

}
