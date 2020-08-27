package com.surekam.modules.tracelableaudit.entity;

import javax.persistence.Column;
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
import java.util.Date;

/**
 * 标签审核Entity
 * @author xy
 * @version 2018-11-28
 */
@Entity
@Table(name = "t_trace_lable_audit")
@DynamicInsert @DynamicUpdate
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class TraceLableAudit extends XGXTEntity<TraceLableAudit> {
	
	private static final long serialVersionUID = 1L;
	private String id;//主键
	private String auditCode;//审核编号
	private String applyId;//标签申请编号
	private String auditExplain;//审核说明
	private String auditResult;//审核结果 0 拒绝   1 通过
	

	public TraceLableAudit() {
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
	
	public String getAuditCode() {
		return this.auditCode;
	}
	public void setAuditCode(String auditCode) {
		this.auditCode = auditCode;
	}
	
	public String getApplyId() {
		return this.applyId;
	}
	public void setApplyId(String applyId) {
		this.applyId = applyId;
	}
	
	public String getAuditExplain() {
		return this.auditExplain;
	}
	public void setAuditExplain(String auditExplain) {
		this.auditExplain = auditExplain;
	}
	
	public String getAuditResult() {
		return this.auditResult;
	}
	public void setAuditResult(String auditResult) {
		this.auditResult = auditResult;
	}
	

	
}


