package com.surekam.modules.traceproperty.entity;

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

/**
 * 溯源属性库管理Entity
 * 
 * @author liw
 * @version 2018-09-06
 */
@Entity
@Table(name = "t_trace_property")
@DynamicInsert
@DynamicUpdate
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class TraceProperty extends XGXTEntity<TraceProperty> {

	private static final long serialVersionUID = 1L;
	private String id;// 主键
	private String officeId;// 企业编号（企业编号为‘10000’默认为溯源平台，溯源平台的溯源属性为通用属性）
	private String corpName;// 企业名称
	private String propertyCode;// 属性编号
	private String modelId;// 模块id
	private String propertyNameEn;// 属性英文名
	private String propertyNameZh;// 属性中文名
	private String propertyType;// 属性类型：例如1.普通文本、2.HTML文本、3.图片、4.链接、5.时间控件
	private String propertyTypeName;// 属性类型名称
	private String timeFlag;// 时间轴标记，只使用于时间属性，默认为0 ，标记为时间轴属性时为 1
	private String timeFlagName;// 时间轴标记名称
	private int sort;// 排序（升序）默认值统一设置为99，按照升序排列
	private String status;// 状态 1启用 0停用
	private String statusName;// 状态名称
	private String creatUserName;// 创建人
	private String modelName;// 所属模块
	private String propertyValue;// 属性值 用于一次性保存模块和属性数据时封装属性值 非数据库字段
	private String propertyDataId;// 属性值对应主键 用于封装传递数据 非数据库字段

	public TraceProperty() {
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

	public String getOfficeId() {
		return this.officeId;
	}

	public void setOfficeId(String officeId) {
		this.officeId = officeId;
	}

	public String getPropertyCode() {
		return this.propertyCode;
	}

	public void setPropertyCode(String propertyCode) {
		this.propertyCode = propertyCode;
	}

	@Transient
	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	/**
	 * @return the modelId
	 */
	public String getModelId() {
		return modelId;
	}

	/**
	 * @param modelId
	 *            the modelId to set
	 */
	public void setModelId(String modelId) {
		this.modelId = modelId;
	}

	public String getPropertyNameEn() {
		return this.propertyNameEn;
	}

	public void setPropertyNameEn(String propertyNameEn) {
		this.propertyNameEn = propertyNameEn;
	}

	public String getPropertyNameZh() {
		return this.propertyNameZh;
	}

	public void setPropertyNameZh(String propertyNameZh) {
		this.propertyNameZh = propertyNameZh;
	}

	public String getPropertyType() {
		return this.propertyType;
	}

	public void setPropertyType(String propertyType) {
		this.propertyType = propertyType;
	}

	public String getTimeFlag() {
		return this.timeFlag;
	}

	public void setTimeFlag(String timeFlag) {
		this.timeFlag = timeFlag;
	}

	public int getSort() {
		return this.sort;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Transient
	public String getPropertyTypeName() {
		return propertyTypeName;
	}

	public void setPropertyTypeName(String propertyTypeName) {
		this.propertyTypeName = propertyTypeName;
	}

	@Transient
	public String getTimeFlagName() {
		return timeFlagName;
	}

	public void setTimeFlagName(String timeFlagName) {
		this.timeFlagName = timeFlagName;
	}

	@Transient
	public String getCreatUserName() {
		return creatUserName;
	}

	public void setCreatUserName(String creatUserName) {
		this.creatUserName = creatUserName;
	}

	@Transient
	public String getCorpName() {
		return corpName;
	}

	public void setCorpName(String corpName) {
		this.corpName = corpName;
	}

	@Transient
	public String getStatusName() {
		return statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}

	@Transient
	public String getPropertyValue() {
		return propertyValue;
	}

	public void setPropertyValue(String propertyValue) {
		this.propertyValue = propertyValue;
	}

	@Transient
	public String getPropertyDataId() {
		return propertyDataId;
	}

	public void setPropertyDataId(String propertyDataId) {
		this.propertyDataId = propertyDataId;
	}

}
