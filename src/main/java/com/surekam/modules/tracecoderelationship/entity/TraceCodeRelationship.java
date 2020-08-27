package com.surekam.modules.tracecoderelationship.entity;

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

/**
 * 标签层级关系表Entity
 * @author liw
 * @version 2019-07-05
 */
@Entity
@Table(name = "t_trace_code_relationship")
@DynamicInsert @DynamicUpdate
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class TraceCodeRelationship extends XGXTEntity<TraceCodeRelationship> {
	
	private static final long serialVersionUID = 1L;
	private String id;//主键
	private String traceCode;//溯源码
	private String packType;//包装类型
	private String parentId;//上级ID

	public TraceCodeRelationship() {
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
	
	public String getPackType() {
		return this.packType;
	}
	public void setPackType(String packType) {
		this.packType = packType;
	}
	
	public String getParentId() {
		return this.parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	
}


