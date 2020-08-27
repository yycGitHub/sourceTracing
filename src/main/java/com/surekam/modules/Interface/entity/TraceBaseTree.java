package com.surekam.modules.Interface.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.PrePersist;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.surekam.common.persistence.XGXTEntity;
import com.surekam.common.utils.IdGen;

import java.lang.String;

/**
 * 基地信息Entity
 * @author xy
 * @version 2019-05-29
 */
@Entity
@Table(name = "t_trace_base_tree")
@DynamicInsert @DynamicUpdate
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class TraceBaseTree extends XGXTEntity<TraceBaseTree> {
	
	private static final long serialVersionUID = 1L;
	private String id;//主键
	private String parentId;//上级ID
	private String parentIds;//所有上级ID，用,隔开
	private String name;//名称
	private String officeId;//公司ID
	private String sort;//排序
	private String longitude;//经度
	private String latitude;//纬度
	private String province;//省
	private String city;//市
	private String area;//区县
	private String address;//详细地址
	private String acreage;//面积
	private String showUrl;//展示URL
	private String baseImg;//基地图片地址
	private String baseCode;//基地唯一编号


	public TraceBaseTree() {
		super();
	}
	
	@PrePersist
	public void prePersist(){
		this.id = IdGen.uuid();
	}
		
	@Id
	public String getId() {
		return this.id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getParentId() {
		return this.parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	
	public String getParentIds() {
		return this.parentIds;
	}
	public void setParentIds(String parentIds) {
		this.parentIds = parentIds;
	}
	
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getOfficeId() {
		return this.officeId;
	}
	public void setOfficeId(String officeId) {
		this.officeId = officeId;
	}
	
	public String getSort() {
		return this.sort;
	}
	public void setSort(String sort) {
		this.sort = sort;
	}
	
	public String getLongitude() {
		return this.longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	
	public String getLatitude() {
		return this.latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	
	public String getProvince() {
		return this.province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	
	public String getCity() {
		return this.city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	
	public String getArea() {
		return this.area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	
	public String getAddress() {
		return this.address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
	public String getAcreage() {
		return this.acreage;
	}
	public void setAcreage(String acreage) {
		this.acreage = acreage;
	}
	
	public String getShowUrl() {
		return this.showUrl;
	}
	public void setShowUrl(String showUrl) {
		this.showUrl = showUrl;
	}
	
	public String getBaseImg() {
		return this.baseImg;
	}
	public void setBaseImg(String baseImg) {
		this.baseImg = baseImg;
	}

	public String getBaseCode() {
		return baseCode;
	}

	public void setBaseCode(String baseCode) {
		this.baseCode = baseCode;
	}
	
}


