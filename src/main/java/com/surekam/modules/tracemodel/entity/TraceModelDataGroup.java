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

/**
 * 溯源模块数据分组Entity
 * 
 * @author ligm
 * @version 2018-10-15
 */
@Entity
@Table(name = "t_trace_model_data_group")
@DynamicInsert
@DynamicUpdate
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class TraceModelDataGroup extends XGXTEntity<TraceModelDataGroup> {

	private static final long serialVersionUID = 1L;
	private String id;// 主键
	private String modelDataId;// 模块数据ID
	private String batchCode; //批次号
	private String baseName; //加工点名称
	private String baseAddress; //加工点地址
	private String isNew; // 状态

	// 以下用于封装数据
	List<TraceProperty> tracePropertyList = new ArrayList<TraceProperty>();// 模块对应属性

	public TraceModelDataGroup() {
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

	@Transient
	public List<TraceProperty> getTracePropertyList() {
		return tracePropertyList;
	}

	public void setTracePropertyList(List<TraceProperty> tracePropertyList) {
		this.tracePropertyList = tracePropertyList;
	}

	public String getModelDataId() {
		return modelDataId;
	}

	public void setModelDataId(String modelDataId) {
		this.modelDataId = modelDataId;
	}

	@Transient
	public String getIsNew() {
		return isNew;
	}

	public void setIsNew(String isNew) {
		this.isNew = isNew;
	}

	public String getBatchCode() {
		return batchCode;
	}

	public void setBatchCode(String batchCode) {
		this.batchCode = batchCode;
	}

	public String getBaseName() {
		return baseName;
	}

	public void setBaseName(String baseName) {
		this.baseName = baseName;
	}

	public String getBaseAddress() {
		return baseAddress;
	}

	public void setBaseAddress(String baseAddress) {
		this.baseAddress = baseAddress;
	}
	
}
