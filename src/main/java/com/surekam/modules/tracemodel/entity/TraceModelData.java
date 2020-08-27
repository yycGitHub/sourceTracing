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
import com.surekam.modules.traceproperty.entity.TracePropertyData;

/**
 * 溯源模块配置管理Entity
 * @author liuyi
 * @version 2018-09-25
 */
@Entity
@Table(name = "t_trace_model_data")
@DynamicInsert @DynamicUpdate
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class TraceModelData extends XGXTEntity<TraceModelData> {
	
	private static final long serialVersionUID = 1L;
	
	private String id;//
	private String modelDataCode;//模块数据编号
	private String officeId;//企业编号	
	private String batchId;//批次号  
	private String modelId;//溯源模块编号	
	private String modelName;//模块名称
	private String sort;//排序（升序）默认值统一设置为99，按照升序排列
	
	//以下用于封装数据
	List<TraceProperty> tracePropertyList = new ArrayList<TraceProperty>();//模块对应属性
	List<TracePropertyData> tracePropertyDataList = new ArrayList<TracePropertyData>();//模块对应属性值

	public TraceModelData() {
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
	
	public String getOfficeId() {
		return this.officeId;
	}
	public void setOfficeId(String officeId) {
		this.officeId = officeId;
	}
	
	public String getModelName() {
		return this.modelName;
	}
	public void setModelName(String modelName) {
		this.modelName = modelName;
	}
	
	/**
	 * @return the modelDataCode
	 */
	public String getModelDataCode() {
		return modelDataCode;
	}

	/**
	 * @param modelDataCode the modelDataCode to set
	 */
	public void setModelDataCode(String modelDataCode) {
		this.modelDataCode = modelDataCode;
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
	 * @return the sort
	 */
	public String getSort() {
		return sort;
	}

	/**
	 * @param sort the sort to set
	 */
	public void setSort(String sort) {
		this.sort = sort;
	}

	@Transient
	public List<TraceProperty> getTracePropertyList() {
		return tracePropertyList;
	}

	public void setTracePropertyList(List<TraceProperty> tracePropertyList) {
		this.tracePropertyList = tracePropertyList;
	}

	@Transient
	public List<TracePropertyData> getTracePropertyDataList() {
		return tracePropertyDataList;
	}

	public void setTracePropertyDataList(
			List<TracePropertyData> tracePropertyDataList) {
		this.tracePropertyDataList = tracePropertyDataList;
	}
	
	
	
	
	
}


