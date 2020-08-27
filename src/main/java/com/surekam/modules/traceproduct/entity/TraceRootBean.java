package com.surekam.modules.traceproduct.entity;

import java.io.Serializable;
import java.util.List;

public class TraceRootBean implements Serializable{

	private static final long serialVersionUID = 1L;
	private String batchId;
	private String batchCode;
    private String title;
    private String img;
    private String codeId;
    private MainModelData mainModelData;
    private List<ModelData> modelData;
    private String auditStatus;
    
    public TraceRootBean() {
		super();
	}
    
	/**
	 * @return the batchId
	 */
	public String getBatchId() {
		return batchId;
	}

	/**
	 * @param batchId the batchId to set
	 */
	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}

	public String getBatchCode() {
		return batchCode;
	}

	public void setBatchCode(String batchCode) {
		this.batchCode = batchCode;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the img
	 */
	public String getImg() {
		return img;
	}

	/**
	 * @param img the img to set
	 */
	public void setImg(String img) {
		this.img = img;
	}

	/**
	 * @return the mainModelData
	 */
	public MainModelData getMainModelData() {
		return mainModelData;
	}

	/**
	 * @param mainModelData the mainModelData to set
	 */
	public void setMainModelData(MainModelData mainModelData) {
		this.mainModelData = mainModelData;
	}

	/**
	 * @return the modelData
	 */
	public List<ModelData> getModelData() {
		return modelData;
	}

	/**
	 * @param modelData the modelData to set
	 */
	public void setModelData(List<ModelData> modelData) {
		this.modelData = modelData;
	}

	public String getCodeId() {
		return codeId;
	}

	public void setCodeId(String codeId) {
		this.codeId = codeId;
	}

	public String getAuditStatus() {
		return auditStatus;
	}

	public void setAuditStatus(String auditStatus) {
		this.auditStatus = auditStatus;
	}
	
}