/**
 * Copyright &copy; 2012-2013 <a href="https://github.com/sureserve/surekam">surekam</a> All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.surekam.modules.sys.entity;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Where;
import org.hibernate.validator.constraints.Length;

import com.google.common.collect.Lists;
import com.surekam.common.persistence.DataEntity;

/**
 * 机构Entity
 * @author sureserve
 * @version 2013-05-15
 */
@Entity
@Table(name = "SYS_OFFICE")
@DynamicInsert @DynamicUpdate
public class Office extends DataEntity<Office> {

	private static final long serialVersionUID = 1L;
	private Office parent;	// 父级编号
	private String parentIds; // 所有父级编号
	private Area area;		// 归属区域
	private String code; 	// 机构编码
	private String name; 	// 机构名称
	private String type; 	// 机构类型（1：公司；2：部门；3：小组）
	private String grade; 	// 机构等级（1：一级；2：二级；3：三级；4：四级）
	private String address; // 联系地址
	private String zipCode; // 邮政编码
	private String master; 	// 负责人
	private String phone; 	// 电话
	private String fax; 	// 传真
	private String email; 	// 邮箱
	
	private String areaName;//所属区域名称  非数据库字段
	private String typeName;// 机构类型（1：公司；2：部门；3：小组）   非数据库字段
	private String invitationCode;//邀请码
	//新加字段
	private String officeLogo; 	        // 企业logo
	private String operatorName; 	    // 运营者姓名
	private String operatorIdnumber; 	// 运营者身份证号
	private String officeCode; 	// 企业的唯一编号
	private String codeMode; 	// 企业打码方式 集中打码1  企业自行打码2
	private String officeScale; 	// 企业规模 大型企业1   中型企业 2   小型企业3

	private String kuid;//权限控制中心公司表ID
	private List<User> userList = Lists.newArrayList();   // 拥有用户列表
	private List<Office> childList = Lists.newArrayList();// 拥有子机构列表
	private String selected;//1-未选中，2-选中

	public Office(){
		super();
	}
	
	public Office(String id){
		this();
		this.id = id;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="parent_id")
	@NotFound(action = NotFoundAction.IGNORE)
	@NotNull
	public Office getParent() {
		return parent;
	}

	public void setParent(Office parent) {
		this.parent = parent;
	}

	@Length(min=1, max=255)
	public String getParentIds() {
		return parentIds;
	}

	public void setParentIds(String parentIds) {
		this.parentIds = parentIds;
	}

	@ManyToOne
	@JoinColumn(name="area_id")
	@NotFound(action = NotFoundAction.IGNORE)
	@NotNull
	public Area getArea() {
		return area;
	}

	public void setArea(Area area) {
		this.area = area;
	}

	@Length(min=1, max=100)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Length(min=1, max=1)
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Length(min=1, max=1)
	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	@Length(min=0, max=255)
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Length(min=0, max=100)
	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	@Length(min=0, max=100)
	public String getMaster() {
		return master;
	}

	public void setMaster(String master) {
		this.master = master;
	}

	@Length(min=0, max=200)
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Length(min=0, max=200)
	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	@Length(min=0, max=200)
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Length(min=0, max=100)
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	@OneToMany(mappedBy = "office", fetch=FetchType.LAZY)
	@Where(clause="del_flag='"+DEL_FLAG_NORMAL+"'")
	@OrderBy(value="id") @Fetch(FetchMode.SUBSELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	public List<User> getUserList() {
		return userList;
	}

	public void setUserList(List<User> userList) {
		this.userList = userList;
	}

	@OneToMany(mappedBy = "parent", fetch=FetchType.LAZY)
	@Where(clause="del_flag='"+DEL_FLAG_NORMAL+"'")
	@OrderBy(value="code") @Fetch(FetchMode.SUBSELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	public List<Office> getChildList() {
		return childList;
	}

	public void setChildList(List<Office> childList) {
		this.childList = childList;
	}

	@Transient
	public static void sortList(List<Office> list, List<Office> sourcelist, String parentId){
		for (int i=0; i<sourcelist.size(); i++){
			Office e = sourcelist.get(i);
			if (e.getParent()!=null && e.getParent().getId()!=null
					&& e.getParent().getId().equals(parentId)){
				list.add(e);
				for (int j=0; j<sourcelist.size(); j++){
					Office child = sourcelist.get(j);
					if (child.getParent()!=null && child.getParent().getId()!=null
							&& child.getParent().getId().equals(e.getId())){
						sortList(list, sourcelist, e.getId());
						break;
					}
				}
			}
		}
	}

	@Transient
	public boolean isRoot(){
		return isRoot(this.id);
	}
	
	@Transient
	public static boolean isRoot(String id){
		return id != null && id.equals("1");
	}

	@Transient
	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	@Transient
	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	@Length(min=0, max=1000)
	public String getOfficeLogo() {
		return officeLogo;
	}

	public void setOfficeLogo(String officeLogo) {
		this.officeLogo = officeLogo;
	}

	@Length(min=0, max=100)
	public String getOperatorName() {
		return operatorName;
	}

	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}

	@Length(min=0, max=18)
	public String getOperatorIdnumber() {
		return operatorIdnumber;
	}

	public void setOperatorIdnumber(String operatorIdnumber) {
		this.operatorIdnumber = operatorIdnumber;
	}

	public String getInvitationCode() {
		return invitationCode;
	}

	public void setInvitationCode(String invitationCode) {
		this.invitationCode = invitationCode;
	}

	public String getOfficeCode() {
		return officeCode;
	}

	public void setOfficeCode(String officeCode) {
		this.officeCode = officeCode;
	}

	public String getCodeMode() {
		return codeMode;
	}

	public void setCodeMode(String codeMode) {
		this.codeMode = codeMode;
	}

	public String getOfficeScale() {
		return officeScale;
	}

	public void setOfficeScale(String officeScale) {
		this.officeScale = officeScale;
	}

	public String getKuid() {
		return kuid;
	}

	public void setKuid(String kuid) {
		this.kuid = kuid;
	}

	@Transient
	public String getSelected() {
		return selected;
	}

	public void setSelected(String selected) {
		this.selected = selected;
	}
	
}