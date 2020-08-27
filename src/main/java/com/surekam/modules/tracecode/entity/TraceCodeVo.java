package com.surekam.modules.tracecode.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.PrePersist;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;


import com.surekam.common.persistence.XGXTEntity;
import com.surekam.common.utils.IdGen;

import java.util.Date;
import java.lang.String;

/**
 * 溯源码Entity
 * @author liuyi
 * @version 2018-09-26
 */
@Entity
@Table(name = "t_trace_code_info")
@DynamicInsert @DynamicUpdate
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class TraceCodeVo extends XGXTEntity<TraceCode> {
	
	private static final long serialVersionUID = 1L;
	private String id;//主键
	private String traceCode;//溯源码（打印时创建溯源码信息）
	private String applyId;//标签申请批次
	private String officeId;//企业id
	private String sysId;//外部系统编号（接口型的系统编号才有数据）
	private String batchId;//产品批次号（接口型在调用接口返回数据后需回写批次号）
	private String traceType;//溯源类型 1配置型  2根据二维码调接口  3根据批次号调接口
	private int traceCount;//扫码次数
	private String packType;//包装类型
	private String antiFakeCode;//防伪码(预留字段)
	private String status;//溯源码状态   1已打印  2已激活
	private Date printDate;//打印日期
	private Date activationDate;//激活日期
	private String activator;//激活人
	//Extra
	private String batchCode;//批次码
	private String productName;//产品名称

	//private String states;//操作状态：A-新增，U-修改，D-删除

	public TraceCodeVo() {
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
	
	public String getTraceCode() {
		return this.traceCode;
	}
	public void setTraceCode(String traceCode) {
		this.traceCode = traceCode;
	}
	
	public String getApplyId() {
		return this.applyId;
	}
	public void setApplyId(String applyId) {
		this.applyId = applyId;
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

	public String getSysId() {
		return this.sysId;
	}
	public void setSysId(String sysId) {
		this.sysId = sysId;
	}
	
	public String getBatchId() {
		return this.batchId;
	}
	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}
	
	public String getTraceType() {
		return this.traceType;
	}
	public void setTraceType(String traceType) {
		this.traceType = traceType;
	}
	@Transient
	public int getTraceCount() {
		return this.traceCount;
	}
	public void setTraceCount(int traceCount) {
		this.traceCount = traceCount;
	}
	
	public String getPackType() {
		return this.packType;
	}
	public void setPackType(String packType) {
		this.packType = packType;
	}
	
	public String getAntiFakeCode() {
		return this.antiFakeCode;
	}
	public void setAntiFakeCode(String antiFakeCode) {
		this.antiFakeCode = antiFakeCode;
	}
	
	public String getStatus() {
		return this.status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	public Date getPrintDate() {
		return this.printDate;
	}
	public void setPrintDate(Date printDate) {
		this.printDate = printDate;
	}
	
	public Date getActivationDate() {
		return this.activationDate;
	}
	public void setActivationDate(Date activationDate) {
		this.activationDate = activationDate;
	}
	
	public String getActivator() {
		return this.activator;
	}
	public void setActivator(String activator) {
		this.activator = activator;
	}
	public String getBatchCode() {
		return batchCode;
	}

	public void setBatchCode(String batchCode) {
		this.batchCode = batchCode;
	}
	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}
	
}


