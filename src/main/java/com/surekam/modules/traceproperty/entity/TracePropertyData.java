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
 * 属性数据Entity
 * @author liuyi
 * @version 2018-09-25
 */
@Entity
@Table(name = "t_trace_property_data")
@DynamicInsert @DynamicUpdate
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class TracePropertyData extends XGXTEntity<TracePropertyData> {
	
	private static final long serialVersionUID = 1L;
	private String id;//主键
	private String modelDataId;//模块数据编号
	private String batchId;//产品编号
	private String propertyId;//模块属性ID
	private String propertyValue;//属性值
	private String modelDataGroupId;//分组主键值
	private int sort;//排序（升序）默认值统一设置为99，按照升序排列
	private String dataType;

	public TracePropertyData() {
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

	public String getPropertyId() {
		return propertyId;
	}

	public void setPropertyId(String propertyId) {
		this.propertyId = propertyId;
	}

	public String getModelDataGroupId() {
		return modelDataGroupId;
	}

	public void setModelDataGroupId(String modelDataGroupId) {
		this.modelDataGroupId = modelDataGroupId;
	}

	public int getSort() {
		return sort;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}

	@Transient
	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	
}


