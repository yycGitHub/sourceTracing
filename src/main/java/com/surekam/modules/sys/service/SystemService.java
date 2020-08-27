package com.surekam.modules.sys.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.Group;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.shiro.SecurityUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.surekam.common.config.Global;
import com.surekam.common.persistence.Page;
import com.surekam.common.persistence.Parameter;
import com.surekam.common.security.Digests;
import com.surekam.common.service.BaseService;
import com.surekam.common.service.ServiceException;
import com.surekam.common.utils.Collections3;
import com.surekam.common.utils.Encodes;
import com.surekam.common.utils.StringUtils;
import com.surekam.modules.sys.dao.MenuDao;
import com.surekam.modules.sys.dao.RoleDao;
import com.surekam.modules.sys.dao.UserDao;
import com.surekam.modules.sys.entity.CommonUser;
import com.surekam.modules.sys.entity.Menu;
import com.surekam.modules.sys.entity.Role;
import com.surekam.modules.sys.entity.User;
import com.surekam.modules.sys.security.SystemAuthorizingRealm;
import com.surekam.modules.sys.utils.UserUtils;

/**
 * 系统管理，安全相关实体的管理类,包括用户、角色、菜单.
 */
@Service
@Transactional(readOnly = true)
public class SystemService extends BaseService  {
	
	public static final String HASH_ALGORITHM = "SHA-1";
	public static final int HASH_INTERATIONS = 1024;
	public static final int SALT_SIZE = 8;
	
	@Autowired
	private UserDao userDao;
	@Autowired
	private RoleDao roleDao;
	@Autowired
	private MenuDao menuDao;
	@Autowired
	private SystemAuthorizingRealm systemRealm;
	@Autowired
	private IdentityService identityService;

	//-- User Service --//
	
	public User getUser(String id) {
		return userDao.get(id);
	}
	
	public Page<User> findUser(Page<User> page, User user) {
		User currentUser = UserUtils.getUser();
		DetachedCriteria dc = userDao.createDetachedCriteria();			 
		if (user.getCompany() != null && StringUtils.isNotBlank(user.getCompany().getId())){
			dc.createAlias("company", "company");
			dc.add(Restrictions.or(
				Restrictions.eq("company.id", user.getCompany().getId()),
				Restrictions.like("company.parentIds", "%," + user.getCompany().getId() + ",%")
			));
		}			
		if (user.getOffice() != null && StringUtils.isNotBlank(user.getOffice().getId())){
			dc.createAlias("office", "office");
			dc.add(Restrictions.or(
				Restrictions.eq("office.id", user.getOffice().getId()),
				Restrictions.like("office.parentIds", "%," + user.getOffice().getId() + ",%")
			));
		}		
		// 如果不是超级管理员，则不显示超级管理员用户
		if (!currentUser.isAdmin()){
			dc.add(Restrictions.ne("id", "1"));  
		}		
		dc.add(dataScopeFilter(currentUser, "office", ""));		
		if (StringUtils.isNotEmpty(user.getLoginName())){
			dc.add(Restrictions.like("loginName", "%" + user.getLoginName() + "%"));
		}
		if (StringUtils.isNotEmpty(user.getName())){
			dc.add(Restrictions.like("name", "%" + user.getName() + "%"));
		}		
		if (StringUtils.isNotEmpty(user.getDelFlag())){
			dc.add(Restrictions.eq("delFlag", user.getDelFlag()));
		}		
		//dc.addOrder(Order.desc("createDate"));
        Page<User> users = userDao.find(page, dc);
		return users;
	}

	//取用户的数据范围
	public String getDataScope(User user){
		return dataScopeFilterString(user, "office", "");
	}
	
	public User getUserByLoginName(String loginName) {
		return userDao.findByLoginName(loginName);
	}
	
	public User getUserByLoginNameOrWxunionid(String loginName) {
		String hql = "from User where loginName=:p1 or unionId=:p2 and delFlag=:p3";
		return  userDao.getByHql(hql,new Parameter(loginName,loginName,User.DEL_FLAG_NORMAL));
	}

	@Transactional(readOnly = false)
	public void saveUser(User user) {
		userDao.clear();
		userDao.save(user);
		systemRealm.clearAllCachedAuthorizationInfo();
	}

