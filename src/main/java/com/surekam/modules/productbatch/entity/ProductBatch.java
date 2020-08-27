package com.surekam.modules.productbatch.entity;

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
import com.surekam.modules.traceproduct.entity.TraceRootBean;

/**
 * 产品批次Entity
 * @author liuyi
 * @version 2018-09-04
 */
@Entity
@Table(name = "t_trace_product_batch")
@DynamicInsert @DynamicUpdate
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ProductBatch extends XGXTEntity<ProductBatch> {
	
	private static final long serialVersionUID = 1L;
	private String id;//主键
	private String batchCode;//产品批次号
	private Long traceCodesQueryCount;//批次扫码总数 
	private String batchName;//批次名称
	private String officeId;//企业编号
	private String productId;//溯源产品编号
	private String isEnd;//批次结束标记（数据过滤）0未结束  1结束
	private String themeId;//溯源主题编号（可预览主题）
	private String isAudit;//是否需要审核
	
	private String corpName;//企业名称
	private String productName;//溯源产品名称
	private String themeName;//主题名称
	private String isEndName;//批次结束标记（数据过滤）0未结束  1结束
	private String creatUserName;//创建人名称
	
	private TraceRootBean traceRootBean;//封装模块的几个固定属性数据 
	private String jhm;//区间激活码
	private String jhs;//绑定激活数
	private String cjrq;//创建日期
	private String sysId;//外部系统编号（0-本系统，1-农事，2-加工）
	private String auditStatus;//0-待审核，1-审核通过，2-审核拒绝,3-审核驳回
	
	private String dsh;//待审核
	private String yjhs;// 已激活数
	private String wjhs;// 未激活数
	
	private String auditFlag;//0-待审核，1-审核通过，2-审核拒绝

	public ProductBatch() {
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
	
	public String getBatchCode() {
		return this.batchCode;
	}
	public void setBatchCode(String batchCode) {
		this.batchCode = batchCode;
	}
	
	public String getBatchName() {
		return this.batchName;
	}
	public void setBatchName(String batchName) {
		this.batchName = batchName;
	}
	
	/**
	 * @return the officeId
	 */
	public String getOfficeId() {
		return officeId;
	}

	/**
	 * @param officeId the officeId to set
	 */
	public void setOfficeId(String officeId) {
		this.officeId = officeId;
	}

	public String getProductId() {
		return this.productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	
	public String getIsEnd() {
		return this.isEnd;
	}
	public void setIsEnd(String isEnd) {
		this.isEnd = isEnd;
	}
	
	public String getThemeId() {
		return this.themeId;
	}
	public void setThemeId(String themeId) {
		this.themeId = themeId;
	}

	/**
	 * @return the corpName
	 */
	@Transient
	public String getCorpName() {
		return corpName;
	}

	/**
	 * @param corpName the corpName to set
	 */
	public void setCorpName(String corpName) {
		this.corpName = corpName;
	}

	/**
	 * @return the productName
	 */
	@Transient
	public String getProductName() {
		return productName;
	}

	/**
	 * @param productName the productName to set
	 */
	public void setProductName(String productName) {
		this.productName = productName;
	}

	/**
	 * @return the themeName
	 */
	@Transient
	public String getThemeName() {
		return themeName;
	}

	/**
	 * @param themeName the themeName to set
	 */
	public void setThemeName(String themeName) {
		this.themeName = themeName;
	}

	/**
	 * @return the isEndName
	 */
	@Transient
	public String getIsEndName() {
		return isEndName;
	}

	/**
	 * @param isEndName the isEndName to set
	 */
	public void setIsEndName(String isEndName) {
		this.isEndName = isEndName;
	}

	/**
	 * @return the creatUserName
	 */
	@Transient
	public String getCreatUserName() {
		return creatUserName;
	}

	/**
	 * @param creatUserName the creatUserName to set
	 */
	public void setCreatUserName(String creatUserName) {
		this.creatUserName = creatUserName;
	}

	@Transient
	public TraceRootBean getTraceRootBean() {
		return traceRootBean;
	}

	public void setTraceRootBean(TraceRootBean traceRootBean) {
		this.traceRootBean = traceRootBean;
	}

	public Long getTraceCodesQueryCount() {
		return traceCodesQueryCount;
	}

	public void setTraceCodesQueryCount(Long traceCodesQueryCount) {
		this.traceCodesQueryCount = traceCodesQueryCount;
	}

	@Transient
	public String getJhm() {
		return jhm;
	}

	public void setJhm(String jhm) {
		this.jhm = jhm;
	}

	@Transient
	public String getJhs() {
		return jhs;
	}

	public void setJhs(String jhs) {
		this.jhs = jhs;
	}

	@Transient
	public String getCjrq() {
		return cjrq;
	}

	public void setCjrq(String cjrq) {
		this.cjrq = cjrq;
	}

	public String getIsAudit() {
		return isAudit;
	}

	public void setIsAudit(String isAudit) {
		this.isAudit = isAudit;
	}

	public String getSysId() {
		return sysId;
	}

	public void setSysId(String sysId) {
		this.sysId = sysId;
	}

	public String getAuditStatus() {
		return auditStatus;
	}

	public void setAuditStatus(String auditStatus) {
		this.auditStatus = auditStatus;
	}

	@Transient
	public String getDsh() {
		return dsh;
	}

	public void setDsh(String dsh) {
		this.dsh = dsh;
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
	public String getAuditFlag() {
		return auditFlag;
	}

	public void setAuditFlag(String auditFlag) {
		this.auditFlag = auditFlag;
	}
	
}


