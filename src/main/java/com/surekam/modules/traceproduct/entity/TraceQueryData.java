package com.surekam.modules.traceproduct.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.PrePersist;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.surekam.common.persistence.BaseEntity;
import com.surekam.common.persistence.XGXTEntity;
import com.surekam.common.utils.IdGen;

import java.io.Serializable;
import java.lang.String;
import java.util.Date;

/**
 * 溯源数据查询
 * @author liuyi
 * @version 2018-09-25
 */
public class TraceQueryData  implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String officeId;//企业编号
	//private String productId;//溯源产品编号
	private String batchId;//批次号
	private String modelId;
	private String modelName;
	private String modelShowType;
	private String isMainModel;
	private String propertyNameEn;
	private String propertyNameZh;
	private String propertyType;
	private String modelDataId;
	private String propertyModelId;
	private String propertyValue;

	public TraceQueryData() {
		super();
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

	/**
	 * @return the modelId
	 */
	public String getModelId() {
		return modelId;
	}

	/**
	 * @param modelId the modelId to set
	 */
	public void setModelId(String modelId) {
		this.modelId = modelId;
	}

	/**
	 * @return the modelName
	 */
	public String getModelName() {
		return modelName;
	}

	/**
	 * @param modelName the modelName to set
	 */
	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	/**
	 * @return the isMainModel
	 */
	public String getIsMainModel() {
		return isMainModel;
	}

	/**
	 * @param isMainModel the isMainModel to set
	 */
	public void setIsMainModel(String isMainModel) {
		this.isMainModel = isMainModel;
	}

	/**
	 * @return the propertyNameEn
	 */
	public String getPropertyNameEn() {
		return propertyNameEn;
	}

	/**
	 * @param propertyNameEn the propertyNameEn to set
	 */
	public void setPropertyNameEn(String propertyNameEn) {
		this.propertyNameEn = propertyNameEn;
	}

	/**
	 * @return the propertyNameZh
	 */
	public String getPropertyNameZh() {
		return propertyNameZh;
	}

	/**
	 * @param propertyNameZh the propertyNameZh to set
	 */
	public void setPropertyNameZh(String propertyNameZh) {
		this.propertyNameZh = propertyNameZh;
	}

	/**
	 * @return the propertyType
	 */
	public String getPropertyType() {
		return propertyType;
	}

	/**
	 * @param propertyType the propertyType to set
	 */
	public void setPropertyType(String propertyType) {
		this.propertyType = propertyType;
	}

	/**
	 * @return the modelDataId
	 */
	public String getModelDataId() {
		return modelDataId;
	}

	/**
	 * @param modelDataId the modelDataId to set
	 */
	public void setModelDataId(String modelDataId) {
		this.modelDataId = modelDataId;
	}

	/**
	 * @return the propertyModelId
	 */
	public String getPropertyModelId() {
		return propertyModelId;
	}

	/**
	 * @param propertyModelId the propertyModelId to set
	 */
	public void setPropertyModelId(String propertyModelId) {
		this.propertyModelId = propertyModelId;
	}

	/**
	 * @return the propertyValue
	 */
	public String getPropertyValue() {
		return propertyValue;
	}

	/**
	 * @param propertyValue the propertyValue to set
	 */
	public void setPropertyValue(String propertyValue) {
		this.propertyValue = propertyValue;
	}

	/**
	 * @return the modelShowType
	 */
	public String getModelShowType() {
		return modelShowType;
	}

	/**
	 * @param modelShowType the modelShowType to set
	 */
	public void setModelShowType(String modelShowType) {
		this.modelShowType = modelShowType;
	}
	
	
	
	
}