	@Transactional(readOnly = false)
	public void deleteUser(String id) {
		userDao.deleteById(id);
	}
	
	@Transactional(readOnly = false)
	public String deleteUsers(String[] ids) {
		String userId = UserUtils.getUser().getId();
		for(String id:ids){
			if (userId.equals(id)) {
				throw new ServiceException("删除用户失败, 不允许删除当前用户");
			} else if (User.isAdmin(id)) {
				throw new ServiceException("删除用户失败, 不允许删除超级管理员用户");
			} else {
				userDao.deleteById(id);
			}
		}
		return "删除用户成功";
	}
	
	@Transactional(readOnly = false)
	public void rebackUsers(String[] ids) {
		try {
			for(String id:ids){
				userDao.updateDelFlag(id,User.DEL_FLAG_NORMAL);
			}
		} catch (Exception e) {
			throw new ServiceException("恢复用户失败！");
		}
	}
	
	@Transactional(readOnly = false)
	public void updatePasswordById(String id, String loginName, String newPassword) {
		userDao.updatePasswordById(entryptPassword(newPassword), id);
		systemRealm.clearCachedAuthorizationInfo(loginName);
	}
	
	@Transactional(readOnly = false)
	public void updateUserLoginInfo(String id) {
		userDao.updateLoginInfo(SecurityUtils.getSubject().getSession().getHost(), new Date(), id);
	}
	
	/**
	 * 生成安全的密码，生成随机的16位salt并经过1024次 sha-1 hash
	 */
	public static String entryptPassword(String plainPassword) {
		byte[] salt = Digests.generateSalt(SALT_SIZE);
		byte[] hashPassword = Digests.sha1(plainPassword.getBytes(), salt, HASH_INTERATIONS);
		return Encodes.encodeHex(salt)+Encodes.encodeHex(hashPassword);
	}
	
	/**
	 * 验证密码
	 * @param plainPassword 明文密码
	 * @param password 密文密码
	 * @return 验证成功返回true
	 */
	public static boolean validatePassword(String plainPassword, String password) {
		byte[] salt = Encodes.decodeHex(password.substring(0,16));
		byte[] hashPassword = Digests.sha1(plainPassword.getBytes(), salt, HASH_INTERATIONS);
		return password.equals(Encodes.encodeHex(salt)+Encodes.encodeHex(hashPassword));
	}
	
	//-- Role Service --//
	
	public Role getRole(String id) {
		return roleDao.get(id);
	}

	public Role findRoleByName(String name) {
		return roleDao.findByName(name);
	}
	
	public List<Role> findAllRole(){
		return UserUtils.getRoleList();
	}
	
	@Transactional(readOnly = false)
	public void saveRole(Role role) {
		roleDao.clear();
		roleDao.save(role);
		//修改角色关联的用户的权限
		//1.获取角色关联用户
		String hql = "select distinct u from Role r, User u where u in elements (r.userList) and r.id=:p1";
		List<User> users = userDao.find(hql, new Parameter(role.getId()));
		if(users!=null && users.size()>0){
			//2.获取关联角色的权限
			
			//3.替换成新的权限
			
		}
		systemRealm.clearAllCachedAuthorizationInfo();
		UserUtils.removeCache(UserUtils.CACHE_ROLE_LIST);
	}

	@Transactional(readOnly = false)
	public void deleteRole(String id) {
		roleDao.deleteById(id);
		systemRealm.clearAllCachedAuthorizationInfo();
		UserUtils.removeCache(UserUtils.CACHE_ROLE_LIST);
	}
	
	@Transactional(readOnly = false)
	public Boolean outUserInRole(Role role, String userId) {
		User user = userDao.get(userId);
		List<String> roleIds = user.getRoleIdList();
		List<Role> roles = user.getRoleList();
		// 
		if (roleIds.contains(role.getId())) {
			roles.remove(role);
			saveUser(user);
			return true;
		}
		return false;
	}
	
	@Transactional(readOnly = false)
	public User assignUserToRole(Role role, String userId) {
		User user = userDao.get(userId);
		List<String> roleIds = user.getRoleIdList();
		if (roleIds.contains(role.getId())) {
			return null;
		}
		user.getRoleList().add(role);
		saveUser(user);		
		return user;
	}

