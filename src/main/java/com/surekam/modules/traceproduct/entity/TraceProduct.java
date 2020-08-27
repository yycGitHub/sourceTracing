package com.surekam.modules.traceproduct.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.surekam.common.persistence.XGXTEntity;
import com.surekam.common.utils.IdGen;
import com.surekam.modules.productbatch.entity.ProductBatch;
import com.surekam.modules.trace.TraceLableApply.entity.TraceLableApply;
import com.surekam.modules.tracemodel.entity.TraceModel;
import com.surekam.modules.traceresumetheme.entity.TraceResumeTheme;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 溯源产品管理Entity
 * 
 * @author ligm
 * @version 2018-08-21
 */
@Entity
@Table(name = "t_trace_product")
@DynamicInsert
@DynamicUpdate
@ApiModel(value = "溯源产品对象", description = "溯源产品TraceProduct")
public class TraceProduct extends XGXTEntity<TraceProduct> {

	private static final long serialVersionUID = 1L;
	@ApiModelProperty(hidden = true)
	private String id;// 主键
	private String officeId;// 企业编号
	@ApiModelProperty(hidden = true)
	private String corpName;// 企业名称
	@ApiModelProperty(hidden = true)
	private String productCode;// 溯源产品编号
	@ApiModelProperty(value = "溯源产品名称", required = true)
	private String productName;// 溯源产品名称
	private String productTitle;// 溯源产品标题
	private String showType;// 显示类型 1模块 2时间轴
	@ApiModelProperty(hidden = true)
	private String showTypeName;// 显示类型名称
	private String themeId;// 溯源主题编号
	@ApiModelProperty(hidden = true)
	private String themeName;// 溯源主题名称
	@ApiModelProperty(value = "产品图片", required = true)
	private String productPic;// 产品图片
	@ApiModelProperty(hidden = true)
	private String productPicUrl;// 产品图片绝对路径
	private String productDiscription;// 产品描述
	private String lableAuditFlag;// 溯源标签申请审核开关 0关闭 1开启
	@ApiModelProperty(hidden = true)
	private String lableAuditFlagName;// 溯源标签申请审核开关名称
	@ApiModelProperty(hidden = true)
	private String sort;// 排序（升序）默认值统一设置为99，按照升序排列
	@ApiModelProperty(hidden = true)
	private String status;// 是否启用
	@ApiModelProperty(hidden = true)
	private String statusName;// 是否启用名称
	@ApiModelProperty(hidden = true)
	private String creatUserName;// 创建人
	private String sysId;//外部系统编号（0-本系统，1-农事，2-加工）

	// 以下用于封装模块数据
	List<TraceModel> traceModelList = new ArrayList<TraceModel>();// 模块
	// 以下用于封装标签数据
	List<TraceLableApply> traceLableList = new ArrayList<TraceLableApply>();// 标签
	TraceResumeTheme theme;// 主题
	ProductBatch productBatch;// 批次
	private String traceCode;// 精品溯源码

	private String sqs;// 申请数
	private String dsh;//待审核
	private String yjhs;// 已激活数
	private String wjhs;// 未激活数

	private String cjrq;// 创建日期
	private Integer batchCount;// 批次数量

	private String token;

	// 总标签数
	private String labelCount;
	// 本周扫码次数
	private String weekTraceCount;
	// 本月扫码次数
	private String monthTraceCount;
	// 总扫码次数
	private String traceCount;
	// 复选框
	private String checkedArray;
	
	private String auditFlag;

