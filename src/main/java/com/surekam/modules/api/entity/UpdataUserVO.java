package com.surekam.modules.api.entity;


/**
 * 修改用户信息，接受参数对象
 * @author 腾农科技
 *
 */
public class UpdataUserVO {
	
	private String id;
	private String companyId;
	private String officeId;
	private String loginName;
	private String password;
	private String name;
	private String userImg;
	private String [] roleListIds;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCompanyId() {
		return companyId;
	}
	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}
	public String getOfficeId() {
		return officeId;
	}
	public void setOfficeId(String officeId) {
		this.officeId = officeId;
	}
	public String getLoginName() {
		return loginName;
	}
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUserImg() {
		return userImg;
	}
	public void setUserImg(String userImg) {
		this.userImg = userImg;
	}
	public String[] getRoleListIds() {
		return roleListIds;
	}
	public void setRoleListIds(String[] roleListIds) {
		this.roleListIds = roleListIds;
	}

}