	//-- Menu Service --//
	
	public Menu getMenu(String id) {
		return menuDao.get(id);
	}

	public List<Menu> findAllMenu(){
		return UserUtils.getMenuList();
	}
	
	public List<Menu> findAllMenu(String  userid){
		return menuDao.findByUserId(userid);
	}
	
	@Transactional(readOnly = false)
	public void saveMenu(Menu menu) {
		menu.setParent(this.getMenu(menu.getParent().getId()));
		String oldParentIds = menu.getParentIds(); // 获取修改前的parentIds，用于更新子节点的parentIds
		menu.setParentIds(menu.getParent().getParentIds()+menu.getParent().getId()+",");
		menuDao.clear();
		menuDao.save(menu);
		// 更新子节点 parentIds
		List<Menu> list = menuDao.findByParentIdsLike("%,"+menu.getId()+",%");
		for (Menu e : list){
			e.setParentIds(e.getParentIds().replace(oldParentIds, menu.getParentIds()));
		}
		menuDao.save(list);
		systemRealm.clearAllCachedAuthorizationInfo();
		UserUtils.removeCache(UserUtils.CACHE_MENU_LIST);
	}

	@Transactional(readOnly = false)
	public void deleteMenu(String id) {
		menuDao.deleteById(id, "%,"+id+",%");
		systemRealm.clearAllCachedAuthorizationInfo();
		UserUtils.removeCache(UserUtils.CACHE_MENU_LIST);
	}

//	@Transactional(readOnly = false)
//	public void saveUserPemissions(String userId, List<Menu> menuList) {
//		User user = userDao.get(userId);
//		user.setMenuList(menuList);
//		userDao.save(user);
//	}

	public Menu getMenuByAppId(String id) {
		return  menuDao.getByHql("from Menu where appId=:p1",new Parameter(id));
	}

	@Transactional(readOnly = false)
	public void bindingWxUser(String openId, String loginName) {
		User user = userDao.findByLoginName(loginName);
		user.setOpenId(openId);
		userDao.save(user);
	}
	
	public List<Role> findRole(Role role) {
		DetachedCriteria dc = roleDao.createDetachedCriteria();
		if(role!=null && StringUtils.isNotBlank(role.getName())){
			dc.add(Restrictions.like("name", role.getName(),MatchMode.ANYWHERE));
		}
		dc.add(Restrictions.eq(Role.FIELD_DEL_FLAG, Role.DEL_FLAG_NORMAL));
		return roleDao.find(dc);
	}
	
	public void deleteUser(User user) {
		userDao.deleteById(user.getId());
	}
	
	
	///////////////// Synchronized to the Activiti //////////////////