	public TraceProduct() {
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

	public String getProductCode() {
		return this.productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getProductName() {
		return this.productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductTitle() {
		return this.productTitle;
	}

	public void setProductTitle(String productTitle) {
		this.productTitle = productTitle;
	}

	public String getShowType() {
		return this.showType;
	}

	public void setShowType(String showType) {
		this.showType = showType;
	}

	/**
	 * @return the officeId
	 */
	public String getOfficeId() {
		return officeId;
	}

	/**
	 * @param officeId
	 *            the officeId to set
	 */
	public void setOfficeId(String officeId) {
		this.officeId = officeId;
	}

	/**
	 * @return the themeId
	 */
	public String getThemeId() {
		return themeId;
	}

	/**
	 * @param themeId
	 *            the themeId to set
	 */
	public void setThemeId(String themeId) {
		this.themeId = themeId;
	}

	public String getProductPic() {
		return this.productPic;
	}

	public void setProductPic(String productPic) {
		this.productPic = productPic;
	}

	public String getLableAuditFlag() {
		return this.lableAuditFlag;
	}

	public void setLableAuditFlag(String lableAuditFlag) {
		this.lableAuditFlag = lableAuditFlag;
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

	@Transient
	public String getShowTypeName() {
		return showTypeName;
	}

	public void setShowTypeName(String showTypeName) {
		this.showTypeName = showTypeName;
	}

	@Transient
	public String getLableAuditFlagName() {
		return lableAuditFlagName;
	}

	public void setLableAuditFlagName(String lableAuditFlagName) {
		this.lableAuditFlagName = lableAuditFlagName;
	}

	@Transient
	public String getStatusName() {
		return statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}

	@Transient
	public String getThemeName() {
		return themeName;
	}

	public void setThemeName(String themeName) {
		this.themeName = themeName;
	}

	@Transient
	public String getCreatUserName() {
		return creatUserName;
	}

	public void setCreatUserName(String creatUserName) {
		this.creatUserName = creatUserName;
	}

	@Transient
	public String getProductPicUrl() {
		return productPicUrl;
	}

	public void setProductPicUrl(String productPicUrl) {
		this.productPicUrl = productPicUrl;
	}

	@Transient
	public String getCorpName() {
		return corpName;
	}

	public void setCorpName(String corpName) {
		this.corpName = corpName;
	}

	public String getProductDiscription() {
		return productDiscription;
	}

	public void setProductDiscription(String productDiscription) {
		this.productDiscription = productDiscription;
	}

	@Transient
	public List<TraceModel> getTraceModelList() {
		return traceModelList;
	}

	public void setTraceModelList(List<TraceModel> traceModelList) {
		this.traceModelList = traceModelList;
	}

	@Transient
	public TraceResumeTheme getTheme() {
		return theme;
	}

	public void setTheme(TraceResumeTheme theme) {
		this.theme = theme;
	}

	@Transient
	public ProductBatch getProductBatch() {
		return productBatch;
	}

	public void setProductBatch(ProductBatch productBatch) {
		this.productBatch = productBatch;
	}

	/**
	 * @return the traceCode
	 */
	@Transient
	public String getTraceCode() {
		return traceCode;
	}

	/**
	 * @param traceCode
	 *            the traceCode to set
	 */
	public void setTraceCode(String traceCode) {
		this.traceCode = traceCode;
	}

	@Transient
	public String getYjhs() {
		return yjhs;
	}

	public void setYjhs(String yjhs) {
		this.yjhs = yjhs;
	}

	@Transient
	public String getWjhs() {
		return wjhs;
	}

	public void setWjhs(String wjhs) {
		this.wjhs = wjhs;
	}

	@Transient
	public String getSqs() {
		return sqs;
	}
	
	public void setSqs(String sqs) {
		this.sqs = sqs;
	}
	
	@Transient
	public String getDsh() {
		return dsh;
	}

	public void setDsh(String dsh) {
		this.dsh = dsh;
	}

	@Transient
	public String getCjrq() {
		return cjrq;
	}

	public void setCjrq(String cjrq) {
		this.cjrq = cjrq;
	}

	@Transient
	public Integer getBatchCount() {
		return batchCount;
	}

	public void setBatchCount(Integer batchCount) {
		this.batchCount = batchCount;
	}

	@Transient
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	@Transient
	public String getLabelCount() {
		return labelCount;
	}

	public void setLabelCount(String labelCount) {
		this.labelCount = labelCount;
	}

	@Transient
	public String getWeekTraceCount() {
		return weekTraceCount;
	}

	public void setWeekTraceCount(String weekTraceCount) {
		this.weekTraceCount = weekTraceCount;
	}

	@Transient
	public String getMonthTraceCount() {
		return monthTraceCount;
	}

	public void setMonthTraceCount(String monthTraceCount) {
		this.monthTraceCount = monthTraceCount;
	}

	@Transient
	public String getTraceCount() {
		return traceCount;
	}

	public void setTraceCount(String traceCount) {
		this.traceCount = traceCount;
	}

	@Transient
	public String getCheckedArray() {
		return checkedArray;
	}

	public void setCheckedArray(String checkedArray) {
		this.checkedArray = checkedArray;
	}
	
	@Transient
	public List<TraceLableApply> getTraceLableList() {
		return traceLableList;
	}

	public void setTraceLableList(List<TraceLableApply> traceLableList) {
		this.traceLableList = traceLableList;
	}

	public String getSysId() {
		return sysId;
	}

	public void setSysId(String sysId) {
		this.sysId = sysId;
	}

	@Transient
	public String getAuditFlag() {
		return auditFlag;
	}

	public void setAuditFlag(String auditFlag) {
		this.auditFlag = auditFlag;
	}

}
