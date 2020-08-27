/**
 * 
 */
package com.surekam.modules.traceproductaudit.entity;

import java.io.Serializable;

/**
 * Title: SavaAuditReq Description: 保存审核请求参数
 * 
 * @author tangjun
 * @date 2019年7月31日
 */
public class SavaAuditReq implements Serializable {

	private static final long serialVersionUID = 1L;

	private String batchId;
	private String applyId;
	private String auditFlag;
	private String remarks;

	public String getBatchId() {
		return batchId;
	}

	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}

	public String getAuditFlag() {
		return auditFlag;
	}

	public void setAuditFlag(String auditFlag) {
		this.auditFlag = auditFlag;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getApplyId() {
		return applyId;
	}

	public void setApplyId(String applyId) {
		this.applyId = applyId;
	}

}
