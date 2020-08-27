package com.surekam.modules.tracemodel.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.surekam.common.persistence.XGXTEntity;
import com.surekam.common.utils.IdGen;
import com.surekam.modules.traceproperty.entity.TraceProperty;
import com.surekam.modules.traceproperty.entity.TracePropertyNew;

/**
 * 溯源模块配置管理Entity
 * 
 * @author liw
 * @version 2018-09-07
 */
@Entity
@Table(name = "t_trace_model")
@DynamicInsert
@DynamicUpdate
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class TraceModel extends XGXTEntity<TraceModel> {

	private static final long serialVersionUID = 1L;
	private String id;//
	private String officeId;// 企业编号
	private String corpName;// 企业名称
	private String productId;// 溯源产品编号
	private String productName;// 溯源产品名称
	private String modelCode;// 溯源模块编号
	private String modelName;// 溯源模块名称
	private String sort;// 排序（升序）默认值统一设置为99，按照升序排列
	private String isMainModel;// 是否为主模块
	private String status;// 是否启用
	private String modelShowType;// 显示类型 1模块 2时间轴 类型为2则表示生长记录
	private String showTypeName;// 显示类型名称
	private String creatUserName;// 创建人
	private String userid;// 用户ID
	private String states1;

	@Transient
	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	private List<String> tracePropertyListIds = new ArrayList<String>();
	// 以下用于封装数据
	private List<TraceProperty> tracePropertyList = new ArrayList<TraceProperty>();// 模块对应属性
	// 以下用于封装数据
	private List<TracePropertyNew> tracePropertyNewList = new ArrayList<TracePropertyNew>();// 模块对应属性

	private String modelDataId;// 模块数据ID

	public TraceModel() {
		super();
	}

	@PrePersist
	public void prePersist() {
		this.id = IdGen.uuid();
	}

	@Id
	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Transient
	public String getCorpName() {
		return corpName;
	}

	public void setCorpName(String corpName) {
		this.corpName = corpName;
	}

	@Transient
	public String getShowTypeName() {
		return showTypeName;
	}

	public void setShowTypeName(String showTypeName) {
		this.showTypeName = showTypeName;
	}

	@Transient
	public List<String> getTracePropertyListIds() {
		return tracePropertyListIds;
	}

	public void setTracePropertyListIds(List<String> tracePropertyListIds) {
		this.tracePropertyListIds = tracePropertyListIds;
	}

	@Transient
	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	@Transient
	public String getCreatUserName() {
		return creatUserName;
	}

	public void setCreatUserName(String creatUserName) {
		this.creatUserName = creatUserName;
	}

	public String getOfficeId() {
		return this.officeId;
	}

	public void setOfficeId(String officeId) {
		this.officeId = officeId;
	}

	public String getProductId() {
		return this.productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getModelCode() {
		return this.modelCode;
	}

	public void setModelCode(String modelCode) {
		this.modelCode = modelCode;
	}

	public String getModelName() {
		return this.modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public String getSort() {
		return this.sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the isMainModel
	 */
	public String getIsMainModel() {
		return isMainModel;
	}

	/**
	 * @param isMainModel
	 *            the isMainModel to set
	 */
	public void setIsMainModel(String isMainModel) {
		this.isMainModel = isMainModel;
	}

	@Transient
	public List<TraceProperty> getTracePropertyList() {
		return tracePropertyList;
	}

	public void setTracePropertyList(List<TraceProperty> tracePropertyList) {
		this.tracePropertyList = tracePropertyList;
	}

	@Transient
	public List<TracePropertyNew> getTracePropertyNewList() {
		return tracePropertyNewList;
	}

	public void setTracePropertyNewList(List<TracePropertyNew> tracePropertyNewList) {
		this.tracePropertyNewList = tracePropertyNewList;
	}

	public String getModelShowType() {
		return modelShowType;
	}

	public void setModelShowType(String modelShowType) {
		this.modelShowType = modelShowType;
	}

	@Transient
	public String getModelDataId() {
		return modelDataId;
	}

	public void setModelDataId(String modelDataId) {
		this.modelDataId = modelDataId;
	}

	@Transient
	public String getStates1() {
		return states1;
	}

	public void setStates1(String states1) {
		this.states1 = states1;
	}

}