	/**
	 * 手工同步所有Activiti数据
	 */
	@Transactional(readOnly = false)
	public void synToActiviti()  {
		try{
			menuDao.updateBySql("delete from ACT_ID_MEMBERSHIP",null);
			menuDao.updateBySql("delete from ACT_ID_GROUP", null);
			menuDao.updateBySql("delete from ACT_ID_USER", null);
			
			List<Group> activitiGroupList = identityService.createGroupQuery().list();
			List<org.activiti.engine.identity.User> activitiUserList = identityService.createUserQuery().list();
			if (activitiGroupList.size() == 0 &&activitiUserList.size() == 0){
			 	//同步时候添加所有用户，所有组，以及关联关系，之后增删改用户，增删改角色时不需要判断用户，组是否存在。
			 	List<User> userList = userDao.findAllList();
			 	for(User user:userList){
			 		org.activiti.engine.identity.User activitiUesr = identityService.newUser(ObjectUtils.toString(user.getId()));
			 		identityService.saveUser(activitiUesr);
			 	}
			 	for(Menu menu:menuDao.findAllActivitiList()){
			 		if (StringUtils.isNotEmpty(menu.getActivitiGroupId())){
				 		Group group = identityService.newGroup(menu.getActivitiGroupId());
				 		identityService.saveGroup(group);
			 		}
			 	}
			 	//创建关联关系
			 	for(User user:userList) {
			 		List<Menu> menuList = menuDao.findAllActivitiList(user.getId());
			 		if(!Collections3.isEmpty(menuList)){
			 			for(Menu menu:menuList) {
			 				if (StringUtils.isNotEmpty(menu.getActivitiGroupId())){
			 					identityService.createMembership(ObjectUtils.toString(user.getId()), menu.getActivitiGroupId());
			 				}
			 			}
			 		}
			 	}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void saveActiviti(Role role) {
		if (!Global.isSynActivitiIndetity()){
			return;
		}
		try{
			if(role!=null) {
				List<User> userList = roleDao.get(role.getId()).getUserList();
				if(!Collections3.isEmpty(userList)) {
				 	for(User user:userList) {
				 		String userId = ObjectUtils.toString(user.getId());
						org.activiti.engine.identity.User activitiUser = identityService.createUserQuery().userId(userId).singleResult();
						// 是新增用户
						if (activitiUser == null) {
							activitiUser = identityService.newUser(userId);
							identityService.saveUser(activitiUser);
						} 
						// 同步用户角色关联数据
				 		List<Menu> menuList = menuDao.findAllActivitiList(user.getId());
				 		merge(user, menuList);
				 	}
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void deleteActiviti(Role role) {
		if (!Global.isSynActivitiIndetity()){
			return;
		}
		try{
			if(role!=null) {
				List<User> userList = roleDao.get(role.getId()).getUserList();
				if(!Collections3.isEmpty(userList)) {
				 	for(User user:userList) {
				 		List<Menu> menuList = menuDao.findAllActivitiList(user.getId());
				 		merge(user, menuList);
				 	}
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void saveActiviti(User user) {
		if (!Global.isSynActivitiIndetity()){
			return;
		}
		try{
			if(user!=null) {
				String userId = ObjectUtils.toString(user.getId());
				org.activiti.engine.identity.User activitiUser = identityService.createUserQuery().userId(userId).singleResult();
				// 是新增用户
				if (activitiUser == null) {
					activitiUser = identityService.newUser(userId);
					identityService.saveUser(activitiUser);
				} 
				// 同步用户角色关联数据
		 		List<Menu> menuList = menuDao.findAllActivitiList(user.getId());
		 		merge(user, menuList);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void deleteActiviti(User user) {
		if (!Global.isSynActivitiIndetity()){
			return;
		}
		try{
			if(user!=null) {
				String userId = ObjectUtils.toString(user.getId());
				identityService.deleteUser(userId);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void saveActiviti(Menu menu) {
		if (!Global.isSynActivitiIndetity()){
			return;
		}
		try{
			if(menu!=null){
				Group group = identityService.createGroupQuery().groupId(menu.getActivitiGroupId()).singleResult();
				if(group!=null) {
					identityService.deleteGroup(group.getId());
				}
				if(Menu.YES.equals(menu.getIsActiviti()) && StringUtils.isNotBlank(menu.getActivitiGroupId())){
					group = identityService.newGroup(menu.getActivitiGroupId());
					group.setName(menu.getActivitiGroupName());
					identityService.saveGroup(group);
					List<Role> roleList = menuDao.get(menu.getId()).getRoleList();
					if(!Collections3.isEmpty(roleList)) {
						for(Role role:roleList) {
							List<User> userList = role.getUserList();
							if(!Collections3.isEmpty(userList)) {
								for(User user:userList) {
									identityService.createMembership(ObjectUtils.toString(user.getId()), menu.getActivitiGroupId());
								}
							}
						}
					}
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void deleteActiviti(String id) {
		if (!Global.isSynActivitiIndetity()){
			return;
		}
		try{
			if(id!=null) {
				Menu menu = menuDao.get(id);
				if(Menu.YES.equals(menu.getIsActiviti()) && StringUtils.isNotBlank(menu.getActivitiGroupId())){
					identityService.deleteGroup(menu.getActivitiGroupId());
				}
				if(menu!=null) {
					List<Menu> menuList = menuDao.findByParentIdsLike("%,"+menu.getId()+",%");
					for(Menu m:menuList) {
						if(Menu.YES.equals(menu.getIsActiviti()) && StringUtils.isNotBlank(m.getActivitiGroupId())){
							identityService.deleteGroup(m.getActivitiGroupId());
						}
					}
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private void merge(User user,List<Menu> menuList) {
		if (!Global.isSynActivitiIndetity()){
			return;
		}
		try{
			String userId = ObjectUtils.toString(user.getId());
			List<Group> activitiGroupList = identityService.createGroupQuery().groupMember(userId).list();
			if(Collections3.isEmpty(menuList)) {
				for(Group group:activitiGroupList) {
					identityService.deleteMembership(userId, group.getId());
				}
			} else {
				Map<String,String> groupMap =Maps.newHashMap();
				for(Menu menu:menuList) {
					groupMap.put(menu.getActivitiGroupId(), menu.getActivitiGroupName());
				}
				Map<String,String> activitiGroupMap = Collections3.extractToMap(activitiGroupList, "id", "name");
				for(String groupId:activitiGroupMap.keySet()) {
					if(StringUtils.isNotBlank(groupId) && !groupMap.containsKey(groupId)) {
						identityService.deleteMembership(userId, groupId);
					}
				}
				for(String groupId:groupMap.keySet()) {
					if(StringUtils.isNotBlank(groupId) && !activitiGroupMap.containsKey(groupId)) {
						identityService.createMembership(userId, groupId);
					}
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}


	public Page<Role> findRole(Page<Role> page, Role role) {
		DetachedCriteria dc = roleDao.createDetachedCriteria();			 
		if (StringUtils.isNotEmpty(role.getName())){
			dc.add(Restrictions.like("name", "%" + role.getName() + "%"));
		}
		dc.add(Restrictions.eq(Role.FIELD_DEL_FLAG, Role.DEL_FLAG_NORMAL));
        Page<Role> roles = roleDao.find(page, dc);
		return roles;
	}
	///////////////// Synchronized to the Activiti end //////////////////
	
	
	public List<Menu> findZtreeMenus(User user, String itemId) {
		DetachedCriteria dc = menuDao.createDetachedCriteria();		
		if(StringUtils.isNotBlank(itemId)){
			dc.createAlias("parent", "menu");
			dc.add(Restrictions.eq("parent.id",itemId));
		}else{
			dc.add(Restrictions.eq("id","1"));
		}
		dc.add(dataScopeFilter(user, dc.getAlias(), ""));
		dc.add(Restrictions.eq(Menu.FIELD_DEL_FLAG, Menu.DEL_FLAG_NORMAL));
		dc.addOrder(Order.asc("sort"));
		menuDao.clear();
		return menuDao.find(dc);
	}

	public Page<Menu> findMenuPage(Page<Menu> page, Menu menu, User user) {
		DetachedCriteria dc = menuDao.createDetachedCriteria();	
		dc.add(dataScopeFilter(user, "menu", ""));	
		if (StringUtils.isNotEmpty(menu.getId())){
			dc.createAlias("parent", "menu");
			dc.add(Restrictions.like("parent.id", menu.getId(),MatchMode.ANYWHERE));
		}
		dc.add(Restrictions.eq(Menu.FIELD_DEL_FLAG, menu.getDelFlag()));
		dc.addOrder(Order.desc("createDate"));
		menuDao.clear();
        Page<Menu> menus = menuDao.find(page, dc);
		return menus;
	}
	
	public String getUserNamesByUserIds(String userIds) {
		DetachedCriteria dc = userDao.createDetachedCriteria();			 
		dc.add(Restrictions.in( "id", userIds.split(",") ));
		List<User> users = userDao.find(dc);
		String names = "";
		for(User user : users)
		{
			if(StringUtils.isNotBlank(names))
				names+=",";
			names+=user.getName();
		}
		return names;
	}
	
	public String getGroupNamesByGroupIds(String groupIds) {
		DetachedCriteria dc = roleDao.createDetachedCriteria();			 
		dc.add(Restrictions.in( "id", groupIds.split(",") ));
		List<Role> roles = roleDao.find(dc);
		String names = "";
		for(Role role : roles)
		{
			if(StringUtils.isNotBlank(names))
				names+=",";
			names+=role.getName();
		}
		return names;
	}

	/**
	 * 通过token获取用户信息
	 * @param token
	 * @return
	 */
	public CommonUser findByToken(String token) {
		return userDao.findByToken(token);
	}
	
	
}
